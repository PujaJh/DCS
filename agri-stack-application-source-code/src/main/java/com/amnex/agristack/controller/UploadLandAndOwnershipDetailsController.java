package com.amnex.agristack.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.UploadLandAndOwnershipDetailsDto;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.service.FarmerLandOwnershipService;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.utils.CustomMessages;
/**
 * 
 * @author janmaijaysingh.bisen
 *
 */
@RestController
@RequestMapping("/uploadLandAndOwnershipDetails")
public class UploadLandAndOwnershipDetailsController {
	@Autowired
	private FarmerLandOwnershipService farmerLandOwnershipService;
	/**
	 * upload excel file for land ownership details 
	 * @param uploaadLandAndOwnershipDetailsDto
	 * @return  The ResponseModel object representing the response.
	 * @throws IOException 
	 * @throws InvalidFormatException 
	 */
	@SuppressWarnings("finally")
	@PostMapping("/uploadExcel")
	public ResponseModel uploadExcelFile(UploadLandAndOwnershipDetailsDto uploaadLandAndOwnershipDetailsDto) throws InvalidFormatException, IOException {
			return farmerLandOwnershipService.uploadExcelFile(uploaadLandAndOwnershipDetailsDto);
			
			//return new ResponseModel(null,CustomMessages.FILE_UPLOAD_SUCCESS, CustomMessages.ADD_SUCCESSFULLY, CustomMessages.FILE_UPLOAD_SUCCESS, CustomMessages.METHOD_POST);
		
		//return null;
	}
	/**
	 * fetch all uploaded file
	 * @return  The ResponseModel object representing the response.
	 */
	@GetMapping("/getAll")
	public ResponseModel getAll() {
		return new ResponseModel(farmerLandOwnershipService.getAllLandAndOwnershipFileHistories(), CustomMessages.SUCCESS, CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
	}
	@PostMapping("/uploadExcelAndReadColumns")
	public ResponseModel uploadExcelAndReadColumns(UploadLandAndOwnershipDetailsDto uploaadLandAndOwnershipDetailsDto) throws InvalidFormatException, IOException {
			return farmerLandOwnershipService.uploadExcelFileReadHeader(uploaadLandAndOwnershipDetailsDto);
			
			//return new ResponseModel(null,CustomMessages.FILE_UPLOAD_SUCCESS, CustomMessages.ADD_SUCCESSFULLY, CustomMessages.FILE_UPLOAD_SUCCESS, CustomMessages.METHOD_POST);
		
		//return null;
	}
	@PostMapping("/uploadShpAndReadColumns")
	public ResponseModel uploadShpAndReadColumns(UploadLandAndOwnershipDetailsDto uploaadLandAndOwnershipDetailsDto) throws InvalidFormatException, IOException {
			return farmerLandOwnershipService.uploadShpAndReadColumns(uploaadLandAndOwnershipDetailsDto);
			
			//return new ResponseModel(null,CustomMessages.FILE_UPLOAD_SUCCESS, CustomMessages.ADD_SUCCESSFULLY, CustomMessages.FILE_UPLOAD_SUCCESS, CustomMessages.METHOD_POST);
		
		//return null;
	}
	
	
	@GetMapping("/uploadPendingDataFromTemp/{villageLgdCode}")
	public ResponseModel uploadPendingDataFromTemp(@PathVariable("villageLgdCode") Long villageLgdCode) {
		farmerLandOwnershipService.readExcelAndUploadDataOfOwnerForUpState(villageLgdCode);
		return new ResponseModel(null, CustomMessages.SUCCESS, CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
	}
	
	
	
//	@GetMapping("/fetchUPLandDataByVillageCode/{villageLgdCode}")
//	public ResponseModel fetchUPLandDataByVillageCode(@PathVariable("villageLgdCode") Long villageLgdCode) {
//		farmerLandOwnershipService.fetchUPLandDataByVillageCode(villageLgdCode);
//		return new ResponseModel(null, CustomMessages.SUCCESS, CustomMessages.GET_DATA_SUCCESS,
//				CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
//	}
	
	
	@GetMapping("/deleteFarmLandPlotWithOwner/{villageLgdCode}")
	public ResponseModel deleteFarmLandPlotWithOwner(@PathVariable("villageLgdCode") Long villageLgdCode) {
		return farmerLandOwnershipService.deleteFarmLandPlotWithOwner(villageLgdCode);
		
	}
	
		/**
		 * fetch all uploaded file by User
		 * @return  The ResponseModel object representing the response.
		 */
		@GetMapping("/getAllByUser")
		public ResponseModel getAllByUser(HttpServletRequest request) {
			return new ResponseModel(farmerLandOwnershipService.getAllLandAndOwnershipFileHistoriesByUser(request), CustomMessages.SUCCESS, CustomMessages.GET_DATA_SUCCESS,
	 				CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
	 	}
	
	
}
