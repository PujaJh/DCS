package com.amnex.agristack.controller;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.dao.ReportInputDAO;
import com.amnex.agristack.service.ReportService;

import reactor.core.publisher.Mono;

/**
 * Controller class for handling report-related endpoints.
 */
@RestController
@RequestMapping("/report")
public class ReportController {

	@Autowired
	ReportService reportService;

	/**
	 * Retrieves user logs based on the provided report input.
	 *
	 * @param reportInputDAO the report input data
	 * @param request        the HTTP servlet request
	 * @return the response entity containing the user logs as a Mono
	 */
	@PostMapping(value = "/getUserLogs")
	public ResponseEntity<Mono<?>> getUserLogs(@RequestBody ReportInputDAO reportInputDAO, HttpServletRequest request) {
		return reportService.getUserLogs(reportInputDAO);
	}

	/**
	 * Retrieves the total count of user logs based on the provided report input.
	 *
	 * @param reportInputDAO the report input data
	 * @param request        the HTTP servlet request
	 * @return the response entity containing the total user logs count as a Mono
	 */
	@PostMapping(value = "/getTotalUserLogsCount")
	public ResponseEntity<Mono<?>> getTotalUserLogsCount(@RequestBody ReportInputDAO reportInputDAO,
			HttpServletRequest request) {
		return reportService.getTotalUserLogsCount(reportInputDAO);
	}

	/**
	 * Retrieves the login activity based on the provided report input.
	 *
	 * @param reportInputDAO the report input data
	 * @param request        the HTTP servlet request
	 * @return the response model containing the login activity
	 */
	@PostMapping(value = "/getLoginActivity")
	public ResponseModel getLoginActivity(@RequestBody ReportInputDAO reportInputDAO, HttpServletRequest request) {
		return reportService.getLoginActivity(reportInputDAO, request);
	}
}
