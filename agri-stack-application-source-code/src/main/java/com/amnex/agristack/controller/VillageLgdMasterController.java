/**
 *
 */
package com.amnex.agristack.controller;

import java.util.List;
import java.util.Objects;

import com.amnex.agristack.dao.CommonDto;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.service.VillageLgdMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.entity.VillageLgdMaster;
import com.amnex.agristack.utils.CustomMessages;

import reactor.core.publisher.Mono;

/**
 * @author kinnari.soni
 *
 */
@RestController
@RequestMapping("/boundary/village")
public class VillageLgdMasterController {

	@Autowired
	private VillageLgdMasterService villageLgdMasterService;

	/**
	 * Return village lgd master list
	 * @param stateLgdCode
	 * @param districtLgdCode
	 * @param subDistrictLgdCode
	 * @return
	 */
	@GetMapping("/getVillageLgdMaster")
	public ResponseEntity<Mono<?>> getVillageLgdMaster(
			@RequestParam(name="stateLgdCode",required = false) Long stateLgdCode,
			@RequestParam(name="districtLgdCode",required = false) Long districtLgdCode,
			@RequestParam(name="subDistrictLgdCode",required = false) Long subDistrictLgdCode){
		return villageLgdMasterService.getVillageList(stateLgdCode,districtLgdCode,subDistrictLgdCode);
	}

	/**
	 * Get village details by lgd code
	 * @param lgdCode
	 * @return
	 */
	@GetMapping("/getVillageByLGDCode/{lgdCode}")
	public ResponseModel getVillageByLGDCode(@PathVariable Long lgdCode) {
		try {
			VillageLgdMaster villageLgdMaster = villageLgdMasterService.getVillageByLGDCode(lgdCode);
			if(Objects.isNull(villageLgdMaster)) {
				return new ResponseModel(null, "Village not found for LGD code: " + lgdCode, CustomMessages.GET_DATA_SUCCESS,CustomMessages.SUCCESS,  CustomMessages.METHOD_POST);
			}
			return new ResponseModel(villageLgdMaster, "Village " + CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,CustomMessages.SUCCESS,  CustomMessages.METHOD_POST);
		} catch(Exception e) {
			return new ResponseModel(null, e.getMessage(),  CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILURE, CustomMessages.FAILED);
		}
	}

	/**
	 * Get village lgd codes by sub district
	 * @param commonDto
	 * @return
	 */
	@PostMapping("getVillageByLGDCodesAndSubDistricts")
	public ResponseModel getVillageByLGDCodesAndSubDistricts(@RequestBody CommonDto commonDto) {
		try {
			List<VillageLgdMaster> villageLgdMasters = villageLgdMasterService
					.getVillageByLGDCodesAndSubDistrictCode(commonDto.getBoundaryLgdCodes(), commonDto.getParentBoundaryLgdCode());
			return new ResponseModel(villageLgdMasters, "Villages " + CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILURE,
					CustomMessages.FAILED);
		}
	}

	/**
	 * Get village lgd codes by list of sub district
	 * @param commonDto
	 * @return
	 */
	@PostMapping("getVillageByLGDCodesAndSubDistrictCodes")
	public ResponseModel getVillageByLGDCodesAndSubDistrictCodes(@RequestBody CommonDto commonDto) {
		try {
			List<VillageLgdMaster> villageLgdMasters = villageLgdMasterService
					.getVillageByLGDCodesAndSubDistrictCodes(commonDto.getBoundaryLgdCodes(), commonDto.getParentBoundaryLgdCodes());
			return new ResponseModel(villageLgdMasters, "Villages " + CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILURE,
					CustomMessages.FAILED);
		}
	}
}
