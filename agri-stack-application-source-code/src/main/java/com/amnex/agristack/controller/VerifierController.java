package com.amnex.agristack.controller;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.dao.SurveyorInputDAO;
import com.amnex.agristack.dao.UserInputDAO;
import com.amnex.agristack.dao.VerifierInputDAO;
import com.amnex.agristack.service.ExceptionLogService;
import com.amnex.agristack.service.VerifierService;
import com.amnex.agristack.utils.CustomMessages;

/**
 * /** Verifier Controller
 * 
 * @author darshankumar.gajjar
 * @since 2023-02-23
 */

@RestController
@RequestMapping("/admin/verifier")
public class VerifierController {

	@Autowired
	ExceptionLogService exceptionLogService;

	@Autowired
	VerifierService verifierService;

	/**
	 * Adds a verifier.
	 *
	 * @param inputDAO The input data for adding a verifier.
	 * @param request The HttpServletRequest object.
	 * @return The response model containing the result of the operation.
	 */
	@PostMapping(value = "/addVerifier")
	public ResponseModel addVerifier(@RequestBody VerifierInputDAO inputDAO, HttpServletRequest request) {
		try {
			return verifierService.addVerifier(inputDAO, request);
		} catch (Exception e) {

			exceptionLogService.addException(e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName(),
					e, null);

			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_POST);
		}
	}

	/**
	 * Adds a self-registered verifier.
	 *
	 * @param inputDAO The input data for adding a self-registered verifier.
	 * @param request The HttpServletRequest object.
	 * @return The response model containing the result of the operation.
	 */
	@PostMapping(value = "/mobile/addVerifier")
	public ResponseModel addSelfRegisteredVerifier(@RequestBody VerifierInputDAO inputDAO, HttpServletRequest request) {
		try {
			return verifierService.addSelfRegisteredVerifier(inputDAO, request);
		} catch (Exception e) {
			exceptionLogService.addException(e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName(),
					e, null);
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_POST);
		}
	}

	/**
	 * Updates a verifier.
	 *
	 * @param inputDAO The input data for updating a verifier.
	 * @param request The HttpServletRequest object.
	 * @return The response model containing the result of the operation.
	 */
	@PostMapping(value = "/updateVerifier")
	public ResponseModel updateVerifier(@RequestBody VerifierInputDAO inputDAO, HttpServletRequest request) {
		try {
			return verifierService.updateVerifier(inputDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_POST);
		}
	}

	/**
	 * get Verifier list
	 * 
	 * @param request The HttpServletRequest Object
	 * @param requestInput input data for get all verifiers
	 * @return The response model containing the result of the operation.
	 */
	@PostMapping(value = "/getAllVerifier")
	public ResponseModel getAllVerifier(@RequestBody VerifierInputDAO requestInput, HttpServletRequest request) {
		try {
//			return verifierService.getAllVerifier(requestInput, request);
			return verifierService.getAllVerifierV2(requestInput, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the details of a verifier.
	 *
	 * @param userId The ID of the verifier.
	 * @param request The HttpServletRequest object.
	 * @return The response model containing the verifier details.
	 */
	@GetMapping(value = "/getVerifierDetail/{userId}")
	public ResponseModel getVerifierDetail(@PathVariable("userId") Long userId, HttpServletRequest request) {
		try {
			return verifierService.getVerifierDetail(userId, request);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_POST);
		}
	}

	/**
	 * Update Verifier Status
	 *
	 * @param VerifierInputDAO input data for update verifier status
	 * @param request The HttpServletRequest Object
	 * @return The response model containing the result of the operation.
	 */
	@PostMapping(value = "/updateVerifierStatus")
	public ResponseModel updateVerifierStatus(@RequestBody VerifierInputDAO VerifierInputDAO,
			HttpServletRequest request) {
		try {
			return verifierService.updateVerifierStatus(VerifierInputDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Delete Verifier
	 *
	 * @param VerifierId verifier id to delete verifier
	 * @param request The HttpServletRequest Object
	 * @return The response model containing the result of the operation.
	 */
	@DeleteMapping(value = "/deleteVerifier/{VerifierId}")
	public ResponseModel deleteVerifier(@PathVariable("VerifierId") Long VerifierId, HttpServletRequest request) {
		try {
			return verifierService.deleteVerifier(VerifierId, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Mark UnAvailable Verifier
	 *
	 * @param inputDAO input data to mark unavailable verifier
	 * @param request The HttpServletRequest Object
	 * @return The response model containing the result of the operation.
	 */
	@PostMapping(value = "/markUnAvailable")
	public ResponseModel markUnAvailable(@RequestBody VerifierInputDAO inputDAO, HttpServletRequest request) {
		try {
			return verifierService.markUnAvailable(inputDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * getUserCountVerifierInspection
	 * @param request The HttpServletRequest Object
	 * @return The ResponseModel object representing the response
	 */
	@GetMapping(value = "/getUserCountVerifierInspection")
	public ResponseModel getUserCountVerifierInspection(HttpServletRequest request) {
		return verifierService.getUserCountVerifierInspection(request);
	}
	
	/**
     * fetch village list.
     *
     * @param userInputDAO input data for getting village list
     * @param request The HttpServletRequest Object
     * @return  The ResponseModel object representing the response.
     */
	@PostMapping(value = "/getVilagelist")
	public ResponseModel getVilagelist(@RequestBody UserInputDAO userInputDAO, HttpServletRequest request) {
		try {
			return verifierService.getVilagelist(userInputDAO, request);
//			return verifierService.getVilagelistV1(userInputDAO, request);
		} catch (Exception e) {
			exceptionLogService.addException(e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName(),
					e, null);
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_POST);
		}
	}
}
