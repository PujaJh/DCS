package com.amnex.agristack.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.amnex.agristack.Enum.ChildMenuEnum;
import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.dao.MenuOutputDAO;
import com.amnex.agristack.dao.PatternDAO;
import com.amnex.agristack.dao.RoleInputDAO;
import com.amnex.agristack.dao.RoleOutputDAO;
import com.amnex.agristack.dao.UserOutputDAO;
import com.amnex.agristack.entity.DepartmentMaster;
import com.amnex.agristack.entity.DistrictLgdMaster;
import com.amnex.agristack.entity.MenuMaster;
import com.amnex.agristack.entity.RoleMaster;
import com.amnex.agristack.entity.RoleMenuMasterMapping;
import com.amnex.agristack.entity.RolePatternMapping;
import com.amnex.agristack.entity.StateLgdMaster;
import com.amnex.agristack.entity.SubDistrictLgdMaster;
import com.amnex.agristack.entity.UserMaster;
import com.amnex.agristack.entity.UserVillageMapping;
import com.amnex.agristack.entity.VillageLgdMaster;
import com.amnex.agristack.repository.DepartmentRepository;
import com.amnex.agristack.repository.DistrictLgdMasterRepository;
import com.amnex.agristack.repository.MenuMasterRepository;
import com.amnex.agristack.repository.RoleMasterRepository;
import com.amnex.agristack.repository.RoleMenuMasterMappingRepository;
import com.amnex.agristack.repository.RolePatternMappingRepository;
import com.amnex.agristack.repository.StateLgdMasterRepository;
import com.amnex.agristack.repository.SubDistrictLgdMasterRepository;
import com.amnex.agristack.repository.UserMasterRepository;
import com.amnex.agristack.repository.UserVillageMappingRepository;
import com.amnex.agristack.repository.VillageLgdMasterRepository;
import com.amnex.agristack.utils.CommonUtil;
import com.amnex.agristack.utils.CustomMessages;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

@Service
public class RoleService {

	@Autowired
	private MenuMasterRepository menuMasterRepository;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private RoleMasterRepository roleMasterRepository;

	@Autowired
	private RolePatternMappingRepository rolePatternMappingRepository;

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private StateLgdMasterRepository stateLgdMasterRepository;

	@Autowired
	private DistrictLgdMasterRepository districtLgdMasterRepository;

	@Autowired
	private SubDistrictLgdMasterRepository subDistrictLgdMasterRepository;

	@Autowired
	private VillageLgdMasterRepository villageLgdMasterRepository;

	@Autowired
	private GeneralService generalService;

	@Autowired
	private UserMasterRepository userMasterRepository;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private UserVillageMappingRepository userVillageMappingRepository;

	@Autowired
	private UserService userService;
	@Autowired
	private RoleMenuMasterMappingRepository roleMenuMasterMappingRepository;

