package com.amnex.agristack.controller;

import com.amnex.agristack.dao.EarlyLateVillageInputDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.service.EarlyLateVillageService;
import com.amnex.agristack.service.SurveyActivityConfigService;
import com.amnex.agristack.utils.CustomMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller class for managing Early Late Village operations.
 * Handles HTTP requests related to Early Late Villages.
 */
@RestController
@RequestMapping("/admin/earlyLateVillage")
public class EarlyLateVillageController {

	@Autowired
	SurveyActivityConfigService surveyActivityService;

	@Autowired
	EarlyLateVillageService earlyLateVillageService;

	/**
	 * Retrieves all Village Mapping.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return the ResponseModel containing the list of Village Mapping
	 */
	@GetMapping(value = "/getAllVillageMapping")
	public ResponseModel getAllVillageMapping(HttpServletRequest request) {
		try {
			return surveyActivityService.getAllActivitiesConfig(request);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * Adds Early Late Villages.
	 *
	 * @param request   the HttpServletRequest object representing the incoming request
	 * @param inputDAO  the EarlyLateVillageInputDAO object containing the Early Late Village details
	 * @return the ResponseModel indicating the result of the Early Late Villages addition
	 */
	@PostMapping(value = "/addEarlyLateVillages")
	public ResponseModel addEarlyLateVillages(HttpServletRequest request, @RequestBody EarlyLateVillageInputDAO inputDAO) {
		return earlyLateVillageService.addEarlyLateVillages(request, inputDAO);
	}

	/**
	 * Checks if marked villages exist.
	 *
	 * @param request   the HttpServletRequest object representing the incoming request
	 * @param inputDAO  the EarlyLateVillageInputDAO object containing the Early Late Village details
	 * @return the ResponseModel indicating the result of the check
	 */
	@PostMapping(value = "/checkMarkVillagesExist")
	public ResponseModel checkMarkVillagesExist(HttpServletRequest request, @RequestBody EarlyLateVillageInputDAO inputDAO) {
		return earlyLateVillageService.checkMarkVillagesExist(request, inputDAO);
	}

	/**
	 * Retrieves the list of early and late marked villages.
	 *
	 * @param request   the HttpServletRequest object representing the incoming request
	 * @param inputDAO  the EarlyLateVillageInputDAO object containing the Early Late Village details
	 * @return the ResponseModel containing the list of early and late marked villages
	 */
	@PostMapping(value = "/getEarlyLateMarkedVillages")
	public ResponseModel getEarlyLateMarkedVillages(HttpServletRequest request, @RequestBody EarlyLateVillageInputDAO inputDAO) {
		return earlyLateVillageService.getEarlyLateMarkedVillages(request, inputDAO);
	}

	/**
	 * Updates the early and late villages.
	 *
	 * @param request   the HttpServletRequest object representing the incoming request
	 * @param inputDAO  the EarlyLateVillageInputDAO object containing the Early Late Village details
	 * @return the ResponseModel indicating the result of the update
	 */
	@PostMapping(value = "/updateEarlyLateVillages")
	public ResponseModel updateEarlyLateVillages(HttpServletRequest request, @RequestBody EarlyLateVillageInputDAO inputDAO) {
		return earlyLateVillageService.updateEarlyLateVillages(request, inputDAO);
	}

	/**
	 * Deletes the early and late villages.
	 *
	 * @param request   the HttpServletRequest object representing the incoming request
	 * @param id        the ID of the Early Late Village to be deleted
	 * @return the ResponseModel indicating the result of the deletion
	 */
	@DeleteMapping(value = "/deleteEarlyLateVillages/{id}")
	public ResponseModel deleteEarlyLateVillages(HttpServletRequest request, @PathVariable("id") Long id) {
		return earlyLateVillageService.deleteEarlyLateVillages(request, id);
	}

	/**
	 * Retrieves the list of marked villages for a taluk.
	 *
	 * @param request   the HttpServletRequest object representing the incoming request
	 * @param inputDAO  the EarlyLateVillageInputDAO object containing the Early Late Village details
	 * @return the ResponseModel containing the list of marked villages for the taluk
	 */
	@PostMapping(value = "/getMarkedVillagesForTaluk")
	public ResponseModel getMarkedVillagesForTaluk(HttpServletRequest request, @RequestBody EarlyLateVillageInputDAO inputDAO) {
		return earlyLateVillageService.getMarkedVillagesForTaluk(request, inputDAO);
	}
}
