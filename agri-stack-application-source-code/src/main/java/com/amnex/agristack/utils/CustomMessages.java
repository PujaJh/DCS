package com.amnex.agristack.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.dao.*;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.FarmerRegistry;
import com.amnex.agristack.entity.MenuMaster;
import com.amnex.agristack.entity.RoleMaster;
import com.amnex.agristack.entity.RolePatternMapping;
import com.amnex.agristack.entity.UserMaster;
import com.google.gson.Gson;

import io.jsonwebtoken.ExpiredJwtException;

public class CustomMessages {

	public static final int INTERNAL_SERVER_ERROR = 500;
	public final static int GET_DATA_SUCCESS = 200;
	public final static int ALREADY_EXIST = 300;
	public final static int GET_DATA_ERROR = 400;
	public final static int NO_DATA_FOUND = 201;
	public final static int UNAUTHORIZED_ERROR = 401;
	public final static int OTP_SEND_SUCCESSFULLY = 1;
	public final static int UNSUPPORTED_MEDIA_TYPE = 415;

	public final static int MENU_ADD_SUCCESSFULLY = 202;
	public final static int INPUT_FIELD_REQUIRED = 101;
	public final static int MENU_CODE_UNIQUE = 102;

	public final static int ADD_SUCCESSFULLY = 301;
	public final static int UPDATE_SUCCESSFULLY = 302;
	public final static int DELETE_SUCCESSFULLY = 303;
	public final static int STATUS_UPDATED_SUCCESSFULLY = 304;

	public final static int FARMER_REGISTRY_ADD_SUCCESSFULLY = 310;
	 
	public final static String SUCCESS = "success";

	public final static String CONFIGURATION_ERROR = "Please add configuration first or contact to admin";

	public final static String FAILED = "failed";
	public final static String UNAUTHORIZED = "unauthorized";

	public final static String METHOD_GET = "GET";
	public final static String METHOD_POST = "POST";
	public final static String METHOD_PUT = "PUT";

	public final static String LOGIN_SUCCESS = "User login successfully.";
	public final static String LOGOUT_SUCCESS = "User logout successfully.";
	public final static String UPDATE_PASSWORD = "Password updated successfully.";
	public final static String FORGOT_SUCCESS = "Your password has been reset successfully and will be shared with you on your registered email address.";
	public final static String INVALID_PASSWORD = "Invalid username or password.";
	public final static String INVALID_PASSWORD2 = "Invalid password.";
	public final static String INVALID_CAPTCHA= "Invalid captcha.";
	
	public final static String FEW_PLOTS_EXIST = " No(s). of plots is already assign to other surveyor.";
	public final static String NOT_FOUND = " not found.";

	public final static String USER_NOT_FOUND = "User not found.";
	
	public final static String ACCOUNT_DOES_NOT_EXISTS = "Account does not exist";
	public final static String ACCOUNT_DOES_NOT_EXISTS_3 = "Account already exist with other user";


	public final static String ACCOUNT_DOES_NOT_EXISTS_2 = " If the Email/User is registered then the password reset link will be sent on your registered email.";

	public final static String INACTIVE_USER = "Inactive user.";

	public final static String USER_ID_NOT_FOUND = "User Id not found.";

	public final static String ROLE_NOT_FOUND = "Invalid role.";

	public final static String USER_NAME_EXIST = "User Name already exist.";

	public final static String USER_MOBILE_NAME_EXIST = "Mobile number is linked with another User.";
	public final static String GOV_ID_EXIST = "Government Id already exist.";

	public final static String USER_AADHAAR_EXIST = "Aadhaar is linked with another User.";

	public final static String USER_EMAIL_ADDRESS_EXIST = "Email is linked with another User.";

	public final static String USER_ADDED_SUCCESS = "User added successfully.";
	public final static String USER_UPDATED_SUCCESS = "User updated successfully.";
	public final static String UNAUTHORIZED_MESSAGE = "UNAUTHORIZED";

	public final static String ROLE_NAME_REQURIED = "Role name requried.";

