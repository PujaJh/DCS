package com.amnex.agristack.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.amnex.agristack.Enum.VerificationType;
import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.config.JwtUserDetailsService;
import com.amnex.agristack.dao.LoginResponseDAO;
import com.amnex.agristack.dao.OTPRequestDAO;
import com.amnex.agristack.entity.OTPRegistration;
import com.amnex.agristack.repository.OTPRepository;
import com.amnex.agristack.repository.UserMasterRepository;
import com.amnex.agristack.utils.CommonUtil;
import com.amnex.agristack.utils.CustomMessages;

@Service
public class UserAuthService {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	private String otpMessage;
	@Autowired
	private OTPRepository otpRepository;
	@Autowired
	private UserMasterRepository userRepository;

	@Autowired
	private JwtUserDetailsService userDetailsService;

	@Value(value = "${otp.testing}")
	private Boolean isTesting;

	@Autowired
	private MessageConfigurationService messageConfigurationService;

	/**
	 * Sends an OTP (One-Time Password) request.
	 *
	 * @param otpRequestDAO The OTP request data.
	 * @param httpServletRequest       The HttpServletRequest object containing the request information.
	 * @return The ResponseModel containing the response data.
	 */
	public ResponseModel generateOTP(OTPRequestDAO otpRequestDAO, HttpServletRequest httpServletRequest) {
		String verificationSource = otpRequestDAO.getVerificationSource();
		//otpRepository.deleteByVerificationSource(verificationSource);
		OTPRegistration existingRegistration = otpRepository
				.findByVerificationSource(otpRequestDAO.getVerificationSource());
		OTPRegistration otpRegistration = new OTPRegistration();
		if (existingRegistration != null) {
			if (existingRegistration.getOtpResendCount()!=null &&  existingRegistration.getOtpResendCount() >= 3) {
				Date cDate = new Date();

				long difference_In_Time = (existingRegistration.getCreatedOn().getTime()) - (cDate.getTime());

				long diffMinutes = difference_In_Time / (60 * 1000) % 60;

				diffMinutes = Math.abs(diffMinutes);

				if (diffMinutes < 10) {
					return new ResponseModel(true,
							"You've exceeded the maximum number of attempts to resend OTP, please try after 10 mins.",
							CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
				} else {
					otpRegistration.setOtpResendCount(1);
				}
				//return new ResponseModel(true, "You've exceeded the maximum number of attempts.",
				//		CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
			} else {
				if(existingRegistration.getOtpResendCount()!=null) {
				otpRegistration.setOtpResendCount(existingRegistration.getOtpResendCount() + 1);
				}else {
					otpRegistration.setOtpResendCount(1);
	
				}

			}
		} else {
			otpRegistration.setOtpResendCount(1);
		}
		otpRepository.deleteByVerificationSource(verificationSource);

		String language = httpServletRequest.getHeader("language");
		MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		Map<String, String> map = new HashMap<String, String>();
		map.put("Content-Type", "application/json");
		headers.setAll(map);

		String otp ="";
		Random random = new Random();

		if(otpRequestDAO.getVerificationType().equals(VerificationType.AADHAAR.toString())) {

		}
		if(isTesting) {
			otp = "123456";
		} else {
			// to Bypasss OTP sending
//			otp = String.format("%06d", random.nextInt(10000));
			otp = "123456";
		}
		otpRegistration.setOtp(otp);
		otpRegistration.setVerificationSource(otpRequestDAO.getVerificationSource());
		otpRegistration.setVerificationType(otpRequestDAO.getVerificationType());
		otpRegistration.setCreatedOn(new Timestamp(new Date().getTime()));
		otpRegistration.setCreatedIp(CommonUtil.getRequestIp(httpServletRequest));
		otpRegistration.setIsActive(Boolean.TRUE);
		otpRegistration.setIsDeleted(Boolean.FALSE);
		otpRegistration = otpRepository.save(otpRegistration);

		messageConfigurationService.sendOTPMobilEmail(otpRegistration,otpRequestDAO);
		return new ResponseModel(otp,"OTP Generate Successfully",CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS,  CustomMessages.METHOD_POST);
	}

	/**
	 * Verifies an OTP (One-Time Password).
	 *
	 * @param req The {@code OTPRequestDAO} request data.
	 * @param request       The HttpServletRequest object containing the request information.
	 * @return The ResponseModel containing the response data.
	 */
	public ResponseModel verifyOTP(OTPRequestDAO req, HttpServletRequest request) {
		String language = request.getHeader("language");
		OTPRegistration otpRegistration = otpRepository.findByOtpAndVerificationSource(req.getOtp(), req.getVerificationSource());

		if (otpRegistration == null) {
			LoginResponseDAO res = new LoginResponseDAO();
			res.setVerificationSource(req.getVerificationSource());
			res.setIsVerified(false);
			return new ResponseModel(res, CustomMessages.INVALID_OTP,CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS,  CustomMessages.METHOD_POST);
		}

		Date cDate = new Date();

		long difference_In_Time = (otpRegistration.getCreatedOn().getTime()) - (cDate.getTime());

		long diffMinutes = difference_In_Time / (60 * 1000) % 60;

		diffMinutes = Math.abs(diffMinutes);

		if (diffMinutes > 5) {
			otpRepository.deleteByVerificationSource(req.getVerificationSource());
			LoginResponseDAO res = new LoginResponseDAO();
			res.setVerificationSource(req.getVerificationSource());
			res.setIsVerified(false);
			return  new ResponseModel(res, CustomMessages.OTP_EXPIRED, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS,  CustomMessages.METHOD_POST);
		}

		try {
			LoginResponseDAO res = new LoginResponseDAO();
			res.setCurrentLanguage(language.toLowerCase());
			otpRepository.deleteByVerificationSource(req.getVerificationSource());
			res.setUserName(req.getUserName());
			res.setDeviceToken(req.getDeviceToken());
			res.setUserName(req.getUserName());
			res.setVerificationSource(req.getVerificationSource());
			res.setIsVerified(true);
			return new ResponseModel(res, CustomMessages.OTP_VERIFY_SUCCESS, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS,  CustomMessages.METHOD_POST);

		}
		catch (BadCredentialsException ex) {
			ex.printStackTrace();
			return new ResponseModel(ex.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR_MESSAGE, CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,  CustomMessages.METHOD_POST);
		}
		catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR_MESSAGE,  CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,  CustomMessages.METHOD_POST);
		}
	}

}
