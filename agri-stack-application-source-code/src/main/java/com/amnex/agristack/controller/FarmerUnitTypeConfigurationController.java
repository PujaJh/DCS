/**
 *
 */
package com.amnex.agristack.controller;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.StateUnitTypeDTO;
import com.amnex.agristack.dao.SubDistrictLandUnitTypeDTO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.service.FarmerUnitTypeConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author kinnari.soni
 *
 *  22 Feb 2023 12:55:36 pm
 */
@RestController
@RequestMapping("/generalConfiguration")
public class FarmerUnitTypeConfigurationController {

	@Autowired
	private FarmerUnitTypeConfigurationService unitTypeConfigurationService;

	/**
	 * Add unit type by state
	 * @param request the HttpServletRequest object representing the incoming request
	 * @param unitTypeMaster the StateUnitTypeDTO object containing the input parameters
	 * @return ResponseModel containing the result of the operation
	 */
	@PostMapping(value = "/addUnitType")
	public ResponseModel addUnitType(HttpServletRequest request, @RequestBody StateUnitTypeDTO unitTypeMaster) {
		return unitTypeConfigurationService.addUnitType(unitTypeMaster);
	}

	/**
	 * Add unit type mapping at sub district level
	 * @param subDistrictLandUnitTypeDTO the SubDistrictLandUnitTypeDTO object containing the input parameters
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return ResponseModel containing the result of the operation
	 */
	@PostMapping("/addUnitTypeMapping")
	public ResponseModel addUnitTypeMapping(@RequestBody SubDistrictLandUnitTypeDTO subDistrictLandUnitTypeDTO,
			HttpServletRequest request) {
		return unitTypeConfigurationService.addUnitTypeMapping(subDistrictLandUnitTypeDTO, request);
	}

	/**
	 * Fetch unit type mapping by state
	 * @param stateLgdCode state lgd code to get unit type
	 * @return ResponseModel containing the result of the operation
	 */
	@GetMapping("/getUnitTypeByState")
	public ResponseModel getUnitTypeByState(@RequestParam("stateLgdCode") Long stateLgdCode) {
		return unitTypeConfigurationService.getUnitTypeByState(stateLgdCode);
	}

	/**
	 * Fetch unit type mapping by sub district
	 * @param request the HttpServletRequest object representing the incoming request
	 * @param subDistrictLandUnitTypeDTO the SubDistrictLandUnitTypeDTO object containing the input parameters
	 * @return ResponseModel containing the result of the operation
	 */
	@PostMapping(value = "/getUnitTypeMapping")
	public ResponseModel getUnitTypeMapping(HttpServletRequest request,
			@RequestBody SubDistrictLandUnitTypeDTO subDistrictLandUnitTypeDTO) {
		return unitTypeConfigurationService.getUnitTypeMapping(subDistrictLandUnitTypeDTO, request);
	}

	/**
	 * Fetch unit type by sub district
	 * @param subDistrictLgdCode subDistrict lgd code to get unit type
	 * @return ResponseModel containing the result of the operation
	 */
	@GetMapping("/getUnitTypeBySubDistrictCode")
	public ResponseModel getUnitTypeBySubDistrictCode(@RequestParam("subDistrictLgdCode") Long subDistrictLgdCode) {
		return unitTypeConfigurationService.getUnitTypeBySubDistrictCode(subDistrictLgdCode);
	}
}