	public final static String MENU_ADD_SUCCESS = "Menu added successfully.";
	public final static String MENU_UPDATE_SUCCESS = "Menu updated successfully.";


	public final static String FILE_UPLOAD_SUCCESS = "File uploaded successfully.";

	public final static String FILE_UPLOAD_INPROGRESS = "File uploading is inprogress.";
	
	public final static String FILE_UPLOAD_FAILED = "File upload failed.";

	public final static String FILE_UPLOAD_SIZE_LIMIT = "File size exceeds the maximum limit of 100MB";

	public final static String FILE_UPLOAD_INVALID_CHARACTERS = "Invalid characters found in filename";

	public final static String FILE_UPLOAD_CONTAINS_EXTENSION = "Filename contains double extension or double dots";
	
	public final static String FILE_EXTENSION_NOT_ALLOWED = "File extension not allowed.";

	public final static String FILE_UPLOAD_CONTAINS_NULL_BYTE = "Filename contains null byte";
	
	public final static String FILE_TYPE_NOT_ALLOWED = "File type not allowed.";

	public final static String FARMER_EMPTY_EXCEL = "Farmer Sheet is empty.";

	public final static String LAND_EMPTY_EXCEL = "Land Sheet is empty.";

	public final static String USER_EMPTY_EXCEL = "User Sheet is empty.";

	public final static String OCC_EMPTY_EXCEL = "Occupation Sheet is empty.";

	public final static String DISABILITY_EMPTY_EXCEL = "Disability Sheet is empty.";

	public final static String STATUS_UPDATED_SUCCESS = "Status updated successfully.";

	public final static String FAILURE = "Internal server error";

	public final static String INVALID_INPUT = "Invalid input.";

	public final static String DUPLICATE_UNIT_TYPE = "Duplicate unit type name";

	public final static String INVALID_AADHAAR = "Aadhaar number is invalid.";

	public final static String GET_RECORD = "Get records.";
	public final static String INACTIVE_USER_MESSAGE = "Inactive user successfully";
	public final static String TYPE_NOT_SUPPORTED = "Unsupported media type.";

	public final static String RECORD_ADD = " added successfully.";
	public final static String RECORD_UPDATE = " updated successfully.";
	public final static String RECORD_DELETE = " deleted successfully.";
	public final static String RECORD_ALREADY_EXIST = " already exist.";

	public final static String EXCEL_UPLAOD_SUCCESS = " Excel uploaded successfully.";

	public final static String INVALID_OTP = "Invalid OTP.";

	public final static String TASK_ASSIGNED = "Task Assigned successfully.";

	public final static String TASK_REMOVED = "Task Removed  successfully.";

	public final static String INVALID_FARMER_EXCEL_HEADER = "Invalid farmer details header format.";

	public final static String INVALID_LAND_EXCEL_HEADER = "Invalid land details header format.";

	public final static String INVALID_OCC_EXCEL_HEADER = "Invalid occupation details header format.";

	public final static String INVALID_DISABILITY_EXCEL_HEADER = "Invalid disability details header format.";

	public final static String INVALID_EXCEL_TEMPLATE = "Invalid excel template.";

	public final static String RESET_PWD_EMAIL_SENT_MSG = "Reset password sent on registred email.";
	
	public final static String PLOT_NOT_FOUND = "Plot not found!";
	
	public final static String PARCELID_REQUIRED = "Parcel Id is Required";
	
	public final static String USER_LAND_ASSIGN_NOT_FOUND = "User land assignment not found";

	public static final int LOGIN_SUCCESSFULLY = 200;

	public static final String GET_SURVEY_ACTIVITY_SUCCESSFULLY = "Survey activity configuration get successfully.";
	public static final String ADD_SURVEY_ACTIVITY_SUCCESSFULLY = "Survey activity configuration added successfully.";
	public static final String CONFIGURATION_NOT_FOUND = "Survey activity configuration not found.";
	public static final String UPDATE_SURVEY_ACTIVITY_SUCCESSFULLY = "Survey activity configuration updated successfully.";
	public static final String UPDATE_SURVEY_VERIFICATION_SUCCESSFULLY = "Survey verification updated successfully.";

