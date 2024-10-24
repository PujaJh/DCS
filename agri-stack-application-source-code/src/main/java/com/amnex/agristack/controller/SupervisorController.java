package com.amnex.agristack.controller;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.dao.SurveyorInputDAO;
import com.amnex.agristack.service.SupervisorService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

/**
 * Supervisor Controller
 * @author kinnari.soni
 *
 * 25 Feb 2023 9:17:12 am
 */

@RestController
@RequestMapping("/admin/supervisor")
public class SupervisorController {

	@Autowired
	SupervisorService supervisorService;

	/**
	 * Retrieves a list of supervisors.
	 * 
	 * @param requestInput  The SurveyorInputDAO object containing the request parameters.
	 * @param request       The HttpServletRequest object.
	 * @return              The ResponseModel object representing the response.
	 */
	@PostMapping(value = "/getAllSurpervisor")
	public ResponseModel getAllSupervisor(@RequestBody SurveyorInputDAO requestInput, HttpServletRequest request) {
		try {
			return supervisorService.getAllSupervisor(requestInput,request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
