package com.amnex.agristack.controller;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.dao.UserDeviceMappingDAO;
import com.amnex.agristack.service.UserDeviceMappingService;

/**
 * Controller class for managing user device mapping details.
 */
@RestController
@RequestMapping("/userDeviceMapping")
public class UserDeviceMappingController {

	@Autowired
	private UserDeviceMappingService userDeviceMappingService;

	/**
	 * Retrieves the user device details provided input
	 * parameters.
	 *
	 * @param req The UserDeviceMappingDAO object containing the input
	 *                 parameters.
	 * @param request  The HttpServletRequest object containing the request
	 *                 information.
	 * @return The response model containing the user device details.
	 */
	@PostMapping("/getUserDeviceDetails")
	ResponseModel getUserDeviceDetails(@RequestBody UserDeviceMappingDAO req, HttpServletRequest request) {
		return userDeviceMappingService.getUserDeviceDetails(req, request);
	}

	/**
	 * update the user device mapping status provided input
	 * parameters.
	 *
	 * @param req The UserDeviceMappingDAO object containing the input
	 *                 parameters.
	 * @param request  The HttpServletRequest object containing the request
	 *                 information.
	 * @return The response model containing the update the user device mapping status.
	 */
	@PostMapping("/updateUserDeviceMappingStatus")
	ResponseModel updateUserDeviceMappingStatus(@RequestBody UserDeviceMappingDAO req, HttpServletRequest request) {
		return userDeviceMappingService.updateUserDeviceMappingStatus(req, request);
	}
}
