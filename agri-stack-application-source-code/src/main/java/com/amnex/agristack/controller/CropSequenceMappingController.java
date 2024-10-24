/**
 * 
 */
package com.amnex.agristack.controller;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.dao.CropSequenceMappingDAO;
import com.amnex.agristack.dao.UserInputDAO;
import com.amnex.agristack.entity.CropSequenceMapping;
import com.amnex.agristack.service.CropSequenceMappingService;

import io.swagger.annotations.ApiOperation;

/**
 * @author majid.belim
 *	Controller class for managing crop sequence mapping details
 */
@RestController
@RequestMapping("/cropSequenceMapping")
public class CropSequenceMappingController {
	
	
	@Autowired
	private CropSequenceMappingService cropSequenceMappingService;
	
	/**
	 * add or update crop Sequence Mapping
	 * parameters.
	 *
	 * @param cropSequenceMappingDAO The CropSequenceMappingDAO object containing the input
	 *                 parameters.
	 * @param request  The HttpServletRequest object containing the request
	 *                 information.
	 * @return The ResponseModel object representing the response.
	 */
	
	@ApiOperation(value = "Add crop Sequence Mapping")
	@PostMapping(value = "/addOrUpdate")
	public ResponseModel add(HttpServletRequest request, @RequestBody CropSequenceMappingDAO cropSequenceMappingDAO) {

		return cropSequenceMappingService.add(request, cropSequenceMappingDAO);

	}
	
	/**
	 * fetch crop detail list by user
	 * parameters.
	 *
	 * @param cropSequenceMappingDAO The CropSequenceMappingDAO object containing the input
	 *                 parameters.
	 * @param request  The HttpServletRequest object containing the request
	 *                 information.
	 * @return The ResponseModel object representing the response.
	 */
	@PostMapping(value = "/getCropDetailListByUser")
	public ResponseModel getCropDetailListByUser(HttpServletRequest request, @RequestBody CropSequenceMappingDAO cropSequenceMappingDAO) {
			return cropSequenceMappingService.getCropDetailListByUser(request, cropSequenceMappingDAO);
	}
	
	/**
	 * fetch configure crop detail list by boundary
	 * parameters.
	 *
	 * @param cropSequenceMappingDAO The CropSequenceMappingDAO object containing the input
	 *                 parameters.
	 * @param request  The HttpServletRequest object containing the request
	 *                 information.
	 * @return The ResponseModel object representing the response.
	 */
	@PostMapping(value = "/getConfigureCropDetailListByBoundary")
	public ResponseModel getConfigureCropDetailListByBoundary(HttpServletRequest request, @RequestBody CropSequenceMappingDAO cropSequenceMappingDAO) {
			return cropSequenceMappingService.getConfigureCropDetailListByBoundary(request, cropSequenceMappingDAO);
	}

}
