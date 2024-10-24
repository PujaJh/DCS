package com.amnex.agristack.centralcore.service;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.MessageConfigurationMaster;
import com.amnex.agristack.entity.MessageCredentialMaster;
import com.amnex.agristack.repository.MessageConfigurationRepository;
import com.amnex.agristack.repository.MessageCredentialRepository;
import com.amnex.agristack.service.MessageConfigurationService;
import com.amnex.agristack.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.amnex.agristack.centralcore.Enum.ACMAttributesDBMapping;
import com.amnex.agristack.centralcore.Enum.ACMOperatorsEnum;
import com.amnex.agristack.centralcore.dao.ACMBulkQueryDao;
import com.amnex.agristack.centralcore.dao.ACMRequestDAO;
import com.amnex.agristack.centralcore.dao.AIFarmerDetailsDAO;
import com.amnex.agristack.centralcore.dao.AIPStoreEncryptedResponseDTO;
import com.amnex.agristack.centralcore.dao.AssetIdentificationRequestDao;
import com.amnex.agristack.centralcore.dao.AssetResponseDAO;
import com.amnex.agristack.centralcore.entity.ACMRequests;
import com.amnex.agristack.centralcore.entity.ForgotFarmerOtp;
import com.amnex.agristack.centralcore.repository.ACMRequestsRepository;
import com.amnex.agristack.centralcore.repository.AssetIdentificationRepository;
import com.amnex.agristack.centralcore.repository.ForgotFarmerOtpRepository;
import com.amnex.agristack.centralcore.util.AESUtil;
import com.amnex.agristack.repository.FarmerRegistryRepository;
import com.amnex.agristack.utils.CustomMessages;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gson.Gson;

import io.netty.util.internal.StringUtil;

@Service
public class AssetIdentificationService {

	@Autowired
	AssetIdentificationRepository assetIdentificationRepository;

	@Autowired
	ACMRequestsRepository acmRequestsRepository;

	@Autowired
	RestTemplate restTemplate;

	@Value("${aip.redirect.url}")
	private String aipUIRedirectURL;

	@Value("${sso.state.endpoint}")
	private String ssoEndpoint;

	@Autowired
	EntityManager entityManager;

	@Autowired
	private MessageCredentialRepository messageCredentialRepository;

	@Autowired
	private MessageConfigurationRepository messageConfigurationRepository;

	@Autowired
	MessageConfigurationService messageConfigurationService;

