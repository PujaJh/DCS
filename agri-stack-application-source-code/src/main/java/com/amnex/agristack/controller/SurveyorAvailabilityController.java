package com.amnex.agristack.controller;


import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.SurveyTaskAllocationFilterDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.service.SurveyorAvailabilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/surveyorAvailability")
public class SurveyorAvailabilityController {

	@Autowired
	private SurveyorAvailabilityService surveyorAvailabilityService;

	/**
	 * Retrieves all active surveyors.
	 *
	 * @param request The HttpServletRequest object.
	 * @return The ResponseModel object containing the active surveyors.
	 */
	@GetMapping("/getAllActiveSurveyor")
	public ResponseModel getAllActiveSurveyor(HttpServletRequest request) {
		return surveyorAvailabilityService.getAllActiveSurveyor(request);
	}
	

	/**
	 * Retrieves all active surveyors based on the provided filter.
	 *
	 * @param request The HttpServletRequest object.
	 * @param surveyTaskAllocationFilterDAO The SurveyTaskAllocationFilterDAO object containing the filter parameters.
	 * @return The ResponseModel object containing the active surveyors that match the filter.
	 */
	@PostMapping("/getAllActiveSurveyorByFilter")
	public ResponseModel getAllActiveSurveyorByFilter(HttpServletRequest request,@RequestBody SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		return surveyorAvailabilityService.getAllActiveSurveyorByFilter(request,surveyTaskAllocationFilterDAO);
	}
	
	
	/**
	 * Retrieves surveyors based on the provided taluka in the filter.
	 *
	 * @param request The HttpServletRequest object.
	 * @param surveyTaskAllocationFilterDAO The SurveyTaskAllocationFilterDAO object containing the filter parameters.
	 * @return The ResponseModel object containing the surveyors that belong to the specified taluka.
	 */
	@PostMapping("/getSurveyorByTaluka")
	public ResponseModel getSurveyorByTaluka(HttpServletRequest request, @RequestBody SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		return surveyorAvailabilityService.getSurveyorByTaluka(request,surveyTaskAllocationFilterDAO);
	}

	
	/**
	 * Retrieves PR surveyors based on the provided taluka in the filter.
	 *
	 * @param request The HttpServletRequest object.
	 * @param surveyTaskAllocationFilterDAO The SurveyTaskAllocationFilterDAO object containing the filter parameters.
	 * @return The ResponseModel object containing the surveyors that belong to the specified taluka.
	 */
	@PostMapping("/getPRSurveyorByTaluka")
	public ResponseModel getPRSurveyorByTaluka(HttpServletRequest request,@RequestBody SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		return surveyorAvailabilityService.getPRSurveyorByTalukaNew(request,surveyTaskAllocationFilterDAO);
	}



}
