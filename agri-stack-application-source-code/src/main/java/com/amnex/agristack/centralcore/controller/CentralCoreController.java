package com.amnex.agristack.centralcore.controller;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.centralcore.Enum.QueryTypeEnum;
import com.amnex.agristack.centralcore.dao.*;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.centralcore.exception.UnauthorizedException;
import com.amnex.agristack.centralcore.service.CentralCoreService;
import com.amnex.agristack.entity.UserMaster;
import com.amnex.agristack.service.SurveyorService;
import com.amnex.agristack.utils.CustomMessages;

@RestController
@RequestMapping("/centralCore")
public class CentralCoreController {

	@Autowired
	private CentralCoreService centralCoreService;

	/**
	 * Performs user login.
	 *
	 * @param request      The HttpServletRequest object containing the request
	 *                     information.
	 * @param transactionRequestDAO The user input data.
	 * @return The ResponseModel containing the response data.
	 */
	@PostMapping(value = "/seek")
	public Map<String, Object> seek(HttpServletRequest request,
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
				return centralCoreService.seek(request, transactionRequestDAO);
			}
		}catch (UnauthorizedException ue){
			ue.printStackTrace();
			 throw new UnauthorizedException("You are not authorized to access this resource.");
		}catch (Exception e) {
			e.printStackTrace();
			Map<String, Object> responseData = new HashMap<>();
			ErrorDAO error = new ErrorDAO();
			error.setCode("500");
			if (e.getMessage().contains("could not execute statement")){
				error.setMessage("Id must be unique");
			} else {
				error.setMessage(e.getMessage());
			}
			responseData.put("error", error);
			return responseData;
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

	/**
	 * Request to retrieve the response of seek request
	 * @param transactionRequestDAO The TransactionRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/onSeek")
	public Object onSeek(HttpServletRequest request, @RequestBody TransactionRequestDAO transactionRequestDAO) {
		try {
			return centralCoreService.onSeek(request, transactionRequestDAO);
		} catch (Exception e) {
			Map<String, Object> responseData = new HashMap<>();
			ErrorDAO error = new ErrorDAO();
			error.setCode("500");
			error.setCode(e.getMessage());
			responseData.put("error", error);
			return responseData;
		}
	}

	/**
	 * Request to retrieve the response of the seek request
	 * @param seekerDAO The TransactionRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/seekerOnSeek")
	public Object seekerOnSeek(HttpServletRequest request, @RequestBody TransactionResponseDAO seekerDAO) {
		try {
			return centralCoreService.seekerOnSeek(request, seekerDAO);
		} catch (Exception e) {
			Map<String, Object> responseData = new HashMap<>();
			ErrorDAO error = new ErrorDAO();
			error.setCode("500");
			error.setCode(e.getMessage());
			responseData.put("error", error);
			return responseData;
		}
	}

	/**
	 * Retrieves the response by reference id
	 * @param requestDAO The TransactionRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/getResponseByRefId")
	public ResponseModel getResponseByRefId(HttpServletRequest request, @RequestBody TransactionResponseDAO requestDAO) {
		return centralCoreService.getResponseByRefId(request,requestDAO.getReferenceId());
	}

	@Autowired
	SurveyorService surveyorService;

	/**
	 * Send sms for consent request
	 * @param dao The AcmMsgRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/sendSMS")
	public Object sendSMS(HttpServletRequest request, @RequestBody ACMMsgRequestDAO dao) {
		try {
			if (!centralCoreService.validateACMToken(request)) {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
			surveyorService.sendSMSForConsentAccessReuqest(dao);
			return new ResponseEntity<>("SMS Sent Successfully!", HttpStatus.OK);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
		}
	}

	/**
	 * Sync request version seek request to provide the response by request body
	 * @param transactionRequestDAO The TransactionRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/onSync/seek")
	public ResponseModel onSync(HttpServletRequest request,
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
				return new ResponseModel(responseData, "Bad Request", CustomMessages.GET_DATA_ERROR,
						CustomMessages.FAILED, CustomMessages.METHOD_POST);
			}else {
				return centralCoreService.onSync(request, transactionRequestDAO);
			}
		}catch (UnauthorizedException ue){
			ue.printStackTrace();
			throw new UnauthorizedException("You are not authorized to access this resource.");
		}catch (Exception e) {
			e.printStackTrace();
			ErrorDAO error = new ErrorDAO();
			error.setCode("500");
			if (e.getMessage().contains("could not execute statement")){
				error.setMessage("Id must be unique");
			} else {
				error.setMessage(e.getMessage());
			}
			return new ResponseModel(null, error.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED, CustomMessages.METHOD_POST);
		}
	}

	/**
	 * Sync request version seek request to provide the response by request body
	 * @param requestDAO The SyncSeekRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping("/sync/seek")
	public ResponseModel getSyncSeek(HttpServletRequest request,
									 @RequestBody SyncSeekRequestDAO requestDAO){
		try{
			return centralCoreService.getSyncSeek(request, requestDAO);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the list of crop sown data
	 * @param requestDAO The list of CropSownRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/getCropSownData")
	public ResponseModel getCropSownData(HttpServletRequest request,
										 @RequestBody List<CropSownRequestDao> requestDAO) {
		return centralCoreService.getCropSownData(request, requestDAO);
	}

	/**
	 * Retrieves the list of crop details
	 * @param requestDAO The list of CropSownRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/getCropDetails")
	public ResponseModel getCropDetails(HttpServletRequest request,
										 @RequestBody List<CropSownRequestDao> requestDAO) {
		return centralCoreService.getCropDetails(request, requestDAO);
	}

//	@PostMapping(value = "/getOwnerAndCropAndAreaData")
//	public ResponseModel getOwnerAndCropAndAreaData(HttpServletRequest request,
//										 @RequestBody List<CropSownRequestDao> requestDAO) {
//		return centralCoreService.getOwnerAndCropAndAreaData(request, requestDAO);
//	}

	/**
	 * Retrieves the list of Crop sown data for FR
	 * @param requestDAO The list of CropSownRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/getCropSownDataForFr")
	public ResponseModel getCropSownDataForFr(HttpServletRequest request,
													@RequestBody List<CropSownRequestDao> requestDAO) {
		return centralCoreService.getCropSownDataForFr(request, requestDAO);
	}

}
