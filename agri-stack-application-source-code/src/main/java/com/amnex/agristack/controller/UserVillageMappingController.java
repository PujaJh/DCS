package com.amnex.agristack.controller;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.service.UserVillageMappingService;

@RestController
@RequestMapping("/userVillageMapping")
public class UserVillageMappingController {

	@Autowired
	UserVillageMappingService userVillageMappingService;

	/**
	 * Get assigned geometry by user id
	 * @param userId user id
	 * @return The response model containing the result of the operation.
	 */
	@GetMapping("/getAssignedGeometryByUserId")
	public ResponseModel getAssignedGeometryByUserId(@RequestParam("userId") Long userId) {
		return userVillageMappingService.getAssignedGeometryByUserId(userId);
	}

	/**
	 * Get assigned geometyr with out authentication
	 * @param userId user id
	 * @return The response model containing the result of the operation.
	 */
	@GetMapping("/authenticate/getAssignedGeometryByUserId")
	public ResponseModel getAssignedGeometryByUserIdPublic(@RequestParam("userId") Long userId) {
		return userVillageMappingService.getAssignedGeometryByUserId(userId);
	}

	/**
	 * Get hierarchical assigned geometry by used id
	 * @param userId user id
	 * @return The response model containing the result of the operation.
	 */
	@GetMapping("/getAssignedTreeGeometryByUserId")
	public ResponseModel getAssignedTreeGeometryByUserId(@RequestParam("userId") Long userId) {
		return userVillageMappingService.getAssignedTreeGeometryByUserId(userId);
	}

	/**
	 * Get hierarchical assigned geometry without authentication
	 * @param userId user id
	 * @return The response model containing the result of the operation.
	 */
	@GetMapping("/authenticate/getAssignedTreeGeometryByUserId")
	public ResponseModel getAssignedTreeGeometryByUserIdPublic(@RequestParam("userId") Long userId) {
		return userVillageMappingService.getAssignedTreeGeometryByUserId(userId);
	}

}
