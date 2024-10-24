package com.amnex.agristack.controller;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.RoleInputDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.dao.CommonRequestDAO;
import com.amnex.agristack.service.MISCenterService;

@RestController
@RequestMapping("/misCentral")
public class MISCenterController {
	
	@Autowired
	private MISCenterService misCenterService;

	/**
	 * Retrieves the list of the cultivated summary details
	 * @param requestDAO The CommonRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/getCultivatedSummaryDetails")
	public ResponseModel getCultivatedSummaryDetails(@RequestBody CommonRequestDAO requestDAO,
													 HttpServletRequest request) {
		try {
			return misCenterService.getCultivatedSummaryDetails(requestDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the list of aggregated details
	 * @param requestDAO The CommonRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/getAggregatedDetails")
	public ResponseModel getAggregatedDetails(@RequestBody CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {
			return misCenterService.getAggregatedDetails(requestDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the list of crop summary details
	 * @param requestDAO The CommonRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/getCropSummaryDetails")
	public ResponseModel getCropSummaryDetails(@RequestBody CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {
			return misCenterService.getCropSummaryDetails(requestDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the list of surveyor department details
	 * @param requestDAO The CommonRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/getSurveyorsDepartmentDetails")
	public ResponseModel getSurveyorsDepartmentDetails(@RequestBody CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {
			return misCenterService.getSurveyorsDepartmentDetails(requestDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the list of irrigation details
	 * @param requestDAO The CommonRequestDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/getIrrigationSourceDetails")
	public ResponseModel getIrrigationSourceDetails(@RequestBody CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {
			return misCenterService.getIrrigationSourceDetails(requestDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the list of village wise plot count
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@GetMapping(value = "/getVillageWisePlotCount")
	public ResponseModel getVillageWisePlotCount( HttpServletRequest request) {
		try {
			return misCenterService.getVillageWisePlotCount( request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
