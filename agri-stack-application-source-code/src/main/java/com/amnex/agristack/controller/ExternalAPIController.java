package com.amnex.agristack.controller;

import com.amnex.agristack.dao.CommonRequestDAO;
import com.amnex.agristack.dao.LandDetailDAO;
import com.amnex.agristack.dao.PaginationDao;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.service.ExternalAPIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/***
 * @author darshankumar.gajjar
 **/
@RestController
@RequestMapping("/external-api")
public class ExternalAPIController {

	@Autowired
	ExternalAPIService externalApiService;

	/**
	 * Retrieves a list of village wise Crop and plot detail.
	 *
	 * @param inputDAO  input dao of paginationDao
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return the ResponseEntity containing the list of village wise Crop and plot details
	 */
	@PostMapping("/getVillageWisePlotAndCropDetail")
	public ResponseModel getVillageWisePlotAndCropDetail(@RequestBody PaginationDao inputDAO, HttpServletRequest request) {
		return externalApiService.getVillageWisePlotAndCropDetail(inputDAO,request);
	}
	
	/**
	 * Retrieves a list of village wise Crop and plot detail.
	 *
	 * @param inputDAO  input dao of paginationDao
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return the ResponseEntity containing the list of village wise Crop and plot details
	 */
	@PostMapping("/getVillageWisePlotAndCropDetailByDate")
	public ResponseModel getVillageWisePlotAndCropDetailByDate(@RequestBody PaginationDao inputDAO, HttpServletRequest request) {
		return externalApiService.getVillageWisePlotAndCropDetailByDate(inputDAO,request);
	}

	/**
	 * Retrieves a list of sub survey number.
	 *
	 * @param detailDAO  input dao of landDetailDao
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return the ResponseEntity containing the list of sub survey number
	 */
	@PostMapping("/getSubSurveyNumber")
	public ResponseModel getSubSurveyNumber(@RequestBody LandDetailDAO detailDAO, HttpServletRequest request) {
		return externalApiService.getSubSurveyNumber(detailDAO, request);
	}

	/**
	 * Retrieves a list of landOwnerShip details.
	 *
	 * @param detailDAO  input dao of landDetailDao
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return the ResponseEntity containing the list of landOwnerShip details
	 */
	@PostMapping("landOwnerShipDetail")
	public ResponseModel landOwnerShipDetail(@RequestBody LandDetailDAO detailDAO, HttpServletRequest request) {
		return externalApiService.landOwnerShipDetail(detailDAO,request);
	}


	/**
	 * Retrieves a list of village wise farmland plot details.
	 *
	 * @param inputDAO  input dao of paginationDao
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return the ResponseEntity containing the list of village wise farmland plot details
	 */
	@PostMapping("/getVillageWiseFarmlandPlotDetail")
	public ResponseModel getVillageWiseFarmlandPlotDetail(@RequestBody PaginationDao inputDAO, HttpServletRequest request) {
		return externalApiService.getVillageWiseFarmlandPlotDetail(inputDAO,request);
	}

	/**
	 * Retrieves a list of village wise crop survey details for gces.
	 *
	 * @param inputDAO  input dao of paginationDao
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return the ResponseEntity containing the list of village wise crop survey details for gces
	 */
	@PostMapping("/getVillageLevelSurveyDetailForGCES")
	public ResponseModel getVillageLevelSurveyDetailForGCES(@RequestBody PaginationDao inputDAO, HttpServletRequest request) {
		return externalApiService.getVillageLevelSurveyDetailForGCES(inputDAO,request);
	}

	/**
	 * Retrieves a list of village wise crop and media details.
	 *
	 * @param requestDAO  input dao of CommonRequestDao
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return the ResponseEntity containing the list of village wise crop and media details
	 */
	@PostMapping(value = "/getVillageWiseCropAndMediaDetails")
	public ResponseModel getVillageWiseCropAndMediaDetails(@RequestBody CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {
			return externalApiService.getVillageWiseCropAndMediaDetails(requestDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves a list of village wise crop identification details.
	 *
	 * @param requestDAO  input dao of CommonRequestDao
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return the ResponseEntity containing the list of village wise crop identification details
	 */
	@PostMapping(value = "/getVillageCropIdentificationDetails")
	public ResponseModel getVillageCropIdentificationDetails(@RequestBody CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {
			return externalApiService.getVillageCropIdentificationDetails(requestDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
