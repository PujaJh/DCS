package com.amnex.agristack.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Base64;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.amnex.agristack.Enum.ErrorCode;
import com.amnex.agristack.centralcore.entity.UserDeviceIntegrityDetailsHistory;
import com.amnex.agristack.dao.ExceptionAuditDTO;
import com.amnex.agristack.entity.ErrorMessageMaster;
import com.amnex.agristack.entity.UserDeviceIntegrityDetails;
import com.amnex.agristack.repository.ErrorMessageMasterRepository;
import com.amnex.agristack.repository.UserDeviceIntegrityDetailsHistoryRepository;
import com.amnex.agristack.repository.UserDeviceIntegrityDetailsRepository;
import com.amnex.agristack.service.ExceptionLogService;
import com.fasterxml.jackson.core.JsonFactory;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
//import com.google.api.services.playintegrity.Playintegrity;
//import com.google.api.services.playintegrity.model.VerifyAppsResponse;
import com.google.api.services.playintegrity.v1.PlayIntegrity;
import com.google.api.services.playintegrity.v1.PlayIntegrity.Builder;
import com.google.api.services.playintegrity.v1.PlayIntegrityRequestInitializer;
import com.google.api.services.playintegrity.v1.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;

@Service
public class GooglePlayIntegrityService {

	@Value("${app.allowed.appIntegrity.credential}")
	private String appCredential;
	@Value("${app.allowed.appIntegrity.certificate}")
	private String appCertificate;

	@Autowired
	private UserDeviceIntegrityDetailsRepository userDeviceIntegrityDetailsRepository;
	
	@Autowired
	private ExceptionLogService exceptionLogService;
	
	@Autowired
	private UserDeviceIntegrityDetailsHistoryRepository userDeviceIntegrityDetailsHistoryRepository;
	
	@Autowired ErrorMessageMasterRepository errorMessageMasterRepository;

