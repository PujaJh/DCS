
package com.amnex.agristack.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.SurveyTaskAllocationFilterDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.dao.PaginationDao;
import com.amnex.agristack.dao.UserInputDAO;
import com.amnex.agristack.dao.UserLandAssignmentInputDAO;
import com.amnex.agristack.service.UserLandAssignmentService;

/**
 * Controller class for managing user land assignment.
 */
@RestController
@RequestMapping("/userLandAssignment")
public class UserLandAssignmentController {

	@Autowired
	private UserLandAssignmentService userLandAssignmentService;

	/**
	 * Assigns land to multiple users.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userLandAssignmentInputDAOList The list of {@code UserLandAssignmentInputDAO} objects containing the land assignment details.
	 * @return The ResponseModel object indicating the success of the land assignment operation.
	 */
	@PostMapping(value = "/assignLand")
	public ResponseModel assignLand(HttpServletRequest request,
			@RequestBody List<UserLandAssignmentInputDAO> userLandAssignmentInputDAOList) {

		return userLandAssignmentService.assignLand(request, userLandAssignmentInputDAOList);

	}

	/**
	 * Retrieves assignment data for a specific village code.
	 *
	 * @param request The HttpServletRequest object.
	 * @param villageCode The village code for which the assignment data is retrieved.
	 * @return The ResponseModel object containing the assignment data for the specified village code.
	 */
	@GetMapping("/getAssignDataByVillageCode/{villageCode}")
	public ResponseModel getAssignDataByVillageCode(HttpServletRequest request,
			@PathVariable("villageCode") Integer villageCode) {
		return userLandAssignmentService.getAssignDataByVillageCode(request, villageCode);
	}

	/**
	 * Retrieves assignment data based on the provided filter criteria.
	 *
	 * @param request The HttpServletRequest object.
	 * @param surveyTaskAllocationFilterDAO The filter criteria for retrieving the assignment data.
	 * @return The ResponseModel object containing the assignment data based on the filter criteria.
	 */
	@PostMapping("/getAssignDataByFilter")
	public ResponseModel getAssignDataByFilter(HttpServletRequest request,
											   @RequestBody SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		return userLandAssignmentService.getAssignDataByFilter(request, surveyTaskAllocationFilterDAO);
	}
	

	/**
	 * Retrieves assignment data based on the provided filter criteria.
	 *
	 * @param request The HttpServletRequest object.
	 * @param surveyTaskAllocationFilterDAO The filter criteria for retrieving the assignment data.
	 * @return The ResponseModel object containing the assignment data based on the filter criteria.
	 */
	@PostMapping("/getAssignDataByFilterV2")
	public ResponseModel getAssignDataByFilterV2(HttpServletRequest request,
			@RequestBody SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		return userLandAssignmentService.getAssignDataByFilterV2(request, surveyTaskAllocationFilterDAO);
	}

	/**
	 * Retrieves surveyors assigned to a specific village based on the village code.
	 *
	 * @param request The HttpServletRequest object.
	 * @param villageCode The code of the village for which to retrieve the assigned surveyors.
	 * @return The ResponseModel object containing the surveyors assigned to the specified village.
	 */
	@GetMapping("/getSurveyorByVillageCode/{villageCode}")
	public ResponseModel getSurveyorByVillageCode(HttpServletRequest request,
			@PathVariable("villageCode") Integer villageCode) {
		return userLandAssignmentService.getSurveyorByVillageCode(request, villageCode);
	}

	/**
	 * Retrieves surveyors assigned to a specific village based on the village code, with additional filtering options.
	 *
	 * @param request The HttpServletRequest object.
	 * @param surveyTaskAllocationFilterDAO The filter criteria for retrieving the surveyors.
	 * @return The ResponseModel object containing the surveyors assigned to the specified village, filtered by the given criteria.
	 */
	@PostMapping("/getSurveyorByVillageCodeWithFilter")
	public ResponseModel getSurveyorByVillageCodeWithFilter(HttpServletRequest request,
			@RequestBody SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		return userLandAssignmentService.getSurveyorByVillageCodeWithFilter(request, surveyTaskAllocationFilterDAO);
	}
	
