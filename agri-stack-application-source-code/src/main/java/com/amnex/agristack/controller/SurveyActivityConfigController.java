package com.amnex.agristack.controller;

import com.amnex.agristack.dao.SurveyActivityInputDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.service.SurveyActivityConfigService;
import com.amnex.agristack.utils.CustomMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller class for managing survey activity configurations.
 */
@RestController
@RequestMapping("/surveyActivityConfig")
public class SurveyActivityConfigController {

	@Autowired
	SurveyActivityConfigService surveyActivityService;

	/**
	 * Retrieves all survey activity configurations.
	 *
	 * @param request the HTTP servlet request
	 * @return the response model containing the list of activity configurations
	 */
	@GetMapping(value = "/getAllActivityConfig")
	public ResponseModel getAllActivityConfig(HttpServletRequest request) {
		try {
			return surveyActivityService.getAllActivitiesConfig(request);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * Adds a new survey activity configuration.
	 *
	 * @param request            the HTTP servlet request
	 * @param surveyActivityInput the survey activity input data
	 * @return the response model containing the result of the addition
	 */
	@PostMapping(value = "/addSurveyActivityConfig")
	public ResponseModel addSurveyActivityConfig(HttpServletRequest request, @RequestBody SurveyActivityInputDAO surveyActivityInput) {
			return surveyActivityService.addSurveyActivityConfig(request, surveyActivityInput);
	}

	/**
	 * Updates an existing survey activity configuration.
	 *
	 * @param request            the HTTP servlet request
	 * @param surveyActivityInput the updated survey activity input data
	 * @return the response model containing the result of the update
	 */
	@PostMapping(value = "/updateSurveyActivityConfig")
	public ResponseModel updateSurveyActivityConfig(HttpServletRequest request, @RequestBody SurveyActivityInputDAO surveyActivityInput) {
		return surveyActivityService.updateSurveyActivityConfig(request, surveyActivityInput);
	}

	/**
	 * Checks if a configuration duration already exists.
	 *
	 * @param request            the HTTP servlet request
	 * @param surveyActivityInput the survey activity input data containing the duration to check
	 * @return the response model containing the result of the duration existence check
	 */
	@PostMapping(value = "/checkConfigurationDurationExist")
	public ResponseModel checkConfigurationDurationExist(HttpServletRequest request, @RequestBody SurveyActivityInputDAO surveyActivityInput) {
		return surveyActivityService.checkConfigurationDurationExist(request, surveyActivityInput);
	}

	/**
	 * Updates the status of a survey configuration.
	 *
	 * @param request            the HTTP servlet request
	 * @param surveyActivityInput the survey activity input data containing the updated status
	 * @return the response model containing the result of the status update
	 */
	@PostMapping(value = "/updateSurveyConfigurationStatus")
	public ResponseModel updateSurveyConfigurationStatus(HttpServletRequest request, @RequestBody SurveyActivityInputDAO surveyActivityInput) {
		return surveyActivityService.updateSurveyConfigurationStatus(request, surveyActivityInput);
	}

	/**
	 * Deletes a configuration duration by its ID.
	 *
	 * @param request the HTTP servlet request
	 * @param id      the ID of the configuration duration to delete
	 * @return the response model containing the result of the deletion
	 */
	@DeleteMapping(value = "/deleteConfigurationDuration/{id}")
	public ResponseModel deleteConfigurationDuration(HttpServletRequest request, @PathVariable("id") Long id) {
		return surveyActivityService.deleteConfigurationDuration(request, id);
	}

	/**
	 * Retrieves a list of survey activity configurations.
	 *
	 * @param request            the HTTP servlet request
	 * @param surveyActivityInput the survey activity input data containing optional filters
	 * @return the response model containing the list of survey activity configurations
	 */
	@PostMapping(value = "/getSurveyActivityConfigList")
	public ResponseModel getSurveyActivityConfigList(HttpServletRequest request, @RequestBody SurveyActivityInputDAO surveyActivityInput) {
		return surveyActivityService.getSurveyActivityConfigList(request, surveyActivityInput);
	}

	/**
	 * Retrieves a survey activity configuration for mobile devices.
	 *
	 * @param request            the HTTP servlet request
	 * @param surveyActivityInput the survey activity input data containing optional filters
	 * @return the response model containing the survey activity configuration for mobile devices
	 */
    @PostMapping(value = "/mobile/getSurveyActivityConfig")
    public ResponseModel getMobileSurveyActivityConfig(HttpServletRequest request, @RequestBody SurveyActivityInputDAO surveyActivityInput) {
        return surveyActivityService.getSurveyActivityConfig(request, surveyActivityInput);
    }

	/**
	 * Retrieves a survey activity configuration.
	 *
	 * @param request            the HTTP servlet request
	 * @param surveyActivityInput the survey activity input data containing optional filters
	 * @return the response model containing the survey activity configuration
	 */
    @PostMapping(value = "/getSurveyActivityConfig")
    public ResponseModel getSurveyActivityConfig(HttpServletRequest request, @RequestBody SurveyActivityInputDAO surveyActivityInput) {
        return surveyActivityService.getSurveyActivityConfig(request, surveyActivityInput);
    }

	/**
	 * Retrieves a survey activity configuration by its ID.
	 *
	 * @param request the HTTP servlet request
	 * @param id      the ID of the survey activity configuration to retrieve
	 * @return the response model containing the survey activity configuration
	 */
	@GetMapping(value = "/getSurveyActivityConfig/{id}")
	public ResponseModel getSurveyActivityConfig(HttpServletRequest request, @PathVariable("id") Long id) {
		return surveyActivityService.getSurveyActivityConfig(request, id);
	}

	/**
	 * Retrieves all active survey activities.
	 *
	 * @param request the HTTP servlet request
	 * @return the response model containing the list of active survey activities
	 */
	@GetMapping(value = "/getAllActiveSurveyActivities")
	public ResponseModel getAllActiveSurveyActivities(HttpServletRequest request) {
		return surveyActivityService.getAllActiveSurveyActivities(request);
	}

}