	public static final String SURVEY_ACTIVITY_ALREADY_EXIST = "Survey activity configuration already exist.";


	public final static String WORK_FLOW_ADD_SUCCESS = "Work flow  added successfully.";
	public final static String WORK_FLOW_UPDATE_SUCCESS = "Work flow  updated successfully.";
	public final static String WORK_FLOW_ESCALATIONS_ADD_SUCCESS = "Work flow escalations  added successfully.";
	//public final static String WORK_FLOW_UPDATE_SUCCESS = "Work flow  updated successfully.";
	public final static String APPROVAL_WORK_FLOW_ADDED_SUCCESS = "Farmer status updated successfully.";
	public final static String DRAFT_CONFIGURATION_ADD_SUCCESS = "Draft configuration  added successfully.";
	public final static String DRAFT_CONFIGURATION_UPDATE_SUCCESS = "Draft configuration updated successfully.";

	public static final String GET_EARLY_LATE_VILLAGES_SUCCESSFULLY = "Village mapping get successfully.";
	public static final String ADD_EARLY_LATE_VILLAGES_SUCCESSFULLY = "Village mapping added successfully.";
	public static final String EARLY_LATE_VILLAGES_NOT_FOUND = "Village mapping not found.";
	public static final String UPDATE_EARLY_LATE_VILLAGES_SUCCESSFULLY = "Village mapping updated successfully.";

	public static final String DELETE_EARLY_LATE_VILLAGES_SUCCESSFULLY = "Village mapping deleted successfully.";

	public static final String EARLY_LATE_VILLAGES_ALREADY_EXIST = "Village mapping already exist.";

	public static final String OTP_VERIFY_SUCCESS = "OTP verify successfully";

	public static final String OTP_EXPIRED = "OTP has been expired.";

	public static final String INTERNAL_SERVER_ERROR_MESSAGE = "something went wrong.";

	public static final String SEASON_NAME_ALREADY_EXIST = "Season Name already exist.";

	public final static String WORK_FLOW_ALREDY_EXISTS = "Work flow with the name match score and work flow request type   already exists.";

	public final static String WORK_FLOW_ESCALATION_DELETED="Work flow escalation  removed successfully";
	public final static String NAME_MATCH_SCORE_ALREDY_EXISTS = "Name match configuration with the name match score range and interpretation name already exists or given name match score is overlapping.";

	public final static String VILLAGE_WISE_LAND_DETAILS_CONFIGURATION_ADD_SUCCESS = "Village wise land details entry mode configuration  update successfully.";
	public final static String VILLAGE_WISE_LAND_DETAILS_UPDATE_SUCCESS = "Draft configuration updated successfully.";

	public static final int CROP_UPDATE_SUCCESSFULLY = 311;
	public static final int CROP_ADDED_SUCCESSFULLY = 316;

	public static final int CROP_VARIETY_UPDATE_SUCCESSFULLY = 312;

	public static final int CROP_CATEGORY_UPDATE_SUCCESSFULLY = 313;

	public static final int SEASON_UPDATE_SUCCESSFULLY = 314;

	public static final int SEASON_ADDED_SUCCESSFULLY = 315;

	public static final int NO_RECORD_FOUND = 203;

	public static final String AVAILABILITY_ADDED = "Availability set successfully";

	public static final String ASSIGNED_DEFAULT_LAND_UNIT="Land Measurement Unit  is assigned successfully";

	public static final String COLUMN_ALREADY_EXISTS="Field name  already exists";
	
	public final static String SAME_USER_IMEI_EXISTS = " The IMEI number already exists with same user.";	
	
	public final static String IMEI_CHANGE_REQUEST_EXISTS = " Device change request is already generated with this Mobile number.";	
		
	public final static String ANOTHER_USER_IMEI_EXISTS = " The IMEI number already exists with another user.";	
		
	public final static String IMEI_EXISTS = " The IMEI number already exists.";	
		
	public final static String IMEI_NOT_NULL = " The IMEI number should not be null.";
	
