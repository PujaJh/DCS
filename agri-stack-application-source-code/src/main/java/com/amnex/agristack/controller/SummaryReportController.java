package com.amnex.agristack.controller;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.CommonRequestDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.dao.ReviewSurveyInputDAO;
import com.amnex.agristack.service.SummaryReportService;
import com.amnex.agristack.utils.CustomMessages;

import java.util.Map;

@RestController
@RequestMapping("/mis/summary")
public class SummaryReportController {
	
	@Autowired
	SummaryReportService summaryReportService;

	// Retrieves crop distribution data by crop
//	@GetMapping("/getHourlyStatusReport")
//	public Object getHourlyStatusReport(HttpServletRequest request) {
//		try {
//			return summaryReportService.getHourlyStatusReport(request);
//		
//		} catch (Exception e) {
//			e.printStackTrace();
//			return new ResponseModel(e.getMessage(), CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
//					CustomMessages.FAILED, CustomMessages.METHOD_POST);
//		}
//	}

	/**
	 * Retrieves the hourly status report.
	 *
	 * @param request       The HttpServletRequest object.
	 * @return              The ResponseModel object representing the response.
	 */
	@GetMapping(value = "/getHourlyStatusReport")
	public Object getHourlyStatusReport(HttpServletRequest request) {
		try {
//			@RequestBody ReviewSurveyInputDAO inputDAO, 
			return summaryReportService.getHourlyStatusReport(request);
		
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(e.getMessage(), CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED, CustomMessages.METHOD_POST);
		}
	}



}
