package com.amnex.agristack.centralcore.controller;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.centralcore.Enum.QueryTypeEnum;
import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.centralcore.dao.ACMBulkQueryDao;
import com.amnex.agristack.centralcore.dao.ACMRequestDAO;
import com.amnex.agristack.centralcore.dao.AIPStoreEncryptedResponseDTO;
import com.amnex.agristack.centralcore.dao.AssetIdentificationRequestDao;
import com.amnex.agristack.centralcore.dao.ErrorDAO;
import com.amnex.agristack.centralcore.dao.ForgotFarmerIdOtp;
import com.amnex.agristack.centralcore.dao.TransactionRequestDAO;
import com.amnex.agristack.centralcore.exception.UnauthorizedException;
import com.amnex.agristack.centralcore.service.AssetIdentificationService;
import com.amnex.agristack.centralcore.service.CentralCoreService;
import com.amnex.agristack.centralcore.util.AESUtil;
import com.amnex.agristack.utils.CustomMessages;

@RestController
@RequestMapping("/assetIdentification")
public class AssetIdentificationController {

	@Autowired
	private AssetIdentificationService assetIdentificationService;

	/**
	 * Retrieves the farmer profile and land details by farmer id
	 * @param dao The AssetIdentificationDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/getFarmerProfileAndLandDetailsByFarmerId")
	public Object getFarmerProfileAndLandDetailsByFarmerId(@RequestBody AssetIdentificationRequestDao dao,
			HttpServletRequest request) {
		try {
			if (!assetIdentificationService.validateToken(request)) {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
			return assetIdentificationService.getFarmerProfileAndLandDetailsByFarmerId(dao, request);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
		}
	}

	/**
	 * Retrieves the farmer number by filter
	 * @param dao The AssetIdentificationDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/getFarmerNumberByFilter")
	public Object getFarmerNumberByFilter(@RequestBody AssetIdentificationRequestDao dao,
			HttpServletRequest request) {
		return assetIdentificationService.getFarmerNumberByFilter(dao, request);
	}

	/**
	 * Store Acm Request
	 * @param dao The AcmRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/storeACMRequest")
	public Object storeACMRequest(@RequestBody ACMRequestDAO dao,
			HttpServletRequest request) {
		if (!assetIdentificationService.validateToken(request)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		return assetIdentificationService.storeACMRequest(dao, request);
	}

	/**
	 * Retrieves the Acm Request
	 * @param reqeustId request id to fetch the acm request
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@GetMapping(value = "/getACMRequest")
	public Object getACMRequest(@RequestParam("requestId") String reqeustId,
			HttpServletRequest request) {
		return assetIdentificationService.getACMRequest(reqeustId, request);
	}

	/**
	 * Store encrypted data
	 * @param dao The AipStoreEncryptedResponseDto containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/storeEncryptedData")
	public Object storeEncryptedData(@RequestBody AIPStoreEncryptedResponseDTO dao,
			HttpServletRequest request) {
		if (!assetIdentificationService.validateToken(request)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		return assetIdentificationService.storeEncryptedData(dao, request);
	}

	/**
	 * Retrieves the encrypted data by request id
	 * @param reqeustId request id to get encrypted data
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@GetMapping(value = "/getEncryptedData")
	public Object getEncryptedData(@RequestParam("requestId") String reqeustId,
			HttpServletRequest request) {
		if (!assetIdentificationService.validateToken(request)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		return assetIdentificationService.getEncryptedData(reqeustId, request);
	}

	/**
	 * Retrieves the list of farmerIds
	 * @param dao The list of ACMBulkQueryDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/bulkFarmerIds")
	public Object bulkFarmerId(@RequestBody List<ACMBulkQueryDao> dao,
			HttpServletRequest request) {
		if (!assetIdentificationService.validateToken(request)) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		return assetIdentificationService.getBulkFarmerIdsForACM(dao);
	}

	@Autowired
	CentralCoreService centralCoreService;

	/**
	 * Async request to provide the data by request body
	 * @param transactionRequestDAO The TransactionRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/seek")
	public Object seek(HttpServletRequest request,
			@RequestBody TransactionRequestDAO transactionRequestDAO) {
		try {
			List<String> nonValidFields = validateSeekRequestDao(transactionRequestDAO);
			if (!nonValidFields.isEmpty()){
				Map<String, Object> responseData = new HashMap<>();
				List<ErrorDAO> errorList = new ArrayList<>();
				for(String message: nonValidFields){
					ErrorDAO error = new ErrorDAO();
					error.setCode("400");
					error.setMessage(message);
					errorList.add(error);
				}
				responseData.put("error", errorList);
				return responseData;
			}else {
				return centralCoreService.seekv2(request, transactionRequestDAO);
			}
		} catch (UnauthorizedException ue) {
			throw new UnauthorizedException("You are not authorized to access this resource.");
		} catch (Exception e) {
			e.printStackTrace();
			Map<String, Object> responseData = new HashMap<>();
			ErrorDAO error = new ErrorDAO();
			error.setCode("500");
			error.setMessage(e.getMessage());
			responseData.put("error", error);
			return responseData;
		}
	}

	/**
	 * Generate otp for forgot farmer id
	 * @param farmerId farmer id to generate otp
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/generateForgotFarmerOtp")
	public Object generateForgotFarmerOtp(@RequestParam("farmerId") String farmerId, HttpServletRequest request) {
		try {
			assetIdentificationService.generateForgotFarmerOTP(farmerId);
			return new ResponseModel("OTP generated SuccessFully", CustomMessages.SUCCESS,
					CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
		}
	}

	/**
	 * Validate the otp for forgot farmer id
	 * @param forgotFarmerIdOtp The ForgotFarmerIdOtp containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/validateForgotFarmerOtpAndGetFarmerId")
	public Object validateForgotFarmerOtp(@RequestBody ForgotFarmerIdOtp forgotFarmerIdOtp,
			HttpServletRequest request) {
		if (assetIdentificationService.validateOTP(forgotFarmerIdOtp.getFarmerId(), forgotFarmerIdOtp.getOtp())) {
			return new ResponseModel(AESUtil.decrypt(forgotFarmerIdOtp.getData()), CustomMessages.SUCCESS,
					CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} else {
			return CustomMessages.makeResponseModel("Invalid OTP", CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
			CustomMessages.FAILED);
		}
	}

	private List<String> validateSeekRequestDao(TransactionRequestDAO transactionRequestDAO) {
		List<String> nonValidFields = new ArrayList<>();
		Boolean isValidQueryType = QueryTypeEnum.isValidQueryType(
				transactionRequestDAO.getMessage().getSearch_request().get(0).getSearch_criteria().getQuery_type());
		if (Boolean.FALSE.equals(isValidQueryType)){
			nonValidFields.add("Query type is not valid");
		}

		Boolean isValidMessageTimeStamp = isValidIsoDateTime(transactionRequestDAO.getHeader().getMessage_ts());
		if (Boolean.FALSE.equals(isValidMessageTimeStamp)){
			nonValidFields.add("Message timestamp is not valid");
		}

		if (transactionRequestDAO.getMessage().getSearch_request()
				.get(0).getTimestamp() == null){
			nonValidFields.add("Search request timestamp is not valid");
		}

		Pattern MapperId_REGEX = Pattern.compile("i[0-9]+:o[0-9]+");
		Boolean isValidMapperId = MapperId_REGEX.matcher(transactionRequestDAO.getMessage().getSearch_request().get(0)
				.getSearch_criteria().getQuery().getMapper_id()).matches();
		if (Boolean.FALSE.equals(isValidMapperId)){
			nonValidFields.add("Mapper ID is not valid");
		}

		Pattern UUID_REGEX = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");
		Boolean isValidReceiverId = UUID_REGEX.matcher(transactionRequestDAO.getHeader().getReceiver_id()).matches();
		if (Boolean.FALSE.equals(isValidReceiverId)){
			nonValidFields.add("Receiver ID is not valid");
		}
		Boolean isValidSenderId = UUID_REGEX.matcher(transactionRequestDAO.getHeader().getSender_id()).matches();
		if (Boolean.FALSE.equals(isValidSenderId)){
			nonValidFields.add("Sender ID is not valid");
		}
		Boolean isValidTransactionId = UUID_REGEX.matcher(transactionRequestDAO.getMessage().getTransaction_id()).matches();
		if (Boolean.FALSE.equals(isValidTransactionId)){
			nonValidFields.add("Transaction ID is not valid");
		}
		Boolean isValidReferenceId = UUID_REGEX.matcher(transactionRequestDAO.getMessage().getSearch_request().get(0)
				.getReference_id()).matches();
		if (Boolean.FALSE.equals(isValidReferenceId)){
			nonValidFields.add("Reference ID is not valid");
		}

		String senderUrl = transactionRequestDAO.getHeader().getSender_uri();
		Boolean isValidSenderUrl = isValidURL(senderUrl);
		Boolean isSenderUrlIsOnSeek = Boolean.FALSE;
		if(Arrays.asList(senderUrl.split("/")).contains("on-seek") ||
				Arrays.asList(senderUrl.split("/")).contains("seekerOnSeek")){
			isSenderUrlIsOnSeek = Boolean.TRUE;
		}
		if (Boolean.FALSE.equals(isValidSenderUrl) || Boolean.FALSE.equals(isSenderUrlIsOnSeek)){
			nonValidFields.add("Sender URI is not valid");
		}

		return nonValidFields;
	}

	Boolean isValidIsoDateTime(String date) {
		try {
			DateTimeFormatter.ISO_DATE_TIME.parse(date);
			return true;
		} catch (DateTimeParseException e) {
			return false;
		}
	}

	Boolean isValidURL(String url) {
		try {
			new URL(url).toURI();
			return true;
		} catch(Exception e){
			return false;
		}
	}
}