/**
 *
 */
package com.amnex.agristack.controller;

import java.util.Objects;

import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.service.StateLgdMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.entity.StateLgdMaster;
import com.amnex.agristack.utils.CustomMessages;

import reactor.core.publisher.Mono;

/**
 * @author kinnari.soni
 *
 */
@RestController
@RequestMapping("/boundary/state")
public class StateLgdMasterController {

	@Autowired
	private StateLgdMasterService stateLgdMasterService;

	/**
	 * Return state master list
	 * @return The ResponseModel object representing the response.
	 */
	@GetMapping("/getStateLgdMaster")
	public ResponseEntity<Mono<?>> getStateLgdMaster(){
		return stateLgdMasterService.getStateList();
	}

	/**
	 * Fetch state details by state lgd code
	 * @param lgdCode state lgd code
	 * @return The ResponseModel object representing the response.
	 */
	@GetMapping("/getStateByLGDCode/{lgdCode}")
	public ResponseModel getStateByLGDCode(@PathVariable Long lgdCode) {
		try {
			StateLgdMaster stateLgdMaster = stateLgdMasterService.getStateByLGDCode(lgdCode);
			if(Objects.isNull(stateLgdMaster)) {
				return new ResponseModel(null, "State not found for LGD code: " + lgdCode, CustomMessages.GET_DATA_SUCCESS,CustomMessages.SUCCESS,  CustomMessages.METHOD_POST);
			}
			return new ResponseModel(stateLgdMaster, "State " + CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,CustomMessages.SUCCESS,  CustomMessages.METHOD_POST);
		} catch(Exception e) {
			return new ResponseModel(null, e.getMessage(),  CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILURE, CustomMessages.FAILED);
		}
	}
}