	public final static String INVALID_IMEI_NUMBER = "Invalid IMEI number.";
	
//	public static final String Config_Key_Exist="Configuration Key already exists";
	public static final String CONFIG_KEY_EXIST="Configuration Key already exixts";
	
	public static final String CONFIG_CODE_EXIST = "Configuration Code already exists";

	public static final int CADASTRAL_DETAIL_ADDED_SUCCSSFULLY = 317;

	public static final String ONLY_PR_SURVEYOR_CAN_UPDATE_AVAILABILITY = "Only private surveyor can update availability";
	
	static Map<String, Object> msgWithData = new HashMap<String, Object>();

	public static String getMessageWithData(int value, Object data) {
		Map<String, Object> msgWithData = new HashMap<String, Object>();
		switch (value) {

		case OTP_SEND_SUCCESSFULLY:
			msgWithData = new HashMap<String, Object>();
			msgWithData.put("code", 200);
			msgWithData.put("status", "success");
			msgWithData.put("data", data);
			msgWithData.put("message", "OTP Send Successfully.");
			return new Gson().toJson(msgWithData);
		case GET_DATA_SUCCESS:
			msgWithData = new HashMap<String, Object>();
			msgWithData.put("code", 200);
			msgWithData.put("status", "success");
			msgWithData.put("data", data);
			msgWithData.put("method", "GET");
			msgWithData.put("message", "Ok.");
			return new Gson().toJson(msgWithData);
		case NO_DATA_FOUND:
			msgWithData.put("code", 200);
			msgWithData.put("status", "success");
			msgWithData.put("method", "GET");
			msgWithData.put("message", "No data Found For value : " + data);
			return new Gson().toJson(msgWithData);
		case MENU_ADD_SUCCESSFULLY:
			msgWithData.put("code", 200);
			msgWithData.put("status", "success");
			msgWithData.put("method", "POST");
			msgWithData.put("data", data);
			msgWithData.put("message", "Menu added successfully");
			return new Gson().toJson(msgWithData);
		case INPUT_FIELD_REQUIRED:
			msgWithData.put("code", 200);
			msgWithData.put("status", "success");
			msgWithData.put("method", "POST");
			msgWithData.put("data", "field " + data + "must required");
			msgWithData.put("message", "Menu added successfully");
			return new Gson().toJson(msgWithData);
		case MENU_CODE_UNIQUE:
			msgWithData.put("code", 200);
			msgWithData.put("status", "success");
			msgWithData.put("method", "POST");
			msgWithData.put("data", "Menu Code " + data + " already exists.");
			msgWithData.put("message", "Menu added successfully");
			return new Gson().toJson(msgWithData);

		case ADD_SUCCESSFULLY:
			msgWithData.put("code", 200);
			msgWithData.put("status", "success");
			msgWithData.put("method", "POST");
			msgWithData.put("data", data);
			msgWithData.put("message", "Added successfully");
			return new Gson().toJson(msgWithData);
		case UPDATE_SUCCESSFULLY:
			msgWithData.put("code", 200);
			msgWithData.put("status", "success");
			msgWithData.put("method", "POST");
			msgWithData.put("data", data);
			msgWithData.put("message", "Updated successfully");
			return new Gson().toJson(msgWithData);
		case DELETE_SUCCESSFULLY:
			msgWithData.put("code", 200);
			msgWithData.put("status", "success");
			msgWithData.put("method", "POST");
			msgWithData.put("data", data);
			msgWithData.put("message", "Deleted successfully");
			return new Gson().toJson(msgWithData);
		case STATUS_UPDATED_SUCCESSFULLY:
			msgWithData.put("code", 200);
			msgWithData.put("status", "success");
			msgWithData.put("method", "POST");
			msgWithData.put("data", data);
			msgWithData.put("message", "Status Updated successfully");
			return new Gson().toJson(msgWithData);
		case FARMER_REGISTRY_ADD_SUCCESSFULLY:
			msgWithData.put("code", 200);
			msgWithData.put("status", "success");
			msgWithData.put("method", "POST");
			msgWithData.put("data", data);
			msgWithData.put("message", " Farmer Registry Added successfully with Farmer Number "
					+ ((FarmerRegistry) data).getFarmerEnrollementNumber());
			return new Gson().toJson(msgWithData);
		case NO_RECORD_FOUND:
			msgWithData.put("code", 203);
			msgWithData.put("status", "success");
			msgWithData.put("method", "GET");
			msgWithData.put("message", "No data Found For value : " + data);
			return new Gson().toJson(msgWithData);
		case CROP_ADDED_SUCCESSFULLY:
			msgWithData.put("code", 200);
			msgWithData.put("status", "success");
			msgWithData.put("method", "POST");
			msgWithData.put("data", data);
			msgWithData.put("message", "Crop added successfully.");
			return new Gson().toJson(msgWithData);
		case CROP_UPDATE_SUCCESSFULLY:
			msgWithData.put("code", 200);
			msgWithData.put("status", "success");
			msgWithData.put("method", "POST");
			msgWithData.put("data", data);
			msgWithData.put("message", "Crop updated successfully.");
			return new Gson().toJson(msgWithData);
		case CROP_VARIETY_UPDATE_SUCCESSFULLY:
			msgWithData.put("code", 200);
			msgWithData.put("status", "success");
			msgWithData.put("method", "POST");
			msgWithData.put("data", data);
			msgWithData.put("message", "Crop variety updated successfully.");
			return new Gson().toJson(msgWithData);
		case CROP_CATEGORY_UPDATE_SUCCESSFULLY:
			msgWithData.put("code", 200);
			msgWithData.put("status", "success");
			msgWithData.put("method", "POST");
			msgWithData.put("data", data);
			msgWithData.put("message", "Crop category updated successfully.");
			return new Gson().toJson(msgWithData);
		case SEASON_UPDATE_SUCCESSFULLY:
			msgWithData.put("code", 200);
			msgWithData.put("status", "success");
			msgWithData.put("method", "POST");
			msgWithData.put("data", data);
			msgWithData.put("message", "Season updated successfully.");
			return new Gson().toJson(msgWithData);
		case SEASON_ADDED_SUCCESSFULLY:
			msgWithData.put("code", 200);
			msgWithData.put("status", "success");
			msgWithData.put("method", "POST");
			msgWithData.put("data", data);
			msgWithData.put("message", "Season added successfully.");
			return new Gson().toJson(msgWithData);
		case CADASTRAL_DETAIL_ADDED_SUCCSSFULLY:
			msgWithData.put("code", 200);
			msgWithData.put("status", "success");
			msgWithData.put("method", "POST");
			msgWithData.put("data", data);
			msgWithData.put("message", "Cadastral detail added successfully.");
			return new Gson().toJson(msgWithData);
		default:
			return null;
		}

	}

