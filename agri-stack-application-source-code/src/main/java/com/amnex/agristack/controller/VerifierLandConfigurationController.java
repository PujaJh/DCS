/**
 * 
 */
package com.amnex.agristack.controller;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.service.VerifierLandConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.dao.PaginationDao;
import com.amnex.agristack.utils.CustomMessages;

/**
 * @author majid.belim
 * Controller class for managing verifier land configuration details.
 */
@RestController
@RequestMapping("/verifierLandConfiguration")
public class VerifierLandConfigurationController {

	@Autowired
	private VerifierLandConfigurationService verifierLandConfigurationService;

	/**
	 * Retrieves the verifier configuration calendar based on the specified pagination details.
	 *
	 * @param paginationDao The pagination details.
	 * @param request The HttpServletRequest object.
	 * @return The response model containing the verifier configuration calendar.
	 */
	@PostMapping("/getVerifierConfigCalender")
	public ResponseModel getVerifierConfigCalender(@RequestBody PaginationDao paginationDao,
												   HttpServletRequest request) {
		try {
			return verifierLandConfigurationService.getVerifierConfigCalender(paginationDao, request);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_POST);
		}
	}
	
	/**
	 * Retrieves the verifier configuration calendar by config id.
	 *
	 * @param paginationDao The pagination details.
	 * @param request The HttpServletRequest object.
	 * @return The response model object representing the response.
	 */
	@PostMapping("/getVerifierConfigCalenderByConfigId")
	public ResponseModel getVerifierConfigCalenderByConfigId(@RequestBody PaginationDao paginationDao,
			HttpServletRequest request) {
		try {
			return verifierLandConfigurationService.getVerifierConfigCalenderByConfigId(paginationDao, request);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_POST);
		}
	}

}
