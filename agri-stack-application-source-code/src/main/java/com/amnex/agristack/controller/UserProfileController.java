package com.amnex.agristack.controller;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.dao.SurveyorInputDAO;
import com.amnex.agristack.service.UserService;
import com.amnex.agristack.utils.CustomMessages;

@RestController
@RequestMapping("/userProfile")
public class UserProfileController {
	 
	@Autowired
	private UserService userService;

	/**
	 * Get user information
	 * @param request The HttpServletRequest Object
	 * @return The response model containing the result of the operation.
	 */
	@GetMapping(value = "/getUserInformation")
	public ResponseModel getUserInformation(HttpServletRequest request) {

		return userService.getUserInformation(request);
	}

	/**
	 * Update user bank details
	 * @param inputDAO input data to update user bank details
	 * @param request The HttpServletRequest Object
	 * @return The response model containing the result of the operation.
	 */
	@PostMapping(value = "/updateUserBankDetails")
	public ResponseModel updateUserBankDetails(@RequestBody SurveyorInputDAO inputDAO, HttpServletRequest request) {
		try {
			return userService.updateUserBankDetails(inputDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_POST);
		}
	}
	

}
