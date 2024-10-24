/**
 * 
 */
package com.amnex.agristack.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.gateway.DAO.ResponseModel;

/**
 * @author majid.belim
 *
 */
@RestController
public class CircuitBreakerController {

	@GetMapping("/defaultUrl")
	ResponseModel error() {
		return makeResponseModel(null, "Something went wrong. Please try again later.", 503, "success");
//		return makeResponseModel(null, "Service is under maintenance.", 503, "success");
	}
	
	@PostMapping("/defaultUrl")
	ResponseModel errorPost() {
		return makeResponseModel(null, "Something went wrong. Please try again later.", 503, "success");
//		return makeResponseModel(null, "Service is under maintenance.", 503, "success");
	}

	public ResponseModel makeResponseModel(Object data, String message, int code, String status) {
		ResponseModel responseModel = new ResponseModel();
		responseModel.setData(data);
		responseModel.setMessage(message);
		responseModel.setCode(code);
		responseModel.setStatus(status);
		return responseModel;
	}

}
