package com.amnex.agristack.controller;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.dao.FarmerGoldenRecordDao;
import com.amnex.agristack.dao.FarmlandPlotDAO;
import com.amnex.agristack.dao.PaginationDao;
import com.amnex.agristack.service.FarmerGoldenRecordService;

@RestController
@RequestMapping("/farmerGoldenRecord")
public class FarmerGoldenRecordController {
	
	@Autowired
	private FarmerGoldenRecordService farmerGoldenRecordService;

	/**
	 * Retrieves a list of farmer details by village lgd code.
	 *
	 * @param dao input dao of FarmerGoldenRecordDao
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return the ResponseEntity containing the list of farmer details by village lgd code
	 */
	@PostMapping(value = "/getFarmerDetailByVillageLgdCode")
	 public ResponseModel getFarmerDetailByVillageLgdCode(@RequestBody FarmerGoldenRecordDao dao, HttpServletRequest request) {
		return farmerGoldenRecordService.getFarmerDetailByVillageLgdCode(dao, request); 
	}
	
//	@PostMapping(value = "/getSurveyNoDetailByFarmerId")
//	public ResponseModel getSurveyNoDetailByFarmerId(@RequestBody FarmerGoldenRecordDao dao, HttpServletRequest request) {
//		return farmerGoldenRecordService.getSurveyNoDetailByFarmerId(dao, request); 
//	}

	/**
	 * Retrieves a list of farmer golden records by plot.
	 *
	 * @param farmerGoldenRecordDao  input dao of FarmerGoldenRecordDao
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return the ResponseEntity containing the list of farmer golden records by plot
	 */
	@PostMapping(value = "geFarmerGoldenRecordDetailsByPlot")
	public ResponseModel getFarmerPlotDetailsByFarmerIdAndSurveyNumber(@RequestBody FarmerGoldenRecordDao farmerGoldenRecordDao, HttpServletRequest request) {
		return farmerGoldenRecordService.geFarmerGoldenRecordDetailsByPlot(farmerGoldenRecordDao, request);
	}

	/**
	 * Retrieves a list of farmer and land details.
	 *
	 * @param dao  input dao of FarmerGoldenRecordDao
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return the ResponseEntity containing the list of farmer and land details
	 */
	@PostMapping(value = "/getFarmerProfileAndLandDetails")
	public ResponseModel getFarmerProfileAndLandDetails(@RequestBody FarmerGoldenRecordDao dao, HttpServletRequest request) {
		return farmerGoldenRecordService.getFarmerProfileAndLandDetails(dao, request); 
	}

}
