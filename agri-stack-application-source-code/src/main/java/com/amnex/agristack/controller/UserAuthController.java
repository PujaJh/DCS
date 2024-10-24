package com.amnex.agristack.controller;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.config.GooglePlayIntegrityService;
import com.amnex.agristack.dao.CentralCoreAuthInputDAO;
import com.amnex.agristack.dao.CheckIntegrityDAO;
import com.amnex.agristack.dao.ExceptionAuditDTO;
import com.amnex.agristack.dao.OTPRequestDAO;
import com.amnex.agristack.dao.TokenDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.service.ExceptionLogService;
import com.amnex.agristack.service.UserAuthMobileService;
import com.amnex.agristack.service.UserAuthService;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.amnex.agristack.dao.UserInputDAO;
import com.amnex.agristack.service.UserService;
import com.amnex.agristack.utils.CustomMessages;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/authenticate/user")
public class UserAuthController {


	@Autowired
	private UserAuthService userAuthService;

	@Autowired
	private UserAuthMobileService userAuthMobileService;
	@Autowired
	private UserService userService;
  
	/**
	 * Performs user login.
	 *
	 * @param request      The HttpServletRequest object containing the request information.
	 * @param userInputDAO The user input data.
	 * @return The ResponseModel containing the response data.
	 */
	@PostMapping(value = "/login")
	public ResponseModel login(HttpServletRequest request, @RequestBody UserInputDAO userInputDAO) {

		return userService.login(request, userInputDAO);

	}

	/**
	 * Sends an OTP (One-Time Password) request.
	 *
	 * @param otpRequestDAO The OTP request data.
	 * @param request       The HttpServletRequest object containing the request information.
	 * @return The ResponseModel containing the response data.
	 */
	@PostMapping("/requestOTP")
	ResponseModel sendOTP(@RequestBody OTPRequestDAO otpRequestDAO, HttpServletRequest request) {

		return userAuthService.generateOTP(otpRequestDAO, request);

	}

	/**
	 * Verifies an OTP (One-Time Password).
	 *
	 * @param otpRequestDAO The OTP request data.
	 * @param request       The HttpServletRequest object containing the request information.
	 * @return The ResponseModel containing the response data.
	 */
	@PostMapping("/verifyOTP")
	ResponseModel verifyOTP(@RequestBody OTPRequestDAO otpRequestDAO, HttpServletRequest request) {

		return userAuthService.verifyOTP(otpRequestDAO, request);

	}

	/**
	 * Performs mobile login.
	 *
	 * @param otpRequestDAO The OTP request data.
	 * @param request       The HttpServletRequest object containing the request information.
	 * @return The ResponseModel containing the response data.
	 */
	@PostMapping("/mobile/login")
	ResponseModel mobileLogin(@RequestBody OTPRequestDAO otpRequestDAO, HttpServletRequest request) {
		  try {
			
			  return userAuthMobileService.login(otpRequestDAO, request);
	     
		  } catch (NoSuchElementException e) {
	        	
	            return new ResponseModel(null,
	            		e.getMessage(), HttpStatus.SC_NOT_FOUND, CustomMessages.FAILED,
	                    CustomMessages.METHOD_POST);
	       }catch (Exception e) {
	            e.printStackTrace();
	            return new ResponseModel(null,
	            		e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
	                    CustomMessages.METHOD_POST);
	        }
	}
	
	/**
	 * Performs mobile logout.
	 *
	 * @param otpRequestDAO The OTP request data.
	 * @param request       The HttpServletRequest object containing the request information.
	 * @return The ResponseModel containing the response data.
	 */
	@PostMapping("/mobile/logout")
	ResponseModel mobileLogout(@RequestBody OTPRequestDAO otpRequestDAO, HttpServletRequest request) {
		return userAuthMobileService.logout(otpRequestDAO, request);
	}
	
	/**
	 * Performs mobile forgot password.
	 *
	 * @param otpRequestDAO The OTP request data.
	 * @param request       The HttpServletRequest object containing the request information.
	 * @return The ResponseModel containing the response data.
	 */
	@PostMapping("/mobile/forgotPassword")
	ResponseModel forgotPassword(@RequestBody OTPRequestDAO otpRequestDAO, HttpServletRequest request) {
		return userAuthMobileService.forgotPassword(otpRequestDAO, request);
	}
	
	/**
	 * Performs web forgot password.
	 *
	 * @param otpRequestDAO The OTP request data.
	 * @param request       The HttpServletRequest object containing the request information.
	 * @return The ResponseModel containing the response data.
	 */
	@PostMapping("/forgotPassword")
	ResponseModel forgotPasswordWeb(@RequestBody OTPRequestDAO otpRequestDAO, HttpServletRequest request) {
		return userAuthMobileService.forgotPasswordWeb(otpRequestDAO, request);
	}

	/**
	 * Change IMEI number by userId.
	 *
	 * @param otpRequestDAO The OTP request data.
	 * @param request       The HttpServletRequest object containing the request information.
	 * @return The ResponseModel containing the response data.
	 */
	@PostMapping("/changeIMEINumberByUser")
	ResponseModel changeIMEINumberByUser(@RequestBody OTPRequestDAO otpRequestDAO, HttpServletRequest request) {
		return userAuthMobileService.changeIMEINumberByUser(otpRequestDAO, request);
	}

	/**
	 * Get Token.
	 *
	 * @param tokenDAO The token dao data
	 * @return The ResponseModel containing the response data.
	 */
	@PostMapping("/getToken")
	ResponseModel getToken(@RequestBody TokenDAO tokenDAO) {
		return userService.getToken(tokenDAO);
	}

	/**
	 * Get Token.
	 *
	 * @param tokenDAO the token dao data
	 * @return The ResponseModel containing the response data.
	 */
	@PostMapping("/getExternalToken")
	ResponseModel getExternalToken(@RequestBody UserInputDAO tokenDAO) {
		return userService.getExternalToken(tokenDAO);
	}

	/**
	 * Get Token.
	 *
	 * @param tokenDAO The token dao data
	 * @return The ResponseModel containing the response data.
	 */
	@PostMapping("/token")
	Map<String, String> getExternalTokenForCentralCore(@RequestBody CentralCoreAuthInputDAO tokenDAO) {
		return userService.getExternalTokenForCentralCore(tokenDAO);
	}

	/**
	 * Get general token.
	 *
	 * @param checkIntegrityDAO the token dao data.
	 * @return The ResponseModel containing the response data.
	 */
	@PostMapping("/genrateToken")
	ResponseModel genrateToken(@RequestBody CheckIntegrityDAO checkIntegrityDAO) {
		return userService.genrateToken(checkIntegrityDAO);
	}

	/**
	 * Check integrity Validation.
	 *
	 * @param checkIntegrityDAO the input data for integrity validation
	 * @return The ResponseModel containing the response data.
	 */
	@PostMapping("/integrityValidation")
	ResponseModel integrityValidation(@RequestBody CheckIntegrityDAO checkIntegrityDAO) {
		return userService.integrityValidation(checkIntegrityDAO);
	}
	

}