	/**
	 * Add or update role
	 * @param roleInputDAO {@link RoleInputDAO}
	 * @param request
	 * @return {@code RoleMenuMasterMapping}
	 */
	public ResponseModel addAndEditRole(RoleInputDAO roleInputDAO, HttpServletRequest request) {
		ResponseModel responseModel = null;
		try {
			List<RoleMenuMasterMapping> finalList = new ArrayList<RoleMenuMasterMapping>();
			if (Objects.nonNull(roleInputDAO.getRoleName())) {
				RoleMaster role = new RoleMaster();
				/* Commented because role was not working below *\
				 * 
				 */
//				if (roleInputDAO.getRoleId() != null) {
//					Optional<RoleMaster> roleOp = roleMasterRepository.findByIsDeletedAndRoleId(Boolean.FALSE,
//							roleInputDAO.getRoleId());
//					if (roleOp.isPresent()) {
//						role = roleOp.get();
//					}
//				}
				BeanUtils.copyProperties(roleInputDAO, role);

				role.setRoleName(roleInputDAO.getRoleName());
				role.setIsActive(true);
				role.setIsDeleted(false);
				Optional<UserMaster> loggedUser = userMasterRepository.findByUserId(roleInputDAO.getUserId());
				if (loggedUser.get().getRoleId().getCode().equals("SUPERADMIN")) {
					role.setIsDefault(true);
					role.setPrefix(role.getPrefix().trim().toUpperCase());
				} else {
					role.setIsDefault(false);
				}
				role.setCode(role.getRoleName().trim().toUpperCase());
				role.setCreatedOn(new Timestamp(new Date().getTime()));
				role.setModifiedOn(new Timestamp(new Date().getTime()));
				role.setCreatedBy(CustomMessages.getUserId(request, jwtTokenUtil));
				role.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
				role.setCreatedIp(CommonUtil.getRequestIp(request));
				role.setModifiedIp(CommonUtil.getRequestIp(request));

				roleMasterRepository.save(role);
				
				if (roleInputDAO.getMenuIds() != null && roleInputDAO.getMenuIds().size() > 0) {
					Set<MenuMaster> menuList = menuMasterRepository
							.findByIsActiveTrueAndIsDeletedFalseAndMenuIdIn(roleInputDAO.getMenuIds());
					Set<Long> ids = menuList.stream().map(x -> x.getMenuId()).collect(Collectors.toSet());

					roleInputDAO.getMenuIds().removeAll(ids);
					role.setMenu(menuList);

					if (roleInputDAO.getMenuIds() != null && roleInputDAO.getMenuIds().size() > 0) {
						
						menuList.forEach(action -> {
							RoleMenuMasterMapping roleMenuMasterMapping = new RoleMenuMasterMapping();
							roleMenuMasterMapping.setMenu(action);
							roleMenuMasterMapping.setRole(role);
							roleMenuMasterMapping.setIsActive(true);
							roleMenuMasterMapping.setIsDeleted(false);
							roleMenuMasterMapping.setCreatedOn(new Timestamp(new Date().getTime()));
							roleMenuMasterMapping.setCreatedBy(CustomMessages.getUserId(request, jwtTokenUtil));
							roleMenuMasterMapping.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
							roleMenuMasterMapping.setCreatedIp(CommonUtil.getRequestIp(request));
							roleMenuMasterMapping.setModifiedIp(CommonUtil.getRequestIp(request));
							roleMenuMasterMapping.setIsAdd(false);
							roleMenuMasterMapping.setIsEdit(false);
							roleMenuMasterMapping.setIsView(false);
							roleMenuMasterMapping.setIsDelete(false);
							finalList.add(roleMenuMasterMapping);
						});

						roleInputDAO.getMenuIds().forEach(action -> {

							finalList.forEach(action2 -> {

								Long idAdd = Long.sum(action2.getMenu().getMenuId(), ChildMenuEnum.Add.getValue());
								Long idEdit = Long.sum(action2.getMenu().getMenuId(), ChildMenuEnum.Edit.getValue());
								Long idDelete = Long.sum(action2.getMenu().getMenuId(),
										ChildMenuEnum.Delete.getValue());
								Long idView = Long.sum(action2.getMenu().getMenuId(), ChildMenuEnum.View.getValue());

								if (idAdd.equals(action)) {
									action2.setIsAdd(true);
									
								} else if (idEdit.equals(action)) {
									action2.setIsEdit(true);
									
								} else if (idDelete.equals(action)) {
									action2.setIsDelete(true);

								} else if (idView.equals(action)) {
									action2.setIsView(true);

								}
							});
						});
						if (finalList != null && finalList.size() > 0) {
							roleMenuMasterMappingRepository.saveAll(finalList);
						}

					}

				}
				responseModel = CustomMessages.makeResponseModel(finalList, CustomMessages.MENU_ADD_SUCCESS,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			} else {
				responseModel = CustomMessages.makeResponseModel(null, CustomMessages.ROLE_NAME_REQURIED,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			}

			return responseModel;
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * get all active roles
	 * @param request
	 * @return {@code RoleMaster}
	 */
	public ResponseModel getAllActive(HttpServletRequest request) {
		ResponseModel responseModel = null;
		try {

			List<RoleOutputDAO> returnList = new ArrayList();
			List<RoleMaster> roleList = roleMasterRepository.findByIsDeletedAndIsDefault(Boolean.FALSE, Boolean.FALSE);
			if (roleList != null && roleList.size() > 0) {
				roleList.forEach(action -> {
					returnList.add(CustomMessages.returnUserOutputDAO(action));
				});

			}
			responseModel = CustomMessages.makeResponseModel(roleList, CustomMessages.MENU_ADD_SUCCESS,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			return responseModel;
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * get all default role
	 * @return {@code RoleOutputDAO}
	 */
	public ResponseModel getAllDefault(HttpServletRequest request) {
		ResponseModel responseModel = null;
		try {

			List<RoleOutputDAO> returnList = new ArrayList();
			List<RoleMaster> roleList = roleMasterRepository.findByIsDeletedAndIsDefault(Boolean.FALSE, Boolean.TRUE);
			if (roleList != null && roleList.size() > 0) {
				roleList.forEach(action -> {
//					returnList.add(CustomMessages.returnUserOutputDAO(action));
					RoleOutputDAO roleOutput = CustomMessages.returnUserOutputDAO(action);
					roleOutput.setCreatedIp(null);
					roleOutput.setModifiedIp(null);
					returnList.add(roleOutput);
				});

			}
			responseModel = CustomMessages.makeResponseModel(returnList, CustomMessages.MENU_ADD_SUCCESS,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			return responseModel;
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Update role status
	 * @param request
	 * @param roleInputDAO {@link RoleInputDAO}
	 * @return the update status of role
	 */
	public ResponseModel updateStatus(HttpServletRequest request, RoleInputDAO roleInputDAO) {
		ResponseModel responseModel = null;
		try {

			Optional<RoleMaster> opRole = roleMasterRepository.findByIsDeletedAndRoleId(Boolean.FALSE,
					roleInputDAO.getRoleId());
			if (opRole.isPresent()) {
				RoleMaster roleMaster = opRole.get();
				roleMaster.setIsActive(roleInputDAO.getIsActive());
				roleMaster.setIsDeleted(roleInputDAO.getIsDeleted());
				roleMasterRepository.save(roleMaster);
			}
			responseModel = CustomMessages.makeResponseModel(null, CustomMessages.STATUS_UPDATED_SUCCESS,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			return responseModel;
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Create role along with users and it's boundary
	 * @param roleInputDAO {@code RoleInputDAO}
	 * @param request
	 * @return {@code RoleOutputDAO}
	 */
	public ResponseModel addRoleWithPrefix(RoleInputDAO roleInputDAO, HttpServletRequest request) {
		ResponseModel responseModel = null;
		try {
			Long userId = new Long(CustomMessages.getUserId(request, jwtTokenUtil));
			List<Long> stateLgdCodes = userVillageMappingRepository.getStateCodesById(userId);
			roleInputDAO.setAssignStateLgdcode(stateLgdCodes.get(0));
			if (Objects.nonNull(roleInputDAO.getRoleName())) {
				RoleMaster role = new RoleMaster();
				role.setRoleName(roleInputDAO.getRoleName());
				role.setIsActive(true);
				role.setIsDeleted(false);
				role.setIsDefault(false);
				role.setCode(role.getRoleName().trim().toUpperCase());
				role.setCreatedOn(new Timestamp(new Date().getTime()));
				role.setModifiedOn(new Timestamp(new Date().getTime()));
				role.setCreatedBy(CustomMessages.getUserId(request, jwtTokenUtil));
				role.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
				role.setCreatedIp(CommonUtil.getRequestIp(request));
				role.setModifiedIp(CommonUtil.getRequestIp(request));
				role.setPrefix(roleInputDAO.getPrefix().trim().toUpperCase());
				role.setStateLgdCode(roleInputDAO.getAssignStateLgdcode());

				if (roleInputDAO.getMenuIds() != null && roleInputDAO.getMenuIds().size() > 0) {
					Set<MenuMaster> menuList = menuMasterRepository
							.findByIsActiveTrueAndIsDeletedFalseAndMenuIdIn(roleInputDAO.getMenuIds());
					role.setMenu(menuList);
				}
				RoleMaster roleMaster = roleMasterRepository.save(role);
//				System.out.println("ROle created.." + roleMaster.getRoleId());

				// Add role and pattern details into mapping table
				List<RoleOutputDAO> users = new ArrayList<>();
				if (roleMaster != null) {

					for (PatternDAO patternDAO : roleInputDAO.getPatternDAO()) {
						RolePatternMapping rolePatternMapping = new RolePatternMapping();
						Optional<DepartmentMaster> department = departmentRepository
								.findById(patternDAO.getDepartmentId());
						rolePatternMapping.setDepartment(department.get());
						rolePatternMapping.setTerritoryLevel(patternDAO.getTerritoryLevel());
						rolePatternMapping.setRole(role);
						rolePatternMapping.setNoOfUser(patternDAO.getNoOfUser());
						rolePatternMapping.setTotalNoOfUser(patternDAO.getTotalNoOfUser());
						rolePatternMapping.setAssignStateLgdCode(roleInputDAO.getAssignStateLgdcode());
						rolePatternMapping.setIsActive(true);
						rolePatternMapping.setIsDeleted(false);
						rolePatternMapping.setCreatedOn(new Timestamp(new Date().getTime()));
						rolePatternMapping.setModifiedOn(new Timestamp(new Date().getTime()));
						rolePatternMapping.setCreatedBy(CustomMessages.getUserId(request, jwtTokenUtil));
						rolePatternMapping.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
						rolePatternMapping.setCreatedIp(CommonUtil.getRequestIp(request));
						rolePatternMapping.setModifiedIp(CommonUtil.getRequestIp(request));
						rolePatternMappingRepository.save(rolePatternMapping);
						System.out.println("Role mapping added..");
						// Generate users
						users.addAll(generateUserNameUsingPattern(rolePatternMapping, roleInputDAO));
//						System.out.println("user name created....");
					}
					responseModel = CustomMessages.makeResponseModel(users, "Pattern " + CustomMessages.RECORD_ADD,
							CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);

				} else {
					responseModel = CustomMessages.makeResponseModel(null, CustomMessages.ROLE_NAME_REQURIED,
							CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
				}
			}
			return responseModel;
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	public List<RoleOutputDAO> generateUserNameUsingPattern(RolePatternMapping rolePatternMapping,
			RoleInputDAO roleInputDAO) {

		List<RoleOutputDAO> output = new ArrayList<>();

		if (rolePatternMapping.getTerritoryLevel().equals("state")) {
			List<Long> assignStateLGDCodes = getAccessibleStateUM(roleInputDAO.getUserId());
			List<UserMaster> userList = new ArrayList<>();
			JSONArray userJsonArray = new JSONArray();
			// Checking for next serial number if exists any
			List<UserMaster> userSerialNumber = userMasterRepository
					.findByDepartmentIdAndRoleIdAndRolePatternMappingId_TerritoryLevelOrderByUserIdDesc( // isactive
							rolePatternMapping.getDepartment(), rolePatternMapping.getRole(), "state");

			String[] userNameArr = userSerialNumber.size() > 0 ? userSerialNumber.get(0).getUserName().split("_", 5)
					: null;
			Long serialNumber = (userSerialNumber.size() > 0 && userNameArr.length > 0) ? Long.parseLong(userNameArr[2])
					: 0;

			if (userSerialNumber.size() == 0) {
				for (int i = 0; i < rolePatternMapping.getNoOfUser(); i++) {
					serialNumber += 1;

					for (Long state : assignStateLGDCodes) {
						// Get Serial number
						String userId = generalService.generateUserNameUsingPattern(
								rolePatternMapping.getRole().getPrefix(),
								rolePatternMapping.getDepartment().getDepartmentCode(), serialNumber, "S", state);

						UserMaster user1 = new UserMaster();
						user1.setUserName(userId);
						user1.setUserPassword(encoder.encode("Admin@123"));
						user1.setRoleId(rolePatternMapping.getRole());
						user1.setDepartmentId(rolePatternMapping.getDepartment());
						user1.setUserStateLGDCode(state.intValue());
						user1.setCreatedBy(roleInputDAO.getUserId().toString());
						user1.setModifiedBy(roleInputDAO.getUserId().toString());
						user1.setIsActive(true);
						user1.setIsDeleted(false);
						user1.setIsPasswordChanged(false);
						user1.setLastPasswordChangedDate(new Date());
						user1.setRolePatternMappingId(rolePatternMapping);
						setUserPasswordHistory(user1);
						userList.add(user1);
					}
				}
				output = returnUserNamePatternOutputDAO(userMasterRepository.saveAll(userList));

				
				
			} else if (userSerialNumber.size() == rolePatternMapping.getTotalNoOfUser()) {
				// if user count and update count is same
				List<Long> userIds = userSerialNumber.stream().map(UserMaster::getUserId).collect(Collectors.toList());
				userMasterRepository.UpdateUsersSetIsActiveAndIsDelete(userIds, true, false);
				userVillageMappingRepository.UpdateUsersSetIsActiveAndIsDeleted(userIds, true, false);

			} else if (userSerialNumber.size() > rolePatternMapping.getTotalNoOfUser()) {
				// if user count is more and request count is less , inactive extra user
				List<Long> userToBeActive = new ArrayList<>();
				List<Long> userToBeInactive = new ArrayList<>();
				for (int i = 0; i < userSerialNumber.size(); i++) {
					UserMaster user1 = userSerialNumber.get(i);
					if (i < rolePatternMapping.getTotalNoOfUser()) {
						user1.setIsActive(true);
						user1.setIsDeleted(false);
						user1.setModifiedBy(roleInputDAO.getUserId().toString());
						userToBeActive.add(user1.getUserId());
					} else {
						user1.setIsActive(false);
						user1.setIsDeleted(true);
						user1.setModifiedBy(roleInputDAO.getUserId().toString());
						userToBeInactive.add(user1.getUserId());
					}
					userList.add(user1);
				}
				userVillageMappingRepository.UpdateUsersSetIsActiveAndIsDeleted(userToBeActive, true, false);
				userVillageMappingRepository.UpdateUsersSetIsActiveAndIsDeleted(userToBeInactive, false, true);
				output = returnUserNamePatternOutputDAO(userMasterRepository.saveAll(userList));
			} else if (userSerialNumber.size() < rolePatternMapping.getTotalNoOfUser()) {
				List<Long> userIds = userSerialNumber.stream().map(UserMaster::getUserId).collect(Collectors.toList());
				userMasterRepository.UpdateUsersSetIsActiveAndIsDelete(userIds, true, false);
				userVillageMappingRepository.UpdateUsersSetIsActiveAndIsDeleted(userIds, true, false);
				int userCount = userSerialNumber.size();
				for (int i = 0; i < rolePatternMapping.getNoOfUser(); i++) {
					serialNumber += 1;
					for (Long state : assignStateLGDCodes) {
						if (userCount < rolePatternMapping.getTotalNoOfUser()) {
							// Get Serial number
							String userId = generalService.generateUserNameUsingPattern(
									rolePatternMapping.getRole().getPrefix(),
									rolePatternMapping.getDepartment().getDepartmentCode(), serialNumber, "S", state);

							UserMaster user1 = new UserMaster();
							user1.setUserName(userId);
							user1.setUserPassword(encoder.encode("Admin@123"));
							user1.setRoleId(rolePatternMapping.getRole());
							user1.setDepartmentId(rolePatternMapping.getDepartment());
							user1.setUserStateLGDCode(state.intValue());
							user1.setCreatedBy(roleInputDAO.getUserId().toString());
							user1.setModifiedBy(roleInputDAO.getUserId().toString());
							user1.setIsActive(true);
							user1.setIsDeleted(false);
							user1.setIsPasswordChanged(false);
							user1.setLastPasswordChangedDate(new Date());
							user1.setRolePatternMappingId(rolePatternMapping);
							setUserPasswordHistory(user1);
							userList.add(user1);

							userCount++;
						}
					}
				}
				userMasterRepository.saveAll(userList);
			}

			List<UserMaster> finalUserList = userMasterRepository
					.findByDepartmentIdAndRoleIdAndRolePatternMappingId_TerritoryLevelAndIsActiveTrueOrderByUserIdDesc( // isactive
							rolePatternMapping.getDepartment(), rolePatternMapping.getRole(), "state");
			output = returnUserNamePatternOutputDAO(userMasterRepository.saveAll(finalUserList));

//			new Thread() {
//				@Override
//				public void run() {
//					try {
//						
//						//save user village start
//						saveUserVillageMapping(userList, rolePatternMapping.getTerritoryLevel());
//						//save user village end
//
////						List<UserVillageMapping> userVillageList = new ArrayList<>();
////						// List<Long> villageCodes = getAccessibleVillageUM(roleInputDAO.getUserId());
////						// List<VillageLgdMaster> villageLgdMaster = villageLgdMasterRepository
////						// .findByVillageLgdCodeIn(villageCodes);
////						// System.out.println("Village count :" + villageLgdMaster.size());
////						userList.stream().forEach((user) -> {
////							List<DistrictLgdMaster> districtCode = districtLgdMasterRepository
////									.findByStateLgdCode_StateLgdCode(new Long(user.getUserStateLGDCode()));
////							districtCode.forEach(district -> {
////								List<VillageLgdMaster> villageLgdMaster = villageLgdMasterRepository
////										.findByDistrictLgdCode_DistrictLgdCode(district.getDistrictLgdCode());
////
////								villageLgdMaster.forEach(village -> {
////									UserVillageMapping userVillageMapping = new UserVillageMapping();
////									userVillageMapping.setUserMaster(user);
////									userVillageMapping.setVillageLgdMaster(village);
////									userVillageMapping.setCreatedBy(roleInputDAO.getUserId().toString());
////									userVillageMapping.setModifiedBy(roleInputDAO.getUserId().toString());
////									userVillageMapping.setIsActive(true);
////									userVillageMapping.setIsDeleted(false);
////									// userVillageList.add(userVillageMapping);
////									userVillageMappingRepository.save(userVillageMapping);
////								});
////							});
////						});
//////						System.out.println("Inside thread  count:" + userVillageList.size());
////						userVillageMappingRepository.saveAll(userVillageList);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}.start();


		} else if (rolePatternMapping.getTerritoryLevel().equals("district")) {
			List<Long> assignDistrictLGDCodes = getAccessibleDistrictUM(roleInputDAO.getUserId());
			List<UserMaster> userList = new ArrayList<>();

			// Checking for next serial number if exists any
			List<UserMaster> userSerialNumber = userMasterRepository
					.findByDepartmentIdAndRoleIdAndRolePatternMappingId_TerritoryLevelOrderByUserIdDesc(
							rolePatternMapping.getDepartment(), rolePatternMapping.getRole(), "state");

			String[] userNameArr = userSerialNumber.size() > 0 ? userSerialNumber.get(0).getUserName().split("_", 5)
					: null;
			Long serialNumber = (userSerialNumber.size() > 0 && userNameArr.length > 0) ? Long.parseLong(userNameArr[2])
					: 0;

			if (userSerialNumber.size() == 0) {
				for (int i = 0; i < rolePatternMapping.getNoOfUser(); i++) {
					serialNumber += 1;

					for (Long district : assignDistrictLGDCodes) {
						// Get Serial number
						String userId = generalService.generateUserNameUsingPattern(
								rolePatternMapping.getRole().getPrefix(),
								rolePatternMapping.getDepartment().getDepartmentCode(), serialNumber, "D", district);

						UserMaster user1 = new UserMaster();
						user1.setUserName(userId);
						user1.setUserPassword(encoder.encode("Admin@123"));
						user1.setRoleId(rolePatternMapping.getRole());
						user1.setDepartmentId(rolePatternMapping.getDepartment());
						user1.setUserDistrictLGDCode(district.intValue());
						user1.setCreatedBy(roleInputDAO.getUserId().toString());
						user1.setModifiedBy(roleInputDAO.getUserId().toString());
						user1.setIsActive(true);
						user1.setIsDeleted(false);
						user1.setIsPasswordChanged(false);
						user1.setLastPasswordChangedDate(new Date());
						user1.setRolePatternMappingId(rolePatternMapping);
						setUserPasswordHistory(user1);
						userList.add(user1);
					}
				}
				output = returnUserNamePatternOutputDAO(userMasterRepository.saveAll(userList));

			} else if (userSerialNumber.size() == rolePatternMapping.getTotalNoOfUser()) {
				// if user count and update count is same
				List<Long> userIds = userSerialNumber.stream().map(UserMaster::getUserId).collect(Collectors.toList());
				userMasterRepository.UpdateUsersSetIsActiveAndIsDelete(userIds, true, false);
				userVillageMappingRepository.UpdateUsersSetIsActiveAndIsDeleted(userIds, true, false);

			} else if (userSerialNumber.size() > rolePatternMapping.getTotalNoOfUser()) {
				// if user count is more and request count is less , inactive extra user
				List<Long> userToBeActive = new ArrayList<>();
				List<Long> userToBeInactive = new ArrayList<>();
				for (int i = 0; i < userSerialNumber.size(); i++) {
					UserMaster user1 = userSerialNumber.get(i);
					if (i < rolePatternMapping.getTotalNoOfUser()) {
						user1.setIsActive(true);
						user1.setIsDeleted(false);
						user1.setModifiedBy(roleInputDAO.getUserId().toString());
						userToBeActive.add(user1.getUserId());
					} else {
						user1.setIsActive(false);
						user1.setIsDeleted(true);
						user1.setModifiedBy(roleInputDAO.getUserId().toString());
						userToBeInactive.add(user1.getUserId());
					}
					userList.add(user1);
				}
				userVillageMappingRepository.UpdateUsersSetIsActiveAndIsDeleted(userToBeActive, true, false);
				userVillageMappingRepository.UpdateUsersSetIsActiveAndIsDeleted(userToBeInactive, false, true);
				output = returnUserNamePatternOutputDAO(userMasterRepository.saveAll(userList));
			} else if (userSerialNumber.size() < rolePatternMapping.getTotalNoOfUser()) {
				List<Long> userIds = userSerialNumber.stream().map(UserMaster::getUserId).collect(Collectors.toList());
				userMasterRepository.UpdateUsersSetIsActiveAndIsDelete(userIds, true, false);
				userVillageMappingRepository.UpdateUsersSetIsActiveAndIsDeleted(userIds, true, false);
				int userCount = userSerialNumber.size();
				for (int i = 0; i < rolePatternMapping.getNoOfUser(); i++) {
					serialNumber += 1;
					for (Long district : assignDistrictLGDCodes) {
						if (userCount < rolePatternMapping.getTotalNoOfUser()) {
							// Get Serial number
							String userId = generalService.generateUserNameUsingPattern(
									rolePatternMapping.getRole().getPrefix(),
									rolePatternMapping.getDepartment().getDepartmentCode(), serialNumber, "D",
									district);

							UserMaster user1 = new UserMaster();
							user1.setUserName(userId);
							user1.setUserPassword(encoder.encode("Admin@123"));
							user1.setRoleId(rolePatternMapping.getRole());
							user1.setDepartmentId(rolePatternMapping.getDepartment());
							user1.setUserDistrictLGDCode(district.intValue());
							user1.setCreatedBy(roleInputDAO.getUserId().toString());
							user1.setModifiedBy(roleInputDAO.getUserId().toString());
							user1.setIsActive(true);
							user1.setIsDeleted(false);
							user1.setIsPasswordChanged(false);
							user1.setLastPasswordChangedDate(new Date());
							user1.setRolePatternMappingId(rolePatternMapping);
							setUserPasswordHistory(user1);
							userList.add(user1);

							userCount++;
						}
					}
				}
				userMasterRepository.saveAll(userList);
			}

			List<UserMaster> finalUserList = userMasterRepository
					.findByDepartmentIdAndRoleIdAndRolePatternMappingId_TerritoryLevelAndIsActiveTrueOrderByUserIdDesc(
							rolePatternMapping.getDepartment(), rolePatternMapping.getRole(), "district");
			output = returnUserNamePatternOutputDAO(userMasterRepository.saveAll(finalUserList));

			

//			new Thread() {
//
//				@Override
//				public void run() {
//					try {
//						//save user village start
//						saveUserVillageMapping(userList, rolePatternMapping.getTerritoryLevel());
//						//save user village end
////						List<UserVillageMapping> userVillageList = new ArrayList<>();
//////						System.out.println("User  count :" + userList.size());
////						userList.stream().forEach((user) -> {
////							// village list by district code
////							List<VillageLgdMaster> villageLgdMaster = villageLgdMasterRepository
////									.findByDistrictLgdCode_DistrictLgdCode(new Long(user.getUserDistrictLGDCode()));
////							villageLgdMaster.forEach(village -> {
////								UserVillageMapping userVillageMapping = new UserVillageMapping();
////								userVillageMapping.setUserMaster(user);
////								userVillageMapping.setVillageLgdMaster(village);
////								userVillageMapping.setCreatedBy(roleInputDAO.getUserId().toString());
////								userVillageMapping.setModifiedBy(roleInputDAO.getUserId().toString());
////								userVillageMapping.setIsActive(true);
////								userVillageMapping.setIsDeleted(false);
////								userVillageList.add(userVillageMapping);
////							});
////						});
//////						System.out.println("Inside thread  count:" + userVillageList.size());
////						userVillageMappingRepository.saveAll(userVillageList);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}.start();

		} else if (rolePatternMapping.getTerritoryLevel().equals("subdistrict")) {

			// List<Long> assignSubDistrictLGDCodes =
			// getAccessibleSubDistrictUM(roleInputDAO.getUserId());
			List<UserMaster> userList = new ArrayList<>();

			// Checking for next serial number if exists any
			List<UserMaster> userSerialNumber = userMasterRepository
					.findByDepartmentIdAndRoleIdAndRolePatternMappingId_TerritoryLevelOrderByUserIdDesc(
							rolePatternMapping.getDepartment(), rolePatternMapping.getRole(), "subdistrict");

			String[] userNameArr = userSerialNumber.size() > 0 ? userSerialNumber.get(0).getUserName().split("_", 5)
					: null;

			Long currentSerialNumber = (userSerialNumber.size() > 0 && userNameArr.length > 0)
					? Long.parseLong(userNameArr[2])
					: 0;

			AtomicLong serialNumber = new AtomicLong(currentSerialNumber);

			if (userSerialNumber.size() == 0) {

				List<UserMaster> allSavedUser = new ArrayList<>();

				for (int i = 0; i < rolePatternMapping.getNoOfUser(); i++) {
					serialNumber.getAndIncrement();

					List<Long> assignedDistrictLGDCodes = getAccessibleDistrictUM(roleInputDAO.getUserId());

//					System.out.println("Adding user subdistrict wise ......");

					assignedDistrictLGDCodes.parallelStream().forEach(district -> {
						// for (Long subDistrict : assignSubDistrictLGDCodes) {

						List<Long> subDistrictCodesByDistrict = userVillageMappingRepository
								.getSubDistrictCodesByDistrictCodeAndUserId(roleInputDAO.getUserId(), district);
						List<UserMaster> tempDistrictList = new ArrayList<>();

						subDistrictCodesByDistrict.parallelStream().forEach(subDistrict -> {
							// Get Serial number
							String userId = generalService.generateUserNameUsingPattern(
									rolePatternMapping.getRole().getPrefix(),
									rolePatternMapping.getDepartment().getDepartmentCode(), serialNumber.get(), "T",
									subDistrict);

							UserMaster user1 = new UserMaster();
							user1.setUserName(userId);
							user1.setUserPassword(encoder.encode("Admin@123"));
							user1.setRoleId(rolePatternMapping.getRole());
							user1.setDepartmentId(rolePatternMapping.getDepartment());
							user1.setUserTalukaLGDCode(subDistrict.intValue());
							user1.setCreatedBy(roleInputDAO.getUserId().toString());
							user1.setModifiedBy(roleInputDAO.getUserId().toString());
							user1.setIsActive(true);
							user1.setIsDeleted(false);
							user1.setIsPasswordChanged(false);
							user1.setLastPasswordChangedDate(new Date());
							user1.setRolePatternMappingId(rolePatternMapping);
							setUserPasswordHistory(user1);
							userList.add(user1);
							tempDistrictList.add(user1);
							// }
						});
						allSavedUser.addAll(userMasterRepository.saveAll(tempDistrictList));
					});
				}
				// output =
				// returnUserNamePatternOutputDAO(userMasterRepository.saveAll(userList));
				output = returnUserNamePatternOutputDAO(allSavedUser);

			} else if (userSerialNumber.size() == rolePatternMapping.getTotalNoOfUser()) {
				// if user count and update count is same
				List<Long> userIds = userSerialNumber.stream().map(UserMaster::getUserId).collect(Collectors.toList());
				userMasterRepository.UpdateUsersSetIsActiveAndIsDelete(userIds, true, false);
				userVillageMappingRepository.UpdateUsersSetIsActiveAndIsDeleted(userIds, true, false);

			} else if (userSerialNumber.size() > rolePatternMapping.getTotalNoOfUser()) {
				// if user count is more and request count is less , inactive extra user
				List<Long> userToBeActive = new ArrayList<>();
				List<Long> userToBeInactive = new ArrayList<>();
				for (int i = 0; i < userSerialNumber.size(); i++) {
					UserMaster user1 = userSerialNumber.get(i);
					if (i < rolePatternMapping.getTotalNoOfUser()) {
						user1.setIsActive(true);
						user1.setIsDeleted(false);
						user1.setModifiedBy(roleInputDAO.getUserId().toString());
						userToBeActive.add(user1.getUserId());
					} else {
						user1.setIsActive(false);
						user1.setIsDeleted(true);
						user1.setModifiedBy(roleInputDAO.getUserId().toString());
						userToBeInactive.add(user1.getUserId());
					}
					userList.add(user1);
				}
				userVillageMappingRepository.UpdateUsersSetIsActiveAndIsDeleted(userToBeActive, true, false);
				userVillageMappingRepository.UpdateUsersSetIsActiveAndIsDeleted(userToBeInactive, false, true);
				output = returnUserNamePatternOutputDAO(userMasterRepository.saveAll(userList));
			} else if (userSerialNumber.size() < rolePatternMapping.getTotalNoOfUser()) {
				List<Long> userIds = userSerialNumber.stream().map(UserMaster::getUserId).collect(Collectors.toList());
				userMasterRepository.UpdateUsersSetIsActiveAndIsDelete(userIds, true, false);
				userVillageMappingRepository.UpdateUsersSetIsActiveAndIsDeleted(userIds, true, false);
				AtomicLong userCount = new AtomicLong(userSerialNumber.size());

				for (int i = 0; i < rolePatternMapping.getNoOfUser(); i++) {

					serialNumber.getAndIncrement();

					List<Long> assignDistrictLGDCodes = getAccessibleDistrictUM(roleInputDAO.getUserId());

					assignDistrictLGDCodes.parallelStream().forEach(district -> {

						List<Long> districtSubdistrictCode = userVillageMappingRepository
								.getSubDistrictCodesByDistrictCodeAndUserId(roleInputDAO.getUserId(), district);
						List<UserMaster> tempDistrictList = new ArrayList<>();

						districtSubdistrictCode.parallelStream().forEach(subDistrict -> {
							if (userCount.get() < rolePatternMapping.getTotalNoOfUser()) {
								// Get Serial number
								String userId = generalService.generateUserNameUsingPattern(
										rolePatternMapping.getRole().getPrefix(),
										rolePatternMapping.getDepartment().getDepartmentCode(), serialNumber.get(), "T",
										subDistrict);

								UserMaster user1 = new UserMaster();
								user1.setUserName(userId);
								user1.setUserPassword(encoder.encode("Admin@123"));
								user1.setRoleId(rolePatternMapping.getRole());
								user1.setDepartmentId(rolePatternMapping.getDepartment());
								user1.setUserTalukaLGDCode(subDistrict.intValue());
								user1.setCreatedBy(roleInputDAO.getUserId().toString());
								user1.setModifiedBy(roleInputDAO.getUserId().toString());
								user1.setIsActive(true);
								user1.setIsDeleted(false);
								user1.setIsPasswordChanged(false);
								user1.setLastPasswordChangedDate(new Date());
								user1.setRolePatternMappingId(rolePatternMapping);
								setUserPasswordHistory(user1);
								userList.add(user1);

								userCount.getAndIncrement();
							}
						});
					});

					// for (Long subDistrict : assignSubDistrictLGDCodes) {
					// if (userCount < rolePatternMapping.getTotalNoOfUser()) {
					// // Get Serial number
					// String userId = generalService.generateUserNameUsingPattern(
					// rolePatternMapping.getRole().getPrefix(),
					// rolePatternMapping.getDepartment().getDepartmentCode(), serialNumber.get(),
					// "T",
					// subDistrict);

					// UserMaster user1 = new UserMaster();
					// user1.setUserName(userId);
					// user1.setUserPassword(encoder.encode("Admin@123"));
					// user1.setRoleId(rolePatternMapping.getRole());
					// user1.setDepartmentId(rolePatternMapping.getDepartment());
					// user1.setUserTalukaLGDCode(subDistrict.intValue());
					// user1.setCreatedBy(roleInputDAO.getUserId().toString());
					// user1.setModifiedBy(roleInputDAO.getUserId().toString());
					// user1.setIsActive(true);
					// user1.setIsDeleted(false);
					// user1.setIsPasswordChanged(false);
					// user1.setLastPasswordChangedDate(new Date());
					// user1.setRolePatternMappingId(rolePatternMapping);
					// setUserPasswordHistory(user1);
					// userList.add(user1);

					// userCount++;
					// }
					// }
				}
				userMasterRepository.saveAll(userList);
			}

			List<UserMaster> finalUserList = userMasterRepository
					.findByDepartmentIdAndRoleIdAndRolePatternMappingId_TerritoryLevelAndIsActiveTrueOrderByUserIdDesc(
							rolePatternMapping.getDepartment(), rolePatternMapping.getRole(), "subdistrict");
			output =

					returnUserNamePatternOutputDAO(userMasterRepository.saveAll(finalUserList));
			
			


//			new Thread() {
//				@Override
//				public void run() {
//					try {
//						//save user village start
//						saveUserVillageMapping(userList, rolePatternMapping.getTerritoryLevel());
//						//save user village end
////						List<UserVillageMapping> userVillageList = new ArrayList<>();
//////						System.out.println("user count :" + userList.size());
////						userList.stream().forEach((user) -> {
////							// village list by sub district code
////							List<VillageLgdMaster> villageLgdMaster = villageLgdMasterRepository
////									.findBySubDistrictLgdCode_SubDistrictLgdCode(new Long(user.getUserTalukaLGDCode()));
////							villageLgdMaster.forEach(village -> {
////								UserVillageMapping userVillageMapping = new UserVillageMapping();
////								userVillageMapping.setUserMaster(user);
////								userVillageMapping.setVillageLgdMaster(village);
////								userVillageMapping.setCreatedBy(roleInputDAO.getUserId().toString());
////								userVillageMapping.setModifiedBy(roleInputDAO.getUserId().toString());
////								userVillageMapping.setIsActive(true);
////								userVillageMapping.setIsDeleted(false);
////								userVillageList.add(userVillageMapping);
////							});
////						});
////						System.out.println("Inside thread  count:" + userVillageList.size());
////						userVillageMappingRepository.saveAll(userVillageList);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}.start();

		} else if (rolePatternMapping.getTerritoryLevel().equals("village")) {

			// List<Long> assignVillageLGDCodes =
			// getAccessibleVillageUM(roleInputDAO.getUserId());
			List<UserMaster> userList = new ArrayList<>();

			// Checking for next serial number if exists any
			List<UserMaster> userSerialNumber = userMasterRepository
					.findByDepartmentIdAndRoleIdAndRolePatternMappingId_TerritoryLevelOrderByUserIdDesc(
							rolePatternMapping.getDepartment(), rolePatternMapping.getRole(), "village");

			String[] userNameArr = userSerialNumber.size() > 0 ? userSerialNumber.get(0).getUserName().split("_", 5)
					: null;
			Long currentSerialNumber = (userSerialNumber.size() > 0 && userNameArr.length > 0)
					? Long.parseLong(userNameArr[2])
					: 0;

			AtomicLong serialNumber = new AtomicLong(currentSerialNumber);
			if (userSerialNumber.size() == 0) {
				List<UserMaster> allSavedUser = new ArrayList<>();

				for (int i = 0; i < rolePatternMapping.getNoOfUser(); i++) {
					// serialNumber += 1;
					serialNumber.getAndIncrement();

					List<Long> assignSubDistrictLGDCodes = getAccessibleSubDistrictUM(roleInputDAO.getUserId());

//					System.out.println("Adding user subdistrict wise ......");
					// for(Long subDistrict : assignSubDistrictLGDCodes) {
					assignSubDistrictLGDCodes.parallelStream().forEach(subDistrict -> {

						List<Long> subDistrictVillageCode = userVillageMappingRepository
								.getVillageCodeBySubDistrictAndUserId(roleInputDAO.getUserId(), subDistrict);
						List<UserMaster> tempSubDistrictList = new ArrayList<>();

						// for (Long village : subDistrictVillageCode) { //30,000
						subDistrictVillageCode.parallelStream().forEach(village -> {
							// Get Serial number
							String userId = generalService.generateUserNameUsingPattern(
									rolePatternMapping.getRole().getPrefix(),
									rolePatternMapping.getDepartment().getDepartmentCode(), serialNumber.get(), "V",
									village);

							UserMaster user1 = new UserMaster();
							user1.setUserName(userId);
							user1.setUserPassword(encoder.encode("Admin@123"));
							user1.setRoleId(rolePatternMapping.getRole());
							user1.setDepartmentId(rolePatternMapping.getDepartment());
							user1.setUserVillageLGDCode(village.intValue());
							user1.setCreatedBy(roleInputDAO.getUserId().toString());
							user1.setModifiedBy(roleInputDAO.getUserId().toString());
							user1.setIsActive(true);
							user1.setIsDeleted(false);
							user1.setIsPasswordChanged(false);
							user1.setLastPasswordChangedDate(new Date());
							user1.setRolePatternMappingId(rolePatternMapping);
							setUserPasswordHistory(user1);
							userList.add(user1);
							tempSubDistrictList.add(user1);

							// }
						});
						allSavedUser.addAll(userMasterRepository.saveAll(tempSubDistrictList));
						// }
					});
				}

				output = returnUserNamePatternOutputDAO(allSavedUser);
			} else if (userSerialNumber.size() == rolePatternMapping.getTotalNoOfUser()) {
				// if user count and update count is same
				List<Long> userIds = userSerialNumber.stream().map(UserMaster::getUserId).collect(Collectors.toList());
				userMasterRepository.UpdateUsersSetIsActiveAndIsDelete(userIds, true, false);
				userVillageMappingRepository.UpdateUsersSetIsActiveAndIsDeleted(userIds, true, false);

			} else if (userSerialNumber.size() > rolePatternMapping.getTotalNoOfUser()) {
				// if user count is more and request count is less , inactive extra user
				List<Long> userToBeActive = new ArrayList<>();
				List<Long> userToBeInactive = new ArrayList<>();
				for (int i = 0; i < userSerialNumber.size(); i++) {
					UserMaster user1 = userSerialNumber.get(i);
					if (i < rolePatternMapping.getTotalNoOfUser()) {
						user1.setIsActive(true);
						user1.setIsDeleted(false);
						user1.setModifiedBy(roleInputDAO.getUserId().toString());
						userToBeActive.add(user1.getUserId());
					} else {
						user1.setIsActive(false);
						user1.setIsDeleted(true);
						user1.setModifiedBy(roleInputDAO.getUserId().toString());
						userToBeInactive.add(user1.getUserId());
					}
					userList.add(user1);
				}
				userVillageMappingRepository.UpdateUsersSetIsActiveAndIsDeleted(userToBeActive, true, false);
				userVillageMappingRepository.UpdateUsersSetIsActiveAndIsDeleted(userToBeInactive, false, true);
				output = returnUserNamePatternOutputDAO(userMasterRepository.saveAll(userList));
			} else if (userSerialNumber.size() < rolePatternMapping.getTotalNoOfUser()) {
				List<Long> userIds = userSerialNumber.stream().map(UserMaster::getUserId).collect(Collectors.toList());
				userMasterRepository.UpdateUsersSetIsActiveAndIsDelete(userIds, true, false);
				userVillageMappingRepository.UpdateUsersSetIsActiveAndIsDeleted(userIds, true, false);
				AtomicLong userCount = new AtomicLong(userSerialNumber.size());
				List<UserMaster> allSavedUser = new ArrayList<>();
				for (int i = 0; i < rolePatternMapping.getNoOfUser(); i++) {
					// serialNumber += 1;
					serialNumber.getAndIncrement();

					List<Long> assignSubDistrictLGDCodes = getAccessibleSubDistrictUM(roleInputDAO.getUserId());

//					System.out.println("Adding user subdistrict wise ......");
					// for(Long subDistrict : assignSubDistrictLGDCodes) {
					assignSubDistrictLGDCodes.parallelStream().forEach(subDistrict -> {

						List<Long> subDistrictVillageCode = userVillageMappingRepository
								.getVillageCodeBySubDistrictAndUserId(roleInputDAO.getUserId(), subDistrict);
						List<UserMaster> tempSubDistrictList = new ArrayList<>();

						// for (Long village : subDistrictVillageCode) { //30,000
						subDistrictVillageCode.parallelStream().forEach(village -> {
							if (userCount.get() < rolePatternMapping.getTotalNoOfUser()) {
								// Get Serial number
								String userId = generalService.generateUserNameUsingPattern(
										rolePatternMapping.getRole().getPrefix(),
										rolePatternMapping.getDepartment().getDepartmentCode(), serialNumber.get(), "V",
										village);

								UserMaster user1 = new UserMaster();
								user1.setUserName(userId);
								user1.setUserPassword(encoder.encode("Admin@123"));
								user1.setRoleId(rolePatternMapping.getRole());
								user1.setDepartmentId(rolePatternMapping.getDepartment());
								user1.setUserVillageLGDCode(village.intValue());
								user1.setCreatedBy(roleInputDAO.getUserId().toString());
								user1.setModifiedBy(roleInputDAO.getUserId().toString());
								user1.setIsActive(true);
								user1.setIsDeleted(false);
								user1.setIsPasswordChanged(false);
								user1.setLastPasswordChangedDate(new Date());
								user1.setRolePatternMappingId(rolePatternMapping);
								setUserPasswordHistory(user1);
								userList.add(user1);
								userList.add(user1);
								tempSubDistrictList.add(user1);

								// }
								userCount.getAndIncrement();
							}
						});
						allSavedUser.addAll(userMasterRepository.saveAll(tempSubDistrictList));
						// }
					});
				}
				output = returnUserNamePatternOutputDAO(allSavedUser);
			}

			List<UserMaster> finalUserList = userMasterRepository
					.findByDepartmentIdAndRoleIdAndRolePatternMappingId_TerritoryLevelAndIsActiveTrueOrderByUserIdDesc(
							rolePatternMapping.getDepartment(), rolePatternMapping.getRole(), "village");
			output = returnUserNamePatternOutputDAO(userMasterRepository.saveAll(finalUserList));

			
//			
//			new Thread() {
//				@Override
//				public void run() {
//					try {
//						//save user village start
//						saveUserVillageMapping(userList, rolePatternMapping.getTerritoryLevel());
//						//save user village end
//						
////						List<UserVillageMapping> userVillageList = new ArrayList<>();
//////						System.out.println("User count :" + userList.size());
////						userList.stream().forEach((user) -> {
////							UserVillageMapping userVillageMapping = new UserVillageMapping();
////							userVillageMapping.setUserMaster(user);
////							userVillageMapping.setVillageLgdMaster(villageLgdMasterRepository
////									.findByVillageLgdCode(new Long(user.getUserVillageLGDCode())));
////							userVillageMapping.setCreatedBy(roleInputDAO.getUserId().toString());
////							userVillageMapping.setModifiedBy(roleInputDAO.getUserId().toString());
////							userVillageMapping.setIsActive(true);
////							userVillageMapping.setIsDeleted(false);
////							userVillageList.add(userVillageMapping);
////						});
//////						System.out.println("Inside thread  count:" + userVillageList.size());
////						// userVillageMappingRepository.saveAll(userVillageList);
////
////						// Add user mapping in chunk
////						int size = userVillageList.size();
////						int counter = 0;
////						List<UserVillageMapping> temp = new ArrayList<>();
////						for (UserVillageMapping bm : userVillageList) {
////							temp.add(bm);
////							if ((counter + 1) % 500 == 0 || (counter + 1) == size) {
////								userVillageMappingRepository.saveAll(temp);
////								temp.clear();
////							}
////							counter++;
////						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}.start();
		}
		return output;
	}

	public List<RoleOutputDAO> returnUserNamePatternOutputDAO(List<UserMaster> users) {
		List<RoleOutputDAO> roles = new ArrayList<>();
		for (UserMaster user : users) {
			RoleOutputDAO roleOutputDAO = new RoleOutputDAO();
			// roleOutputDAO.setRoleId(roleMaster.getRoleId());

			roleOutputDAO.setUserName(user.getUserName());
			roleOutputDAO.setUserId(user.getUserId());
			// List<RolePatternMapping> roleMapping =
			// rolePatternMappingRepository.findByRole(user.getRoleId());
			roleOutputDAO.setTerritoryLevel(user.getRolePatternMappingId().getTerritoryLevel());

			if (user.getRolePatternMappingId().getTerritoryLevel().equals("state")) {
				roleOutputDAO.setLgdCode(user.getUserStateLGDCode());
				StateLgdMaster state = stateLgdMasterRepository
						.findByStateLgdCode(new Long(user.getUserStateLGDCode()));
				roleOutputDAO.setTerritoryLevelName(state.getStateName());
			} else if (user.getRolePatternMappingId().getTerritoryLevel().equals("district")) {
				roleOutputDAO.setLgdCode(user.getUserDistrictLGDCode());
				DistrictLgdMaster district = districtLgdMasterRepository
						.findByDistrictLgdCode(new Long(user.getUserDistrictLGDCode()));
				roleOutputDAO.setTerritoryLevelName(district.getDistrictName());
			} else if (user.getRolePatternMappingId().getTerritoryLevel().equals("subdistrict")) {
				roleOutputDAO.setLgdCode(user.getUserTalukaLGDCode());
				SubDistrictLgdMaster subDistrict = subDistrictLgdMasterRepository
						.findBySubDistrictLgdCode(new Long(user.getUserTalukaLGDCode()));
				roleOutputDAO.setTerritoryLevelName(subDistrict.getSubDistrictName());
			} else if (user.getRolePatternMappingId().getTerritoryLevel().equals("village")) {
				roleOutputDAO.setLgdCode(user.getUserVillageLGDCode());
				VillageLgdMaster village = villageLgdMasterRepository
						.findByVillageLgdCode(new Long(user.getUserVillageLGDCode()));
				roleOutputDAO.setTerritoryLevelName(village.getVillageName());
			}

			roleOutputDAO.setCreatedOn(user.getCreatedOn());
			roleOutputDAO.setModifiedOn(user.getModifiedOn());
			roleOutputDAO.setCreatedBy(user.getCreatedBy());
			roleOutputDAO.setModifiedBy(user.getModifiedBy());
			roleOutputDAO.setCreatedIp(user.getCreatedIp());
			roleOutputDAO.setModifiedIp(user.getModifiedIp());
			roleOutputDAO.setIsActive(user.getIsActive());
			roleOutputDAO.setIsDeleted(user.getIsDeleted());
			roles.add(roleOutputDAO);
		}
		return roles;
	}

	/**
	 * Check for duplicate prefix
	 *
	 * @param prefix
	 * @param request
	 * @return
	 */
	public ResponseModel validateRolePrefix(String prefix, Long state, HttpServletRequest request) {
		ResponseModel responseModel = null;
		try {
			if (prefix != null) {

				List<RoleMaster> defaultPrefix = roleMasterRepository
						.findByPrefixIgnoreCaseAndIsDefaultTrueAndIsActiveTrueAndIsDeletedFalse(prefix);
				if (defaultPrefix.size() > 0) {
					responseModel = CustomMessages.makeResponseModel(prefix, "Duplicate prefix name : " + prefix,
							CustomMessages.ALREADY_EXIST, CustomMessages.FAILED);
				} else {
					List<RoleMaster> duplicatePrefix = roleMasterRepository
							.findByPrefixIgnoreCaseAndStateLgdCodeAndIsActiveTrueAndIsDeletedFalse(prefix, state);
					if (duplicatePrefix.size() > 0) {
						responseModel = CustomMessages.makeResponseModel(prefix, "Duplicate prefix name : " + prefix,
								CustomMessages.ALREADY_EXIST, CustomMessages.FAILED);
					} else {
						responseModel = CustomMessages.makeResponseModel(prefix, "Prefix is unique : " + prefix,
								CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
					}
				}
			} else {
				responseModel = CustomMessages.makeResponseModel(null, "Prefix name required.",
						CustomMessages.GET_DATA_ERROR, CustomMessages.FAILED);
			}
			return responseModel;
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	public ResponseModel validateDefaultRolePrefix(String prefix, HttpServletRequest request) {
		ResponseModel responseModel = null;
		try {
			if (prefix != null) {

				List<RoleMaster> defaultPrefix = roleMasterRepository
						.findByPrefixIgnoreCaseAndIsDefaultTrueAndIsActiveTrueAndIsDeletedFalse(prefix);
				if (defaultPrefix.size() > 0) {
					responseModel = CustomMessages.makeResponseModel(prefix, "Duplicate prefix name : " + prefix,
							CustomMessages.ALREADY_EXIST, CustomMessages.FAILED);
				} else {
					// Optional<RoleMaster> duplicatePrefix = roleMasterRepository
					// .findByPrefixIgnoreCaseAndStateLgdCodeAndIsActiveTrueAndIsDeletedFalse(prefix,
					// state);
					// if (duplicatePrefix.isPresent()) {
					// responseModel = CustomMessages.makeResponseModel(prefix, "Duplicate prefix
					// name : " + prefix,
					// CustomMessages.ALREADY_EXIST, CustomMessages.FAILED);
					// } else {
					responseModel = CustomMessages.makeResponseModel(prefix, "Prefix is unique : " + prefix,
							CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
					// }
				}
			} else {
				responseModel = CustomMessages.makeResponseModel(null, "Prefix name required.",
						CustomMessages.GET_DATA_ERROR, CustomMessages.FAILED);
			}
			return responseModel;
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Check for duplicate prefix
	 *
	 * @param roleName
	 * @param state
	 * @param request
	 * @return
	 */
	public ResponseModel validateRoleName(String roleName, Long state, HttpServletRequest request) {
		ResponseModel responseModel = null;
		try {
			if (roleName != null) {

				Optional<RoleMaster> defaultRoleName = roleMasterRepository
						.findByRoleNameIgnoreCaseAndIsDefaultTrueAndIsActiveTrueAndIsDeletedFalse(roleName);
				if (defaultRoleName.isPresent()) {
					responseModel = CustomMessages.makeResponseModel(roleName, "Duplicate role name : " + roleName,
							CustomMessages.ALREADY_EXIST, CustomMessages.FAILED);
				} else {
					Optional<RoleMaster> duplicateRoleName = roleMasterRepository
							.findByRoleNameIgnoreCaseAndStateLgdCodeAndIsActiveTrueAndIsDeletedFalse(roleName, state);
					if (duplicateRoleName.isPresent()) {
						responseModel = CustomMessages.makeResponseModel(roleName, "Duplicate role name : " + roleName,
								CustomMessages.ALREADY_EXIST, CustomMessages.FAILED);
					} else {
						responseModel = CustomMessages.makeResponseModel(roleName, "Rolename is unique : " + roleName,
								CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
					}
				}
			} else {
				responseModel = CustomMessages.makeResponseModel(null, "Role name required.",
						CustomMessages.GET_DATA_ERROR, CustomMessages.FAILED);
			}
			return responseModel;
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	// public List<Integer> getAccessibleStates(Long userId) {
	// List<Integer> stateLGDCodes = new ArrayList<>();
	// UserMaster user = userMasterRepository.findByUserId(userId).orElse(null);
	// if (Objects.nonNull(user)) {
	// GeographicalAreaOutputDAO geographicalAreaOutputDAO = new
	// Gson().fromJson(user.getGeographicalArea(),
	// GeographicalAreaOutputDAO.class);
	// user.setGeographicalAreaOutputDAO(geographicalAreaOutputDAO);
	// List<UserStateDAO> userStates = geographicalAreaOutputDAO.getState();
	// stateLGDCodes =
	// userStates.stream().distinct().map(UserStateDAO::getStateLgdCode).mapToInt(Long::intValue)
	// .boxed().collect(Collectors.toList());
	//
	// }
	// return stateLGDCodes;
	// }
	//
	// public List<Integer> getAccessibleDistrict(Long userId) {
	// List<Integer> stateAccessibleDistrictCodes = new ArrayList<>();
	// UserMaster user = userMasterRepository.findByUserId(userId).orElse(null);
	// if (Objects.nonNull(user)) {
	// GeographicalAreaOutputDAO geographicalAreaOutputDAO = new
	// Gson().fromJson(user.getGeographicalArea(),
	// GeographicalAreaOutputDAO.class);
	// user.setGeographicalAreaOutputDAO(geographicalAreaOutputDAO);
	// List<UserStateDAO> userStates = geographicalAreaOutputDAO.getState();
	//
	// List<Long> stateLGDCodes =
	// userStates.stream().distinct().map(UserStateDAO::getStateLgdCode)
	// .collect(Collectors.toList());
	//
	// if (stateLGDCodes.size() > 0) {
	// userStates.forEach(state -> {
	// state.getDistrict().forEach(district -> {
	// stateAccessibleDistrictCodes.add(district.getDistrictLgdCode().intValue());
	// });
	// });
	// }
	// }
	// return stateAccessibleDistrictCodes;
	// }
	//
	// public List<Integer> getAccessibleTaluk(Long userId) {
	// List<Integer> userAccessibleTalukCodes = new ArrayList<>();
	// UserMaster user = userMasterRepository.findByUserId(userId).orElse(null);
	// if (Objects.nonNull(user)) {
	// GeographicalAreaOutputDAO geographicalAreaOutputDAO = new
	// Gson().fromJson(user.getGeographicalArea(),
	// GeographicalAreaOutputDAO.class);
	// user.setGeographicalAreaOutputDAO(geographicalAreaOutputDAO);
	//
	// List<UserStateDAO> userStates = geographicalAreaOutputDAO.getState();
	// List<Long> stateLGDCodes =
	// userStates.stream().distinct().map(UserStateDAO::getStateLgdCode)
	// .collect(Collectors.toList());
	//
	// if (stateLGDCodes.size() > 0) {
	// userStates.forEach(state -> {
	// state.getDistrict().forEach(district -> {
	// district.getSubDistrict().forEach(subDistrict -> {
	// userAccessibleTalukCodes.add(subDistrict.getSubDistrictLgdCode().intValue());
	// });
	// });
	// });
	// }
	// }
	// return userAccessibleTalukCodes;
	// }
	//
	// public List<Long> getAccessibleVillage(Long userId) {
	// List<Long> userAccessibleVillageCodes = new ArrayList<>();
	// UserMaster user = userMasterRepository.findByUserId(userId).orElse(null);
	// if (Objects.nonNull(user)) {
	// GeographicalAreaOutputDAO geographicalAreaOutputDAO = new
	// Gson().fromJson(user.getGeographicalArea(),
	// GeographicalAreaOutputDAO.class);
	// user.setGeographicalAreaOutputDAO(geographicalAreaOutputDAO);
	//
	// List<UserStateDAO> userStates = geographicalAreaOutputDAO.getState();
	// List<Long> stateLGDCodes =
	// userStates.stream().distinct().map(UserStateDAO::getStateLgdCode)
	// .collect(Collectors.toList());
	//
	// if (stateLGDCodes.size() > 0) {
	// userStates.forEach(state -> {
	// state.getDistrict().forEach(district -> {
	// district.getSubDistrict().forEach(subDistrict -> {
	// subDistrict.getVillage().forEach(village -> {
	// userAccessibleVillageCodes.add(village.getVillageLgdCode());
	// });
	// });
	// });
	// });
	// }
	// }
	// return userAccessibleVillageCodes;
	// }

	/**
	 * @param request
	 * @return
	 */
	public ResponseModel getUserRolesByAssignState(HttpServletRequest request) {
		ResponseModel responseModel = null;
		try {
			List<String> roleNames = Arrays.asList("SuperAdmin", "StateAdmin");
			Long userId = new Long(CustomMessages.getUserId(request, jwtTokenUtil));
			List<Long> stateLgdCodes = userVillageMappingRepository.getStateCodesById(userId);
			
			Optional<UserMaster> userOp=userMasterRepository.findByUserIdAndIsDeletedAndIsActive(userId, Boolean.FALSE,Boolean.TRUE);
			List<RoleOutputDAO> returnList = new ArrayList();
			List<RoleMaster> roleList = new ArrayList<>();
			if(userOp.isPresent()) {
				UserMaster userMaster=userOp.get();
				List<String> tmpRoles=roleNames.stream().filter(x->x.equals(userMaster.getRoleId().getRoleName())).collect(Collectors.toList());
				if(tmpRoles!=null && tmpRoles.size()>0) {
					roleList = roleMasterRepository.findByIsDeletedAndStateLgdCodeInOrderByRoleIdDesc(false, stateLgdCodes);		
				}else {
					roleList = roleMasterRepository.findByIsDeletedAndStateLgdCodeInAndCreatedByOrderByRoleIdDesc(false, stateLgdCodes,String.valueOf(userId));
				}
				
				
			}

			

			if (roleList != null && roleList.size() > 0) {
				roleList.parallelStream().forEach(role -> {
//					returnList.add(returnRoleMappingOutputDAO(role, request));
					RoleOutputDAO roleOutputDAO = returnRoleMappingOutputDAO(role, request);
					roleOutputDAO.setCreatedIp(null);
					roleOutputDAO.setModifiedIp(null);
					returnList.add(roleOutputDAO);
				});

				if (returnList != null && returnList.size() > 0) {
					Set<Long> userIds = returnList.stream().map(x -> Long.parseLong(x.getModifiedBy()))
							.collect(Collectors.toSet());
					List<UserMaster> users = userMasterRepository.findByUserIdInAndIsDeletedAndIsActive(userIds, false,
							true);
					returnList.parallelStream().forEach(action -> {
						users.forEach(action2 -> {
							if (action2.getUserId().equals(Long.parseLong(action.getModifiedBy()))) {
								action.setModifiedByName(action2.getUserName());
							}
						});
						// action.getModifiedBy()
					});

				}
			}

			// if (rolePatternMappingList != null && rolePatternMappingList.size() > 0) {
			// rolePatternMappingList.forEach(rolePatternMap -> {
			// returnList.add(returnRolePatternMappingOutputDAO(rolePatternMap));
			// });
			// }
			responseModel = CustomMessages.makeResponseModel(returnList, CustomMessages.MENU_ADD_SUCCESS,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			return responseModel;
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	public RoleOutputDAO returnRolePatternMappingOutputDAO(RolePatternMapping rolePatternMapping) {
		RoleOutputDAO roleOutputDAO = new RoleOutputDAO();
		roleOutputDAO.setRoleId(rolePatternMapping.getRole().getRoleId());
		roleOutputDAO.setRoleName(rolePatternMapping.getRole().getRoleName());
		roleOutputDAO.setTerritoryLevel(rolePatternMapping.getTerritoryLevel());
		roleOutputDAO.setNoOfUsers(rolePatternMapping.getNoOfUser());
		List<UserMaster> roleCount = userMasterRepository.findByRoleIdAndIsDeletedFalse(rolePatternMapping.getRole());
		roleOutputDAO.setTotalUserCount(roleCount.size());

		Optional<UserMaster> createdBy = userMasterRepository
				.findByUserId(new Long(rolePatternMapping.getModifiedBy()));
		roleOutputDAO.setCreatedOn(rolePatternMapping.getRole().getCreatedOn());
		roleOutputDAO.setModifiedOn(rolePatternMapping.getRole().getModifiedOn());
		roleOutputDAO.setCreatedBy(createdBy.get().getUserName());
		roleOutputDAO.setModifiedBy(createdBy.get().getUserName());
		roleOutputDAO.setCreatedIp(rolePatternMapping.getRole().getCreatedIp());
		roleOutputDAO.setModifiedIp(rolePatternMapping.getRole().getModifiedIp());

		roleOutputDAO.setIsActive(rolePatternMapping.getRole().getIsActive());
		roleOutputDAO.setIsDeleted(rolePatternMapping.getRole().getIsDeleted());
		return roleOutputDAO;
	}

	/**
	 * Get all active by id
	 * @param request
	 * @return {@code RoleMaster} all active by id
	 */
	public ResponseModel getAllActiveById(HttpServletRequest request) {
		ResponseModel responseModel = null;
		try {

			List<RoleOutputDAO> returnList = new ArrayList();
			// Long userId = Long.parseLong(userService.getUserId(request));
			Long userId = null;
			List<RoleMaster> roleList = new ArrayList<>();
			if (userId != null) {
				roleList = roleMasterRepository.findByIsDeletedAndIsDefaultAndCreatedBy(Boolean.FALSE, Boolean.TRUE,
						userId + "");
			} else {
				// roleList = roleMasterRepository.findByIsDeletedAndIsDefault(Boolean.FALSE,
				// Boolean.FALSE);
				roleList = roleMasterRepository.findByIsDeletedAndIsActiveAndIsDefaultOrderByRoleIdAsc(Boolean.FALSE, Boolean.TRUE,
						Boolean.TRUE);
			}

			if (roleList != null && roleList.size() > 0) {
				roleList.forEach(action -> {
					returnList.add(CustomMessages.returnUserOutputDAO(action));
				});

			}
			responseModel = CustomMessages.makeResponseModel(roleList, CustomMessages.MENU_ADD_SUCCESS,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			return responseModel;
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	// public ResponseModel updateRolePatternStatus(HttpServletRequest request,
	// RoleInputDAO roleInputDAO) {
	// ResponseModel responseModel = null;
	// try {
	// RoleMaster roleId =
	// roleMasterRepository.findByRoleId(roleInputDAO.getRoleId());
	// if (roleId != null) {
	// List<RolePatternMapping> rolePatternMaster =
	// rolePatternMappingRepository.findAllByRole(roleId);
	// if (rolePatternMaster != null) {
	//
	// rolePatternMaster.forEach((rolePattern) -> {
	// rolePattern.setIsActive(roleInputDAO.getIsActive());
	// rolePattern.setIsDeleted(roleInputDAO.getIsDeleted());
	// rolePattern.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
	// });
	// rolePatternMappingRepository.saveAll(rolePatternMaster); // Change role
	// pattern status
	//
	//
	// List<UserMaster> users = userMasterRepository.findByRoleId(roleId);
	// users.stream().forEach((u) -> {
	// u.setIsActive(roleInputDAO.getIsActive());
	// u.setIsDeleted(roleInputDAO.getIsDeleted());
	// u.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
	// userMasterRepository.save(u);
	//
	// new Thread() {
	// @Override
	// public void run() {
	// //change user village mapping status
	// List<UserVillageMapping> userVillageList =
	// userVillageMappingRepository.findByUserMaster(u);
	// userVillageList.stream().forEach((uv)->{
	// uv.setIsActive(roleInputDAO.getIsActive());
	// uv.setIsDeleted(roleInputDAO.getIsDeleted());
	// uv.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
	// // userVillageMappingRepository.save(uv);
	// });
	//
	// userVillageMappingRepository.saveAll(userVillageList);
	// }
	// }.start();
	// });
	// // userMasterRepository.saveAll(users); // change user status
	// // }
	// // }.start();
	//
	// roleId.setIsActive(roleInputDAO.getIsActive());
	// roleId.setIsDeleted(roleInputDAO.getIsDeleted());
	// roleId.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
	// roleMasterRepository.save(roleId); // change role status
	// }
	// }
	// responseModel = CustomMessages.makeResponseModel(null,
	// CustomMessages.STATUS_UPDATED_SUCCESS,
	// CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
	// return responseModel;
	// } catch (Exception e) {
	// e.printStackTrace();
	// responseModel = CustomMessages.makeResponseModel(e.getMessage(),
	// CustomMessages.FAILURE,
	// CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
	// return responseModel;
	// }
	// }

	@Transactional
	public ResponseModel updateRolePatternStatus(HttpServletRequest request, RoleInputDAO roleInputDAO) {
		ResponseModel responseModel = null;
		try {
			String userId = CustomMessages.getUserId(request, jwtTokenUtil);

			if (roleInputDAO.getRoleId() != null) {

				List<Long> users = userMasterRepository.findUserIdByRoleId(roleInputDAO.getRoleId());

				String userList = getJoinedValuefromList(users);
				userMasterRepository.updateRolePatternStatus(roleInputDAO.getIsDeleted(),roleInputDAO.getIsActive(),Long.valueOf(userId),userList,roleInputDAO.getRoleId());
//
				// change role status
				roleMasterRepository.updateRoleMasterStatusAndDelete(roleInputDAO.getIsDeleted(),
						roleInputDAO.getIsActive(), roleInputDAO.getRoleId(), userId);

			}
			responseModel = CustomMessages.makeResponseModel(null, CustomMessages.STATUS_UPDATED_SUCCESS,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			return responseModel;
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * @param role
	 * @param request
	 * @return {@code RoleOutputDAO} the role pattern user details
	 */
	public ResponseModel getRolePatternUserDetails(Long role, HttpServletRequest request) {
		ResponseModel responseModel = null;
		try {
			RoleOutputDAO roleOutputDao = new RoleOutputDAO();
			RoleMaster roleId = roleMasterRepository.findByRoleIdAndIsDeletedFalse(role);
			if (roleId != null) {
				List<RolePatternMapping> rolePatternMaster = rolePatternMappingRepository
						.findAllByRoleAndIsDeletedFalse(roleId);
				if (rolePatternMaster != null) {
					List<UserMaster> users = userMasterRepository.findByRoleIdAndIsDeletedFalse(roleId);

					List<UserOutputDAO> userList = new ArrayList<>();
					users.stream().forEach((u) -> { // Set Users
						UserOutputDAO userDao = new UserOutputDAO();
						userDao.setUserId(u.getUserId());
						userDao.setUserName(u.getUserName());
						if (u.getUserStateLGDCode() != null) {
							userDao.setUserStateLGDCode(u.getUserStateLGDCode());
							StateLgdMaster state = stateLgdMasterRepository
									.findByStateLgdCode(new Long(u.getUserStateLGDCode()));
							userDao.setStateName(state.getStateName());
						}

						if (u.getUserDistrictLGDCode() != null) {
							userDao.setUserDistrictLGDCode(u.getUserDistrictLGDCode());
							DistrictLgdMaster district = districtLgdMasterRepository
									.findByDistrictLgdCode(new Long(u.getUserDistrictLGDCode()));
							userDao.setDistrictName(district.getDistrictName());
						}

						if (u.getUserTalukaLGDCode() != null) {
							userDao.setUserTalukaLGDCode(u.getUserTalukaLGDCode());
							SubDistrictLgdMaster subDistrict = subDistrictLgdMasterRepository
									.findBySubDistrictLgdCode(new Long(u.getUserTalukaLGDCode()));
							userDao.setTalukaName(subDistrict.getSubDistrictName());
						}

						if (u.getUserVillageLGDCode() != null) {
							userDao.setUserVillageLGDCode(
									u.getUserVillageLGDCode() != null ? u.getUserVillageLGDCode() : null);
							VillageLgdMaster village = villageLgdMasterRepository
									.findByVillageLgdCode(new Long(u.getUserVillageLGDCode()));
							userDao.setVillageName(village.getVillageName());
						}
						userDao.setIsActive(u.getIsActive());
						userList.add(userDao);
					});
					roleOutputDao.setUserList(userList);

					List<PatternDAO> patternList = new ArrayList<>(); // Set role Pattern Mapping
					rolePatternMaster.stream().forEach((p) -> {
						PatternDAO pattern = new PatternDAO();
						pattern.setRolePatternMappingId(p.getRolePatternMappingId());
						pattern.setDepartmentId(p.getDepartment().getDepartmentId());
						pattern.setDepartmentName(p.getDepartment().getDepartmentName());
						pattern.setTerritoryLevel(p.getTerritoryLevel());
						pattern.setNoOfUser(p.getNoOfUser());
						pattern.setIsActive(p.getIsActive());
						pattern.setIsDeleted(p.getIsDeleted());
						pattern.setCreatedOn(p.getCreatedOn());
						patternList.add(pattern);
					});
					roleOutputDao.setPatterMappingList(patternList);

					if (roleId.getMenu() != null && roleId.getMenu().size() > 0) { // Set menu list

						List<MenuOutputDAO> menuList = new ArrayList<>();
						roleId.getMenu().forEach(action -> {
							menuList.add(returnUserOutputDAO(action));

						});
						roleOutputDao.setMenuList(menuList);

					}
					roleOutputDao.setRoleName(roleId.getRoleName());
					roleOutputDao.setPrefix(roleId.getPrefix());
					roleOutputDao.setRoleId(roleId.getRoleId());
				}
			}
			responseModel = CustomMessages.makeResponseModel(roleOutputDao, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			return responseModel;
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	public MenuOutputDAO returnUserOutputDAO(MenuMaster menuMaster) {

		MenuOutputDAO menuOutputDAO = new MenuOutputDAO();
		menuOutputDAO.setDisplaySrNo(menuMaster.getDisplaySrNo());
		menuOutputDAO.setMenuDescription(menuMaster.getMenuDescription());
		menuOutputDAO.setMenuId(menuMaster.getMenuId());
		menuOutputDAO.setMenuName(menuMaster.getMenuName());
		menuOutputDAO.setMenuParentId(menuMaster.getMenuParentId());
		menuOutputDAO.setMenuUrl(menuMaster.getMenuUrl());
		menuOutputDAO.setMenuIcon(menuMaster.getMenuIcon());
		menuOutputDAO.setMenuLevel(menuMaster.getMenuLevel());
		menuOutputDAO.setMenuType(menuMaster.getMenuType().name());
		return menuOutputDAO;
	}

	/**
	 * Update role and it's user details
	 * @param roleInputDAO {@code RoleInputDAO}
	 * @param request
	 * @return {@code RoleOutputDAO}
	 */
	public ResponseModel editRoleWithPrefix(RoleInputDAO roleInputDAO, HttpServletRequest request) {
		ResponseModel responseModel = null;
		try {
			if (Objects.nonNull(roleInputDAO.getRoleId())) {
				RoleMaster roleMaster = roleMasterRepository.findByRoleId(roleInputDAO.getRoleId());

				if (roleMaster != null) {
					if (roleInputDAO.getMenuIds() != null && roleInputDAO.getMenuIds().size() > 0) {
						Set<MenuMaster> menuList = menuMasterRepository
								.findByIsActiveTrueAndIsDeletedFalseAndMenuIdIn(roleInputDAO.getMenuIds());
						roleMaster.setMenu(menuList);

					}
					roleMasterRepository.save(roleMaster);
				}
//				System.out.println("Role fetched.." + roleMaster.getRoleName());

				// Delete pattern incase removed
				if (roleInputDAO.getRemovedPatternDAO().size() > 0) {
					for (PatternDAO removePatternDAO : roleInputDAO.getRemovedPatternDAO()) {
						RolePatternMapping removeRolePatternMapping = null;
						if (removePatternDAO.getRolePatternMappingId() != null) {

							// Remove user
							removeRolePatternMapping = rolePatternMappingRepository
									.findByRolePatternMappingId(removePatternDAO.getRolePatternMappingId());
							List<UserMaster> removeUserList = userMasterRepository
									.findByRolePatternMappingIdOrderByUserIdDesc(removeRolePatternMapping);
							List<UserMaster> userList = new ArrayList<>();
							removeUserList.stream().forEach((u) -> {
								u.setIsActive(false);
								u.setIsDeleted(true);
								u.setModifiedOn(new Timestamp(new Date().getTime()));
								u.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
								u.setModifiedIp(CommonUtil.getRequestIp(request));
								userList.add(u);
							});

							// Remove USer village Mapping
							List<UserVillageMapping> removeUserVillageList = userVillageMappingRepository
									.findByUserMasterIn(userList);
							List<UserVillageMapping> userVillageList = new ArrayList<>();
							removeUserVillageList.stream().forEach((uv) -> {
								uv.setIsActive(false);
								uv.setIsDeleted(true);
								uv.setModifiedOn(new Timestamp(new Date().getTime()));
								uv.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
								uv.setModifiedIp(CommonUtil.getRequestIp(request));
								userVillageList.add(uv);
							});
							userVillageMappingRepository.saveAll(userVillageList);

							// removeRolePatternMapping = rolePatternMappingRepository
							// .findByRolePatternMappingId(removePatternDAO.getRolePatternMappingId());
							removeRolePatternMapping.setIsActive(false);
							removeRolePatternMapping.setIsDeleted(true);
							removeRolePatternMapping.setModifiedOn(new Timestamp(new Date().getTime()));
							removeRolePatternMapping.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
							removeRolePatternMapping.setModifiedIp(CommonUtil.getRequestIp(request));
							rolePatternMappingRepository.save(removeRolePatternMapping);

							userMasterRepository.saveAll(userList); // Remove last
						}
					}
				}

				// Add role and pattern details into mapping table
				List<RoleOutputDAO> users = new ArrayList<>();
				if (roleMaster != null) {
					for (PatternDAO patternDAO : roleInputDAO.getPatternDAO()) {

						RolePatternMapping rolePatternMapping = null;
						if (patternDAO.getRolePatternMappingId() != null) { // Fetch pattern if available
							rolePatternMapping = rolePatternMappingRepository
									.findByRolePatternMappingId(patternDAO.getRolePatternMappingId());
							rolePatternMapping.setNoOfUser(patternDAO.getNoOfUser());
							rolePatternMapping.setTotalNoOfUser(patternDAO.getTotalNoOfUser());
							rolePatternMapping.setModifiedOn(new Timestamp(new Date().getTime()));
							rolePatternMapping.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
							rolePatternMapping.setModifiedIp(CommonUtil.getRequestIp(request));
							rolePatternMappingRepository.save(rolePatternMapping);

//							System.out.println("Role mapping updated..");
							// Generate users
							users.addAll(
									editGeneratedUserNameUsingPattern(rolePatternMapping, roleInputDAO, patternDAO));
//							System.out.println("user name created....");
						} else {
							rolePatternMapping = new RolePatternMapping();
							Optional<DepartmentMaster> department = departmentRepository
									.findById(patternDAO.getDepartmentId());
							rolePatternMapping.setDepartment(department.get());
							rolePatternMapping.setTerritoryLevel(patternDAO.getTerritoryLevel());
							rolePatternMapping.setRole(roleMaster);
							rolePatternMapping.setNoOfUser(patternDAO.getNoOfUser());
							rolePatternMapping.setTotalNoOfUser(patternDAO.getTotalNoOfUser());
							rolePatternMapping.setAssignStateLgdCode(roleInputDAO.getAssignStateLgdcode());
							rolePatternMapping.setIsActive(true);
							rolePatternMapping.setIsDeleted(false);
							rolePatternMapping.setCreatedOn(new Timestamp(new Date().getTime()));
							rolePatternMapping.setCreatedBy(CustomMessages.getUserId(request, jwtTokenUtil));
							rolePatternMapping.setCreatedIp(CommonUtil.getRequestIp(request));
							rolePatternMapping.setModifiedOn(new Timestamp(new Date().getTime()));
							rolePatternMapping.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
							rolePatternMapping.setModifiedIp(CommonUtil.getRequestIp(request));

							rolePatternMappingRepository.save(rolePatternMapping);
							System.out.println("Role mapping added..");
							// Generate users
							users.addAll(generateUserNameUsingPattern(rolePatternMapping, roleInputDAO));
							System.out.println("user name created....");
						}

					}
					responseModel = CustomMessages.makeResponseModel(users, "Pattern " + CustomMessages.RECORD_ADD,
							CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);

				} else {
					responseModel = CustomMessages.makeResponseModel(null, CustomMessages.ROLE_NAME_REQURIED,
							CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
				}
			} else {
				responseModel = CustomMessages.makeResponseModel(null,
						"Role id : " + roleInputDAO.getRoleId() + CustomMessages.NOT_FOUND,
						CustomMessages.NO_DATA_FOUND, CustomMessages.FAILED);
			}
			return responseModel;
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	public List<RoleOutputDAO> editGeneratedUserNameUsingPattern(RolePatternMapping rolePatternMapping,
			RoleInputDAO roleInputDAO, PatternDAO patternDAO) {
		List<RoleOutputDAO> output = new ArrayList<>();

		if (rolePatternMapping.getTerritoryLevel().equals("state")) {
			List<Long> assignStateLGDCodes = getAccessibleStateUM(roleInputDAO.getUserId());
			List<UserMaster> userList = new ArrayList<>();

			// Checking for next serial number if exists any
			List<UserMaster> userSerialNumber = userMasterRepository
					.findByRolePatternMappingIdOrderByUserIdDesc(rolePatternMapping);

			String[] userNameArr = userSerialNumber.size() > 0 ? userSerialNumber.get(0).getUserName().split("_", 5)
					: null;
			Long serialNumber = (userSerialNumber.size() > 0 && userNameArr.length > 0) ? Long.parseLong(userNameArr[2])
					: 0;

			if (userSerialNumber.size() == 0) {
				for (int i = 0; i < rolePatternMapping.getNoOfUser(); i++) {
					serialNumber += 1;

					for (Long state : assignStateLGDCodes) {
						// Get Serial number
						String userId = generalService.generateUserNameUsingPattern(
								rolePatternMapping.getRole().getPrefix(),
								rolePatternMapping.getDepartment().getDepartmentCode(), serialNumber, "S", state);

						UserMaster user1 = new UserMaster();
						user1.setUserName(userId);
						user1.setUserPassword(encoder.encode("Admin@123"));
						user1.setRoleId(rolePatternMapping.getRole());
						user1.setDepartmentId(rolePatternMapping.getDepartment());
						user1.setUserStateLGDCode(state.intValue());
						user1.setCreatedBy(roleInputDAO.getUserId().toString());
						user1.setModifiedBy(roleInputDAO.getUserId().toString());
						user1.setIsActive(true);
						user1.setIsDeleted(false);
						user1.setIsPasswordChanged(false);
						user1.setLastPasswordChangedDate(new Date());
						user1.setRolePatternMappingId(rolePatternMapping);
						setUserPasswordHistory(user1);
						userList.add(user1);
					}
				}
				output = returnUserNamePatternOutputDAO(userMasterRepository.saveAll(userList));

			} else if (userSerialNumber.size() == rolePatternMapping.getTotalNoOfUser()) {
				// if user count and update count is same
				List<Long> userIds = userSerialNumber.stream().map(UserMaster::getUserId).collect(Collectors.toList());
				userMasterRepository.UpdateUsersSetIsActiveAndIsDelete(userIds, true, false);
				userVillageMappingRepository.UpdateUsersSetIsActiveAndIsDeleted(userIds, true, false);

			} else if (userSerialNumber.size() > rolePatternMapping.getTotalNoOfUser()) {
				// if user count is more and request count is less , inactive extra user
				List<Long> userToBeActive = new ArrayList<>();
				List<Long> userToBeInactive = new ArrayList<>();
				for (int i = 0; i < userSerialNumber.size(); i++) {
					UserMaster user1 = userSerialNumber.get(i);
					if (i < rolePatternMapping.getTotalNoOfUser()) {
						user1.setIsActive(true);
						user1.setIsDeleted(false);
						user1.setModifiedBy(roleInputDAO.getUserId().toString());
						userToBeActive.add(user1.getUserId());
					} else {
						user1.setIsActive(false);
						user1.setIsDeleted(true);
						user1.setModifiedBy(roleInputDAO.getUserId().toString());
						userToBeInactive.add(user1.getUserId());
					}
					userList.add(user1);
				}
				userVillageMappingRepository.UpdateUsersSetIsActiveAndIsDeleted(userToBeActive, true, false);
				userVillageMappingRepository.UpdateUsersSetIsActiveAndIsDeleted(userToBeInactive, false, true);
				userMasterRepository.saveAll(userList);
				// output =
				// returnUserNamePatternOutputDAO(userMasterRepository.saveAll(userList));
			} else if (userSerialNumber.size() < rolePatternMapping.getTotalNoOfUser()) {
				List<Long> userIds = userSerialNumber.stream().map(UserMaster::getUserId).collect(Collectors.toList());
				userMasterRepository.UpdateUsersSetIsActiveAndIsDelete(userIds, true, false);
				userVillageMappingRepository.UpdateUsersSetIsActiveAndIsDeleted(userIds, true, false);
				int userCount = userSerialNumber.size();
				for (int i = patternDAO.getOldNoOfUser(); i < patternDAO.getNoOfUser(); i++) {
					serialNumber += 1;
					for (Long state : assignStateLGDCodes) {
						if (userCount < rolePatternMapping.getTotalNoOfUser()) {
							// Get Serial number
							String userId = generalService.generateUserNameUsingPattern(
									rolePatternMapping.getRole().getPrefix(),
									rolePatternMapping.getDepartment().getDepartmentCode(), serialNumber, "S", state);

							UserMaster user1 = new UserMaster();
							user1.setUserName(userId);
							user1.setUserPassword(encoder.encode("Admin@123"));
							user1.setRoleId(rolePatternMapping.getRole());
							user1.setDepartmentId(rolePatternMapping.getDepartment());
							user1.setUserStateLGDCode(state.intValue());
							user1.setCreatedBy(roleInputDAO.getUserId().toString());
							user1.setModifiedBy(roleInputDAO.getUserId().toString());
							user1.setIsActive(true);
							user1.setIsDeleted(false);
							user1.setIsPasswordChanged(false);
							user1.setLastPasswordChangedDate(new Date());
							user1.setRolePatternMappingId(rolePatternMapping);
							setUserPasswordHistory(user1);
							userList.add(user1);

							userCount++;
						}
					}
				}
				userMasterRepository.saveAll(userList);
			}

			new Thread() {
				@Override
				public void run() {
					try {
						List<UserVillageMapping> userVillageList = new ArrayList<>();
						// List<Long> villageCodes = getAccessibleVillageUM(roleInputDAO.getUserId());
						// List<VillageLgdMaster> villageLgdMaster = villageLgdMasterRepository
						// .findByVillageLgdCodeIn(villageCodes);
						// System.out.println("Village count :" + villageLgdMaster.size());
						userList.stream().forEach((user) -> {
							List<DistrictLgdMaster> districtCode = districtLgdMasterRepository
									.findByStateLgdCode_StateLgdCode(new Long(user.getUserStateLGDCode()));
							districtCode.forEach(district -> {
								List<VillageLgdMaster> villageLgdMaster = villageLgdMasterRepository
										.findByDistrictLgdCode_DistrictLgdCode(district.getDistrictLgdCode());

								villageLgdMaster.forEach(village -> {
									UserVillageMapping userVillageMapping = new UserVillageMapping();
									userVillageMapping.setUserMaster(user);
									userVillageMapping.setVillageLgdMaster(village);
									userVillageMapping.setCreatedBy(roleInputDAO.getUserId().toString());
									userVillageMapping.setModifiedBy(roleInputDAO.getUserId().toString());
									userVillageMapping.setIsActive(true);
									userVillageMapping.setIsDeleted(false);
									// userVillageList.add(userVillageMapping);
									userVillageMappingRepository.save(userVillageMapping);
								});
							});
						});
						System.out.println("Inside thread  count:" + userVillageList.size());
						// userVillageMappingRepository.saveAll(userVillageList);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();

			List<UserMaster> updatedList = userMasterRepository
					.findByRolePatternMappingIdAndIsActiveTrueOrderByUserIdDesc(rolePatternMapping);
			output = returnUserNamePatternOutputDAO(updatedList);
		} else if (rolePatternMapping.getTerritoryLevel().equals("district")) {
			List<Long> assignDistrictLGDCodes = getAccessibleDistrictUM(roleInputDAO.getUserId());
			List<UserMaster> userList = new ArrayList<>();

			// Checking for next serial number if exists any
			List<UserMaster> userSerialNumber = userMasterRepository
					.findByRolePatternMappingIdOrderByUserIdDesc(rolePatternMapping);

			String[] userNameArr = userSerialNumber.size() > 0 ? userSerialNumber.get(0).getUserName().split("_", 5)
					: null;
			Long serialNumber = (userSerialNumber.size() > 0 && userNameArr.length > 0) ? Long.parseLong(userNameArr[2])
					: 0;

			if (userSerialNumber.size() == 0) {
				for (int i = 0; i < rolePatternMapping.getNoOfUser(); i++) {
					serialNumber += 1;

					for (Long district : assignDistrictLGDCodes) {
						// Get Serial number
						String userId = generalService.generateUserNameUsingPattern(
								rolePatternMapping.getRole().getPrefix(),
								rolePatternMapping.getDepartment().getDepartmentCode(), serialNumber, "D", district);

						UserMaster user1 = new UserMaster();
						user1.setUserName(userId);
						user1.setUserPassword(encoder.encode("Admin@123"));
						user1.setRoleId(rolePatternMapping.getRole());
						user1.setDepartmentId(rolePatternMapping.getDepartment());
						user1.setUserDistrictLGDCode(district.intValue());
						user1.setCreatedBy(roleInputDAO.getUserId().toString());
						user1.setModifiedBy(roleInputDAO.getUserId().toString());
						user1.setIsActive(true);
						user1.setIsDeleted(false);
						user1.setIsPasswordChanged(false);
						user1.setLastPasswordChangedDate(new Date());
						user1.setRolePatternMappingId(rolePatternMapping);
						setUserPasswordHistory(user1);
						userList.add(user1);
					}
				}
				output = returnUserNamePatternOutputDAO(userMasterRepository.saveAll(userList));
			} else if (userSerialNumber.size() == rolePatternMapping.getTotalNoOfUser()) {
				// if user count and update count is same
				List<Long> userIds = userSerialNumber.stream().map(UserMaster::getUserId).collect(Collectors.toList());
				userMasterRepository.UpdateUsersSetIsActiveAndIsDelete(userIds, true, false);
				userVillageMappingRepository.UpdateUsersSetIsActiveAndIsDeleted(userIds, true, false);

			} else if (userSerialNumber.size() > rolePatternMapping.getTotalNoOfUser()) {
				// if user count is more and request count is less , inactive extra user
				List<Long> userToBeActive = new ArrayList<>();
				List<Long> userToBeInactive = new ArrayList<>();
				for (int i = 0; i < userSerialNumber.size(); i++) {
					UserMaster user1 = userSerialNumber.get(i);
					if (i < rolePatternMapping.getTotalNoOfUser()) {
						user1.setIsActive(true);
						user1.setIsDeleted(false);
						user1.setModifiedBy(roleInputDAO.getUserId().toString());
						userToBeActive.add(user1.getUserId());
					} else {
						user1.setIsActive(false);
						user1.setIsDeleted(true);
						user1.setModifiedBy(roleInputDAO.getUserId().toString());
						userToBeInactive.add(user1.getUserId());
					}
					userList.add(user1);
				}
				output = returnUserNamePatternOutputDAO(userMasterRepository.saveAll(userList));
			} else if (userSerialNumber.size() < rolePatternMapping.getTotalNoOfUser()) {
				List<Long> userIds = userSerialNumber.stream().map(UserMaster::getUserId).collect(Collectors.toList());
				userMasterRepository.UpdateUsersSetIsActiveAndIsDelete(userIds, true, false);
				int userCount = userSerialNumber.size();
				for (int i = patternDAO.getOldNoOfUser(); i < patternDAO.getNoOfUser(); i++) {
					serialNumber += 1;
					for (Long district : assignDistrictLGDCodes) {
						if (userCount < rolePatternMapping.getTotalNoOfUser()) {
							// Get Serial number
							String userId = generalService.generateUserNameUsingPattern(
									rolePatternMapping.getRole().getPrefix(),
									rolePatternMapping.getDepartment().getDepartmentCode(), serialNumber, "D",
									district);

							UserMaster user1 = new UserMaster();
							user1.setUserName(userId);
							user1.setUserPassword(encoder.encode("Admin@123"));
							user1.setRoleId(rolePatternMapping.getRole());
							user1.setDepartmentId(rolePatternMapping.getDepartment());
							user1.setUserDistrictLGDCode(district.intValue());
							user1.setCreatedBy(roleInputDAO.getUserId().toString());
							user1.setModifiedBy(roleInputDAO.getUserId().toString());
							user1.setIsActive(true);
							user1.setIsDeleted(false);
							user1.setIsPasswordChanged(false);
							user1.setLastPasswordChangedDate(new Date());
							user1.setRolePatternMappingId(rolePatternMapping);
							setUserPasswordHistory(user1);
							userList.add(user1);

							userCount++;
						}
					}
				}
				userMasterRepository.saveAll(userList);
			}
			new Thread() {

				@Override
				public void run() {
					try {
						List<UserVillageMapping> userVillageList = new ArrayList<>();
						System.out.println("User  count :" + userList.size());
						userList.stream().forEach((user) -> {
							// village list by district code
							List<VillageLgdMaster> villageLgdMaster = villageLgdMasterRepository
									.findByDistrictLgdCode_DistrictLgdCode(new Long(user.getUserDistrictLGDCode()));
							villageLgdMaster.forEach(village -> {
								UserVillageMapping userVillageMapping = new UserVillageMapping();
								userVillageMapping.setUserMaster(user);
								userVillageMapping.setVillageLgdMaster(village);
								userVillageMapping.setCreatedBy(roleInputDAO.getUserId().toString());
								userVillageMapping.setModifiedBy(roleInputDAO.getUserId().toString());
								userVillageMapping.setIsActive(true);
								userVillageMapping.setIsDeleted(false);
								userVillageList.add(userVillageMapping);
							});
						});
						System.out.println("Inside thread  count:" + userVillageList.size());
						userVillageMappingRepository.saveAll(userVillageList);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();

			List<UserMaster> updatedList = userMasterRepository
					.findByRolePatternMappingIdAndIsActiveTrueOrderByUserIdDesc(rolePatternMapping);
			output =

					returnUserNamePatternOutputDAO(updatedList);

		} else if (rolePatternMapping.getTerritoryLevel().equals("subdistrict")) {

			List<Long> assignSubDistrictLGDCodes = getAccessibleSubDistrictUM(roleInputDAO.getUserId());
			List<UserMaster> userList = new ArrayList<>();

			// Checking for next serial number if exists any
			List<UserMaster> userSerialNumber = userMasterRepository
					.findByRolePatternMappingIdOrderByUserIdDesc(rolePatternMapping);

			String[] userNameArr = userSerialNumber.size() > 0 ? userSerialNumber.get(0).getUserName().split("_", 5)
					: null;
			Long currentSerialNumber = (userSerialNumber.size() > 0 && userNameArr.length > 0)
					? Long.parseLong(userNameArr[2])
					: 0;

			AtomicLong serialNumber = new AtomicLong(currentSerialNumber);

			if (userSerialNumber.size() == 0) {
				for (int i = 0; i < rolePatternMapping.getNoOfUser(); i++) {
					serialNumber.getAndIncrement();
					List<Long> assignedDistrictLGDCodes = getAccessibleDistrictUM(roleInputDAO.getUserId());

					assignedDistrictLGDCodes.parallelStream().forEach(district -> {
						// for (Long subDistrict : assignSubDistrictLGDCodes) {

						List<Long> subDistrictCodesByDistrict = userVillageMappingRepository
								.getSubDistrictCodesByDistrictCodeAndUserId(roleInputDAO.getUserId(), district);
						List<UserMaster> tempDistrictList = new ArrayList<>();

						subDistrictCodesByDistrict.parallelStream().forEach(subDistrict -> {
							// for (Long subDistrict : assignSubDistrictLGDCodes) {
							// Get Serial number
							String userId = generalService.generateUserNameUsingPattern(
									rolePatternMapping.getRole().getPrefix(),
									rolePatternMapping.getDepartment().getDepartmentCode(), serialNumber.get(), "T",
									subDistrict);

							UserMaster user1 = new UserMaster();
							user1.setUserName(userId);
							user1.setUserPassword(encoder.encode("Admin@123"));
							user1.setRoleId(rolePatternMapping.getRole());
							user1.setDepartmentId(rolePatternMapping.getDepartment());
							user1.setUserTalukaLGDCode(subDistrict.intValue());
							user1.setCreatedBy(roleInputDAO.getUserId().toString());
							user1.setModifiedBy(roleInputDAO.getUserId().toString());
							user1.setIsActive(true);
							user1.setIsDeleted(false);
							user1.setIsPasswordChanged(false);
							user1.setLastPasswordChangedDate(new Date());
							user1.setRolePatternMappingId(rolePatternMapping);
							setUserPasswordHistory(user1);
							userList.add(user1);
							// }
						});
					});
				}
				output = returnUserNamePatternOutputDAO(userMasterRepository.saveAll(userList));
			} else if (userSerialNumber.size() == rolePatternMapping.getTotalNoOfUser()) {
				// if user count and update count is same
				List<Long> userIds = userSerialNumber.stream().map(UserMaster::getUserId).collect(Collectors.toList());
				userMasterRepository.UpdateUsersSetIsActiveAndIsDelete(userIds, true, false);
				userVillageMappingRepository.UpdateUsersSetIsActiveAndIsDeleted(userIds, true, false);

			} else if (userSerialNumber.size() > rolePatternMapping.getTotalNoOfUser()) {
				// if user count is more and request count is less , inactive extra user
				List<Long> userToBeActive = new ArrayList<>();
				List<Long> userToBeInactive = new ArrayList<>();
				for (int i = 0; i < userSerialNumber.size(); i++) {
					UserMaster user1 = userSerialNumber.get(i);
					if (i < rolePatternMapping.getTotalNoOfUser()) {
						user1.setIsActive(true);
						user1.setIsDeleted(false);
						user1.setModifiedBy(roleInputDAO.getUserId().toString());
						userToBeActive.add(user1.getUserId());
					} else {
						user1.setIsActive(false);
						user1.setIsDeleted(true);
						user1.setModifiedBy(roleInputDAO.getUserId().toString());
						userToBeInactive.add(user1.getUserId());
					}
					userList.add(user1);
				}
				output = returnUserNamePatternOutputDAO(userMasterRepository.saveAll(userList));
			} else if (userSerialNumber.size() < rolePatternMapping.getTotalNoOfUser()) {
				List<Long> userIds = userSerialNumber.stream().map(UserMaster::getUserId).collect(Collectors.toList());
				userMasterRepository.UpdateUsersSetIsActiveAndIsDelete(userIds, true, false);
				AtomicLong userCount = new AtomicLong(userSerialNumber.size());
				for (int i = patternDAO.getOldNoOfUser(); i < patternDAO.getNoOfUser(); i++) {

					serialNumber.getAndIncrement();

					List<Long> assignDistrictLGDCodes = getAccessibleDistrictUM(roleInputDAO.getUserId());

					assignDistrictLGDCodes.parallelStream().forEach(district -> {

						List<Long> districtSubdistrictCode = userVillageMappingRepository
								.getSubDistrictCodesByDistrictCodeAndUserId(roleInputDAO.getUserId(), district);
						List<UserMaster> tempDistrictList = new ArrayList<>();

						districtSubdistrictCode.parallelStream().forEach(subDistrict -> {
							if (userCount.get() < rolePatternMapping.getTotalNoOfUser()) {
								// Get Serial number
								String userId = generalService.generateUserNameUsingPattern(
										rolePatternMapping.getRole().getPrefix(),
										rolePatternMapping.getDepartment().getDepartmentCode(), serialNumber.get(), "T",
										subDistrict);

								UserMaster user1 = new UserMaster();
								user1.setUserName(userId);
								user1.setUserPassword(encoder.encode("Admin@123"));
								user1.setRoleId(rolePatternMapping.getRole());
								user1.setDepartmentId(rolePatternMapping.getDepartment());
								user1.setUserTalukaLGDCode(subDistrict.intValue());
								user1.setCreatedBy(roleInputDAO.getUserId().toString());
								user1.setModifiedBy(roleInputDAO.getUserId().toString());
								user1.setIsActive(true);
								user1.setIsDeleted(false);
								user1.setIsPasswordChanged(false);
								user1.setLastPasswordChangedDate(new Date());
								user1.setRolePatternMappingId(rolePatternMapping);
								setUserPasswordHistory(user1);
								userList.add(user1);

								userCount.getAndIncrement();
							}
						});
					});
					// for (Long subDistrict : assignSubDistrictLGDCodes) {
					// if (userCount < rolePatternMapping.getTotalNoOfUser()) {
					// // Get Serial number
					// String userId = generalService.generateUserNameUsingPattern(
					// rolePatternMapping.getRole().getPrefix(),
					// rolePatternMapping.getDepartment().getDepartmentCode(), serialNumber.get(),
					// "T",
					// subDistrict);

					// UserMaster user1 = new UserMaster();
					// user1.setUserName(userId);
					// user1.setUserPassword(encoder.encode("Admin@123"));
					// user1.setRoleId(rolePatternMapping.getRole());
					// user1.setDepartmentId(rolePatternMapping.getDepartment());
					// user1.setUserTalukaLGDCode(subDistrict.intValue());
					// user1.setCreatedBy(roleInputDAO.getUserId().toString());
					// user1.setModifiedBy(roleInputDAO.getUserId().toString());
					// user1.setIsActive(true);
					// user1.setIsDeleted(false);
					// user1.setIsPasswordChanged(false);
					// user1.setLastPasswordChangedDate(new Date());
					// user1.setRolePatternMappingId(rolePatternMapping);
					// setUserPasswordHistory(user1);
					// userList.add(user1);

					// userCount++;
					// }
					// }
				}
				userMasterRepository.saveAll(userList);
			}

			new Thread() {
				@Override
				public void run() {
					try {
						List<UserVillageMapping> userVillageList = new ArrayList<>();
						// List<Long> villageCodes = getAccessibleVillage(roleInputDAO.getUserId());
						// List<VillageLgdMaster> villageLgdMaster = villageLgdMasterRepository
						// .findByVillageLgdCodeIn(villageCodes);
						System.out.println("user count :" + userList.size());
						userList.stream().forEach((user) -> {
							// village list by sub district code
							List<VillageLgdMaster> villageLgdMaster = villageLgdMasterRepository
									.findBySubDistrictLgdCode_SubDistrictLgdCode(new Long(user.getUserTalukaLGDCode()));
							villageLgdMaster.forEach(village -> {
								UserVillageMapping userVillageMapping = new UserVillageMapping();
								userVillageMapping.setUserMaster(user);
								userVillageMapping.setVillageLgdMaster(village);
								userVillageMapping.setCreatedBy(roleInputDAO.getUserId().toString());
								userVillageMapping.setModifiedBy(roleInputDAO.getUserId().toString());
								userVillageMapping.setIsActive(true);
								userVillageMapping.setIsDeleted(false);
								userVillageList.add(userVillageMapping);
							});
						});
						System.out.println("Inside thread  count:" + userVillageList.size());
						userVillageMappingRepository.saveAll(userVillageList);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
			// }
			List<UserMaster> updatedList = userMasterRepository
					.findByRolePatternMappingIdAndIsActiveTrueOrderByUserIdDesc(rolePatternMapping);
			output = returnUserNamePatternOutputDAO(updatedList);
		} else if (rolePatternMapping.getTerritoryLevel().equals("village")) {

			// List<Long> assignVillageLGDCodes =
			// getAccessibleVillageUM(roleInputDAO.getUserId());
			List<UserMaster> userList = new ArrayList<>();

			// Checking for next serial number if exists any
			List<UserMaster> userSerialNumber = userMasterRepository
					.findByRolePatternMappingIdOrderByUserIdDesc(rolePatternMapping);

			String[] userNameArr = userSerialNumber.size() > 0 ? userSerialNumber.get(0).getUserName().split("_", 5)
					: null;
			Long value = (userSerialNumber.size() > 0 && userNameArr.length > 0) ? Long.parseLong(userNameArr[2]) : 0;

			AtomicLong serialNumber = new AtomicLong(value);
			if (userSerialNumber.size() == 0) {
				List<UserMaster> allSavedUser = new ArrayList<>();

				for (int i = 0; i < rolePatternMapping.getNoOfUser(); i++) {
					// serialNumber += 1;
					serialNumber.getAndIncrement();

					List<Long> assignSubDistrictLGDCodes = getAccessibleSubDistrictUM(roleInputDAO.getUserId());

					System.out.println("Editing user subdistrict wise ......");
					// for(Long subDistrict : assignSubDistrictLGDCodes) {
					assignSubDistrictLGDCodes.parallelStream().forEach(subDistrict -> {

						List<Long> subDistrictVillageCode = userVillageMappingRepository
								.getVillageCodeBySubDistrictAndUserId(roleInputDAO.getUserId(), subDistrict);
						List<UserMaster> tempSubDistrictList = new ArrayList<>();

						// for (Long village : subDistrictVillageCode) { //30,000
						subDistrictVillageCode.parallelStream().forEach(village -> {
							// Get Serial number
							String userId = generalService.generateUserNameUsingPattern(
									rolePatternMapping.getRole().getPrefix(),
									rolePatternMapping.getDepartment().getDepartmentCode(), serialNumber.get(), "V",
									village);

							UserMaster user1 = new UserMaster();
							user1.setUserName(userId);
							user1.setUserPassword(encoder.encode("Admin@123"));
							user1.setRoleId(rolePatternMapping.getRole());
							user1.setDepartmentId(rolePatternMapping.getDepartment());
							user1.setUserVillageLGDCode(village.intValue());
							user1.setCreatedBy(roleInputDAO.getUserId().toString());
							user1.setModifiedBy(roleInputDAO.getUserId().toString());
							user1.setIsActive(true);
							user1.setIsDeleted(false);
							user1.setIsPasswordChanged(false);
							user1.setLastPasswordChangedDate(new Date());
							user1.setRolePatternMappingId(rolePatternMapping);
							setUserPasswordHistory(user1);
							userList.add(user1);
							tempSubDistrictList.add(user1);

							// }
						});
						allSavedUser.addAll(userMasterRepository.saveAll(tempSubDistrictList));
						// }
					});
				}

				output = returnUserNamePatternOutputDAO(allSavedUser);
			} else if (userSerialNumber.size() == rolePatternMapping.getTotalNoOfUser()) {
				// if user count and update count is same
				List<Long> userIds = userSerialNumber.stream().map(UserMaster::getUserId).collect(Collectors.toList());
				userMasterRepository.UpdateUsersSetIsActiveAndIsDelete(userIds, true, false);
				userVillageMappingRepository.UpdateUsersSetIsActiveAndIsDeleted(userIds, true, false);

			} else if (userSerialNumber.size() > rolePatternMapping.getTotalNoOfUser()) {
				List<Long> userToBeActive = new ArrayList<>();
				List<Long> userToBeInactive = new ArrayList<>();
				for (int i = 0; i < userSerialNumber.size(); i++) {
					UserMaster user1 = userSerialNumber.get(i);
					if (i < rolePatternMapping.getTotalNoOfUser()) {
						user1.setIsActive(true);
						user1.setIsDeleted(false);
						user1.setModifiedBy(roleInputDAO.getUserId().toString());
						userToBeActive.add(user1.getUserId());
					} else {
						user1.setIsActive(false);
						user1.setIsDeleted(true);
						user1.setModifiedBy(roleInputDAO.getUserId().toString());
						userToBeInactive.add(user1.getUserId());
					}
					userList.add(user1);
				}
				output = returnUserNamePatternOutputDAO(userMasterRepository.saveAll(userList));
			} else if (userSerialNumber.size() < rolePatternMapping.getTotalNoOfUser()) {
				List<Long> userIds = userSerialNumber.stream().map(UserMaster::getUserId).collect(Collectors.toList());
				userMasterRepository.UpdateUsersSetIsActiveAndIsDelete(userIds, true, false);
				AtomicLong userCount = new AtomicLong(userSerialNumber.size());
				List<UserMaster> allSavedUser = new ArrayList<>();
				for (int i = patternDAO.getOldNoOfUser(); i < patternDAO.getNoOfUser(); i++) {
					// serialNumber += 1;
					serialNumber.getAndIncrement();

					List<Long> assignSubDistrictLGDCodes = getAccessibleSubDistrictUM(roleInputDAO.getUserId());

					System.out.println("Editing user subdistrict wise ......");
					// for(Long subDistrict : assignSubDistrictLGDCodes) {
					assignSubDistrictLGDCodes.parallelStream().forEach(subDistrict -> {

						List<Long> subDistrictVillageCode = userVillageMappingRepository
								.getVillageCodeBySubDistrictAndUserId(roleInputDAO.getUserId(), subDistrict);
						List<UserMaster> tempSubDistrictList = new ArrayList<>();

						// for (Long village : subDistrictVillageCode) { //30,000
						subDistrictVillageCode.parallelStream().forEach(village -> {
							// Get Serial number
							if (userCount.get() < rolePatternMapping.getTotalNoOfUser()) {
								String userId = generalService.generateUserNameUsingPattern(
										rolePatternMapping.getRole().getPrefix(),
										rolePatternMapping.getDepartment().getDepartmentCode(), serialNumber.get(), "V",
										village);

								UserMaster user1 = new UserMaster();
								user1.setUserName(userId);
								user1.setUserPassword(encoder.encode("Admin@123"));
								user1.setRoleId(rolePatternMapping.getRole());
								user1.setDepartmentId(rolePatternMapping.getDepartment());
								user1.setUserVillageLGDCode(village.intValue());
								user1.setCreatedBy(roleInputDAO.getUserId().toString());
								user1.setModifiedBy(roleInputDAO.getUserId().toString());
								user1.setIsActive(true);
								user1.setIsDeleted(false);
								user1.setIsPasswordChanged(false);
								user1.setLastPasswordChangedDate(new Date());
								user1.setRolePatternMappingId(rolePatternMapping);
								setUserPasswordHistory(user1);
								userList.add(user1);
								userList.add(user1);
								tempSubDistrictList.add(user1);

								userCount.getAndIncrement();
							}
							// }
						});
						allSavedUser.addAll(userMasterRepository.saveAll(tempSubDistrictList));
						// }
					});
				}
				output = returnUserNamePatternOutputDAO(allSavedUser);
			}

			new Thread() {
				@Override
				public void run() {
					try {
						List<UserVillageMapping> userVillageList = new ArrayList<>();
						System.out.println("User count :" + userList.size());

						userList.stream().forEach((user) -> {
							// villageLgdMaster.forEach(village -> {
							UserVillageMapping userVillageMapping = new UserVillageMapping();
							userVillageMapping.setUserMaster(user);
							userVillageMapping.setVillageLgdMaster(villageLgdMasterRepository
									.findByVillageLgdCode(new Long(user.getUserVillageLGDCode())));
							userVillageMapping.setCreatedBy(roleInputDAO.getUserId().toString());
							userVillageMapping.setModifiedBy(roleInputDAO.getUserId().toString());
							userVillageMapping.setIsActive(true);
							userVillageMapping.setIsDeleted(false);
							userVillageList.add(userVillageMapping);
							// });
						});
						System.out.println("Inside thread count:" + userVillageList.size());

						// Add user mapping in chunk
						int size = userVillageList.size();
						int counter = 0;
						List<UserVillageMapping> temp = new ArrayList<>();
						for (UserVillageMapping bm : userVillageList) {
							temp.add(bm);
							if ((counter + 1) % 500 == 0 || (counter + 1) == size) {
								userVillageMappingRepository.saveAll(temp);
								temp.clear();
							}
							counter++;
						}

						// userVillageMappingRepository.saveAll(userVillageList);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();

			List<UserMaster> updatedList = userMasterRepository
					.findByRolePatternMappingIdAndIsActiveTrueOrderByUserIdDesc(rolePatternMapping);
			output = returnUserNamePatternOutputDAO(updatedList);
		}
		return output;
	}

	public RoleOutputDAO returnRoleMappingOutputDAO(RoleMaster roleMaster, HttpServletRequest request) {
		RoleOutputDAO roleOutputDAO = new RoleOutputDAO();
		roleOutputDAO.setRoleId(roleMaster.getRoleId());
		roleOutputDAO.setRoleName(roleMaster.getRoleName());

		List<UserMaster> roleCount = userMasterRepository.findByRoleIdAndIsDeletedFalse(roleMaster);
		roleOutputDAO.setTotalUserCount(roleCount.size());

		List<RolePatternMapping> roleMapping = rolePatternMappingRepository.findAllByRole(roleMaster);
		roleMapping.forEach(rolePatternMap -> {
			roleOutputDAO.setTerritoryLevel(rolePatternMap.getTerritoryLevel());
			roleOutputDAO.setNoOfUsers(rolePatternMap.getNoOfUser()
					+ (roleOutputDAO.getNoOfUsers() != null ? roleOutputDAO.getNoOfUsers() : 0));
		});

		// Optional<UserMaster> createdBy = userMasterRepository.findByUserId(new
		// Long(roleMaster.getModifiedBy()));

		roleOutputDAO.setCreatedOn(roleMaster.getCreatedOn());
		roleOutputDAO.setModifiedOn(roleMaster.getModifiedOn());
		roleOutputDAO.setCreatedBy(CustomMessages.getUserId(request, jwtTokenUtil));
		roleOutputDAO.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
		roleOutputDAO.setCreatedIp(roleMaster.getCreatedIp());
		roleOutputDAO.setModifiedIp(roleMaster.getModifiedIp());

		roleOutputDAO.setIsActive(roleMaster.getIsActive());
		roleOutputDAO.setIsDeleted(roleMaster.getIsDeleted());
		return roleOutputDAO;
	}

	public List<Long> getAccessibleStateUM(Long userId) {
		// List<UserVillageMapping> userVillageList = userVillageMappingRepository
		// .findByUserMaster(userMasterRepository.findByUserId(userId).get());
		// Set<Long> stateSet = new HashSet<>();
		//
		// userVillageList.forEach(userVillage -> {
		// VillageLgdMaster villageMaster = userVillage.getVillageLgdMaster();
		// stateSet.add(villageMaster.getStateLgdCode().getStateLgdCode());
		// });
		//
		// List<Long> stateList = new ArrayList<>();
		// stateList.addAll(stateSet);

		List<Long> stateList = userVillageMappingRepository.getStateCodesById(userId);
		return stateList;
	}

	public List<Long> getAccessibleDistrictUM(Long userId) {
		// List<UserVillageMapping> userVillageList = userVillageMappingRepository
		// .findByUserMaster(userMasterRepository.findByUserId(userId).get());
		// Set<Long> districtSet = new HashSet<>();
		// userVillageList.forEach(userVillage -> {
		// VillageLgdMaster villageMaster = userVillage.getVillageLgdMaster();
		// districtSet.add(villageMaster.getDistrictLgdCode().getDistrictLgdCode());
		// });
		//
		// List<Long> districtList = new ArrayList<>();
		// districtList.addAll(districtSet);

		List<Long> districtList = userVillageMappingRepository.getDistrictCodesById(userId);
		return districtList;
	}

	public List<Long> getAccessibleSubDistrictUM(Long userId) {
		// List<UserVillageMapping> userVillageList = userVillageMappingRepository
		// .findByUserMaster(userMasterRepository.findByUserId(userId).get());
		// Set<Long> subDistrictSet = new HashSet<>();
		//
		// userVillageList.forEach(userVillage -> {
		// VillageLgdMaster villageMaster = userVillage.getVillageLgdMaster();
		// subDistrictSet.add(villageMaster.getSubDistrictLgdCode().getSubDistrictLgdCode());
		// });
		//
		// List<Long> subDistrictList = new ArrayList<>();
		// subDistrictList.addAll(subDistrictSet);

		List<Long> subDistrictList = userVillageMappingRepository.getSubDistrictCodesById(userId);
		return subDistrictList;
	}

	public List<Long> getAccessibleVillageUM(Long userId) {
		// List<UserVillageMapping> userVillageList = userVillageMappingRepository
		// .findByUserMaster(userMasterRepository.findByUserId(userId).get());
		// Set<Long> villageSet = new HashSet<>();
		//
		// userVillageList.forEach(userVillage -> {
		// VillageLgdMaster villageMaster = userVillage.getVillageLgdMaster();
		// villageSet.add(villageMaster.getVillageLgdCode());
		// });
		//
		// List<Long> villageList = new ArrayList<>();
		// villageList.addAll(villageSet);

		List<Long> villageList = userVillageMappingRepository.getVillageCodesByUserId(userId);
		return villageList;
	}

	public void setUserPasswordHistory(UserMaster userMaster) {
		try {
			Gson gson = new Gson();
			JsonArray passwordHistory = new JsonArray();
			if (userMaster.getPasswordHistory() != null) {
				passwordHistory = gson.fromJson(userMaster.getPasswordHistory().toString(), JsonArray.class);
			}
			passwordHistory.add(userMaster.getUserPassword());
			if (passwordHistory.size() > 3) {
				passwordHistory.remove(0);
			}
			userMaster.setPasswordHistory(passwordHistory.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create role along with users and it's boundary with permission
	 * @param roleInputDAO {@code RoleInputDAO}
	 * @param request
	 * @return {@code RoleOutputDAO}
	 */
	public ResponseModel addRolePatternWithPrefixNew(RoleInputDAO roleInputDAO, HttpServletRequest request) {
		ResponseModel responseModel = null;
		try {
			if (Objects.nonNull(roleInputDAO.getRoleName())) {
				RoleMaster role = new RoleMaster();
				role.setRoleName(roleInputDAO.getRoleName());
				role.setIsActive(true);
				role.setIsDeleted(false);
				role.setIsDefault(false);
				role.setCode(role.getRoleName().trim().toUpperCase());
				role.setCreatedOn(new Timestamp(new Date().getTime()));
				role.setModifiedOn(new Timestamp(new Date().getTime()));
				role.setCreatedBy(CustomMessages.getUserId(request, jwtTokenUtil));
				role.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
				role.setCreatedIp(CommonUtil.getRequestIp(request));
				role.setModifiedIp(CommonUtil.getRequestIp(request));
				role.setPrefix(roleInputDAO.getPrefix().trim().toUpperCase());
				role.setStateLgdCode(roleInputDAO.getAssignStateLgdcode());
				RoleMaster roleMaster = roleMasterRepository.save(role);
				System.out.println("ROle created..");

				if (roleInputDAO.getMenuIds() != null && roleInputDAO.getMenuIds().size() > 0) {
					Set<MenuMaster> menuList = menuMasterRepository
							.findByIsActiveTrueAndIsDeletedFalseAndMenuIdIn(roleInputDAO.getMenuIds());
					Set<Long> ids = menuList.stream().map(x -> x.getMenuId()).collect(Collectors.toSet());

					roleInputDAO.getMenuIds().removeAll(ids);
					System.out.println(ids);
					System.out.println(roleInputDAO.getMenuIds());
					// roleInputDAO.getMenuIds()
					// menuList
					role.setMenu(menuList);

					if (roleInputDAO.getMenuIds() != null && roleInputDAO.getMenuIds().size() > 0) {
						List<RoleMenuMasterMapping> finalList = new ArrayList<RoleMenuMasterMapping>();
						menuList.forEach(action -> {
							RoleMenuMasterMapping roleMenuMasterMapping = new RoleMenuMasterMapping();
							roleMenuMasterMapping.setMenu(action);
							roleMenuMasterMapping.setRole(role);
							roleMenuMasterMapping.setIsActive(true);
							roleMenuMasterMapping.setIsDeleted(false);
							roleMenuMasterMapping.setCreatedOn(new Timestamp(new Date().getTime()));
							roleMenuMasterMapping.setModifiedOn(new Timestamp(new Date().getTime()));
							roleMenuMasterMapping.setCreatedBy(CustomMessages.getUserId(request, jwtTokenUtil));
							roleMenuMasterMapping.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
							roleMenuMasterMapping.setCreatedIp(CommonUtil.getRequestIp(request));
							roleMenuMasterMapping.setModifiedIp(CommonUtil.getRequestIp(request));
							roleMenuMasterMapping.setIsAdd(false);
							roleMenuMasterMapping.setIsEdit(false);
							roleMenuMasterMapping.setIsView(false);
							roleMenuMasterMapping.setIsDelete(false);
							finalList.add(roleMenuMasterMapping);
						});

						roleInputDAO.getMenuIds().forEach(action -> {

							finalList.forEach(action2 -> {

								Long idAdd = Long.sum(action2.getMenu().getMenuId(), ChildMenuEnum.Add.getValue());
								Long idEdit = Long.sum(action2.getMenu().getMenuId(), ChildMenuEnum.Edit.getValue());
								Long idDelete = Long.sum(action2.getMenu().getMenuId(),
										ChildMenuEnum.Delete.getValue());
								Long idView = Long.sum(action2.getMenu().getMenuId(), ChildMenuEnum.View.getValue());

								if (idAdd.equals(action)) {
									action2.setIsAdd(true);
									System.out.println(idAdd + "ADD ==>" + (action));
								} else if (idEdit.equals(action)) {
									action2.setIsEdit(true);
									System.out.println(idEdit + "edit ==>" + (action));
								} else if (idDelete.equals(action)) {
									System.out.println(idDelete + "delete ==>" + (action));

									action2.setIsDelete(true);

								} else if (idView.equals(action)) {
									System.out.println(idView + "view ==>" + (action));

									action2.setIsView(true);

								}
							});

							// if(action) {
							//
							// }

						});
						if (finalList != null && finalList.size() > 0) {
							roleMenuMasterMappingRepository.saveAll(finalList);
						}

					}

				}

				// Add role and pattern details into mapping table
				List<RoleOutputDAO> users = new ArrayList<>();
				if (roleMaster != null) {

					for (PatternDAO patternDAO : roleInputDAO.getPatternDAO()) {
						RolePatternMapping rolePatternMapping = new RolePatternMapping();
						Optional<DepartmentMaster> department = departmentRepository
								.findById(patternDAO.getDepartmentId());
						rolePatternMapping.setDepartment(department.get());
						rolePatternMapping.setTerritoryLevel(patternDAO.getTerritoryLevel());
						rolePatternMapping.setRole(role);
						rolePatternMapping.setNoOfUser(patternDAO.getNoOfUser());
						rolePatternMapping.setTotalNoOfUser(patternDAO.getTotalNoOfUser());
						rolePatternMapping.setAssignStateLgdCode(roleInputDAO.getAssignStateLgdcode());
						rolePatternMapping.setIsActive(true);
						rolePatternMapping.setIsDeleted(false);
						rolePatternMapping.setCreatedOn(new Timestamp(new Date().getTime()));
						rolePatternMapping.setModifiedOn(new Timestamp(new Date().getTime()));
						rolePatternMapping.setCreatedBy(CustomMessages.getUserId(request, jwtTokenUtil));
						rolePatternMapping.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
						rolePatternMapping.setCreatedIp(CommonUtil.getRequestIp(request));
						rolePatternMapping.setModifiedIp(CommonUtil.getRequestIp(request));
						rolePatternMappingRepository.save(rolePatternMapping);
						System.out.println("Role mapping added..");
						// Generate users
						users.addAll(generateUserNameUsingPattern(rolePatternMapping, roleInputDAO));
						System.out.println("user name created....");
					}
					responseModel = CustomMessages.makeResponseModel(users, "Pattern " + CustomMessages.RECORD_ADD,
							CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);

				} else {
					responseModel = CustomMessages.makeResponseModel(null, CustomMessages.ROLE_NAME_REQURIED,
							CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
				}
			}
			return responseModel;
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	@Transactional
	public ResponseModel editRolePatternWithPrefixNew(RoleInputDAO roleInputDAO, HttpServletRequest request) {
		ResponseModel responseModel = null;
		try {
			if (Objects.nonNull(roleInputDAO.getRoleId())) {
				RoleMaster roleMaster = roleMasterRepository.findByRoleId(roleInputDAO.getRoleId());

				if (roleMaster != null) {
					if (roleInputDAO.getMenuIds() != null && roleInputDAO.getMenuIds().size() > 0) {
						Set<MenuMaster> menuList = menuMasterRepository
								.findByIsActiveTrueAndIsDeletedFalseAndMenuIdIn(roleInputDAO.getMenuIds());
						Set<Long> ids = menuList.stream().map(x -> x.getMenuId()).collect(Collectors.toSet());

						roleInputDAO.getMenuIds().removeAll(ids);
						System.out.println(ids);
						System.out.println(roleInputDAO.getMenuIds());
						roleMaster.setMenu(menuList);

						List<RoleMenuMasterMapping> roleMenuMaster = roleMenuMasterMappingRepository
								.findByRoleAndIsActiveAndIsDeleted(roleMaster, true, false);
						if (roleMenuMaster != null && roleMenuMaster.size() > 0) {
							roleMenuMaster.forEach(action -> {
								action.setIsActive(false);
								action.setIsDeleted(true);

							});
							roleMenuMasterMappingRepository.saveAll(roleMenuMaster);
						}

						if (roleInputDAO.getMenuIds() != null && roleInputDAO.getMenuIds().size() > 0) {
							List<RoleMenuMasterMapping> finalList = new ArrayList<RoleMenuMasterMapping>();
							menuList.forEach(action -> {
								RoleMenuMasterMapping roleMenuMasterMapping = new RoleMenuMasterMapping();
								roleMenuMasterMapping.setMenu(action);
								roleMenuMasterMapping.setRole(roleMaster);
								roleMenuMasterMapping.setIsActive(true);
								roleMenuMasterMapping.setIsDeleted(false);
								roleMenuMasterMapping.setCreatedOn(new Timestamp(new Date().getTime()));
								roleMenuMasterMapping.setModifiedOn(new Timestamp(new Date().getTime()));
								roleMenuMasterMapping.setCreatedBy(CustomMessages.getUserId(request, jwtTokenUtil));
								roleMenuMasterMapping.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
								roleMenuMasterMapping.setCreatedIp(CommonUtil.getRequestIp(request));
								roleMenuMasterMapping.setModifiedIp(CommonUtil.getRequestIp(request));
								roleMenuMasterMapping.setIsAdd(false);
								roleMenuMasterMapping.setIsEdit(false);
								roleMenuMasterMapping.setIsView(false);
								roleMenuMasterMapping.setIsDelete(false);
								finalList.add(roleMenuMasterMapping);
							});

							roleInputDAO.getMenuIds().forEach(action -> {

								finalList.forEach(action2 -> {
									Long idAdd = Long.sum(action2.getMenu().getMenuId(), ChildMenuEnum.Add.getValue());
									Long idEdit = Long.sum(action2.getMenu().getMenuId(),
											ChildMenuEnum.Edit.getValue());
									Long idDelete = Long.sum(action2.getMenu().getMenuId(),
											ChildMenuEnum.Delete.getValue());
									Long idView = Long.sum(action2.getMenu().getMenuId(),
											ChildMenuEnum.View.getValue());

									if (idAdd.equals(action)) {
										action2.setIsAdd(true);
										System.out.println(idAdd + "ADD ==>" + (action));
									} else if (idEdit.equals(action)) {
										action2.setIsEdit(true);
										System.out.println(idEdit + "edit ==>" + (action));
									} else if (idDelete.equals(action)) {
										System.out.println(idDelete + "delete ==>" + (action));

										action2.setIsDelete(true);

									} else if (idView.equals(action)) {
										System.out.println(idView + "view ==>" + (action));

										action2.setIsView(true);

									}
								});

								// if(action) {
								//
								// }

							});
							if (finalList != null && finalList.size() > 0) {
								roleMenuMasterMappingRepository.saveAll(finalList);
							}

						}
					}
					roleMasterRepository.save(roleMaster);
				}
				System.out.println("Role fetched.." + roleMaster.getRoleName());

				// Delete pattern incase removed
				String userId = CustomMessages.getUserId(request, jwtTokenUtil);
				if (roleInputDAO.getRemovedPatternDAO().size() > 0) {
					for (PatternDAO removePatternDAO : roleInputDAO.getRemovedPatternDAO()) {
						RolePatternMapping removeRolePatternMapping = null;
						if (removePatternDAO.getRolePatternMappingId() != null) {
							System.out.println("ROle pattern : " + removePatternDAO.getRolePatternMappingId());
//							List<Long> users = userMasterRepository
//									.findUserIdByRolePatternMappingId(removePatternDAO.getRolePatternMappingId());

//							new Thread() {
//								@Override
//								public void run() {
//									// change user village mapping status
//									userVillageMappingRepository.updateUserVillageMappingStatus(
//											true, false, users, userId);
//
//								}
//							}.start();
//
//							// Remove user
//							userMasterRepository.updateUserStatusAndDelete(true,
//									false, users, userId);
//							
//							// Change role pattern mapping status
//							rolePatternMappingRepository.updateRolePatternMappingStatusByRolePatternMappingIdAndDelete(
//							true, false,removePatternDAO.getRolePatternMappingId(),
//							userId);

							removeRolePatternMapping = rolePatternMappingRepository
									.findByRolePatternMappingId(removePatternDAO.getRolePatternMappingId());
							List<UserMaster> removeUserList = userMasterRepository
									.findByRolePatternMappingIdOrderByUserIdDesc(removeRolePatternMapping);
							List<UserMaster> userList = new ArrayList<>();
							removeUserList.stream().forEach((u) -> {
								u.setIsActive(false);
								u.setIsDeleted(true);
								u.setModifiedOn(new Timestamp(new Date().getTime()));
								u.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
								u.setModifiedIp(CommonUtil.getRequestIp(request));
								userList.add(u);
							});

							new Thread() {
								@Override
								public void run() {
//							 Remove USer village Mapping
									List<UserVillageMapping> removeUserVillageList = userVillageMappingRepository
											.findByUserMasterIn(userList);
									List<UserVillageMapping> userVillageList = new ArrayList<>();
									removeUserVillageList.stream().forEach((uv) -> {
										uv.setIsActive(false);
										uv.setIsDeleted(true);
										uv.setModifiedOn(new Timestamp(new Date().getTime()));
										uv.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
										uv.setModifiedIp(CommonUtil.getRequestIp(request));
										userVillageList.add(uv);
									});
									userVillageMappingRepository.saveAll(userVillageList);
								}
							}.start();

							removeRolePatternMapping = rolePatternMappingRepository
									.findByRolePatternMappingId(removePatternDAO.getRolePatternMappingId());
							removeRolePatternMapping.setIsActive(false);
							removeRolePatternMapping.setIsDeleted(true);
							removeRolePatternMapping.setModifiedOn(new Timestamp(new Date().getTime()));
							removeRolePatternMapping.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
							removeRolePatternMapping.setModifiedIp(CommonUtil.getRequestIp(request));
							rolePatternMappingRepository.save(removeRolePatternMapping);

							userMasterRepository.saveAll(userList); // Remove last
						}
					}
				}

				// Add role and pattern details into mapping table
				List<RoleOutputDAO> users = new ArrayList<>();
				if (roleMaster != null) {
					for (PatternDAO patternDAO : roleInputDAO.getPatternDAO()) {

						RolePatternMapping rolePatternMapping = null;
						if (patternDAO.getRolePatternMappingId() != null) { // Fetch pattern if available
							rolePatternMapping = rolePatternMappingRepository
									.findByRolePatternMappingId(patternDAO.getRolePatternMappingId());
							rolePatternMapping.setNoOfUser(patternDAO.getNoOfUser());
							rolePatternMapping.setTotalNoOfUser(patternDAO.getTotalNoOfUser());
							rolePatternMapping.setModifiedOn(new Timestamp(new Date().getTime()));
							rolePatternMapping.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
							rolePatternMapping.setModifiedIp(CommonUtil.getRequestIp(request));
							rolePatternMappingRepository.save(rolePatternMapping);

							System.out.println("Role mapping updated..");
							// Generate users
							users.addAll(
									editGeneratedUserNameUsingPattern(rolePatternMapping, roleInputDAO, patternDAO));
							System.out.println("user name created....");
						} else {
							rolePatternMapping = new RolePatternMapping();
							Optional<DepartmentMaster> department = departmentRepository
									.findById(patternDAO.getDepartmentId());
							rolePatternMapping.setDepartment(department.get());
							rolePatternMapping.setTerritoryLevel(patternDAO.getTerritoryLevel());
							rolePatternMapping.setRole(roleMaster);
							rolePatternMapping.setNoOfUser(patternDAO.getNoOfUser());
							rolePatternMapping.setTotalNoOfUser(patternDAO.getTotalNoOfUser());
							rolePatternMapping.setAssignStateLgdCode(roleInputDAO.getAssignStateLgdcode());
							rolePatternMapping.setIsActive(true);
							rolePatternMapping.setIsDeleted(false);
							rolePatternMapping.setCreatedOn(new Timestamp(new Date().getTime()));
							rolePatternMapping.setCreatedBy(CustomMessages.getUserId(request, jwtTokenUtil));
							rolePatternMapping.setCreatedIp(CommonUtil.getRequestIp(request));
							rolePatternMapping.setModifiedOn(new Timestamp(new Date().getTime()));
							rolePatternMapping.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
							rolePatternMapping.setModifiedIp(CommonUtil.getRequestIp(request));

							rolePatternMappingRepository.save(rolePatternMapping);
							System.out.println("Role mapping added..");
							// Generate users
							users.addAll(generateUserNameUsingPattern(rolePatternMapping, roleInputDAO));
							System.out.println("user name created....");
						}

					}
					responseModel = CustomMessages.makeResponseModel(users, "Pattern " + CustomMessages.RECORD_ADD,
							CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);

				} else {
					responseModel = CustomMessages.makeResponseModel(null, CustomMessages.ROLE_NAME_REQURIED,
							CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
				}
			} else {
				responseModel = CustomMessages.makeResponseModel(null,
						"Role id : " + roleInputDAO.getRoleId() + CustomMessages.NOT_FOUND,
						CustomMessages.NO_DATA_FOUND, CustomMessages.FAILED);
			}
			return responseModel;
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * get role and pattern details by role id for permission
	 * @param roleId
	 * @param request
	 * @return {@code RoleOutputDAO} role pattern with user details
	 */
	public ResponseModel getRolePatternUserDetailsNew(Long role, HttpServletRequest request) {
		ResponseModel responseModel = null;
		try {
			RoleOutputDAO roleOutputDao = new RoleOutputDAO();
			RoleMaster roleId = roleMasterRepository.findByRoleIdAndIsDeletedFalse(role);
			if (roleId != null) {
				List<RolePatternMapping> rolePatternMaster = rolePatternMappingRepository
						.findAllByRoleAndIsDeletedFalse(roleId);
				if (rolePatternMaster != null) {
					List<UserMaster> users = userMasterRepository.findByRoleIdAndIsDeletedFalse(roleId);

					List<UserOutputDAO> userList = new ArrayList<>();
					users.stream().forEach((u) -> { // Set Users
						UserOutputDAO userDao = new UserOutputDAO();
						userDao.setUserId(u.getUserId());
						userDao.setUserName(u.getUserName());
						if (u.getUserStateLGDCode() != null) {
							userDao.setUserStateLGDCode(u.getUserStateLGDCode());
							StateLgdMaster state = stateLgdMasterRepository
									.findByStateLgdCode(new Long(u.getUserStateLGDCode()));
							userDao.setStateName(state.getStateName());
						}

						if (u.getUserDistrictLGDCode() != null) {
							userDao.setUserDistrictLGDCode(u.getUserDistrictLGDCode());
							DistrictLgdMaster district = districtLgdMasterRepository
									.findByDistrictLgdCode(new Long(u.getUserDistrictLGDCode()));
							userDao.setDistrictName(district.getDistrictName());
						}

						if (u.getUserTalukaLGDCode() != null) {
							userDao.setUserTalukaLGDCode(u.getUserTalukaLGDCode());
							SubDistrictLgdMaster subDistrict = subDistrictLgdMasterRepository
									.findBySubDistrictLgdCode(new Long(u.getUserTalukaLGDCode()));
							userDao.setTalukaName(subDistrict.getSubDistrictName());
						}

						if (u.getUserVillageLGDCode() != null) {
							userDao.setUserVillageLGDCode(
									u.getUserVillageLGDCode() != null ? u.getUserVillageLGDCode() : null);
							VillageLgdMaster village = villageLgdMasterRepository
									.findByVillageLgdCode(new Long(u.getUserVillageLGDCode()));
							userDao.setVillageName(village.getVillageName());
						}
						userDao.setIsActive(u.getIsActive());
						userList.add(userDao);
					});
					roleOutputDao.setUserList(userList);

					List<PatternDAO> patternList = new ArrayList<>(); // Set role Pattern Mapping
					rolePatternMaster.stream().forEach((p) -> {
						PatternDAO pattern = new PatternDAO();
						pattern.setRolePatternMappingId(p.getRolePatternMappingId());
						pattern.setDepartmentId(p.getDepartment().getDepartmentId());
						pattern.setDepartmentName(p.getDepartment().getDepartmentName());
						pattern.setTerritoryLevel(p.getTerritoryLevel());
						pattern.setNoOfUser(p.getNoOfUser());
						pattern.setIsActive(p.getIsActive());
						pattern.setIsDeleted(p.getIsDeleted());
						pattern.setCreatedOn(p.getCreatedOn());
						patternList.add(pattern);
					});
					roleOutputDao.setPatterMappingList(patternList);

					if (roleId.getMenu() != null && roleId.getMenu().size() > 0) { // Set menu list
						List<MenuOutputDAO> menuList = new ArrayList<>();
						// start
						List<RoleMenuMasterMapping> roleMenuMaster = roleMenuMasterMappingRepository
								.findByRoleAndIsActiveAndIsDeleted(roleId, true, false);
						roleMenuMaster.forEach(action -> {
							menuList.add(returnUserOutputDAO(action.getMenu()));

						});
						List<MenuMaster> addList = new ArrayList<>();
						if (roleMenuMaster != null && roleMenuMaster.size() > 0) {
							Set<Long> menuIds = roleMenuMaster.stream().map(x -> x.getMenu().getMenuId())
									.collect(Collectors.toSet());
							// Set<Long> parnetMenuIdsNot=new HashSet();
							Set<Long> parnetMenuIds = roleMenuMaster.stream().map(x -> x.getMenu().getMenuParentId())
									.collect(Collectors.toSet());
							;

							if (menuIds != null && menuIds.size() > 0) {
								menuIds.removeIf(Objects::isNull);
								parnetMenuIds.removeIf(Objects::isNull);
								System.out.println(menuIds);
								System.out.println(parnetMenuIds);
								menuIds.removeAll(parnetMenuIds);
								System.out.println(menuIds);
								roleMenuMaster.forEach(action -> {
									menuIds.forEach(action2 -> {
										if (action.getMenu().getMenuId().equals(action2)) {

											if (action.getIsAdd().equals(true)) {
												menuList.add(CommonUtil.returnMenuOutputDAO(action.getMenu(),
														action.getMenu().getMenuId() + ChildMenuEnum.Add.getValue(),
														ChildMenuEnum.Add.toString()));
											}
											if (action.getIsEdit().equals(true)) {
												menuList.add(CommonUtil.returnMenuOutputDAO(action.getMenu(),
														action.getMenu().getMenuId() + ChildMenuEnum.Edit.getValue(),
														ChildMenuEnum.Edit.toString()));
											}
											if (action.getIsDelete().equals(true)) {
												menuList.add(CommonUtil.returnMenuOutputDAO(action.getMenu(),
														action.getMenu().getMenuId() + ChildMenuEnum.Delete.getValue(),
														ChildMenuEnum.Delete.toString()));
											}
											if (action.getIsView().equals(true)) {
												menuList.add(CommonUtil.returnMenuOutputDAO(action.getMenu(),
														action.getMenu().getMenuId() + ChildMenuEnum.View.getValue(),
														ChildMenuEnum.View.toString()));
											}

										}
									});
								});

							}

						}
						// end
						roleOutputDao.setMenuList(menuList);

						// old code start
						// roleId.getMenu().forEach(action -> {
						// menuList.add(returnUserOutputDAO(action));
						//
						// });
						// roleOutputDao.setMenuList(menuList);
						// old code end
					}
					roleOutputDao.setRoleName(roleId.getRoleName());
					roleOutputDao.setPrefix(roleId.getPrefix());
					roleOutputDao.setRoleId(roleId.getRoleId());
				}
			}
			responseModel = CustomMessages.makeResponseModel(roleOutputDao, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			return responseModel;
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * @param roleInputDAO
	 * @param request
	 * @return {@code RoleMenuMasterMapping}
	 */
	public ResponseModel editRoleMenus(RoleInputDAO roleInputDAO, HttpServletRequest request) {
		ResponseModel responseModel = null;
		try {
			if (Objects.nonNull(roleInputDAO.getRoleId())) {
				RoleMaster roleMaster = roleMasterRepository.findByRoleId(roleInputDAO.getRoleId());
				List<RoleMenuMasterMapping> finalList = new ArrayList<RoleMenuMasterMapping>();
				if (roleMaster != null) {
					if (roleInputDAO.getMenuIds() != null && roleInputDAO.getMenuIds().size() > 0) {
						Set<MenuMaster> menuList = menuMasterRepository
								.findByIsActiveTrueAndIsDeletedFalseAndMenuIdIn(roleInputDAO.getMenuIds());
						roleMaster.setMenu(menuList);
						Set<Long> ids = menuList.stream().map(x -> x.getMenuId()).collect(Collectors.toSet());

						roleInputDAO.getMenuIds().removeAll(ids);

						List<RoleMenuMasterMapping> roleMenuMaster = roleMenuMasterMappingRepository
								.findByRoleAndIsActiveAndIsDeleted(roleMaster, true, false);
						if (roleMenuMaster != null && roleMenuMaster.size() > 0) {
							roleMenuMaster.forEach(action -> {
								action.setIsActive(false);
								action.setIsDeleted(true);

							});
							roleMenuMasterMappingRepository.saveAll(roleMenuMaster);
						}
						
						if (roleInputDAO.getMenuIds() != null && roleInputDAO.getMenuIds().size() > 0) {
							
							menuList.forEach(action -> {
								RoleMenuMasterMapping roleMenuMasterMapping = new RoleMenuMasterMapping();
								roleMenuMasterMapping.setMenu(action);
								roleMenuMasterMapping.setRole(roleMaster);
								roleMenuMasterMapping.setIsActive(true);
								roleMenuMasterMapping.setIsDeleted(false);
								roleMenuMasterMapping.setCreatedOn(new Timestamp(new Date().getTime()));
								roleMenuMasterMapping.setModifiedOn(new Timestamp(new Date().getTime()));
								roleMenuMasterMapping.setCreatedBy(CustomMessages.getUserId(request, jwtTokenUtil));
								roleMenuMasterMapping.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
								roleMenuMasterMapping.setCreatedIp(CommonUtil.getRequestIp(request));
								roleMenuMasterMapping.setModifiedIp(CommonUtil.getRequestIp(request));
								roleMenuMasterMapping.setIsAdd(false);
								roleMenuMasterMapping.setIsEdit(false);
								roleMenuMasterMapping.setIsView(false);
								roleMenuMasterMapping.setIsDelete(false);
								finalList.add(roleMenuMasterMapping);
							});

							roleInputDAO.getMenuIds().forEach(action -> {

								finalList.forEach(action2 -> {
									Long idAdd = Long.sum(action2.getMenu().getMenuId(), ChildMenuEnum.Add.getValue());
									Long idEdit = Long.sum(action2.getMenu().getMenuId(),
											ChildMenuEnum.Edit.getValue());
									Long idDelete = Long.sum(action2.getMenu().getMenuId(),
											ChildMenuEnum.Delete.getValue());
									Long idView = Long.sum(action2.getMenu().getMenuId(),
											ChildMenuEnum.View.getValue());

									if (idAdd.equals(action)) {
										action2.setIsAdd(true);
									} else if (idEdit.equals(action)) {
										action2.setIsEdit(true);
									} else if (idDelete.equals(action)) {

										action2.setIsDelete(true);

									} else if (idView.equals(action)) {

										action2.setIsView(true);

									}
								});
							});
							if (finalList != null && finalList.size() > 0) {
								roleMenuMasterMappingRepository.saveAll(finalList);
							}
						}
					}
					roleMasterRepository.save(roleMaster);
					responseModel = CustomMessages.makeResponseModel(finalList,
							"MenuList " + CustomMessages.UPDATE_SUCCESSFULLY, CustomMessages.GET_DATA_SUCCESS,
							CustomMessages.SUCCESS);
				}
			}
			return responseModel;
		} catch (Exception e) {
			responseModel = CustomMessages.makeResponseModel(null, CustomMessages.NOT_FOUND,
					CustomMessages.NO_DATA_FOUND, CustomMessages.FAILED);
			// TODO: handle exception
			return responseModel;
		}
	}

	/**
	 * Get active user roles by assigned state
	 * @return {@code RoleOutputDAO}
	 */
	public ResponseModel getActiveUserRolesByAssignState(HttpServletRequest request) {
		ResponseModel responseModel = null;
		try {
			Long userId = new Long(CustomMessages.getUserId(request, jwtTokenUtil));
			List<Long> stateLgdCodes = userVillageMappingRepository.getStateCodesById(userId);
			List<RoleOutputDAO> returnList = new ArrayList();
			Optional<UserMaster> userData = userMasterRepository.findByUserId(userId);
			List<RoleMaster> roleList;
			if(userData.isPresent() && userData.get().getRoleId().getCode().equals("SUPERADMIN")) {
				roleList = roleMasterRepository.findRolesExcludingSurveyorAndVerifierAndSuperAdmin();
//				responseModel = CustomMessages.makeResponseModel(roleList, CustomMessages.MENU_ADD_SUCCESS,
//						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			}else {
			 roleList = roleMasterRepository.findByIsDeletedAndIsActiveAndStateLgdCodeIn(false, true,
					stateLgdCodes);

			}
			roleList.forEach(action->{
				RoleOutputDAO roleOutput = CustomMessages.returnUserOutputDAO(action);
				roleOutput.setCreatedIp(null);
				roleOutput.setModifiedIp(null);
				returnList.add(roleOutput);
			});
			responseModel = CustomMessages.makeResponseModel(returnList, CustomMessages.MENU_ADD_SUCCESS,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			return responseModel;
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * @param roleId
	 * @return territoryLevel by role rolePatternMappinglist
	 */
	public ResponseModel getTerritoryLevelByRole(Long roleId) {
		try {
			List<String> rolePatternMappinglist = rolePatternMappingRepository.findTerritoryByrole(roleId);

			return CustomMessages.makeResponseModel(rolePatternMappinglist, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}
	
	
	void saveUserVillageMapping(List<UserMaster> userList,String territory){

		JSONArray userDetailsList = new JSONArray();

		userList.parallelStream().forEach(action->{
			JSONObject userdetails=new JSONObject();
			userdetails.put("userId", action.getUserId());
			userdetails.put("territory", territory);
			
			
			switch(territory) {
			case "state":
				userdetails.put("lgdCode", action.getUserStateLGDCode());
				break;
			case "district":
				userdetails.put("lgdCode", action.getUserDistrictLGDCode());
				break;
			case "subdistrict":
				userdetails.put("lgdCode", action.getUserTalukaLGDCode());
				break;
			case "village":
				userdetails.put("lgdCode", action.getUserVillageLGDCode());
				break;
			}
			
			
			
			userDetailsList.put(userdetails);
		});
		
		if(userDetailsList!=null) {
			 userMasterRepository.userAssignVillageByTerritory(userDetailsList.toString());
		}

	}

	private String getJoinedValuefromList(List<Long> list) {
		try {
			return list.stream().map(i -> i.toString()).collect(Collectors.joining(", "));
		} catch (Exception e) {
			return "";
		}
	}

}
