package com.amnex.agristack.controller;


import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.dao.APISpecificationsRequestDao;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.service.APISpecificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/apiSpecifications")
public class APISpecificationsController {

	@Autowired
	private APISpecificationsService apiSpecificationsService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	/**
	 * Retrieves the geo referenced maps data.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @param requestDAO input dao of APISpecificationRequestDao
	 * @return a ResponseModel object containing the geo referenced maps data
	 */
	@PostMapping(value = "/getGeoReferencedMaps")
	public ResponseModel getGeoReferencedMaps(HttpServletRequest request,
											  @RequestBody APISpecificationsRequestDao requestDAO) {
		return apiSpecificationsService.getGeoReferencedMaps(request,requestDAO);
	}

	/**
	 * Retrieves the crop survey data.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @param requestDAO input dao of APISpecificationRequestDao
	 * @return a ResponseModel object containing the crop survey data
	 */
	@PostMapping(value = "/getCropSurveyData")
	public ResponseModel getCropSurveyData(HttpServletRequest request,
										   @RequestBody APISpecificationsRequestDao requestDAO) {
		return apiSpecificationsService.getCropSurveyData(request,requestDAO);
	}

	

}
