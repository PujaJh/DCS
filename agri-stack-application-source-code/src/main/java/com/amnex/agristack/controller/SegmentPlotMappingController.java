package com.amnex.agristack.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.dao.CommonRequestDAO;
import com.amnex.agristack.dao.SegmentPlotMappingDAO;
import com.amnex.agristack.service.SegmentPlotMappingService;

@RestController
@RequestMapping("/plotSegment")
public class SegmentPlotMappingController {

	@Autowired
	SegmentPlotMappingService segmentPlotMappingService;

	/**
	 * Retrieves Plot details by user ID.
	 *
	 * @param request   the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@GetMapping("/getPlotDetailsByUserId")
	public ResponseModel getPlotDetailsByUserId(HttpServletRequest request) {
		return segmentPlotMappingService.getPlotDetailsByUserId(request);
	}
	 
//	@PostMapping("/getSegmentsBySurveyNumber")
//	public ResponseModel getSegmentBySurveyNumber(@RequestBody SegmentPlotMappingDAO requestDAO, HttpServletRequest request) {
//		return segmentPlotMappingService.getSegmentsBySurveyNumber(requestDAO,request);
//	}

	/**
	 * Retrieves nearest segments.
	 *
	 * @param requestDAO the SegmentPlotMappingDto containing the input params
	 * @param request   the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping("/getNearestSegments")
	public ResponseModel getNearestSegments(@RequestBody SegmentPlotMappingDAO requestDAO, HttpServletRequest request) {
		return segmentPlotMappingService.getNearestSegments(requestDAO,request);
	}

	/**
	 * Retrieves segment by current location.
	 *
	 * @param requestDAO the SegmentPlotMappingDto containing the input params
	 * @param request   the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping("/getSegmentBasedOnCurrentLocation")
	public ResponseModel getSegmentBasedOnCurrentLocation(@RequestBody SegmentPlotMappingDAO requestDAO, HttpServletRequest request) {
		return segmentPlotMappingService.getSegmentBasedOnCurrentLocation(requestDAO,request);
	}

	/**
	 * Add segment plot mapping details.
	 *
	 * @param requestDAO the list of SegmentPLotMappingDto containing the input params
	 * @param request   the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping("/addSegmentPlotMappingDetails")
	public ResponseModel addSegmentPlotMappingDetails(@RequestBody List<SegmentPlotMappingDAO> requestDAO, HttpServletRequest request) {
		return segmentPlotMappingService.addSegmentPlotMappingDetails(requestDAO,request);
	}

	/**
	 * Retrieves segment plot mapping report.
	 *
	 * @param requestDAO the CommonRequestDao containing the input params
	 * @param request   the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping("/getSegmentPlotMappingReport")
	public ResponseModel getSegmentPlotMappingReport(@RequestBody CommonRequestDAO requestDAO, HttpServletRequest request) {
		return segmentPlotMappingService.getSegmentPlotMappingReport(requestDAO,request);
	}
}