	public void appIntegrity(String token,Long userId) {
		

		DecodeIntegrityTokenRequest requestObj = new DecodeIntegrityTokenRequest();
		requestObj.setIntegrityToken(token);
		// Configure downloaded Json file
		GoogleCredentials credentials;
		DecodeIntegrityTokenResponse response = null;
		try {
			if(token==null||token.equals("")) {
//				throw new RuntimeException("App token is not valid. Please contact admin. (Error: 111)");
				
				Optional<ErrorMessageMaster> cmop=errorMessageMasterRepository.findByErrorCode(ErrorCode.TOKEN_CHECK_NULL.getNumericalCode());
				String message="App token is not valid. Please contact admin. (Error: 111)";
				if(cmop.isPresent()) {
					message=cmop.get().getErrorMessage();
				}
				throw new NoSuchElementException(message);
			}
//			credentials = GoogleCredentials.fromStream(new ClassPathResource("AGs-0-000236-103-26f4fdab9846.json").getInputStream());
			credentials = GoogleCredentials.fromStream(new ClassPathResource(appCredential).getInputStream());

			HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

			HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
			com.google.api.client.json.JsonFactory JSON_FACTORY = new JacksonFactory();
			GoogleClientRequestInitializer initialiser = new PlayIntegrityRequestInitializer();

			Builder playIntegrity = new PlayIntegrity.Builder(HTTP_TRANSPORT, JSON_FACTORY, requestInitializer)
					.setApplicationName("DigitalCropSurvey").setGoogleClientRequestInitializer(initialiser);
			PlayIntegrity play = playIntegrity.build();

			 response = play.v1().decodeIntegrityToken("com.amnex.agristack", requestObj)
					.execute();

			String licensingVerdict = response.getTokenPayloadExternal().getAccountDetails().getAppLicensingVerdict();
//			if (!licensingVerdict.equalsIgnoreCase("LICENSED")) {
//				throw new Exception("Licence is not valid.");
//
//			}
			AppIntegrity appIntegrity = response.getTokenPayloadExternal().getAppIntegrity();

//			checkAppIntegrity(response, appIntegrity.getPackageName());
			
			if (!appIntegrity.getAppRecognitionVerdict().equalsIgnoreCase("PLAY_RECOGNIZED")) {
				
				Optional<ErrorMessageMaster> cmop=errorMessageMasterRepository.findByErrorCode(ErrorCode.PLAY_RECOGNIZED_CHECK.getNumericalCode());
				String message="The certificate or package name does not match Google Play records. Please contact admin. (Error: 113)";
				if(cmop.isPresent()) {
					message=cmop.get().getErrorMessage();
				}
				throw new NoSuchElementException(message);
//				throw new Exception("The certificate or package name does not match Google Play records. Please contact admin. (Error: 113)");
			}
			if (!appIntegrity.getPackageName().equalsIgnoreCase(appIntegrity.getPackageName())) {
				Optional<ErrorMessageMaster> cmop=errorMessageMasterRepository.findByErrorCode(ErrorCode.APP_PACKAGE_CHECK.getNumericalCode());
				String message="App package name does not match with Google Play records. Please contact admin. (Error: 114)";
				if(cmop.isPresent()) {
					message=cmop.get().getErrorMessage();
				}
				throw new NoSuchElementException(message);
//				throw new Exception("App package name does not match with Google Play records. Please contact admin. (Error: 114)");

			}

			if (appIntegrity.getCertificateSha256Digest() != null) {
				// If the app is deployed in Google PlayStore then Download the App signing key
				// certificate from Google Play Console (If you are using managed signing key).
				// otherwise download Upload key certificate and then find checksum of the
				// certificate.

//		         Certificate cert = getCertificate("deployment_cert_Sign_Agristack.der");
				Certificate cert = getCertificate(appCertificate);

				MessageDigest md = MessageDigest.getInstance("SHA-256");

				byte[] der = cert.getEncoded();
				md.update(der);
				byte[] sha256 = md.digest();

				// String checksum = Base64.getEncoder().encodeToString(sha256);
				String checksum = Base64.getUrlEncoder().encodeToString(sha256);
				/**
				 * Sometimes checksum value ends with '=' character, you can avoid this
				 * character before perform the match
				 **/
				checksum = checksum.replaceAll("=", "");
				if (!appIntegrity.getCertificateSha256Digest().get(0).contains(checksum)) {
					Optional<ErrorMessageMaster> cmop=errorMessageMasterRepository.findByErrorCode(ErrorCode.APP_CERTIFICATE_CHECK_MISMATCH.getNumericalCode());
					String message="App certificate does not match with Google Play records. Please contact admin. (Error: 115))";
					if(cmop.isPresent()) {
						message=cmop.get().getErrorMessage();
					}
					throw new NoSuchElementException(message);
//					throw new Exception("App certificate does not match with Google Play records. Please contact admin. (Error: 115)");
				}
			}

			Optional<UserDeviceIntegrityDetails> userOp = userDeviceIntegrityDetailsRepository
					.findByUniqueKey(response.getTokenPayloadExternal().getRequestDetails().getNonce());
			if (!userOp.isPresent()) {
				
				Optional<ErrorMessageMaster> cmop=errorMessageMasterRepository.findByErrorCode(ErrorCode.UNIQUE_CHECK_DB.getNumericalCode());
				String message="Looks like app is not downloaded from trusted source. Please contact admin. (Error: 112)";
				if(cmop.isPresent()) {
					message=cmop.get().getErrorMessage();
				}
				throw new NoSuchElementException(message);
//				throw new Exception("Looks like app is not downloaded from trusted source. Please contact admin. (Error: 112)");
			}else {
				UserDeviceIntegrityDetails userDeviceIntegrityDetails=userOp.get();
				userDeviceIntegrityDetails.setDecodeIntegrityTokenResponse(response.toString());
				userDeviceIntegrityDetails.setModifiedBy(userId+"");
				userDeviceIntegrityDetails.setToken(token);
				userDeviceIntegrityDetailsRepository.save(userDeviceIntegrityDetails);
				
				Optional<UserDeviceIntegrityDetailsHistory> opHistory=userDeviceIntegrityDetailsHistoryRepository.findByUniqueKeyAndDeviceId(userDeviceIntegrityDetails.getUniqueKey(), userDeviceIntegrityDetails.getDeviceId());
				if(opHistory.isPresent()) {
					UserDeviceIntegrityDetailsHistory userDeviceIntegrityDetailsHistory=opHistory.get();
					userDeviceIntegrityDetailsHistory.setDecodeIntegrityTokenResponse(response.toString());
					userDeviceIntegrityDetailsHistory.setModifiedBy(userId+"");
					userDeviceIntegrityDetailsHistory.setToken(token);
					userDeviceIntegrityDetailsHistoryRepository.save(userDeviceIntegrityDetailsHistory);
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
//			throw new RuntimeException("Remove unsupported frameworks before using agristack.", e);
			ExceptionAuditDTO exceptionAuditDTO=new ExceptionAuditDTO();
			  exceptionAuditDTO.setExceptionCode(4444);
				
			  exceptionAuditDTO.setControllerName("GooglePlayIntegrityService");
			  exceptionAuditDTO.setActionName("PlayIntegrity");
			  exceptionAuditDTO.setExceptionDescription(e.getMessage());
			  if(response!=null) {
				  exceptionAuditDTO.setExceptionOriginDetails(response.toString());  
			  }
			  exceptionAuditDTO.setExceptionType("Play Integrity Exception");
			  exceptionAuditDTO.setUserId(userId);
			  
			  exceptionLogService.addExceptionFromMobile(exceptionAuditDTO);
			throw new RuntimeException(e.getMessage());
		}
	}

	public void checkAppIntegrity(DecodeIntegrityTokenResponse response, String appId) throws Exception {
		AppIntegrity appIntegrity = response.getTokenPayloadExternal().getAppIntegrity();

		if (!appIntegrity.getAppRecognitionVerdict().equalsIgnoreCase("PLAY_RECOGNIZED")) {
			throw new Exception("The certificate or package name does not match Google Play records. Please contact admin. (Error: 113)");
		}
		if (!appIntegrity.getPackageName().equalsIgnoreCase(appId)) {
			throw new Exception("App package name does not match with Google Play records. Please contact admin. (Error: 114)");

		}

		if (appIntegrity.getCertificateSha256Digest() != null) {
			// If the app is deployed in Google PlayStore then Download the App signing key
			// certificate from Google Play Console (If you are using managed signing key).
			// otherwise download Upload key certificate and then find checksum of the
			// certificate.

//	         Certificate cert = getCertificate("deployment_cert_Sign_Agristack.der");
			Certificate cert = getCertificate(appCertificate);

			MessageDigest md = MessageDigest.getInstance("SHA-256");

			byte[] der = cert.getEncoded();
			md.update(der);
			byte[] sha256 = md.digest();

			// String checksum = Base64.getEncoder().encodeToString(sha256);
			String checksum = Base64.getUrlEncoder().encodeToString(sha256);
			/**
			 * Sometimes checksum value ends with '=' character, you can avoid this
			 * character before perform the match
			 **/
			checksum = checksum.replaceAll("=", "");
			if (!appIntegrity.getCertificateSha256Digest().get(0).contains(checksum)) {
				throw new Exception("App certificate does not match with Google Play records. Please contact admin. (Error: 115)");
			}
		}
	}

	public static Certificate getCertificate(String certificatePath) throws Exception {
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");
//	    FileInputStream in = new FileInputStream(certificatePath);

		Certificate certificate = certificateFactory
				.generateCertificate(new ClassPathResource(certificatePath).getInputStream());
//	    in.close();

		return certificate;
	}
}
