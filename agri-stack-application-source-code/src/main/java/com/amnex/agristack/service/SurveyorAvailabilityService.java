package com.amnex.agristack.service;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.Enum.ConfigCode;
import com.amnex.agristack.Enum.DepartmentEnum;
import com.amnex.agristack.Enum.RoleEnum;
import com.amnex.agristack.Enum.StatusEnum;
import com.amnex.agristack.dao.*;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.*;
import com.amnex.agristack.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.amnex.agristack.utils.CustomMessages;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

@Service
public class SurveyorAvailabilityService {

	@Autowired
	private UserAvailabilityRepository userAvailabilityRepository;
	@Autowired
	private UserLandAssignmentRepository userLandAssignmentRepository;

	@Autowired
	private VillageLgdMasterRepository villageLgdMasterRepository;

	@Autowired
	private ConfigurationRepository configurationRepository;

	@Autowired
	private YearRepository yearRepository;

	@Autowired
	UserMasterRepository userRepository;
	@Autowired
	private RoleMasterRepository roleRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private UserLandAssignmentService userLandAssignmentService;

	@Autowired
	private VerifierLandAssignmentService verifierLandAssignmentService;

	@Autowired
	private RoleMasterRepository roleMasterRepository;
	
	@Autowired
	private DepartmentRepository departmentRepository;