	public static String getMessage(int value) {
		Map<String, Object> msg = new HashMap<String, Object>();
		switch (value) {

		case INTERNAL_SERVER_ERROR:
			msg.put("code", 500);
			msg.put("status", "failed");
			msg.put("message", "Internal server error.");
			return new Gson().toJson(msg);

		default:
			return null;
		}
	}

	private static String setResponseMessage(Integer responseCode, String status, Object data, String responseMessage,
			String method) {
		Map<String, Object> msgWithData = new HashMap<String, Object>();
		msgWithData.put("code", responseCode);
		msgWithData.put("status", status);
		msgWithData.put("data", data);
		msgWithData.put("method", method);
		msgWithData.put("message", responseMessage);
		return new Gson().toJson(msgWithData);
	}

	private static String setResponseMessage(Integer responseCode, String status, String responseMessage,
			String method) {
		Map<String, Object> msgWithData = new HashMap<String, Object>();
		msgWithData.put("code", responseCode);
		msgWithData.put("status", status);
		msgWithData.put("message", responseMessage);
		msgWithData.put("method", method);
		return new Gson().toJson(msgWithData);
	}

	public static ResponseModel makeResponseModel(Object data, String message, int code, String status) {
		ResponseModel responseModel = new ResponseModel();
		responseModel.setData(data);
		responseModel.setMessage(message);
		responseModel.setCode(code);
		responseModel.setStatus(status);
		return responseModel;
	}

