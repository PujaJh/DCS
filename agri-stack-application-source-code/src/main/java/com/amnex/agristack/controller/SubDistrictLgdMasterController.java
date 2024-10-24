/**
 *
 */
package com.amnex.agristack.controller;

import java.util.List;
import java.util.Objects;

import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.service.SubDistrictLgdMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.amnex.agristack.entity.SubDistrictLgdMaster;
import com.amnex.agristack.utils.CustomMessages;

import reactor.core.publisher.Mono;

/**
 * @author kinnari.soni
 *
 */
@RestController
@RequestMapping("/boundary/subDistrict")
public class SubDistrictLgdMasterController {

	@Autowired
	private SubDistrictLgdMasterService subDistrictLgdMasterService;

	/**
	 * Return sub district lgd master list
	 * @param stateLgdCode state lgd code
	 * @param districtLgdCode district lgd code
	 * @return The ResponseEntity object representing the response.
	 */
	@GetMapping("/getSubDistrictLgdMaster")
	public ResponseEntity<Mono<?>> getSubDistrictLgdMaster(
			@RequestParam(name = "stateLgdCode", required = false) Long stateLgdCode,
			@RequestParam(name = "districtLgdCode", required = false) Long districtLgdCode) {
		return subDistrictLgdMasterService.getSubDistrictList(stateLgdCode, districtLgdCode);
	}

	/**
	 * Fetch sub district details by sub district lgd code
	 * @param lgdCode sub district lgd code
	 * @return The ResponseModel object representing the response.
	 */
	@GetMapping("/getSubDistrictByLGDCode/{lgdCode}")
	public ResponseModel getSubDistrictByLGDCode(@PathVariable Long lgdCode) {
		try {
			SubDistrictLgdMaster subDistrictLgdMaster = subDistrictLgdMasterService.getSubDistrictByLGDCode(lgdCode);
			if (Objects.isNull(subDistrictLgdMaster)) {
				return new ResponseModel(null, "Subdistrict not found for LGD code: " + lgdCode,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
			}
			return new ResponseModel(subDistrictLgdMaster, "Subdistrict " + CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILURE,
					CustomMessages.FAILED);
		}
	}

	/**
	 * Fetch list of sub district by sub-district lgd codes
	 * @param lgdCodes the list of sub district lgd codes
	 * @return The ResponseModel object representing the response.
	 */
	@GetMapping("getSubDistrictsByLGDCodes")
	public ResponseModel getSubDistrictsByLGDCodes(@RequestParam("lgdCodes") List<Long> lgdCodes) {
		try {
			List<SubDistrictLgdMaster> subDistrictLgdMasterList = subDistrictLgdMasterService
					.getSubDistrictsByLGDCodes(lgdCodes);
			return new ResponseModel(subDistrictLgdMasterList, "Subdistrict " + CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILURE,
					CustomMessages.FAILED);
		}
	}

	/**
	 * Fetch list of sub district by sub-district lgd codes
	 * @param lgdCodes the list of sub-district lgd codes
	 * @return The ResponseModel object representing the response.
	 */
	@PostMapping("getSubDistrictsByLGDCodesV2")
	public ResponseModel getSubDistrictsByLGDCodesV2(@RequestBody List<Long> lgdCodes) {
		try {
			List<SubDistrictLgdMaster> subDistrictLgdMasterList = subDistrictLgdMasterService
					.getSubDistrictsByLGDCodes(lgdCodes);
			return new ResponseModel(subDistrictLgdMasterList, "Subdistrict " + CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILURE,
					CustomMessages.FAILED);
		}
	}

	/**
	 * Fetch subdistricts by district and list of sub district lgd code
	 * @param subDistrictLgdCode sub district lgd codes
	 * @param districtCode district lgd codes
	 * @return The ResponseModel object representing the response.
	 */
	@GetMapping("getSubDistrictsByLGDCodesAndDistrictCode")
	public ResponseModel getSubDistrictsByLGDCodes(@RequestParam("subDistrictLgdCodes") List<Long> subDistrictLgdCode,@RequestParam("districtCode") Long districtCode) {
		try {
			List<SubDistrictLgdMaster> subDistrictLgdMasterList = subDistrictLgdMasterService
					.getSubLgdMastersByLgdCodeInAndDistrictCode(subDistrictLgdCode, districtCode);
			return new ResponseModel(subDistrictLgdMasterList, "Subdistrict " + CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILURE,
					CustomMessages.FAILED);
		}
	}

	/**
	 * Fetch subdistricts by list of district and list of sub district lgd code
	 * @param subDistrictLgdCode sub district lgd codes
	 * @param districtCodes district lgd codes
	 * @return The ResponseModel object representing the response.
	 */
	@GetMapping("getSubDistrictsByLGDCodesAndDistrictCodeIn")
	public ResponseModel getSubDistrictsByLGDCodesAndDistrictCodeIn(
			@RequestParam("subDistrictLgdCodes") List<Long> subDistrictLgdCode,
			@RequestParam("districtCodes") List<Long> districtCodes) {
		try {
			List<SubDistrictLgdMaster> subDistrictLgdMasterList = subDistrictLgdMasterService
					.getSubLgdMastersByLgdCodeInAndDistrictCodeIn(subDistrictLgdCode, districtCodes);
			return new ResponseModel(subDistrictLgdMasterList, "Subdistrict " + CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILURE,
					CustomMessages.FAILED);
		}
	}

}
