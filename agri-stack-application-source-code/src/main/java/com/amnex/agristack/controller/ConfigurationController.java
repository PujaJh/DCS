package com.amnex.agristack.controller;

import com.amnex.agristack.dao.ConfigurationDAO;
import com.amnex.agristack.dao.CropCategoryInputDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.service.GeneralService;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller class for managing configuration.
 */

@RestController
@RequestMapping("/configuration")
public class ConfigurationController {

	@Autowired
	private GeneralService generalService;

	/**
	 * Retrieves the active configurations for mobile devices.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseModel object containing the active configurations
	 */
	@GetMapping(value = "/mobile/getActiveConfigurations")
	public ResponseModel getActiveConfigurations(HttpServletRequest request) {
		return generalService.getConfigurationForMobile(request);
	}
	
	/**
	 * add configuration 
	 * @param configurationDAO input dao of Configuration
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return  The ResponseModel object representing the response.
	 */
	@PostMapping(value = "/addConfiguration")
	public ResponseModel addConfiguration(@RequestBody ConfigurationDAO configurationDAO, HttpServletRequest request) {
		return generalService.addConfiguration(configurationDAO, request);
	}

	/**
	 * Retrieves the flexible survey reasons for mobile devices.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseModel object containing the flexible survey reasons
	 */
	@GetMapping(value = "/mobile/getFlexibleSurveyReasons")
	public ResponseModel getFlexibleSurveyReasons(HttpServletRequest request) {
		return generalService.getFlexibleSurveyReasons(request);
	}
	
	/**
	 * fetch config by id key
	 * @param key The config id to retrieve the config
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return  The ResponseModel object representing the response.
	 */
	@GetMapping(value = "/getConfigById/{key}")
	public ResponseModel getConfigById(HttpServletRequest request,@PathVariable("key") String key) {
		return generalService.getConfigById(request,key);
	}
}
