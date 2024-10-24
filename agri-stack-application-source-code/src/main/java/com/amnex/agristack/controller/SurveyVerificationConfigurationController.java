package com.amnex.agristack.controller;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.service.SurveyVerificationConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.dao.SurveyVerificationConfigurationMasterDAO;
import com.amnex.agristack.utils.CustomMessages;

@RestController
@RequestMapping("/surveyVerificationConfiguration")
public class SurveyVerificationConfigurationController {

	@Autowired
	private SurveyVerificationConfigurationService surveyVerificationConfigurationService;

	/**
	 * Retrieves all survey verification configurations.
	 *
	 * @param request the HTTP servlet request
	 * @return the response model containing the list of survey verification configurations
	 */
	@GetMapping
	public ResponseModel getSurveyVerificationConfiguration(HttpServletRequest request) {
		try {
			return surveyVerificationConfigurationService.getAllSurveyVerificationConfiguration(request);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	/**
	 * Retrieves a survey verification configuration by state LGD code.
	 *
	 * @param stateLgdCode the LGD code of the state
	 * @return the response model containing the survey verification configuration
	 */
	@GetMapping("/{stateLgdCode}")
	public ResponseModel getSurveyVerificationConfiguration(@PathVariable("stateLgdCode") Long stateLgdCode) {
		try {
			return surveyVerificationConfigurationService.getSurveyVerificationConfiguration(stateLgdCode);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	/**
	 * Adds a new survey verification configuration.
	 *
	 * @param surveyVerificationConfigurationMasterDAO the survey verification configuration data
	 * @param request                                 the HTTP servlet request
	 * @return the response model containing the result of the addition
	 */
	@PostMapping
	public ResponseModel addSurveyVerificationConfiguration(
			@RequestBody SurveyVerificationConfigurationMasterDAO surveyVerificationConfigurationMasterDAO, HttpServletRequest request) {
		try {
			return surveyVerificationConfigurationService
					.addSurveyVerificationConfiguration(surveyVerificationConfigurationMasterDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_POST);
		}
	}

	/**
	 * Updates an existing survey verification configuration.
	 *
	 * @param surveyVerificationConfigurationMasterDAO the updated survey verification configuration data
	 * @return the response model containing the result of the update
	 */
	@PostMapping("/update")
	public ResponseModel updateSurveyVerificationConfiguration(
			@RequestBody SurveyVerificationConfigurationMasterDAO surveyVerificationConfigurationMasterDAO) {
		try {
			return surveyVerificationConfigurationService
					.updateSurveyVerificationConfiguration(surveyVerificationConfigurationMasterDAO);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_POST);
		}
	}

	/**
	 * Deletes a survey verification configuration by its ID.
	 *
	 * @param surveyVerificationConfigurationMasterId the ID of the survey verification configuration to delete
	 * @return the response model containing the result of the deletion
	 */
	@GetMapping("/delete/{surveyVerificationConfigurationMasterId}")
	public ResponseModel deleteSurveyVerificationConfiguration(
			@PathVariable("surveyVerificationConfigurationMasterId") Integer surveyVerificationConfigurationMasterId) {
		try {
			return surveyVerificationConfigurationService
					.deleteSurveyVerificationConfiguration(surveyVerificationConfigurationMasterId);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}
}