	public ResponseModel getFarmerProfileAndLandDetailsByFarmerId(AssetIdentificationRequestDao dao,
			HttpServletRequest request) {
		try {
			Gson gson = new Gson();
			String seasondetails = gson.toJson(dao.getSurvey_season());
			String response = assetIdentificationRepository.getFarmerProfileAndLandDetailsByFarmerId(dao.getFarmer_id(),
					seasondetails);

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			AssetResponseDAO resultObject = mapper.readValue(response, AssetResponseDAO.class);

			return new ResponseModel(resultObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
		}
	}

	public ResponseModel getFarmerNumberByFilter(AssetIdentificationRequestDao dao, HttpServletRequest request) {
		try {

			String encAadharNo = "";

			if (!StringUtil.isNullOrEmpty(dao.getAadharNo())) {
				encAadharNo = Base64.getEncoder().encodeToString(dao.getAadharNo().getBytes());

			}

			String response = assetIdentificationRepository.getFarmerNumberByFilter(dao.getDistrictLgdCode(),
					dao.getSubDistrictLgdCode(), dao.getVillageLgdCode(), dao.getSurveyNo(), dao.getSubSurveyNo(),
					encAadharNo);

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<AIFarmerDetailsDAO> resultObject = mapper.readValue(response,
					typeFactory.constructCollectionType(List.class, AIFarmerDetailsDAO.class));
			List<AIFarmerDetailsDAO> finalResult = resultObject.stream().map((AIFarmerDetailsDAO farmerDetail) -> {
				try {
					farmerDetail.setFarmerNumber(AESUtil.encrypt(farmerDetail.getFarmerNumber()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				return farmerDetail;
			}).collect(Collectors.toList());
			return new ResponseModel(finalResult, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
		}
	}

	public ResponseModel storeACMRequest(ACMRequestDAO dao, HttpServletRequest request) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			String payload = objectMapper.writeValueAsString(dao);
			ACMRequests acmRequest = new ACMRequests();
			acmRequest.setRequestId(dao.getRequest_id());
			acmRequest.setPayload(payload);
			acmRequestsRepository.save(acmRequest);
			HashMap<String, String> responseMap = new HashMap<>();
			responseMap.put("redirectURL", aipUIRedirectURL + acmRequest.getRequestId());
			return new ResponseModel(responseMap, CustomMessages.RECORD_ADD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
		}
	}

	public ResponseModel getACMRequest(String requestId, HttpServletRequest request) {
		try {
			ACMRequests acmRequest = acmRequestsRepository.findByRequestId(requestId).get();
			return new ResponseModel(acmRequest, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
		}
	}

	public ResponseModel storeEncryptedData(AIPStoreEncryptedResponseDTO dto, HttpServletRequest request) {
		try {
			ACMRequests acmRequest = acmRequestsRepository.findByRequestId(dto.getRequest_id()).get();
			acmRequest.setResponse(AESUtil.encrypt(dto.getData()));
			acmRequestsRepository.save(acmRequest);
			return new ResponseModel(null, CustomMessages.RECORD_UPDATE, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
		}
	}

	public ResponseModel getEncryptedData(String requestId, HttpServletRequest request) {
		try {
			ACMRequests acmRequest = acmRequestsRepository.findByRequestId(requestId).get();
			HashMap<String, String> responseMap = new HashMap<>();
			responseMap.put("request_id", acmRequest.getRequestId());
			responseMap.put("encrypted_string", acmRequest.getResponse());
			return new ResponseModel(responseMap, CustomMessages.RECORD_UPDATE, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
		}
	}

	public Boolean validateToken(HttpServletRequest request) {
		String authToken = request.getHeader("authorization");
		String apiUrl = ssoEndpoint;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", authToken);
		HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(
					apiUrl, HttpMethod.GET, requestEntity, String.class);
			responseEntity.getHeaders().get("Authorization");
			return true;
		} catch (HttpClientErrorException e) {
			// 401 will throw error here
			return false;
		}
	}

	public Object getBulkFarmerIdsForACM(List<ACMBulkQueryDao> dao) {
		List<ACMBulkQueryDao> sortedQuery = dao.stream()
				.sorted(Comparator.comparing(ACMBulkQueryDao::getSeq_number))
				.collect(Collectors.toList());
		StringBuilder queryStringBuilder = new StringBuilder();
		sortedQuery.forEach((ACMBulkQueryDao queryDao) -> {
			if (queryStringBuilder.length() == 0) {
				queryStringBuilder.append("where");
			}
			String attribute = ACMAttributesDBMapping.valueOf(queryDao.getCriteria().getAttribute_name()).getValue();
			String attributeOperator = ACMOperatorsEnum.getByOperator(queryDao.getCriteria().getOperator());
			String attributeValue = queryDao.getCriteria().getAttribute_value();
			String condition = queryDao.getCondition();
			String predicate = "";
			switch (queryDao.getCriteria().getAttribute_name()) {
				case "DOB":
					predicate = " ( date(" + attribute + ") " + attributeOperator + " TO_DATE('" + attributeValue
							+ "', 'DD-MM-YYYY')) ";
					break;
				case "AADHAAR":
					// String aadharHash = DigestUtils.sha256Hex(attributeValue);
					String aadharHash = new String(Base64.getEncoder().encode(attributeValue.getBytes()));
					predicate = " ( lower(" + attribute + ") " + attributeOperator + " lower('" + aadharHash + "')) ";
					break;
				default:
					predicate = " (" + attribute + " " + attributeOperator + " " + attributeValue + ") ";
					break;
			}
			queryStringBuilder.append(predicate);
			if (!condition.toUpperCase().equals("NA")) {
				queryStringBuilder.append(condition);
			}
		});

		System.out.println(queryStringBuilder);
		Query query = entityManager.createNativeQuery(
				"Select fr_farmer_number from  agri_stack.farmer_registry " + queryStringBuilder.toString());
		List<Object[]> result = query.getResultList();
		return result;
	}

	@Autowired
	private ForgotFarmerOtpRepository forgotFarmerOtpRepository;

	@Autowired
	private FarmerRegistryRepository farmerRegistryRepository;

	public void generateForgotFarmerOTP(String farmerId) {

		// Generate and save OTP
		String otp = generateOTP();
		LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(30); // Set expiry time as needed

		ForgotFarmerOtp forgotFarmerOtp = new ForgotFarmerOtp();
		forgotFarmerOtp.setFarmerId(farmerId);
		forgotFarmerOtp.setOtp(otp);
		forgotFarmerOtp.setExpiryTime(expiryTime);

		forgotFarmerOtpRepository.save(forgotFarmerOtp);

		String mobileNo = farmerRegistryRepository.findByFarmerRegistryNumber(farmerId);
		System.out.println(mobileNo);
		try {
//			sendOTP(otp, mobileNo);
			sendOTPForForgotFarmer(otp, mobileNo);
		} catch (Exception e) {
		}
	}

	public static void sendOTP(String code, String mobileNumber) {
		String smsText = code
				+ " is OTP for ACM login, valid for 5 minutes. Please do not share OTP with anyone. (Generated at "
				+ new Date() + ")";
		String hostURL = "https://mkisan.gov.in/api/SMSOtp";
		String userName = "agristack";
		String password = "9082023";
		try {
			URL url = new URL(hostURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			connection.setRequestProperty("AuthKey", password);

			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			String smsId = "O" + timestamp.getTime() + String.format("%02d", 10 + new Random().nextInt(90));

			StringBuilder requestBody = new StringBuilder();
			requestBody.append("MobileNumber=").append(mobileNumber)
					.append("&SMSText=").append(smsText)
					.append("&SMSId=").append(smsId)
					.append("&UserID=").append(userName);

			connection.setDoOutput(true);
			try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
				wr.writeBytes(requestBody.toString());
				wr.flush();
			}

			int responseCode = connection.getResponseCode();
			System.out.println(responseCode);
			// Handle the response if needed

			connection.disconnect();
		} catch (Exception e) {
			e.printStackTrace(); // Handle the exception appropriately
		}
	}

	public static String generateOTP() {
		// Define the range for the OTP (6-digit number)
		int otpLength = 6;
		int min = (int) Math.pow(10, otpLength - 1);
		int max = (int) Math.pow(10, otpLength) - 1;

		// Generate a random number within the defined range
		Random random = new Random();
		int otpValue = random.nextInt(max - min + 1) + min;

		// Format the OTP to ensure it is always 6 digits
		return String.format("%06d", otpValue);
	}

	public boolean validateOTP(String farmerId, String otp) {
		Optional<ForgotFarmerOtp> otpEntity = forgotFarmerOtpRepository.findFirstByFarmerIdAndOtpOrderByCreatedOnDesc(
				farmerId,
				otp);

		if (otpEntity.isPresent() && !otpEntity.get().getExpiryTime().isBefore(LocalDateTime.now())) {
			forgotFarmerOtpRepository.delete(otpEntity.get()); // Remove the OTP after successful validation
			return true;
		} else {
			return false;
		}
	}

	public void sendOTPForForgotFarmer(String otp, String mobileNo){
		MessageCredentialMaster messageCredentialMaster = messageCredentialRepository
				.findByMessageCredentialType("MOBILE").get();

		// findByTemplateId
		MessageConfigurationMaster messageConfiguartionMaster = messageConfigurationRepository
				.findByTemplateId(Constants.FORGOT_FARMER_OTP_SMS_TEMPLATE_ID).get();

		if (!messageConfiguartionMaster.getTemplate().isEmpty()) {
			String template = messageConfiguartionMaster.getTemplate();
			String smsTemplate = template.replace("{$1}", otp).replace("{$2}", "Forgot Farmer id")
					.replace("{$3}", String.valueOf(new Date()));

			messageConfigurationService.sendOTP(messageCredentialMaster.getHost(), messageCredentialMaster.getUserName(), messageCredentialMaster.getPassword(), mobileNo, smsTemplate);
		}
	}

}