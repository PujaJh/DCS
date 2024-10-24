package com.amnex.agristack.controller;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.CentralCoreDAO;
import com.amnex.agristack.dao.PaginationDao;
import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.amnex.agristack.service.CentralCoreAPIService;

import java.util.Map;

/***
 * @author darshankumar.gajjar
 **/
@RestController
@RequestMapping("/centralCoreAPI")
public class CentralCoreAPIController {

	@Autowired
	CentralCoreAPIService centralCoreAPIService;

	/**
	 * Retrieves the village wise crop sown data.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseModel object containing the village wise crop sown data
	 */
	@PostMapping("/village-crop-sown-data")
	public Map<String, Object> getCropSownDetails(HttpServletRequest request) {
		return centralCoreAPIService.getCropSownDetails(request);
	}

	/**
	 * Retrieves the crop sown geometry details.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseModel object containing the crop sown geometry details
	 */
	@GetMapping("/getCropSownGeometryDetails")
	public ResponseModel getCropSownGeometryDetails(HttpServletRequest request) {
		return centralCoreAPIService.getCropSownGeometryDetails(request);
	}

	/**
	 * Receives the acknowledgement.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseModel object containing the acknowledgement
	 */
	@PostMapping("/acknowledgement")
	public ResponseModel acknowledgeData(@RequestBody CentralCoreDAO centralCoreDAO, HttpServletRequest request) {
		return centralCoreAPIService.acknowledgeData(centralCoreDAO, request);
	}

	/**
	 * Retrieves the Geomap data.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseModel object containing the geomap data
	 */
	@PostMapping("/geomaps")
	public Map<String, Object> getGeomapsDetails( HttpServletRequest request) {
		return centralCoreAPIService.getGeomapsDetails(request);
	}

	/**
	 * Retrieves the village wise crop sown data log.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseModel object containing the village wise crop sown data log
	 */
	@GetMapping("/getCropSownLog")
	public ResponseModel getCropSownLog(HttpServletRequest request) {
		return centralCoreAPIService.getCropSownLog(request);
	}

	/**
	 * Retrieves the geomap data share log.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseModel object containing the geomap data share log
	 */
	@GetMapping("/getGeoMapSharedLog")
	public ResponseModel getGeoMapSharedLog(HttpServletRequest request) {
		return centralCoreAPIService.getGeoMapSharedLog(request);
	}

	/**
	 * Retrieves the village wise crop sown data shared by referencedId.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseModel object containing the village wise crop sown data shared by referenceId
	 */
	@GetMapping("/getSownDataSharedDataByReferenceId/{referenceId}")
	public ResponseModel getSownDataSharedDataByReferenceId(@PathVariable String referenceId, HttpServletRequest request) {
		return centralCoreAPIService.getSownDataSharedDataByReferenceId(referenceId,request);
	}

	/**
	 * Retrieves the geomap data shared by referenceId.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseModel object containing the geomap data shared by referenceId
	 */
	@GetMapping("/getGeoMapSharedDataByReferenceId/{referenceId}")
	public ResponseModel getGeoMapSharedDataByReferenceId(@PathVariable String referenceId, HttpServletRequest request) {
		return centralCoreAPIService.getGeoMapSharedDataByReferenceId(referenceId,request);
	}
	
}