	public static UserOutputDAO returnUserOutputDAO(UserMaster userMaster) {
		UserOutputDAO userOutputDAO = new UserOutputDAO();

		userOutputDAO.setUserId(userMaster.getUserId());
		userOutputDAO.setUserName(userMaster.getUserName());
		userOutputDAO.setUserToken(userMaster.getUserToken());
		userOutputDAO.setUserFirstName(userMaster.getUserFirstName());

		userOutputDAO.setUserLastName(userMaster.getUserLastName());

		userOutputDAO.setUserFullName(userMaster.getUserFullName());

		GeographicalAreaOutputDAO geographicalAreaOutputDAO = new Gson().fromJson(userMaster.getGeographicalArea(),
				GeographicalAreaOutputDAO.class);
		userMaster.setGeographicalAreaOutputDAO(geographicalAreaOutputDAO);

		if(geographicalAreaOutputDAO!=null) {

			if (userMaster.getGeographicalAreaOutputDAO().getState() != null) {
				List<UserStateDAO> userStates = userMaster.getGeographicalAreaOutputDAO().getState();
				List<Long> stateLGDCodes = userStates.stream().distinct().map(UserStateDAO::getStateLgdCode)
						.collect(Collectors.toList());
				userOutputDAO.setAssignStateLGDcodes(stateLGDCodes);
			}
		}

		if (userMaster.getUserCountryLGDCode() != null) {
			userOutputDAO.setUserCountryLGDCode(userMaster.getUserCountryLGDCode());
		}

		if (userMaster.getUserStateLGDCode() != null) {
			userOutputDAO.setUserStateLGDCode(userMaster.getUserStateLGDCode());
		}

		if (userMaster.getUserDistrictLGDCode() != null) {
			userOutputDAO.setUserDistrictLGDCode(userMaster.getUserDistrictLGDCode());
		}

		if (userMaster.getUserTalukaLGDCode() != null) {

			userOutputDAO.setUserTalukaLGDCode(userMaster.getUserTalukaLGDCode());

		}

		if (userMaster.getUserVillageLGDCode() != null) {
			userOutputDAO.setUserVillageLGDCode(userMaster.getUserVillageLGDCode());
		}

		userOutputDAO.setUserAadhaarHash(userMaster.getUserAadhaarHash());

		userOutputDAO.setUserAadhaarVaultRefCentral(userMaster.getUserAadhaarVaultRefCentral());

		userOutputDAO.setUserRejectionReason(userMaster.getUserRejectionReason());

		userOutputDAO.setUserMobileNumber(userMaster.getUserMobileNumber());

		userOutputDAO.setUserAlternateMobileNumber(userMaster.getUserAlternateMobileNumber());

		userOutputDAO.setUserEmailAddress(userMaster.getUserEmailAddress());

		userOutputDAO.setIsPasswordChanged(userMaster.getIsPasswordChanged());

		userOutputDAO.setLastPasswordChangedDate(userMaster.getLastPasswordChangedDate());

		userOutputDAO.setDefaultPage(userMaster.getDefaultPage());

		if (userMaster.getRoleId() != null) {
			RoleOutputDAO roleOutputDAO = returnUserOutputDAO(userMaster.getRoleId());
			if (userMaster.getRoleId().getMenu() != null && userMaster.getRoleId().getMenu().size() > 0) {
				List<MenuOutputDAO> menuList = new ArrayList<>();
				List<MenuMaster> menuLists = userMaster.getRoleId().getMenu().stream().filter(ele -> ele.getIsActive().equals(Boolean.TRUE) && ele.getIsDeleted().equals(Boolean.FALSE)).collect(Collectors.toList());
				menuLists.forEach(action -> {
					menuList.add(returnUserOutputDAO(action));
				});
				roleOutputDAO.setMenuList(menuList);

			}

			userOutputDAO.setRole(roleOutputDAO);
		}

		if(userMaster.getRolePatternMappingId() != null) {
			userOutputDAO.setTerritoryLevel(userMaster.getRolePatternMappingId().getTerritoryLevel());
		}

		return userOutputDAO;
	}

