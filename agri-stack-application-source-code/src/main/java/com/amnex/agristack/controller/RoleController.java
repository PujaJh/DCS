package com.amnex.agristack.controller;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.dao.RoleInputDAO;
import com.amnex.agristack.service.RoleService;

/**
 * @author kinnari.soni
 *
 * 18 May 2023 2:41:30 pm
 */
@RestController
@RequestMapping("/role")
public class RoleController {

	@Autowired
	RoleService roleService;

	/**
	 * Add or update role
	 * @param roleInputDAO {@link RoleInputDAO}
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/addAndEditRole")
	public ResponseModel addAndEditRole(@RequestBody RoleInputDAO roleInputDAO, HttpServletRequest request) {
		try {
			return roleService.addAndEditRole(roleInputDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * get all active roles
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@GetMapping(value = "/getAllActive")
	public ResponseModel getAllActive(HttpServletRequest request) {
		try {
			return roleService.getAllActive(request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Update role status
	 * @param request the HTTP servlet request
	 * @param roleInputDAO {@link RoleInputDAO}
	 * @return The ResponseModel object representing the response
	 */
	@PutMapping(value = "/updateStatus")
	public ResponseModel updateStatus(HttpServletRequest request, @RequestBody RoleInputDAO roleInputDAO) {

		try {
			return roleService.updateStatus(request, roleInputDAO);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Create role along with users and it's boundary
	 * @param roleInputDAO {@code RoleInputDAO}
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/addRolePatternWithPrefix")
	public ResponseModel addRolePatternWithPrefix(@RequestBody RoleInputDAO roleInputDAO, HttpServletRequest request) {
		try {
			return roleService.addRoleWithPrefix(roleInputDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Create role along with users and it's boundary with permission
	 * @param roleInputDAO {@code RoleInputDAO}
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/addRolePatternWithPrefixNew")
	public ResponseModel addRolePatternWithPrefixNew(@RequestBody RoleInputDAO roleInputDAO, HttpServletRequest request) {
		try {
			return roleService.addRolePatternWithPrefixNew(roleInputDAO,request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Update role and it's user details
	 * @param roleInputDAO {@code RoleInputDAO}
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/editRolePatternWithPrefix")
	public ResponseModel editRolePatternWithPrefix(@RequestBody RoleInputDAO roleInputDAO, HttpServletRequest request) {
		try {
			return roleService.editRoleWithPrefix(roleInputDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Update role and it's user details with permission
	 * @param roleInputDAO {@code RoleInputDAO}
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/editRolePatternWithPrefixNew")
	public ResponseModel editRolePatternWithPrefixNew(@RequestBody RoleInputDAO roleInputDAO, HttpServletRequest request) {
		try {
			return roleService.editRolePatternWithPrefixNew(roleInputDAO,request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Validate role prefix for duplication
	 * @param prefix prefix to validate
	 * @param state state lgd code
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@GetMapping(value = "/validateRolePrefixByState")
	public ResponseModel validateRolePrefix(@RequestParam("prefix") String prefix,
			@RequestParam(name = "stateLgdCode", required = true) Long state, HttpServletRequest request) {
		return roleService.validateRolePrefix(prefix, state, request);
	}

	/**
	 * Validate default role prefix for duplication
	 * @param prefix prefix to validate
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@GetMapping(value = "/validateDefaultRolePrefix")
	public ResponseModel validateDefaultRolePrefix(@RequestParam("prefix") String prefix, HttpServletRequest request) {
		return roleService.validateDefaultRolePrefix(prefix, request);
	}

	/**
	 * Validate role name for duplication
	 * @param roleName roleName to validate
	 * @param state state lgd code
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@GetMapping(value = "/validateRoleNameByState")
	public ResponseModel validateRoleName(@RequestParam(name = "roleName", required = true) String roleName,
			@RequestParam(name = "stateLgdCode", required = true) Long state, HttpServletRequest request) {
		return roleService.validateRoleName(roleName, state, request);
	}

	/**
	 * get list of roles by assigned state
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@GetMapping(value = "/getUserRolesByAssignState")
	public ResponseModel getUserRolesByAssignState(HttpServletRequest request) {
		return roleService.getUserRolesByAssignState(request);
	}

	/**
	 * Update active and delete status of users role
	 * @param request the HTTP servlet request
	 * @param roleInputDAO The RoleInputDao containing the input params
	 * @return The ResponseModel object representing the response
	 */
	@PutMapping(value = "/updateRolePatternStatus")
	public ResponseModel updateRolePatternStatus(HttpServletRequest request, @RequestBody RoleInputDAO roleInputDAO) {
		return roleService.updateRolePatternStatus(request, roleInputDAO);
	}

	/**
	 * get role and pattern details by role id
	 * @param roleId role id
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@GetMapping(value = "/getRolePatternUserDetails")
	public ResponseModel getRolePatternUserDetails(@RequestParam(name = "roleId", required = true) Long roleId,
			HttpServletRequest request) {
		return roleService.getRolePatternUserDetails(roleId, request);
	}

	/**
	 * get role and pattern details by role id for permission
	 * @param roleId role id
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@GetMapping(value = "/getRolePatternUserDetailsNew")
	public ResponseModel getRolePatternUserDetailsNew(@RequestParam(name="roleId",required=true) Long roleId,HttpServletRequest request) {
		return roleService.getRolePatternUserDetailsNew(roleId,request);
	}

	/**
	 * Get all active by id
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@GetMapping(value = "/getAllActiveById")
	public ResponseModel getAllActiveById(HttpServletRequest request) {
		try {
			return roleService.getAllActiveById(request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * get all default role
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@GetMapping(value = "/getAllDefault")
	public ResponseModel getAllDefault(HttpServletRequest request) {
		try {
			return roleService.getAllDefault(request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Update menu mapping by role
	 * @param roleInputDAO The RoleInputDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping(value = "/editRoleAssignedMenusByRole")
	public ResponseModel editRoleMenusByRole
	(@RequestBody RoleInputDAO roleInputDAO, HttpServletRequest request) {
		return roleService.editRoleMenus(roleInputDAO,request);
	}

	/**
	 * Get active user roles by assigned state
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@GetMapping(value = "/getActiveUserRolesByAssignState")
	public ResponseModel getActiveUserRolesByAssignState(HttpServletRequest request) {
		return roleService.getActiveUserRolesByAssignState(request);
	}

	/**
	 * Fetch territory level by role
	 * @param roleId role id
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@GetMapping("/getTerritoryLevelByRole")
	public ResponseModel getTerritoryLevelByRole(@RequestParam("role") Long roleId,HttpServletRequest request) {
		return roleService.getTerritoryLevelByRole(roleId);
	}
}
