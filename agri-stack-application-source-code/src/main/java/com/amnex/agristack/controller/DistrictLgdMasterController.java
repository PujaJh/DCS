/**
 *
 */
package com.amnex.agristack.controller;

import java.util.List;
import java.util.Objects;

import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.service.DistrictLgdMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.entity.DistrictLgdMaster;
import com.amnex.agristack.utils.CustomMessages;

import reactor.core.publisher.Mono;

/**
 * @author kinnari.soni
 *
 */
@RestController
@RequestMapping("/boundary/district")
public class DistrictLgdMasterController {

	@Autowired
	private DistrictLgdMasterService districtLgdMasterService;

	/**
	 * Return district master list
	 * @param stateLgdCode
	 * @return
	 */
	@GetMapping("/getDistrictLgdMaster")
	public ResponseEntity<Mono<?>> getDistrictLgdMaster(
			@RequestParam(name="stateLgdCode",required = false) Long stateLgdCode){
		return districtLgdMasterService.getDistrictList(stateLgdCode);
	}

	/**
	 * Fetch District details by district lgd code
	 * @param lgdCode
	 * @return
	 */
	@GetMapping("/getDistrictByLGDCode/{lgdCode}")
	public ResponseModel getDistrictByLGDCode(@PathVariable Long lgdCode) {
		//		return districtLgdMasterService.getDistrictByLGDCode(lgdCode);
		try {
			DistrictLgdMaster districtLgdMaster = districtLgdMasterService.getDistrictByLGDCode(lgdCode);
			if(Objects.isNull(districtLgdMaster)) {
				return new ResponseModel(null, "District not found for LGD code: " + lgdCode, CustomMessages.GET_DATA_SUCCESS,CustomMessages.SUCCESS,  CustomMessages.METHOD_POST);
			}
			return new ResponseModel(districtLgdMaster, "District " + CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,CustomMessages.SUCCESS,  CustomMessages.METHOD_POST);
		} catch(Exception e) {
			return new ResponseModel(null, e.getMessage(),  CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILURE, CustomMessages.FAILED);
		}
	}

	/**
	 * Fetch list of district details based on given districts
	 * @param districtLgdCodes
	 * @return
	 */
	@GetMapping("/getDistrictLgdMasterByLgdCodes")
	public ResponseModel getDistrictLgdMasterByLgdCodes(
			@RequestParam(name="districtLgdCodes",required = false) List<Long> districtLgdCodes){
		//return districtLgdMasterService.getDistrictByLgdCodes(districtLgdCodes);
		return new ResponseModel( districtLgdMasterService.getDistrictByLgdCodes(districtLgdCodes), "District " + CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,CustomMessages.SUCCESS,  CustomMessages.METHOD_GET);

	}

}
