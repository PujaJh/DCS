package com.amnex.agristack.controller;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.dao.NAPlotInputDAO;
import com.amnex.agristack.service.ExceptionLogService;
import com.amnex.agristack.service.MarkNAPlotService;
import com.amnex.agristack.utils.CustomMessages;


/**
 * @author majid.belim
 *
 */
@RestController
@RequestMapping("/admin/markNAPlot")
public class MarkNAPlotController {

	@Autowired
	ExceptionLogService exceptionLogService;

	@Autowired
	MarkNAPlotService markNAPlotService;

	/**
	 * Adds NA plot details.
	 *
	 * @param inputDAO NA plot input data
	 * @param request  HTTP request
	 * @return ResponseModel containing the result of the operation
	 */
	@PostMapping(value = "/addNAPlotDetails")
	public ResponseModel addNAPlotDetails(@RequestBody NAPlotInputDAO inputDAO, HttpServletRequest request) {
		try {
			return markNAPlotService.addNAPlotDetails(inputDAO, request);
		} catch (Exception e) {
			// Log exception
			exceptionLogService.addException(e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName(),
					e, null);

			e.printStackTrace();
			// Return error response
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_POST);
		}
	}

	/**
	 * Gets NA plot details.
	 *
	 * @param inputDAO NA plot input data
	 * @param request  HTTP request
	 * @return ResponseModel containing the result of the operation
	 */
	@PostMapping(value = "/getNAPlotDetails")
	public ResponseModel getNAPlotDetails(@RequestBody NAPlotInputDAO inputDAO, HttpServletRequest request) {
		try {
			return markNAPlotService.getNAPlotDetails(inputDAO, request);
		} catch (Exception e) {
			// Log exception
			exceptionLogService.addException(e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName(),
					e, null);

			e.printStackTrace();
			// Return error response
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_POST);
		}
	}

	/**
	 * Gets farmland plots.
	 *
	 * @param inputDAO NA plot input data
	 * @param request  HTTP request
	 * @return ResponseModel containing the result of the operation
	 */
	@PostMapping(value = "/getFarmlandPlots")
	public ResponseModel getFarmlandPlots(@RequestBody NAPlotInputDAO inputDAO, HttpServletRequest request) {
		try {
			return markNAPlotService.getFarmlandPlots(inputDAO, request);
		} catch (Exception e) {
			// Log exception
			exceptionLogService.addException(e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName(),
					e, null);

			e.printStackTrace();
			// Return error response
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_POST);
		}
	}
	
	/**
	 * Delete NA Plot Details.
	 *
	 * @param inputDao NA plot input data
	 * @param request  HTTP request
	 * @return ResponseModel containing the result of the operation
	 */
	@PostMapping(value = "/deleteNAPlotDetails")
	public ResponseModel deleteFarmNAPlotDetails(@RequestBody NAPlotInputDAO inputDao, HttpServletRequest request) {
		try {
 			return markNAPlotService.deleteNAPlotDetails(inputDao, request);
		} catch (Exception e) {
			// Log exception
			exceptionLogService.addException( e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName(),
					e, null);
			
			e.printStackTrace();
			//Return error response
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_POST);
		}
	}

}
