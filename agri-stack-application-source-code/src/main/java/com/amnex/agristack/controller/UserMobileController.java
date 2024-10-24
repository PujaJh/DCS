package com.amnex.agristack.controller;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.dao.SurveyorInputDAO;
import com.amnex.agristack.dao.UserInputDAO;
import com.amnex.agristack.service.UserAuthService;
import com.amnex.agristack.service.UserService;
import com.amnex.agristack.utils.JsonDateDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@RestController
@RequestMapping("/mobile/user")
public class UserMobileController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserAuthService userAuthService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	
	/**
	 * Updates the user profile.
	 *
	 * @param request The HttpServletRequest object.
	 * @param jsonData The JSON data containing the user profile details.
	 * @param files The image file for the user profile (optional).
	 * @return The ResponseModel object indicating the success of updating the user profile.
	 */
	@PostMapping(value = "/updateUserProfile")
	public ResponseModel updateUserProfile(HttpServletRequest request, @RequestPart("data") String jsonData,
										   @RequestPart(name = "image", required = false) MultipartFile files) {

		Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDateDeserializer()).create();
		JSONObject obj = new JSONObject(jsonData);
		SurveyorInputDAO inputDAO = gson.fromJson(obj.toString(), SurveyorInputDAO.class);
		return userService.updateUserProfileMob(request,inputDAO, files);
	}
	
	/**
	 * Changes the user's password.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing the user details.
	 * @return The ResponseModel object indicating the success of changing the password.
	 */
	@PutMapping(value = "/changePassword")
	public ResponseModel changePassword(HttpServletRequest request, @RequestBody UserInputDAO userInputDAO) {

		return userService.changePassword(request,userInputDAO);

	}
	
}
