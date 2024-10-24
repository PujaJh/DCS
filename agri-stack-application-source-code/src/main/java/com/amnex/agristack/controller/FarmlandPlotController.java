package com.amnex.agristack.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.FarmLandPlotRegistryDto;
import com.amnex.agristack.dao.FarmerInputDAO;
import com.amnex.agristack.dao.SurveyTaskAllocationFilterDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.service.FarmLandPlotRegistryService;
import com.amnex.agristack.service.FarmlandPlotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.dao.PaginationDao;

import reactor.core.publisher.Mono;
/**
 * 
 * @author janmaijaysingh.bisen
 *
 */
@RestController
@RequestMapping("/farmlandPlot")
public class FarmlandPlotController {
	
	@Autowired
	private FarmlandPlotService farmlandPlotService;
	
	@Autowired
	private FarmLandPlotRegistryService farmlandPlotRegistryService;
	/**
	 * fetch farm land plot by given ids
	 * @param request the HttpServletRequest object representing the incoming request
	 * @param ids list of ids to get farmland plot
	 * @return The ResponseModel object representing the response.
	 */
	@PostMapping("/getFarmlandPlot")
	public ResponseModel getFarmlandPlot(HttpServletRequest request, @RequestBody List<Integer> ids) {
		return farmlandPlotService.getFarmlandPlot(request,ids);
	}
	/**
	 * fetch farm land plot by filters
	 * @param request the HttpServletRequest object representing the incoming request
	 * @param surveyTaskAllocationFilterDAO input dao of SurveyTaskAllocationDao
	 * @return  The ResponseModel object representing the response.
	 */
	@PostMapping("/getFarmlandPlotByFilter")
	public ResponseModel getFarmlandPlotByFilter(HttpServletRequest request,@RequestBody SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
//		return farmlandPlotService.getFarmlandPlotByFilter(request,surveyTaskAllocationFilterDAO);
		return farmlandPlotService.getFarmlandPlotByFilterNew(request,surveyTaskAllocationFilterDAO);
	}
	/**
	 * fetch farm land plot by village
	 * @param request the HttpServletRequest object representing the incoming request
	 * @param villageCode village lgd code to get farmland plot
	 * @return  The ResponseModel object representing the response.
	 */
	@GetMapping("/getFarmlandPlotByVillageCode/{villageCode}")
	public ResponseModel getFarmlandPlotByVillageCode(HttpServletRequest request,@PathVariable("villageCode") Integer villageCode) {
		return farmlandPlotService.getFarmlandPlotByVillageCode(request,villageCode);
	}
	
	/**
	 * fetch unassign plot by filter
	 * @param request the HttpServletRequest object representing the incoming request
	 * @param surveyTaskAllocationFilterDAO input dao of SurveyTaskAllocationDao
	 * @return  The ResponseModel object representing the response.
	 */
	@PostMapping("/getUnassignPlotByfilter")
	public ResponseModel getUnassignPlotByfilter(HttpServletRequest request,@RequestBody SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		return farmlandPlotService.getUnassignPlotByfilter(request,surveyTaskAllocationFilterDAO);
	}
	/**
	 * fetch farm land plot for supervisor
	 * @param request the HttpServletRequest object representing the incoming request
	 * @param paginationDao input dao of PaginationDao
	 * @return  The ResponseModel object representing the response.
	 */
	@PostMapping("/getFarmlandPlotForSupervisor")
	public ResponseModel getFarmlandPlotForSupervisor(HttpServletRequest request,@RequestBody PaginationDao paginationDao) {
		return farmlandPlotService.getFarmlandPlotForSupervisor(request,paginationDao);
	}
	/**
	 * fetch farm land plot for supervisor filter
	 * @param request the HttpServletRequest object representing the incoming request
	 * @param paginationDao input dao of PaginationDao
	 * @return  The ResponseModel object representing the response.
	 */
	@PostMapping("/getFarmlandPlotForSupervisorByFilter")
	public ResponseModel getFarmlandPlotForSupervisorByFilter(HttpServletRequest request,@RequestBody PaginationDao paginationDao) {
		return farmlandPlotService.getFarmlandPlotForSupervisorByFilter(request, paginationDao);
	}
	/**
	 * fetch farm land plot geometry by village
	 * @param request the HttpServletRequest object representing the incoming request
	 * @param villageCode village lgd code to get farmland plot
	 * @return  The ResponseModel object representing the response.
	 */
	@GetMapping("/getFarmlandPlotsByVillageCode/{villageCode}")
	public ResponseModel getFarmlandPlotsByVillageCode(HttpServletRequest request,@PathVariable("villageCode") Integer villageCode) {
		return farmlandPlotService.getFarmlandPlotsByVillageCode(request,villageCode);
	}
	
	/**
	 * fetch unassign plot by filter
	 * @param request the HttpServletRequest object representing the incoming request
	 * @param surveyTaskAllocationFilterDAO input dao of SurveyTaskAllocationDao
	 * @return  The ResponseModel object representing the response.
	 */
	@PostMapping("/getUnassignPlotByfilterV2")
	public ResponseModel getUnassignPlotByfilterV2(HttpServletRequest request,@RequestBody SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		return farmlandPlotService.getUnassignPlotByfilterV2(request,surveyTaskAllocationFilterDAO);
	}

	/**
	 * fetch plot details by village lgd code
	 * @param request the HttpServletRequest object representing the incoming request
	 * @param inputDao input dao of SurveyTaskAllocationDao
	 * @return  The ResponseModel object representing the response.
	 */
	@PostMapping(value = "/getPlotDetailByVillageLgdCode")
	 public ResponseModel getPlotDetailByVillageLgdCode(@RequestBody SurveyTaskAllocationFilterDAO inputDao, HttpServletRequest request) {
		 return farmlandPlotService.getPlotDetailByVillageLgdCode(inputDao, request); 
	 }

	/**
	 * fetch farm land details
	 * @param request the HttpServletRequest object representing the incoming request
	 * @param farmerInput input dao of FarmerInputDao
	 * @return  The ResponseModel object representing the response.
	 */
	@PostMapping("/getFarmerLandDetails")
	public  ResponseModel getFarmerLandDetails(@RequestBody FarmerInputDAO farmerInput, HttpServletRequest request) {
		return farmlandPlotService.getFarmerLandDetails(farmerInput, request);
	}

	/**
	 * fetch farm land plot survey details
	 * @param request the HttpServletRequest object representing the incoming request
	 * @param requestDTO input dao of FarmlandPlotRegistryDao
	 * @return  The ResponseModel object representing the response.
	 */
	@PostMapping(value = "/getFarmlandPlotSurvey")
    public String getFarmlandPlotSurvey(@RequestBody FarmLandPlotRegistryDto requestDTO, HttpServletRequest request) {
        return farmlandPlotRegistryService.getFarmlandPlotSurvey(requestDTO,request);
    }
}