	/**
	 * Retrieves surveyors assigned to a specific village based on the village code, with additional filtering options.
	 *
	 * @param request The HttpServletRequest object.
	 * @param surveyTaskAllocationFilterDAO The filter criteria for retrieving the surveyors.
	 * @return The ResponseModel object containing the surveyors assigned to the specified village, filtered by the given criteria.
	 */
	@PostMapping("/getSurveyorByVillageCodeWithFilterV2")
	public ResponseModel getSurveyorByVillageCodeWithFilterV2(HttpServletRequest request,
			@RequestBody SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		return userLandAssignmentService.getSurveyorByVillageCodeWithFilterV2(request, surveyTaskAllocationFilterDAO);
	}

	/**
	 * Removes land assignments for a list of users based on the given filter criteria.
	 *
	 * @param request The HttpServletRequest object.
	 * @param surveyTaskAllocationFilterDAO The filter criteria for removing the land assignments.
	 * @return The ResponseModel object indicating the status of the land removal process.
	 */
	@PutMapping(value = "/removeLandByUserList")
	public ResponseModel removeLandByUserList(HttpServletRequest request , @RequestBody SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {

		return userLandAssignmentService.removeLandByUserList(request, surveyTaskAllocationFilterDAO);

	}
	
	/**
	 * Removes land assignments for a list of lands based on the given filter criteria.
	 *
	 * @param request The HttpServletRequest object.
	 * @param surveyTaskAllocationFilterDAO The filter criteria for removing the land assignments.
	 * @return The ResponseModel object indicating the status of the land removal process.
	 */
	@PutMapping(value = "/removeLandByLandList")
	public ResponseModel removeLandByLandList(HttpServletRequest request, @RequestBody SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {


		return userLandAssignmentService.removeLandByLandList(request, surveyTaskAllocationFilterDAO);

	}

	/**
	 * Retrieves pending land assignments for the specified users based on the given filter criteria.
	 *
	 * @param request The HttpServletRequest object.
	 * @param surveyTaskAllocationFilterDAO The filter criteria for retrieving pending land assignments.
	 * @return The ResponseModel object containing the pending land assignments for the users.
	 */
    @PostMapping(value = "/getPendingLandByusers")
	public ResponseModel getPendingLandByusers(HttpServletRequest request, @RequestBody SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {

		return userLandAssignmentService.getPendingLandByusers(request, surveyTaskAllocationFilterDAO);

	}
    
    /**
     * Retrieves surveyors by taluka code with the specified filter criteria.
     *
     * @param request The HttpServletRequest object.
     * @param surveyTaskAllocationFilterDAO The filter criteria for retrieving surveyors.
     * @return The ResponseModel object containing the surveyors.
     */
	@PostMapping("/getSurveyorByTalukaCodeWithFilter")
	public ResponseModel getSurveyorByTalukaCodeWithFilter(HttpServletRequest request,
			@RequestBody SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
//		return userLandAssignmentService.getSurveyorByTalukaCodeWithFilter(request, surveyTaskAllocationFilterDAO);
		return userLandAssignmentService.getSurveyorByTalukaCodeWithFilterNew(request, surveyTaskAllocationFilterDAO);
	}
	
	/**
     * Retrieves unable survey plot details.
     *
     * @param request The HttpServletRequest object.
     * @param requestDAO The filter criteria for retrieving data.
     * @return The ResponseModel object containing the unable survey plot details.
     */
	@PostMapping(value = "/getUnableToSurveyDetails")
	public ResponseModel getUnableToSurveyDetails(@RequestBody PaginationDao requestDAO,
			HttpServletRequest request) {
		try {
			return userLandAssignmentService.getUnableToSurveyDetails(requestDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
     * Retrieves unable survey plot enable.
     *
     * @param inputDAO The filter criteria for retrieving data.
     * @return The ResponseModel object containing the enable the unable survey plot.
     */
	 @PostMapping(value =  "/unableToSurveyEnable")
		public ResponseModel unableToSurveyEnable(@RequestBody UserInputDAO inputDAO) {
			return userLandAssignmentService.unableToSurveyEnable(inputDAO);
		}
}