	public static MenuOutputDAO returnUserOutputDAO(MenuMaster menuMaster) {

		MenuOutputDAO menuOutputDAO = new MenuOutputDAO();
		menuOutputDAO.setDisplaySrNo(menuMaster.getDisplaySrNo());
		menuOutputDAO.setMenuDescription(menuMaster.getMenuDescription());
		menuOutputDAO.setMenuId(menuMaster.getMenuId());
		menuOutputDAO.setMenuName(menuMaster.getMenuName());
		menuOutputDAO.setMenuParentId(menuMaster.getMenuParentId());
		menuOutputDAO.setMenuUrl(menuMaster.getMenuUrl());
		menuOutputDAO.setMenuIcon(menuMaster.getMenuIcon());
		menuOutputDAO.setMenuLevel(menuMaster.getMenuLevel());
		if(Objects.nonNull(menuMaster.getProjectType())) {
			List<String> projectTypeList = Arrays.asList(menuMaster.getProjectType().split(","));
			List<String> projectType = projectTypeList.stream()
					.map(String::valueOf)
					.collect(Collectors.toList());
			menuOutputDAO.setProjectType(projectType);
		}
		return menuOutputDAO;
	}

	public static RoleOutputDAO returnUserOutputDAO(RoleMaster roleMaster) {
		RoleOutputDAO roleOutputDAO = new RoleOutputDAO();
		roleOutputDAO.setRoleId(roleMaster.getRoleId());
		roleOutputDAO.setRoleName(roleMaster.getRoleName());
		roleOutputDAO.setRoleCode(roleMaster.getCode());
		roleOutputDAO.setCreatedOn(roleMaster.getCreatedOn());
		roleOutputDAO.setModifiedOn(roleMaster.getModifiedOn());
		roleOutputDAO.setCreatedBy(roleMaster.getCreatedBy());
		roleOutputDAO.setModifiedBy(roleMaster.getModifiedBy());
		roleOutputDAO.setCreatedIp(roleMaster.getCreatedIp());
		roleOutputDAO.setModifiedIp(roleMaster.getModifiedIp());

		roleOutputDAO.setIsActive(roleMaster.getIsActive());
		roleOutputDAO.setIsDeleted(roleMaster.getIsDeleted());
		return roleOutputDAO;
	}

	public static String getUserId(HttpServletRequest request, JwtTokenUtil jwtTokenUtil) {

		String requestTokenHeader = request.getHeader("Authorization");
		String userId = null;

		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {

			String jwtToken = requestTokenHeader.substring(7);

			try {

				userId = jwtTokenUtil.getUsernameFromToken(jwtToken);

			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return userId;
				// logger.info("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
				e.printStackTrace();
				return userId;
				// logger.info("JWT Token has expired");
			} catch (Exception e) {
				e.printStackTrace();
				return userId;
				// logger.info("JWT Token has expired" + e);
			}

		} else {

			// logger.warning("JWT Token does not begin with Bearer String");
		}
		return userId;

	}

	public static UserMaster returnUserMasterFromUserInputDAO(UserInputDAO userInputDAO) {
		UserMaster userMaster = new UserMaster();
		userMaster.setUserName(userInputDAO.getUserName());
		userMaster.setUserEmailAddress(userInputDAO.getUserEmailAddress());
		userMaster.setUserMobileNumber(userInputDAO.getUserMobileNumber());
		return userMaster;
	}

	public static UserMaster returnUserMasterFromUserInputDAOForMultipleStateUser(UserInputDAO userInputDAO) {
		UserMaster userMaster = new UserMaster();
		userMaster.setUserName(userInputDAO.getUserName());
		userMaster.setUserFullName(userInputDAO.getUserName());
		userMaster.setUserEmailAddress(userInputDAO.getUserEmailAddress());
		userMaster.setUserMobileNumber(userInputDAO.getUserMobileNumber());
		return userMaster;
	}

