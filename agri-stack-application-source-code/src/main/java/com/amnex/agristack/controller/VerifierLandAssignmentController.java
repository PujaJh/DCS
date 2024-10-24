package com.amnex.agristack.controller;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.dao.VerifierTaskAllocationDAO;
import com.amnex.agristack.service.VerifierLandAssignmentService;

@RestController
@RequestMapping("/verifierLandAssignment")
public class VerifierLandAssignmentController {

	@Autowired
	private VerifierLandAssignmentService verifierLandAssignmentService;

	/**
	 * Retrieves farmland plots based on the specified filter criteria.
	 *
	 * @param request The HttpServletRequest object.
	 * @param verifierTaskAllocationDAO The verifier task allocation filter criteria.
	 * @return The response model containing the farmland plots.
	 */
	@PostMapping("/getFarmlandPlotByFilter")
	public ResponseModel getFarmlandPlotByFilter(HttpServletRequest request,
												 @RequestBody VerifierTaskAllocationDAO verifierTaskAllocationDAO) {
		return verifierLandAssignmentService.getFarmlandPlotByFilter(request, verifierTaskAllocationDAO);
	}

	/**
	 * Retrieves assigned data based on the specified filter criteria.
	 *
	 * @param request The HttpServletRequest object.
	 * @param verifierTaskAllocationDAO The verifier task allocation filter criteria.
	 * @return The response model containing the assigned data.
	 */
	@PostMapping("/getAssignedDataByFilter")
	public ResponseModel getAssignedDataByFilter(HttpServletRequest request,
			@RequestBody VerifierTaskAllocationDAO verifierTaskAllocationDAO) {
		return verifierLandAssignmentService.getAssignedDataByFilter(request, verifierTaskAllocationDAO);
	}

	/**
	 * Retrieves available verifiers based on the specified filter criteria.
	 *
	 * @param request The HttpServletRequest object.
	 * @param verifierTaskAllocationDAO The verifier task allocation filter criteria.
	 * @return The response model containing the available verifiers.
	 */
	@PostMapping("/getAvailableVerifiers")
	public ResponseModel getAvailableVerifiers(HttpServletRequest request,
			@RequestBody VerifierTaskAllocationDAO verifierTaskAllocationDAO) {
		return verifierLandAssignmentService.getAvailableVerifiers(request, verifierTaskAllocationDAO);
	}

	/**
	 * Assigns a task to a verifier based on the specified allocation details.
	 *
	 * @param request The HttpServletRequest object.
	 * @param verifierTaskAllocationDAO The verifier task allocation details.
	 * @return The response model indicating the success of the task assignment.
	 */
	@PostMapping("/assignTaskToVerifier")
	public ResponseModel assignTaskToVerifier(HttpServletRequest request,
			@RequestBody VerifierTaskAllocationDAO verifierTaskAllocationDAO) {
		return verifierLandAssignmentService.assignTaskToVerifier(request, verifierTaskAllocationDAO);
	}

	/**
	 * Retrieves farmland plots based on the specified village code and verifier task allocation details.
	 *
	 * @param request The HttpServletRequest object.
	 * @param verifierTaskAllocationDAO The verifier task allocation details.
	 * @return The response model containing the farmland plots.
	 */
	@PostMapping("/getFarmlandPlotByVillageCode")
	public ResponseModel getFarmlandPlotByVillageCode(HttpServletRequest request,
			@RequestBody VerifierTaskAllocationDAO verifierTaskAllocationDAO) {
		return verifierLandAssignmentService.getFarmlandPlotByVillageCode(request, verifierTaskAllocationDAO);
	}

	/**
	 * Retrieves the list of villages for verifier task allocation based on the specified verifier task allocation details.
	 *
	 * @param request The HttpServletRequest object.
	 * @param verifierTaskAllocationDAO The verifier task allocation details.
	 * @return The response model containing the list of villages.
	 */
	@PostMapping("/getVillageListForVerifierTaskAllocation")
	public ResponseModel getVillageListForVerifierTaskAllocation(HttpServletRequest request,
			@RequestBody VerifierTaskAllocationDAO verifierTaskAllocationDAO) {
		return verifierLandAssignmentService.getVillageListForVerifierTaskAllocation(request, verifierTaskAllocationDAO);
	}

	/**
	 * Unassigns a verifier task allocation based on the specified verifier task allocation details.
	 *
	 * @param request The HttpServletRequest object.
	 * @param verifierTaskAllocationDAO The verifier task allocation details.
	 * @return The response model indicating the success of the unassignment.
	 */
	@PostMapping("/unassignVerifierTaskAllocation")
	public ResponseModel unassignVerifierTaskAllocation(HttpServletRequest request,
			@RequestBody VerifierTaskAllocationDAO verifierTaskAllocationDAO) {
		return verifierLandAssignmentService.unassignVerifierTaskAllocation(request, verifierTaskAllocationDAO);
	}

	/**
	 * Retrieves the list of verifiers by filter.
	 *
	 * @param request The HttpServletRequest object.
	 * @param verifierTaskAllocationDAO The verifier task allocation details.
	 * @return The response model containing the list of verifiers.
	 */
	@PostMapping("/getVerifierByVillageCodeWithFilter")
	public ResponseModel getVerifierByVillageCodeWithFilter(HttpServletRequest request,
			@RequestBody VerifierTaskAllocationDAO verifierTaskAllocationDAO) {
		return verifierLandAssignmentService.getVerifierByVillageCodeWithFilter(request, verifierTaskAllocationDAO);
	}

	/**
	 * Retrieves the list of available verifiers by filter.
	 *
	 * @param request The HttpServletRequest object.
	 * @param verifierTaskAllocationDAO The verifier task allocation details.
	 * @return The response model containing the list of available verifiers.
	 */
	@PostMapping("/getAvailableVerifiersByFilter")
	public ResponseModel getAvailableVerifiersByFilter(HttpServletRequest request,
			@RequestBody VerifierTaskAllocationDAO verifierTaskAllocationDAO) {
		return verifierLandAssignmentService.getAvailableVerifiersByFilter(request, verifierTaskAllocationDAO);
	}
}
