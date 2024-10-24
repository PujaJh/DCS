package com.amnex.agristack.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.UploadLandAndOwnershipDetailsDto;
import com.amnex.agristack.dao.common.ResponseModel;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.service.StateRoRDataDumpService;

/***
 * @author darshankumar.gajjar
 **/
@RestController
@RequestMapping("/stateRoRDataDump")
public class StateRoRDataDumpController {

	@Autowired
	StateRoRDataDumpService stateRoRDataDumpService;


	/**
	 * Upload up state data by village lgd code.
	 *
	 * @param villageLgdCode village lgd code.
	 * @param request       The HttpServletRequest object.
	 * @return              The ResponseModel object representing the response.
	 */
	@GetMapping("/uploadUPStateDataByVillageCode/{villageLgdCode}")
	public ResponseModel uploadUPStateDataByVillageCode(@PathVariable("villageLgdCode") Integer villageLgdCode,
														HttpServletRequest request) {
		return stateRoRDataDumpService.uploadUPStateDataByVillageCode(villageLgdCode, request);
	}

	/**
	 * Upload land ownership detail.
	 *
	 * @param uploaadLandAndOwnershipDetailsDto  The UploadLandAndOwnershipDetailsDto object containing the request parameters.
	 * @param request       The HttpServletRequest object.
	 * @return              The ResponseModel object representing the response.
	 */
	@SuppressWarnings("finally")
	@PostMapping("/uploadExcel")
	public ResponseModel uploadExcelFile(UploadLandAndOwnershipDetailsDto uploaadLandAndOwnershipDetailsDto, HttpServletRequest request)
			throws InvalidFormatException, IOException {
		return stateRoRDataDumpService.uploadExcelFile(uploaadLandAndOwnershipDetailsDto,request);
	}

	/**
	 * Upload od state data by village lgd code.
	 *
	 * @param villageLgdCode village lgd code.
	 * @param request       The HttpServletRequest object.
	 * @return              The ResponseModel object representing the response.
	 */
	@GetMapping("/uploadODStateDataByVillageCode/{villageLgdCode}")
	public ResponseModel uploadODStateDataByVillageCode(@PathVariable("villageLgdCode") Integer villageLgdCode,
			HttpServletRequest request) {
		return stateRoRDataDumpService.uploadODStateDataBulk();
	}

}