	public static UserMaster returnUserMasterFromUserInputDAOForStateUser(UserInputDAO userInputDAO, String userId) {
		UserMaster userMaster = new UserMaster();
		userMaster.setUserName(userId);
		userMaster.setUserFullName(userInputDAO.getUserName());
		userMaster.setUserStateLGDCode(userInputDAO.getGeographicalArea().get(0).getStateLgdCode().intValue());
		userMaster.setUserEmailAddress(userInputDAO.getUserEmailAddress());
		userMaster.setUserMobileNumber(userInputDAO.getUserMobileNumber());
		return userMaster;
	}

	// public static List<UserStateDAO> returnUserStateDAO(List<String>
	// statelgdCodes) {
	// List<UserStateDAO> userStateDAOs = new ArrayList<UserStateDAO>();
	// statelgdCodes.forEach(action -> {
	// UserStateDAO userStateDAO = new UserStateDAO();
	// userStateDAO.setStateLgdCode(action);
	// userStateDAOs.add(userStateDAO);
	// });
	// return userStateDAOs;
	// }

	public static RoleOutputDAO returnRolePatternMappingOutputDAO(RolePatternMapping rolePatternMapping) {
		RoleOutputDAO roleOutputDAO = new RoleOutputDAO();
		//		roleOutputDAO.setRoleId(roleMaster.getRoleId());
		roleOutputDAO.setRoleName(rolePatternMapping.getRole().getRoleName());
		roleOutputDAO.setTerritoryLevel(rolePatternMapping.getTerritoryLevel());
		roleOutputDAO.setNoOfUsers(rolePatternMapping.getNoOfUser());

		roleOutputDAO.setCreatedOn(rolePatternMapping.getCreatedOn());
		roleOutputDAO.setModifiedOn(rolePatternMapping.getModifiedOn());
		roleOutputDAO.setCreatedBy(rolePatternMapping.getCreatedBy());
		roleOutputDAO.setModifiedBy(rolePatternMapping.getModifiedBy());
		roleOutputDAO.setCreatedIp(rolePatternMapping.getCreatedIp());
		roleOutputDAO.setModifiedIp(rolePatternMapping.getModifiedIp());

		roleOutputDAO.setIsActive(rolePatternMapping.getIsActive());
		roleOutputDAO.setIsDeleted(rolePatternMapping.getIsDeleted());
		return roleOutputDAO;
	}

	public static RoleMaster addRoleMasterDefault(String action, String upperCase) {
		// TODO Auto-generated method stub
		RoleMaster roleMaster=new RoleMaster();
		roleMaster.setRoleName(action);
		roleMaster.setCode(upperCase);
		switch(upperCase) {
		case "SUPERADMIN":
			roleMaster.setPrefix("SPADM");
			break;

		case "SURVEYOR":
			roleMaster.setPrefix("SUR");
			break;
		case "STATEADMIN":
			roleMaster.setPrefix("SADM");
			break;
		case "VERIFIER":
			roleMaster.setPrefix("VER");
			break;
		case "FARMER":
			roleMaster.setPrefix("FAR");
			break;

		case "CR_STATE_ADMIN":
			roleMaster.setPrefix("CAD");
			break;
		}
		roleMaster.setIsActive(true);
		roleMaster.setIsDeleted(false);
		roleMaster.setIsDefault(true);
		return roleMaster;
	}

	public static RoleMaster addRoleMasterDefault(Long i,String action, String upperCase) {
		// TODO Auto-generated method stub
		RoleMaster roleMaster=new RoleMaster();
		roleMaster.setRoleId(i);
		roleMaster.setRoleName(action);
		roleMaster.setCode(upperCase);
		switch(upperCase) {
		case "SUPERADMIN":
			roleMaster.setPrefix("SPADM");
			break;

		case "SURVEYOR":
			roleMaster.setPrefix("SUR");
			break;
		case "STATEADMIN":
			roleMaster.setPrefix("SADM");
			break;
		case "VERIFIER":
			roleMaster.setPrefix("VER");
			break;

		}

		roleMaster.setIsActive(true);
		roleMaster.setIsDeleted(false);
		roleMaster.setIsDefault(true);
		return roleMaster;
	}


}
