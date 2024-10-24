package com.amnex.agristack.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.dao.OTPRequestDAO;
import com.amnex.agristack.dao.PaginationDao;
import com.amnex.agristack.dao.ResetPasswordDAO;
import com.amnex.agristack.dao.UserInputDAO;
import com.amnex.agristack.service.UserAuthService;
import com.amnex.agristack.service.UserService;
import com.amnex.agristack.service.UserVillageMappingService;
import com.amnex.agristack.utils.CustomMessages;

/**
 * Controller class for managing user details.
 */
@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private UserAuthService userAuthService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	UserVillageMappingService userVillageMappingService;

	
	/**
	 * Adds a new user.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing user input data.
	 * @return The ResponseModel object representing the response.
	 */
	@PostMapping(value = "/add")
	public ResponseModel login(HttpServletRequest request, @RequestBody UserInputDAO userInputDAO) {

		return userService.addStateUser(request, userInputDAO);

	}

	/**
	 * Updates a user.
	 *
	 * @param request  The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing user input data.
	 * @return The ResponseModel object representing the response.
	 */
	@PutMapping(value = "/update")
	public ResponseModel update(HttpServletRequest request, @RequestBody UserInputDAO userInputDAO) {

		return userService.updateUser(request, userInputDAO);

	}

	/**
	 * Retrieves a list of all active users.
	 *
	 * @param request The HttpServletRequest object.
	 * @return The ResponseModel object representing the response containing the list of active users.
	 */
	@GetMapping(value = "/getAllActiveUserList")
	public ResponseModel getAllActiveUserList(HttpServletRequest request) {

		return userService.getAllActiveUserList(request);

	}
	/**
	 * Retrieves a user by their ID.
	 *
	 * @param request The HttpServletRequest object.
	 * @param id The ID of the user to retrieve.
	 * @return The ResponseModel object representing the response containing the user.
	 */
	@GetMapping(value = "/getUserById/{id}")
	public ResponseModel getUserById(HttpServletRequest request, @PathVariable("id") Long id) {

		return userService.getUserById(request, id);

	}

	/**
	 * Retrieves all active users with pagination.
	 *
	 * @param request The HttpServletRequest object.
	 * @param paginationDao The PaginationDao object containing pagination parameters.
	 * @return The ResponseModel object representing the response containing the active users.
	 */
	@PostMapping(value = "/getAllActiveUserByPagination")
	public ResponseModel getAllActiveUserByPagination(HttpServletRequest request,
			@RequestBody PaginationDao paginationDao) {

		return userService.getAllActiveUserByPaginationNew(request, paginationDao);

	}

	/**
	 * Updates the status of a user.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing the user input data.
	 * @return The ResponseModel object representing the response after updating the status.
	 */
	@PutMapping(value = "/updateStatus")
	public ResponseModel updateStatus(HttpServletRequest request, @RequestBody UserInputDAO userInputDAO) {

		try {
			return userService.updateStatus(request, userInputDAO);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Checks if an email already exists in the system.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing the user input data.
	 * @return The ResponseModel object representing the response of the email existence check.
	 */
	@PostMapping(value = "/checkEmailExist")
	public ResponseModel checkEmailExist(HttpServletRequest request, @RequestBody UserInputDAO userInputDAO) {
		return userService.checkEmailExist(request, userInputDAO);

	}

	/**
	 * Checks if a mobile number already exists in the system.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing the user input data.
	 * @return The ResponseModel object representing the response of the mobile number existence check.
	 */
	@PostMapping(value = "/checkMobileExist")
	public ResponseModel checkMobileExist(HttpServletRequest request, @RequestBody UserInputDAO userInputDAO) {

		return userService.checkMobileExist(request, userInputDAO);

	}

	/**
	 * Checks if a government ID already exists in the system.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing the user input data.
	 * @return The ResponseModel object representing the response of the government ID existence check.
	 */
	@PostMapping(value = "/checkGovIdExists")
	public ResponseModel checkGovIdExists(HttpServletRequest request, @RequestBody UserInputDAO userInputDAO) {

		return userService.checkGovIdExists(request, userInputDAO);

	}

	/**
	 * Checks if an Aadhaar number already exists in the system.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing the user input data.
	 * @return The ResponseModel object representing the response of the Aadhaar existence check.
	 */
	@PostMapping(value = "/checkAadhaarExist")
	public ResponseModel checkAadhaarExist(HttpServletRequest request, @RequestBody UserInputDAO userInputDAO) {
		return userService.checkAadhaarExist(request, userInputDAO);

	}
	@PostMapping(value = "/checkBankAccountExist")
	public ResponseModel checkBankAccountExist(HttpServletRequest request, @RequestBody UserInputDAO userInputDAO) {
		return userService.checkBankAccountExist(request, userInputDAO);

	}

	/**
	 * Retrieves the accessible states based on user input.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing the user input data.
	 * @return The ResponseModel object representing the response of the accessible states retrieval.
	 */
	@PostMapping(value = "/getAccessibleStates")
	public ResponseModel getAccessibleStates(HttpServletRequest request, @RequestBody UserInputDAO userInputDAO) {

		return userService.getAccessibleStates(request, userInputDAO);

	}

	/**
	 * Retrieves the accessible districts based on user input.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing the user input data.
	 * @return The ResponseModel object representing the response of the accessible districts retrieval.
	 */
	@PostMapping(value = "/getAccessibleDistrict")
	public ResponseModel getAccessibleDistrict(HttpServletRequest request, @RequestBody UserInputDAO userInputDAO) {

		return userService.getAccessibleDistrict(request, userInputDAO);

	}

	/**
	 * Retrieves the accessible taluks based on user input.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing the user input data.
	 * @return The ResponseModel object representing the response of the accessible taluks retrieval.
	 */
	@PostMapping(value = "/getAccessibleTaluk")
	public ResponseModel getAccessibleTaluk(HttpServletRequest request, @RequestBody UserInputDAO userInputDAO) {

		return userService.getAccessibleTaluk(request, userInputDAO);

	}

	/**
	 * Retrieves the accessible villages based on user input.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing the user input data.
	 * @return The ResponseModel object representing the response of the accessible villages retrieval.
	 */
	@PostMapping(value = "/getAccessibleVillage")
	public ResponseModel getAccessibleVillage(HttpServletRequest request, @RequestBody UserInputDAO userInputDAO) {

		return userService.getAccessibleVillage(request, userInputDAO);

	}

	/**
	 * Changes the password for a user.
	 *
	 * @param resetPassword The ResetPasswordDAO object containing the reset password data.
	 * @param request The HttpServletRequest object.
	 * @return The ResponseModel object representing the response of the password change operation.
	 */
	@PostMapping(path = "/changePassword")
	public ResponseModel changePassword(@RequestBody ResetPasswordDAO resetPassword, HttpServletRequest request) {
		return userService.changePassword(resetPassword);
	}

	/**
	 * Sends an OTP (One-Time Password) to the user.
	 *
	 * @param otpRequestDAO The OTPRequestDAO object containing the OTP request data.
	 * @param request The HttpServletRequest object.
	 * @return The ResponseModel object representing the response of the OTP generation operation.
	 */
	@PostMapping("/requestOTP")
	ResponseModel sendOTP(@RequestBody OTPRequestDAO otpRequestDAO, HttpServletRequest request) {
		return userService.generateOTP(otpRequestDAO, request);
	}

	/**
	 * Verifies the OTP (One-Time Password) provided by the user.
	 *
	 * @param otpRequestDAO The OTPRequestDAO object containing the OTP verification data.
	 * @param request The HttpServletRequest object.
	 * @return The ResponseModel object representing the response of the OTP verification operation.
	 */
	@PostMapping("/verifyOTP")
	ResponseModel verifyOTP(@RequestBody OTPRequestDAO otpRequestDAO, HttpServletRequest request) {
		return userAuthService.verifyOTP(otpRequestDAO, request);
	}

	/**
	 * Updates the mobile contact details of a user.
	 *
	 * @param userId The ID of the user.
	 * @param mobile The new mobile number.
	 * @param request The HttpServletRequest object.
	 * @return The ResponseModel object representing the response of the mobile contact update operation.
	 */
	@PostMapping(value = "/mobileUpdate")
	public ResponseModel userContactUpdate(@RequestParam("userId") Long userId, @RequestParam("mobile") String mobile,
			HttpServletRequest request) {
		return userService.userContactDetailsUpdate(userId, mobile, request);
	}

	/**
	 * Updates the email contact details of a user.
	 *
	 * @param userId The ID of the user.
	 * @param email The new email address.
	 * @param request The HttpServletRequest object.
	 * @return The ResponseModel object representing the response of the email contact update operation.
	 */
	@PostMapping(value = "/emailUpdate")
	public ResponseModel userEmailUpdate(@RequestParam("userId") Long userId, @RequestParam("email") String email,
			HttpServletRequest request) {
		return userService.userEmailDetailsUpdate(userId, email, request);
	}

	/**
	 * Sets the default page for a user.
	 *
	 * @param userId The ID of the user.
	 * @param defaultPage The default page to be set.
	 * @param request The HttpServletRequest object.
	 * @return The ResponseModel object representing the response of the default page setting operation.
	 */
	@PostMapping(value = "/setDefaultPage")
	public ResponseModel setDefaultPage(@RequestParam("userId") Long userId,
			@RequestParam("defaultPage") String defaultPage, HttpServletRequest request) {
		return userService.setDefaultPage(userId, defaultPage, request);
	}

	/**
	 * Checks if an email already exists for a user identified by their user ID.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing the user ID and email to check.
	 * @return The ResponseModel object representing the result of the email existence check.
	 */
	@PostMapping(value = "/checkEmailExistByUserId")
	public ResponseModel checkEmailExistByUserId(HttpServletRequest request, @RequestBody UserInputDAO userInputDAO) {
		return userService.checkEmailExistByUserId(request, userInputDAO);

	}

	/**
	 * Checks if a mobile number already exists for a user identified by their user ID.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing the user ID and mobile number to check.
	 * @return The ResponseModel object representing the result of the mobile number existence check.
	 */
	@PostMapping(value = "/checkMobileExistByUserId")
	public ResponseModel checkMobileExistByUserId(HttpServletRequest request, @RequestBody UserInputDAO userInputDAO) {

		return userService.checkMobileExistByUserId(request, userInputDAO);

	}

	/**
	 * Checks if a government ID already exists for a user identified by their user ID.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing the user ID and government ID to check.
	 * @return The ResponseModel object representing the result of the government ID existence check.
	 */
	@PostMapping(value = "/checkGovIdExistByUserId")
	public ResponseModel checkGovIdExistByUserId(HttpServletRequest request, @RequestBody UserInputDAO userInputDAO) {

		return userService.checkGovIdExistByUserId(request, userInputDAO);

	}

	/**
	 * Checks if an Aadhaar number already exists for a user identified by their user ID.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing the user ID and Aadhaar number to check.
	 * @return The ResponseModel object representing the result of the Aadhaar number existence check.
	 */
	@PostMapping(value = "/checkAadhaarExistByUserId")
	public ResponseModel checkAadhaarExistByUserId(HttpServletRequest request, @RequestBody UserInputDAO userInputDAO) {
		return userService.checkAadhaarExistByUserId(request, userInputDAO);

	}

	/**
	 * Logs out the user by deleting their token.
	 *
	 * @param request The HttpServletRequest object.
	 * @return The ResponseModel object representing the result of the logout operation.
	 */
	@PostMapping("/logout")
	public ResponseModel logoutUser(HttpServletRequest request) {
		return userService.deleteToken(request);
	}

	/**
	 * Retrieves a list of all active users in plain format.
	 *
	 * @param request The HttpServletRequest object.
	 * @return The ResponseModel object containing the list of active users.
	 */
	@GetMapping(value = "/getAllActiveUserPlainList")
	public ResponseModel getAllActiveUserPlainList(HttpServletRequest request) {

		return userService.getAllActiveUserPlainList(request);

	}

	/**
	 * Creates a state admin user.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing the user input data.
	 * @return The ResponseModel object indicating the status of the operation.
	 */
	@PostMapping(value = "/addStateAdmin")
	public ResponseModel addStateAdmin(HttpServletRequest request, @RequestBody UserInputDAO userInputDAO) {
		return userService.createStateAdminUser(request, userInputDAO);
	}

	/**
	 * Retrieves the accessible boundary by a list of user inputs.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing the user input data.
	 * @return The ResponseModel object containing the accessible boundary information.
	 */
	@PostMapping(value = "/getAccessibleBoundaryByList")
	public ResponseModel getAccessibleBoundaryByList(HttpServletRequest request,
			@RequestBody UserInputDAO userInputDAO) {

		return userService.getAccessibleBoundaryByList(request, userInputDAO);

	}
	
	/**
	 * Retrieves the accessible boundary by a list of user inputs.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing the user input data.
	 * @return The ResponseModel object containing the accessible boundary information.
	 */
	@PostMapping(value = "/getAccessibleBoundaryByListNew")
	public ResponseModel getAccessibleBoundaryByListNew(HttpServletRequest request,
			@RequestBody UserInputDAO userInputDAO) {

		return userService.getAccessibleBoundaryByListNew(request, userInputDAO);

	}

	/**
	 * Retrieves territories by territory level and user.
	 *
	 * @param territoryLevel The territory level parameter.
	 * @param request The HttpServletRequest object.
	 * @return The ResponseModel object containing the territories information.
	 */
	@GetMapping(value = "/getTerritoriesByLevel")
	public ResponseModel getTerritoriesByTerritoryLevelAndUser(@RequestParam("territoryLevel") String territoryLevel,
			HttpServletRequest request) {
		try {
			return CustomMessages.makeResponseModel(
					userVillageMappingService.getTerritoriesByTerritoryLevelAndUser(territoryLevel, request),
					CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(
					e.getMessage(),
					CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * Retrieves the village assigned tree for a user by user ID.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing the user ID.
	 * @return The ResponseModel object containing the village assigned tree information.
	 */
	@PostMapping(value = "/getUserVillageAssignedTreeByUserId")
	public ResponseModel getUserVillageAssignedTreeByUserId(HttpServletRequest request,
			@RequestBody UserInputDAO userInputDAO) {
		return userService.getUserVillageAssignedTreeByUserId(request, userInputDAO);
	}

	/**
	 * Updates the token for a user.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing the user information and the new token.
	 * @return The ResponseModel object indicating the success or failure of the token update.
	 */
	@PutMapping(value = "/updateToken")
	public ResponseModel updateToken(HttpServletRequest request, @RequestBody UserInputDAO userInputDAO) {

		try {
			return userService.updateToken(request, userInputDAO);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves all accessible boundaries based on the provided list.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing the list of boundaries.
	 * @return The ResponseModel object containing the accessible boundaries.
	 */
	@PostMapping(value = "/getAllAccessibleBoundaryByList")
	public ResponseModel getAllAccessibleBoundaryByList(HttpServletRequest request,
			@RequestBody UserInputDAO userInputDAO) {

		return userService.getAllAccessibleBoundaryByList(request, userInputDAO);

	}

	/**
	 * Retrieves the verification survey sub-district by its ID.
	 *
	 * @param request The HttpServletRequest object.
	 * @param id The ID of the verification survey sub-district.
	 * @return The ResponseModel object containing the verification survey sub-district.
	 */
	@GetMapping(value = "/getVerficationSurveySubDistrictById/{id}")
	public ResponseModel getVerficationSurveySubDistrictById(HttpServletRequest request, @PathVariable("id") Long id) {

		return userService.getVerficationSurveySubDistrictById(request, id);

	}

	/**
	 * Downloads the user template based on the provided role, territory level, and LGD codes.
	 *
	 * @param role The ID of the role.
	 * @param territoryLevel The territory level.
	 * @param lgdCodes The list of LGD codes.
	 * @return The ResponseEntity object containing the user template file.
	 */
	@GetMapping("/downloadUserTemplate")
	public ResponseEntity<?> downloadUsers(@RequestParam(name = "role") Long role,
			@RequestParam(name = "territoryLevel") String territoryLevel,
			@RequestParam(name = "lgdCodes") List<Integer> lgdCodes) {
		return userService.downloadUsersByRoleAndTerritory(role, territoryLevel, lgdCodes);

	}

	/**
	 * Bulk uploads users to update their name, email, and mobile.
	 *
	 * @param file The MultipartFile object containing the user data.
	 * @param request The HttpServletRequest object.
	 * @return The ResponseModel object indicating the success of the bulk update.
	 */
	@PostMapping("/bulkUpdateUser")
	public ResponseModel bulkUpdateUsers(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
		return userService.bulkUpdateUser(file, request);

	}

	/**
	 * Adds a CR state admin user.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing the user details.
	 * @return The ResponseModel object indicating the success of adding the CR state admin user.
	 */
	@PostMapping(value = "/addCRStateAdmin")
	public ResponseModel addCRStateAdmin(HttpServletRequest request, @RequestBody UserInputDAO userInputDAO) {
		return userService.createCRStateAdminUser(request, userInputDAO);
	}

	
	/**
	 * Retrieves user details by role with pagination.
	 *
	 * @param request The HttpServletRequest object.
	 * @param paginationDao The PaginationDao object containing the pagination details.
	 * @return The ResponseModel object containing the user details.
	 */
	@PostMapping(value = "/getUserByRole")
	public ResponseModel getUserByRole(HttpServletRequest request, @RequestBody PaginationDao paginationDao) {
		return userService.getUserDetailByRole(request, paginationDao);
	}

	/**
	 * Resets the password of a user by their ID.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userId The ID of the user.
	 * @return The ResponseModel object indicating the success of resetting the password.
	 */
	@GetMapping(value = "/resetPasswordByMail")
	public ResponseModel resetPasswordByMail(HttpServletRequest request,@RequestParam("userId") Long userId) {
		return userService.resetPasswordByMail(userId,request);
	}
	
	/**
	 * Assign All Jurisdiction To User.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing user input data.
	 * @return The ResponseModel object representing the response.
	 */
	@PostMapping(value = "/assignAllJurisdictionToUser")
	public ResponseModel assignAllJurisdictionToUser(HttpServletRequest request,@RequestBody UserInputDAO userInputDAO) {
		return userService.assignAllJurisdictionToUser(request,userInputDAO);
	}
	
	
	/**
     * fetch user count.
     *
     * @param userType user type
     * @param request The HttpServletRequest Object
     * @return  The ResponseModel object representing the response.
     */
	@PostMapping(value = "/getUserCount")
	public ResponseModel getUserCount(HttpServletRequest request,@RequestBody String userType) {
		return userService.getUserCount(request,userType);
	}
	
	/**
	 * Retrieves the accessible boundary by a list of user inputs.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userInputDAO The UserInputDAO object containing the user input data.
	 * @return The ResponseModel object containing the accessible boundary information.
	 */
	@PostMapping(value = "/getAccessibleBoundaryByListV3")
	public ResponseModel getAccessibleBoundaryByListV3(HttpServletRequest request,
			@RequestBody UserInputDAO userInputDAO) {

		return userService.getAccessibleBoundaryByList(request, userInputDAO);

	}
	
	
	/**
     * fetch de active user.
     *
     * @return  The ResponseModel object representing the response.
     */
	@GetMapping(value = "/deActiveUser")
	public ResponseModel deActiveUser(HttpServletRequest request) {
		return userService.deActiveUser(request);
	}

}