	/**
	 * Retrieves all active surveyors.
	 *
	 * @param request The HttpServletRequest object.
	 * @return The ResponseModel object containing the active surveyors.
	 */
	public ResponseModel getAllActiveSurveyor(HttpServletRequest request) {
		ResponseModel responseModel = null;
		try {

			List<UserAvailability> dataList = userAvailabilityRepository.findByIsAvailable(Boolean.TRUE);

			if (dataList != null && dataList.size() > 0) {
				Set<Long> userIdList = dataList.stream().map(map -> map.getUserId().getUserId())
						.collect(Collectors.toSet());
				List<UserLandCount> count = userLandAssignmentRepository.getCountDetailsByVillageCode(userIdList);
				if (count != null && count.size() > 0) {
					dataList.forEach(action -> {
						count.forEach(action2 -> {
							if (action.getUserId().getUserId().equals(action2.getUserId())) {
								action.setTotalCount(action2.getTotalCount());
							}
						});
					});
				}
			}

			return CustomMessages.makeResponseModel(dataList, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Retrieves all active surveyors based on the provided filter.
	 *
	 * @param request                       The HttpServletRequest object.
	 * @param surveyTaskAllocationFilterDAO The SurveyTaskAllocationFilterDAO object
	 *                                      containing the filter parameters.
	 * @return The ResponseModel object containing the active surveyors that match
	 *         the filter.
	 */
	public ResponseModel getAllActiveSurveyorByFilter(HttpServletRequest request,
			SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		ResponseModel responseModel = null;
		try {

			List<UserAvailability> dataList = new ArrayList<>();

			RoleMaster role = roleRepository.findByCode(RoleEnum.SURVEYOR.toString()).orElse(null);
			switch (surveyTaskAllocationFilterDAO.getBoundaryType()) {
			case "taluka":
				VillageLgdMaster village = villageLgdMasterRepository
						.findByVillageLgdCode(surveyTaskAllocationFilterDAO.getVillageLgdCode());
				if (village != null) {

					dataList = userAvailabilityRepository
							.findByUserId_RoleId_RoleIdAndUserId_UserStatusAndIsAvailableAndSeasonEndYearAndSeasonIdAndUserId_UserTalukaLGDCodeOrderByUserId_UserFullNameAsc(
									role.getRoleId(), StatusEnum.APPROVED.getValue(), true,
									surveyTaskAllocationFilterDAO.getYear(),
									surveyTaskAllocationFilterDAO.getSeasonId(),
									village.getSubDistrictLgdCode().getSubDistrictLgdCode().intValue());
				}
				break;
			default:
				dataList = userAvailabilityRepository
						.findByUserId_RoleId_RoleIdAndUserId_UserStatusAndIsAvailableAndSeasonEndYearAndSeasonIdAndUserId_UserVillageLGDCodeOrderByUserId_UserFullNameAsc(
								role.getRoleId(), StatusEnum.APPROVED.getValue(), true,
								surveyTaskAllocationFilterDAO.getYear(), surveyTaskAllocationFilterDAO.getSeasonId(),
								surveyTaskAllocationFilterDAO.getVillageLgdCode().intValue());
			}

			if (dataList != null && dataList.size() > 0) {
				Set<Long> userIdList = dataList.stream().map(map -> map.getUserId().getUserId())
						.collect(Collectors.toSet());
				Optional<ConfigurationMaster> op = configurationRepository
//                        .findByIsActiveTrueAndIsDeletedFalseAndConfigCode(ConfigCode.SURVEY_PLOT_ASSIGNMENT_NUMBER);
						.findByIsActiveTrueAndIsDeletedFalseAndConfigCode(
								ConfigCode.MAXIMUM_SURVEY_PLOT_ASSIGNMENT_NUMBER);
				Long countNumber = 0l;
				if (op.isPresent()) {
					ConfigurationMaster configurationMaster = op.get();
					if (configurationMaster.getConfigValue() != null) {
						countNumber = Long.parseLong(configurationMaster.getConfigValue());
					}

				}
				Long surveyPlotAssignmentNumber = countNumber;
				dataList.forEach(action -> {
					action.setNotAssignCount(surveyPlotAssignmentNumber);
				});

//				List<UserLandCount> count = userLandAssignmentRepository.getCountDetailsByVillageCode(userIdList);
				List<UserLandCount> count = userLandAssignmentRepository.getCountDetailsByVillageCode(userIdList,surveyTaskAllocationFilterDAO.getSeasonId(),surveyTaskAllocationFilterDAO.getStartYear(),surveyTaskAllocationFilterDAO.getEndYear());
				if (count != null && count.size() > 0) {

					dataList.forEach(action -> {

						count.forEach(action2 -> {
							if (action.getUserId().getUserId().equals(action2.getUserId())) {
								action.setTotalCount(action2.getTotalCount());
							}
						});
					});
				}
			}

			return CustomMessages.makeResponseModel(dataList, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Adds user availability for seasons.
	 *
	 * @param surveyorInput The surveyor input data.
	 * @param request       The HttpServletRequest object containing the request
	 *                      information.
	 * @return The ResponseModel containing the response data.
	 */
	public ResponseModel addUserAvailabilityForSeasons(SurveyorInputDAO surveyorInput, HttpServletRequest request) {

		ResponseModel responseModel = new ResponseModel();
		try {
			YearMaster yearMaster = yearRepository
					.findByStartYearAndEndYear(surveyorInput.getStartingYear(), surveyorInput.getEndingYear())
					.orElse(null);
			Long userId = Long.valueOf(userService.getUserId(request));
			UserMaster user = userRepository.findByIsDeletedAndUserId(false, userId).orElse(null);
			if (user != null
					&& DepartmentEnum.PRIVATE_RESIDENT.getValue().equals(user.getDepartmentId().getDepartmentType())) {
				if (surveyorInput.getSeasonIdList() != null && !Objects.isNull(yearMaster) && !Objects.isNull(user)) {

					List<UserAvailability> availabilites = userAvailabilityRepository.findAllUserAvailability(
							yearMaster.getStartYear(), yearMaster.getEndYear(), user.getUserId());
					UserMaster userMaster = userRepository.findByUserId(Long.parseLong(userService.getUserId(request)))
							.orElse(null);
					List<Long> availableSeason = availabilites.stream().map(UserAvailability::getSeasonId)
							.collect(Collectors.toList());
					availableSeason.removeAll(surveyorInput.getSeasonIdList());
					if (availabilites.size() > 0 && userMaster != null) {
						// Unavailable all Surveyor Availability
						availabilites.forEach(ele -> {
							ele.setIsActive(false);
							ele.setIsDeleted(true);
							ele.setIsAvailable(false);
							ele.setModifiedOn(new Timestamp(new Date().getTime()));
							ele.setModifiedIp(request.getRemoteAddr());
							ele.setModifiedBy(userService.getUserId(request));
							userAvailabilityRepository.save(ele);
						});

						// unassigned land from Surveyor
						if (userMaster.getRoleId().getRoleName().toUpperCase().equals(RoleEnum.SURVEYOR.toString())) {
							for (Long season : availableSeason) {
								SurveyTaskAllocationFilterDAO surveyTaskAllocationRequest = new SurveyTaskAllocationFilterDAO();
								surveyTaskAllocationRequest.setSeasonId(season);
								surveyTaskAllocationRequest.setStartYear(Integer.parseInt(yearMaster.getStartYear()));
								surveyTaskAllocationRequest.setEndYear(Integer.parseInt(yearMaster.getEndYear()));
								List<Long> surveyorIds = new ArrayList<>();
								surveyorIds.add(user.getUserId());
								surveyTaskAllocationRequest.setUserList(surveyorIds);
								userLandAssignmentService.removeSurveyorLandAssignment(request,
										surveyTaskAllocationRequest);
							}

						}

					}

					// Create or update Surveyor Availability
					for (Long season : surveyorInput.getSeasonIdList()) {
						List<UserAvailability> availabilities = userAvailabilityRepository
								.findUserAvailabilityWithSeason(yearMaster.getStartYear(), yearMaster.getEndYear(),
										user.getUserId(), season);
						if (availabilities.size() == 0) {
							UserAvailability userAvailability = new UserAvailability();
							userAvailability.setSeasonId(season);
							userAvailability.setIsAvailable(surveyorInput.getIsAvailable());
							userAvailability.setSeasonStartYear(yearMaster.getStartYear());
							userAvailability.setSeasonEndYear(yearMaster.getEndYear());
							userAvailability.setUserId(user);
							userAvailability.setIsActive(true);
							userAvailability.setIsDeleted(false);
							userAvailability.setCreatedOn(new Timestamp(new Date().getTime()));
							userAvailability.setCreatedIp(request.getRemoteAddr());
							userAvailability.setCreatedBy(userService.getUserId(request));
							userAvailabilityRepository.save(userAvailability);
						} else {
							UserAvailability surveyorAvailability = availabilities.get(0);
							surveyorAvailability.setIsActive(true);
							surveyorAvailability.setIsDeleted(false);
							surveyorAvailability.setIsAvailable(true);
							surveyorAvailability.setCreatedOn(new Timestamp(new Date().getTime()));
							surveyorAvailability.setCreatedIp(request.getRemoteAddr());
							surveyorAvailability.setCreatedBy(userService.getUserId(request));
							userAvailabilityRepository.save(surveyorAvailability);
						}

					}
				}
				return new ResponseModel(surveyorInput, CustomMessages.AVAILABILITY_ADDED,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
			} else {
				return new ResponseModel(null, CustomMessages.ONLY_PR_SURVEYOR_CAN_UPDATE_AVAILABILITY,
						CustomMessages.GET_DATA_ERROR, CustomMessages.INVALID_INPUT, CustomMessages.METHOD_POST);
			}

		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(null, CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
		return responseModel;
	}

	/**
	 * Retrieves user availability.
	 *
	 * @param surveyorInput The surveyor input data.
	 * @param request       The HttpServletRequest object containing the request
	 *                      information.
	 * @return The ResponseModel containing the response data.
	 */
	public ResponseModel getUserAvailability(SurveyorInputDAO surveyorInput, HttpServletRequest request) {
		ResponseModel responseModel = new ResponseModel();
		List<UserAvailability> userAvailabilities = new ArrayList<>();
		UserAvailabilityOutputDAO userAvailabilityDAO = new UserAvailabilityOutputDAO();
		try {
			YearMaster yearMaster = yearRepository
					.findByStartYearAndEndYear(surveyorInput.getStartingYear(), surveyorInput.getEndingYear())
					.orElse(null);
			Long userId = Long.valueOf(userService.getUserId(request));
			UserMaster user = userRepository.findByIsDeletedAndUserId(false, userId).orElse(null);
			if (!Objects.isNull(yearMaster) && !Objects.isNull(user)) {
				userAvailabilities = userAvailabilityRepository.findUserAvailabilityForUser(yearMaster.getStartYear(),
						yearMaster.getEndYear(), true, user.getUserId(), true, false);
			}
			if (userAvailabilities.size() > 0) {
				userAvailabilityDAO.setSeasonIdList(userAvailabilities.stream().distinct()
						.map(UserAvailability::getSeasonId).collect(Collectors.toList()));
				userAvailabilityDAO.setStartingYear(surveyorInput.getStartingYear());
				userAvailabilityDAO.setEndingYear(surveyorInput.getEndingYear());
				userAvailabilityDAO.setYear(yearMaster.getId());
			}
			return new ResponseModel(userAvailabilityDAO, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
		return responseModel;
	}

	/**
	 * Retrieves surveyors by Taluka.
	 *
	 * @param request                       The HttpServletRequest object containing
	 *                                      the request information.
	 * @param surveyTaskAllocationFilterDAO The survey task allocation filter data.
	 * @return The ResponseModel containing the response data.
	 */
	public ResponseModel getSurveyorByTaluka(HttpServletRequest request,
			SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		ResponseModel responseModel = null;
		try {

			List<UserAvailability> dataList = new ArrayList<>();
			List<UserDAO> finalList = new ArrayList<>();
			RoleMaster role = roleRepository.findByCode(RoleEnum.SURVEYOR.toString()).orElse(null);

			if (role != null) {
				if (surveyTaskAllocationFilterDAO.getSubDistrictLgdCodeList() != null
						&& surveyTaskAllocationFilterDAO.getSubDistrictLgdCodeList().size() > 0) {
//					surveyTaskAllocationFilterDAO
					List<Integer> subDistrictCodes = surveyTaskAllocationFilterDAO.getSubDistrictLgdCodeList().stream()
							.map(x -> x.intValue()).collect(Collectors.toList());
					dataList = userAvailabilityRepository
							.findByUserId_RoleId_RoleIdAndUserId_UserStatusAndIsAvailableAndSeasonStartYearAndSeasonEndYearAndSeasonIdAndUserId_UserTalukaLGDCodeInOrderByUserId_UserFullNameAsc(
									role.getRoleId(), StatusEnum.APPROVED.getValue(), true,
									surveyTaskAllocationFilterDAO.getStartYear() + "",
									surveyTaskAllocationFilterDAO.getEndYear() + "",
									surveyTaskAllocationFilterDAO.getSeasonId(), subDistrictCodes);
				}

			}

			if (dataList != null && dataList.size() > 0) {
				dataList.forEach(action -> {
					UserDAO userDAO = new UserDAO();
					userDAO.setUserId(action.getUserId().getUserId());
					userDAO.setUserFirstName(action.getUserId().getUserFirstName());
					userDAO.setUserLastName(action.getUserId().getUserLastName());
					userDAO.setUserFullName(action.getUserId().getUserFullName());
					userDAO.setUserName(action.getUserId().getUserName());
					userDAO.setUserPrId(action.getUserId().getUserPrId());
					userDAO.setUserStateLGDCode(action.getUserId().getUserStateLGDCode());
					userDAO.setUserDistrictLGDCode(action.getUserId().getUserDistrictLGDCode());
					userDAO.setUserTalukaLGDCode(action.getUserId().getUserTalukaLGDCode());
					userDAO.setUserVillageLGDCode(action.getUserId().getUserVillageLGDCode());

					finalList.add(userDAO);

				});

			}

			return CustomMessages.makeResponseModel(finalList, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	public Integer addCurrentSeasonAvailabilityForGovEmp(SowingSeason currentSeason) {
		try {
			Optional<RoleMaster> surveyorRole = roleMasterRepository.findByCode(RoleEnum.SURVEYOR.toString());
			Optional<RoleMaster> verifierRole = roleMasterRepository.findByCode(RoleEnum.VERIFIER.toString());
			List<UserMaster> governmentEmpAsSurveyorList = userRepository
					.findByIsDeletedAndRoleId_IsDefaultAndRoleId_RoleIdAndDepartmentId_DepartmentTypeIsNot(
							Boolean.FALSE, Boolean.TRUE, surveyorRole.get().getRoleId(),
							DepartmentEnum.PRIVATE_RESIDENT.getValue());
			List<UserMaster> governmentEmpAsVerifierList = userRepository
					.findByIsDeletedAndRoleId_IsDefaultAndRoleId_RoleIdAndDepartmentId_DepartmentTypeIsNot(
							Boolean.FALSE, Boolean.TRUE, verifierRole.get().getRoleId(),
							DepartmentEnum.PRIVATE_RESIDENT.getValue());
			Integer surveyorCount = addGovernmentEmpAvailability(currentSeason, governmentEmpAsSurveyorList);
			System.out.println("User Availability added for total : " + surveyorCount + " Surveyors.");
			Integer verifierCount = addGovernmentEmpAvailability(currentSeason, governmentEmpAsVerifierList);
			System.out.println("User Availability added for total: " + verifierCount + " Verifiers.");
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Integer addGovernmentEmpAvailability(SowingSeason currentSeason,
			List<UserMaster> governmentEmpAsSurveyorList) {
		Integer availabilityCount = 0;
		try {
			List<UserAvailability> userAvailabilities = new ArrayList<>();
			governmentEmpAsSurveyorList.stream().forEach(surveyor -> {
				UserAvailability availability = new UserAvailability();
				availability.setUserId(surveyor);
				availability.setIsAvailable(true);
				availability.setIsActive(true);
				availability.setIsDeleted(false);
				availability.setSeasonId(Objects.nonNull(currentSeason) ? currentSeason.getSeasonId() : null);
				availability.setSeasonStartYear(currentSeason.getStartingYear().toString());
				availability.setSeasonEndYear(currentSeason.getEndingYear().toString());
				availability.setCreatedBy("1");
				availability.setCreatedOn(new Timestamp(new Date().getTime()));
				userAvailabilities.add(availability);
			});
			userAvailabilityRepository.saveAll(userAvailabilities);
			availabilityCount = userAvailabilities.size();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return availabilityCount;
	}

	public ResponseModel getPRSurveyorByTaluka(HttpServletRequest request,
			SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		ResponseModel responseModel = null;
		try {

			List<UserAvailability> dataList = new ArrayList<>();
			List<UserDAO> finalList = new ArrayList<>();
			RoleMaster role = roleRepository.findByCode(RoleEnum.SURVEYOR.toString()).orElse(null);
			
			DepartmentEnum department = DepartmentEnum.valueOf(DepartmentEnum.PRIVATE_RESIDENT.toString());
			DepartmentMaster departmentMaster = departmentRepository.findByDepartmentId(Long.valueOf(department.getValue()));
			
			if (role != null) {
				if (surveyTaskAllocationFilterDAO.getSubDistrictLgdCodeList() != null
						&& surveyTaskAllocationFilterDAO.getSubDistrictLgdCodeList().size() > 0) {
//					surveyTaskAllocationFilterDAO
					List<Integer> subDistrictCodes = surveyTaskAllocationFilterDAO.getSubDistrictLgdCodeList().stream()
							.map(x -> x.intValue()).collect(Collectors.toList());
//					dataList = userAvailabilityRepository
//							.findByUserId_RoleId_RoleIdAndUserId_UserStatusAndIsAvailableAndSeasonStartYearAndSeasonEndYearAndSeasonIdAndUserId_UserTalukaLGDCodeInOrderByUserId_UserFullNameAsc(
//									role.getRoleId(), StatusEnum.APPROVED.getValue(), true,
//									surveyTaskAllocationFilterDAO.getStartYear() + "",
//									surveyTaskAllocationFilterDAO.getEndYear() + "",
//									surveyTaskAllocationFilterDAO.getSeasonId(), subDistrictCodes);
					
					dataList = userAvailabilityRepository
							.findByUserId_RoleId_RoleIdAndUserId_UserStatusAndIsAvailableAndSeasonStartYearAndSeasonEndYearAndSeasonIdAndUserId_UserTalukaLGDCodeInAndUserId_DepartmentIdOrderByUserId_UserFullNameAsc(
									role.getRoleId(), StatusEnum.APPROVED.getValue(), true,
									surveyTaskAllocationFilterDAO.getStartYear() + "",
									surveyTaskAllocationFilterDAO.getEndYear() + "",
									surveyTaskAllocationFilterDAO.getSeasonId(), subDistrictCodes,departmentMaster);
					
				}

			}

			if (dataList != null && dataList.size() > 0) {
				dataList.forEach(action -> {
					UserDAO userDAO = new UserDAO();
					userDAO.setUserId(action.getUserId().getUserId());
					userDAO.setUserFirstName(action.getUserId().getUserFirstName());
					userDAO.setUserLastName(action.getUserId().getUserLastName());
					userDAO.setUserFullName(action.getUserId().getUserFullName());
					userDAO.setUserName(action.getUserId().getUserName());
					userDAO.setUserPrId(action.getUserId().getUserPrId());
					userDAO.setUserStateLGDCode(action.getUserId().getUserStateLGDCode());
					userDAO.setUserDistrictLGDCode(action.getUserId().getUserDistrictLGDCode());
					userDAO.setUserTalukaLGDCode(action.getUserId().getUserTalukaLGDCode());
					userDAO.setUserVillageLGDCode(action.getUserId().getUserVillageLGDCode());

					finalList.add(userDAO);

				});

			}

			return CustomMessages.makeResponseModel(finalList, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}
	public ResponseModel getPRSurveyorByTalukaNew(HttpServletRequest request,
			SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		ResponseModel responseModel = null;
		try {
			
			String codes = getJoinedValuefromList(surveyTaskAllocationFilterDAO.getSubDistrictLgdCodeList());
			String response = userAvailabilityRepository
					.getPrSurveyorDetail(surveyTaskAllocationFilterDAO.getSeasonId(),
							surveyTaskAllocationFilterDAO.getStartYear() + "",
							surveyTaskAllocationFilterDAO.getEndYear() + "", codes);
			
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<Object> finalList = mapper.readValue(response,
					typeFactory.constructCollectionType(List.class, Object.class));
			

			return CustomMessages.makeResponseModel(finalList, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
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
