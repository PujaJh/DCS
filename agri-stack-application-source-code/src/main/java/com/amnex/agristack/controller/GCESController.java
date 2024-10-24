package com.amnex.agristack.controller;

import com.amnex.agristack.dao.CommonRequestDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.service.GCESApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/***
 * @author seet.mansura
 **/
@RestController
@RequestMapping("/gces")
public class GCESController {

	@Autowired
	GCESApiService gcesApiService;

	/**
	 * Retrieves the list of survey number by village lgd codes
	 *
	 * @param requestDAO input dao of CommonRequestDao
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return ResponseModel according to the request
	 */
	@PostMapping(value = "/getSurveyNumberByVillage")
	public ResponseModel getSurveyNumberByVillage(@RequestBody CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {
			return gcesApiService.getSurveyNumberByVillage(requestDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Returns the boolean for whether the survey has done or not in provided village
	 *
	 * @param requestDAO input dao of CommonRequestDao
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return ResponseModel according to the request
	 */
	@PostMapping(value = "/checkSurveyDoneByVillage")
	public ResponseModel checkSurveyDoneByVillage(@RequestBody CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {
			return gcesApiService.checkSurveyDoneByVillage(requestDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the list of approved survey details by village lgd code
	 *
	 * @param requestDAO input dao of CommonRequestDao
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return ResponseModel according to the request
	 */
	@PostMapping(value = "/getApprovedSurveyDetailsByVillage")
	public ResponseModel getApprovedSurveyDetailsByVillage(@RequestBody CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {
			return gcesApiService.getApprovedSurveyDetailsByVillage(requestDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
