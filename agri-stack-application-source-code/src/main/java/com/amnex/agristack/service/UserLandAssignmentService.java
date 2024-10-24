package com.amnex.agristack.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.Enum.ConfigCode;
import com.amnex.agristack.Enum.StatusEnum;
import com.amnex.agristack.dao.*;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.*;
import com.amnex.agristack.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.utils.CustomMessages;
import com.amnex.agristack.utils.DBUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.springframework.transaction.annotation.Transactional;

@Service
public class UserLandAssignmentService {

	@Autowired
	private UserMasterRepository userMasterRepository;

	@Autowired
	private SeasonMasterRepository seasonMasterRepository;

	@Autowired
	private VillageLgdMasterRepository villageLgdMasterRepository;

	@Autowired
	private UserLandAssignmentRepository userLandAssignmentRepository;
	
	@Autowired
	private StatusMasterRepository statusMasterRepository;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	@Autowired
	private PRPaymentReleaseRepository prPaymentReleaseRepository;
	
	@Autowired
	private FarmlandPlotRegistryRepository farmlandPlotRegistryRepository;
	
	@Autowired
	private ConfigurationRepository configurationRepository;
	@Autowired
	private DBUtils dbUtils;
	/**
	 * Assigns land to multiple users.
	 *
	 * @param request The HttpServletRequest object.
	 * @param userLandAssignmentInputDAOList The list of {@code UserLandAssignmentInputDAO} objects containing the land assignment details.
	 * @return The ResponseModel object indicating the success of the land assignment operation.
	 */
	public ResponseModel assignLand(HttpServletRequest request,
			List<UserLandAssignmentInputDAO> userLandAssignmentInputDAOList) {
		ResponseModel responseModel = null;
		try {
			List<UserLandAssignment> userLandAssignmentList = new ArrayList<>();
			if (userLandAssignmentInputDAOList != null && userLandAssignmentInputDAOList.size() > 0) {

				Set<Long> userIds = userLandAssignmentInputDAOList.stream().map(us -> us.getUserId())
						.collect(Collectors.toSet());

				Set<Long> seasonIds = userLandAssignmentInputDAOList.stream().map(us -> us.getSeasonId())
						.collect(Collectors.toSet());

				Set<Long> villagelgdCodes = userLandAssignmentInputDAOList.stream().map(us -> us.getVillageLgdCode())
						.collect(Collectors.toSet());
				
				List<String>  farmLandList=userLandAssignmentInputDAOList.stream().map(us -> us.getFarmlandId())
				.collect(Collectors.toList());
				
				if(farmLandList!=null && farmLandList.size()>0) {
					List<UserLandAssignment> userLandAssignmentListExist=userLandAssignmentRepository.findByYearAndSeason_SeasonIdAndFarmlandIdInAndIsActiveAndIsDeletedOrderByUser_UserFullNameAsc
					(userLandAssignmentInputDAOList.get(0).getYear(), userLandAssignmentInputDAOList.get(0).getSeasonId(), farmLandList, true, false);
					if(userLandAssignmentListExist!=null && userLandAssignmentListExist.size()>0) {
						
						String plotMessge=userLandAssignmentListExist.size()+CustomMessages.FEW_PLOTS_EXIST;
						return responseModel = CustomMessages.makeResponseModel(null, plotMessge,
								CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
					}	
					
				}

				List<UserMaster> userList = userMasterRepository.findByUserIdInOrderByUserFullNameAsc(userIds);
				List<SowingSeason> seasonList = seasonMasterRepository.findBySeasonIdIn(seasonIds);
				List<VillageLgdMaster> villageList = villageLgdMasterRepository.findByVillageLgdCodeIn(villagelgdCodes);
				StatusMaster status =statusMasterRepository.findByIsDeletedFalseAndStatusCode(StatusEnum.PENDING.getValue());
				userLandAssignmentInputDAOList.forEach(action -> {
					UserLandAssignment userLandAssignment = new UserLandAssignment();
					userLandAssignment.setYear(action.getYear());
					if(status !=null) {
						userLandAssignment.setStatus(status);
					}
					String userId=CustomMessages.getUserId(request, jwtTokenUtil);
					if(userId!=null ){
						userLandAssignment.setCreatedBy(userId);	
						userLandAssignment.setModifiedBy(userId);	
					}
					
					userLandAssignment.setStartingYear(action.getStartingYear());
					userLandAssignment.setEndingYear(action.getEndingYear());
					userLandAssignment.setFarmlandId(action.getFarmlandId());
					userLandAssignment.setLandParcelId(action.getLandParcelId());
					userLandAssignment.setParcelId(action.getParcelId());
					userLandAssignment.setIsBoundarySurvey(action.getIsBoundarySurvey());
					userLandAssignment.setIsActive(true);
					userLandAssignment.setIsDeleted(false);

					if (userList != null && userList.size() > 0) {
						userList.forEach(user -> {
							if (user.getUserId().equals(action.getUserId())) {
								userLandAssignment.setUser(user);
							}
						});

					}

					if (seasonList != null && seasonList.size() > 0) {
						seasonList.forEach(season -> {

							if (season.getSeasonId().equals(action.getSeasonId())) {
								userLandAssignment.setSeason(season);
							}
						});

					}

					if (villageList != null && villageList.size() > 0) {
						villageList.forEach(village -> {

							if (village.getVillageLgdCode().equals(action.getVillageLgdCode())) {
								userLandAssignment.setVillageLgdCode(village);
							}
						});

					}
					userLandAssignmentList.add(userLandAssignment);
				});

				if (userLandAssignmentList != null && userLandAssignmentList.size() > 0) {
					userLandAssignmentRepository.saveAll(userLandAssignmentList);
				}
			}

			return new ResponseModel(userLandAssignmentList, CustomMessages.TASK_ASSIGNED,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;

		}

	}

	/**
	 * Retrieves assignment data for a specific village code.
	 *
	 * @param request The HttpServletRequest object.
	 * @param villageCode The village code for which the assignment data is retrieved.
	 * @return The ResponseModel object containing the assignment data for the specified village code.
	 */
	public ResponseModel getAssignDataByVillageCode(HttpServletRequest request, Integer villageCode) {
		ResponseModel responseModel = null;
		try {
			List<UserLandAssignment> userLandAssignMent = userLandAssignmentRepository
					.findByVillageLgdCode_villageLgdCode(Long.valueOf(villageCode));
			List<UserLandCount> count = userLandAssignmentRepository
					.getCountDetailsByVillageCode(Long.valueOf(villageCode));
			if (userLandAssignMent != null && userLandAssignMent.size() > 0 && count != null && count.size() > 0) {
				userLandAssignMent.forEach(action->{
					count.forEach(action2->{
						if(action.getUser().getUserId().equals(action2.getUserId())){
							action.setFarmAssignCount(action2.getTotalCount());
						}
					});
				});
			}

			return CustomMessages.makeResponseModel(userLandAssignMent, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}
	
	/**
	 * Retrieves surveyors assigned to a specific village based on the village code.
	 *
	 * @param request The HttpServletRequest object.
	 * @param villageCode The code of the village for which to retrieve the assigned surveyors.
	 * @return The ResponseModel object containing the surveyors assigned to the specified village.
	 */
	public ResponseModel getSurveyorByVillageCode(HttpServletRequest request, Integer villageCode) {
		ResponseModel responseModel = null;
		try {
			List<UserMaster> finalUserDetails=new ArrayList<>();
			List<UserLandCount> count = userLandAssignmentRepository
					.getCountDetailsByVillageCode(Long.valueOf(villageCode));
			if(count!=null && count.size()>0) {
				Set<Long> userIds=count.stream().map(data->data.getUserId()).collect(Collectors.toSet());
				List<UserMaster> usermasterIds = userMasterRepository.findByUserIdInOrderByUserFullNameAsc(userIds);
				
				if (usermasterIds != null && usermasterIds.size() > 0) {
					usermasterIds.forEach(action->{
						count.forEach(action2->{
							if(action.getUserId().equals(action2.getUserId())){
								action.setFarmAssignCount(action2.getTotalCount());
								finalUserDetails.add(action);
							}
						});
					});
				}
			}
			
	
			
			
	

			return CustomMessages.makeResponseModel(finalUserDetails, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}
	
	/**
	 * Retrieves surveyors assigned to a specific village based on the village code, with additional filtering options.
	 *
	 * @param request The HttpServletRequest object.
	 * @param surveyTaskAllocationFilterDAO The filter criteria for retrieving the surveyors.
	 * @return The ResponseModel object containing the surveyors assigned to the specified village, filtered by the given criteria.
	 */
	public ResponseModel getSurveyorByVillageCodeWithFilter(HttpServletRequest request, SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		ResponseModel responseModel = null;
		try {
			List<UserMaster> finalUserDetails=new ArrayList<>();
			List<UserLandCount> count = userLandAssignmentRepository
					.getCountDetailsByVillageCodeWithfilter(surveyTaskAllocationFilterDAO.getSeasonId(), surveyTaskAllocationFilterDAO.getVillageLgdCode(), surveyTaskAllocationFilterDAO.getYear());
			if(count!=null && count.size()>0) {
				StatusMaster statusMaster=statusMasterRepository.findByIsDeletedFalseAndStatusCode(StatusEnum.PENDING.getValue());

				
				Set<Long> userIds=count.stream().map(data->data.getUserId()).collect(Collectors.toSet());
				List<UserMaster> usermasterIds = userMasterRepository.findByUserIdInOrderByUserFullNameAsc(userIds);
				
				if (usermasterIds != null && usermasterIds.size() > 0) {
					usermasterIds.forEach(action->{
						count.forEach(action2->{
							if(action.getUserId().equals(action2.getUserId())){
								action.setFarmAssignCount(action2.getTotalCount());
								finalUserDetails.add(action);
							}
						});
					});
					
//					List<UserLandAssignment> userLandAssignMentList = userLandAssignmentRepository
//							.findByYearAndSeason_SeasonIdAndVillageLgdCode_villageLgdCodeAndIsActiveAndIsDeleted(surveyTaskAllocationFilterDAO.getYear(),surveyTaskAllocationFilterDAO.getSeasonId(),surveyTaskAllocationFilterDAO.getVillageLgdCode(),true,false);
					List<UserLandCount> assignLandCounts= userLandAssignmentRepository.getAssignLandListByFilter(surveyTaskAllocationFilterDAO.getStartYear(), surveyTaskAllocationFilterDAO.getEndYear(), surveyTaskAllocationFilterDAO.getSeasonId(), surveyTaskAllocationFilterDAO.getVillageLgdCode());					
					if(assignLandCounts!=null && assignLandCounts.size()>0) {
						
						finalUserDetails.forEach(action->{
							assignLandCounts.forEach(action2->{
								
								
								if(action.getUserId().equals(action2.getUserId())){
									action.setCompletedSurveyNumber(action2.getTotalCount());
								}
							});
						});
		
						
					}
					if(statusMaster!=null ) {
						
//						List<UserLandCount> landDataList=userLandAssignmentRepository.getCountDetailsByVillageCodeWithfilterPending(surveyTaskAllocationFilterDAO.getSeasonId(), surveyTaskAllocationFilterDAO.getVillageLgdCode(), surveyTaskAllocationFilterDAO.getYear(),statusMaster.getStatusId());
						
						finalUserDetails.forEach(action->{	
							if(action.getCompletedSurveyNumber()!=null) {
								Long pendingCount=action.getFarmAssignCount()-action.getCompletedSurveyNumber();
								action.setFarmPendingCount(pendingCount);
							}else {
								action.setFarmPendingCount(action.getFarmAssignCount());
							}
							
							
										
							
							
						});
					}
				}
			}
			
	
			
			
	

			return CustomMessages.makeResponseModel(finalUserDetails, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}
	
	/**
	 * Retrieves assignment data based on the provided filter criteria.
	 *
	 * @param request The HttpServletRequest object.
	 * @param surveyTaskAllocationFilterDAO The filter criteria for retrieving the assignment data.
	 * @return The ResponseModel object containing the assignment data based on the filter criteria.
	 */
	public ResponseModel getAssignDataByFilter(HttpServletRequest request, SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		ResponseModel responseModel = null;
		try {
			List<UserLandAssignment> userLandAssignMent = userLandAssignmentRepository
					.findByYearAndSeason_SeasonIdAndVillageLgdCode_villageLgdCodeAndIsActiveAndIsDeletedOrderByUser_UserFullNameAsc(surveyTaskAllocationFilterDAO.getYear(),surveyTaskAllocationFilterDAO.getSeasonId(),surveyTaskAllocationFilterDAO.getVillageLgdCode(),true,false);
			List<UserLandCount> count = userLandAssignmentRepository
					.getCountDetailsByVillageCode(surveyTaskAllocationFilterDAO.getVillageLgdCode(),surveyTaskAllocationFilterDAO.getSeasonId(),surveyTaskAllocationFilterDAO.getYear());
			if (userLandAssignMent != null && userLandAssignMent.size() > 0 ) {
				
				if( count != null && count.size() > 0) {
					
					userLandAssignMent.forEach(action->{
					count.forEach(action2->{
								if(action.getUser().getUserId().equals(action2.getUserId())){
									action.setFarmAssignCount(action2.getTotalCount());
								}
							});
						});
				}
				List<String> farmlandids=userLandAssignMent.stream().map(x->x.getFarmlandId()).collect(Collectors.toList());
				if(farmlandids!=null && farmlandids.size()>0) {
					List<SurveyStatusDAO> surveyStatusList=userLandAssignmentRepository.getfarmLandwithsurveyStatus(surveyTaskAllocationFilterDAO.getStartYear(), surveyTaskAllocationFilterDAO.getEndYear(), surveyTaskAllocationFilterDAO.getSeasonId(),surveyTaskAllocationFilterDAO.getVillageLgdCode(),farmlandids);
					List<FarmlandPlotRegistry> flprList=farmlandPlotRegistryRepository.findByFarmlandIdIn(farmlandids);
					
					if(surveyStatusList != null && surveyStatusList.size() > 0) {
						userLandAssignMent.forEach(action->{
							
							List<FarmlandPlotRegistry> filter=flprList.stream().filter(x->x.getFarmlandId().equals(action.getFarmlandId())).collect(Collectors.toList());
							if(filter!=null && filter.size()>0) {
								action.setSurveyNumber(filter.get(0).getSurveyNumber());
								action.setSubSurveyNumber(filter.get(0).getSubSurveyNumber());
							}
							surveyStatusList.forEach(action2->{
								if(action.getFarmlandId().equals(action2.getFarmlandId())) {
									action.setSurveyOneStatusCode(action2.getSurveyOneStatusCode());
									action.setSurveyOneStatus(action2.getSurveyOneStatus());
									action.setSurveyTwoStatusCode(action2.getSurveyTwoStatusCode());
									action.setSurveyTwoStatus(action2.getSurveyTwoStatus());
									action.setMainStatusCode(action2.getMainStatusCode());
									action.setMainStatus(action2.getMainStatus());
								}
								
							});
							
						});
						
					}
				}

				

			}
			
			

			return CustomMessages.makeResponseModel(userLandAssignMent, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}
	
	/**
	 * Removes land assignments for a list of users based on the given filter criteria.
	 *
	 * @param request The HttpServletRequest object.
	 * @param surveyTaskAllocationFilterDAO The filter criteria for removing the land assignments.
	 * @return The ResponseModel object indicating the status of the land removal process.
	 */	
	public ResponseModel removeLandByUserList(HttpServletRequest request,
			SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		ResponseModel responseModel = null;
		try {
			
			if (surveyTaskAllocationFilterDAO.getUserList() != null && surveyTaskAllocationFilterDAO.getUserList().size() > 0) {
				
				List<LandUserDAO> filterList=userLandAssignmentRepository.getfamlandIdByFilter(surveyTaskAllocationFilterDAO.getStartYear(), surveyTaskAllocationFilterDAO.getEndYear(), surveyTaskAllocationFilterDAO.getSeasonId(), surveyTaskAllocationFilterDAO.getVillageLgdCode(), surveyTaskAllocationFilterDAO.getUserList());
//				List<UserLandAssignment> userLandAssigment=userLandAssignmentRepository.findByUser_UserIdInAndIsActiveAndIsDeletedAndStatus_StatusCodeAndSeason_SeasonIdAndYear(surveyTaskAllocationFilterDAO.getUserList(), Boolean.TRUE, Boolean.FALSE,StatusEnum.PENDING.getValue(),surveyTaskAllocationFilterDAO.getSeasonId(),surveyTaskAllocationFilterDAO.getYear());
//				if (userLandAssigment != null && userLandAssigment.size() > 0) {
//					userLandAssigment.forEach(action->{
//						action.setIsActive(Boolean.FALSE);
//						action.setIsDeleted(Boolean.TRUE);
//						String userId=CustomMessages.getUserId(request, jwtTokenUtil);
//						if(userId!=null ){
//							action.setModifiedBy(userId);
//						}
//					});
//					userLandAssignmentRepository.saveAll(userLandAssigment);
//				}
				if(filterList!=null && filterList.size()>0) {
					List<String> farmlandIds=filterList.stream().map(x->x.getFarmLandId()).collect(Collectors.toList());
					List<UserLandAssignment> userLandAssigment=userLandAssignmentRepository.findByIsActiveAndIsDeletedAndSeason_SeasonIdAndStartingYearAndEndingYearAndFarmlandIdIn(true,false,surveyTaskAllocationFilterDAO.getSeasonId(),surveyTaskAllocationFilterDAO.getStartYear(), surveyTaskAllocationFilterDAO.getEndYear(),farmlandIds);
					if (userLandAssigment != null && userLandAssigment.size() > 0) {
						userLandAssigment.forEach(action->{
							action.setIsActive(Boolean.FALSE);
							action.setIsDeleted(Boolean.TRUE);
							String userId=CustomMessages.getUserId(request, jwtTokenUtil);
							if(userId!=null ){
								action.setModifiedBy(userId);	
							}
						});
						userLandAssignmentRepository.saveAll(userLandAssigment);
					}	
					
				}
					

			}

			return new ResponseModel(null, CustomMessages.TASK_REMOVED,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;

		}

	}
	
	/**
	 * Removes land assignments for a list of lands based on the given filter criteria.
	 *
	 * @param request The HttpServletRequest object.
	 * @param surveyTaskAllocationFilterDAO The filter criteria for removing the land assignments.
	 * @return The ResponseModel object indicating the status of the land removal process.
	 */
	public ResponseModel removeLandByLandList(HttpServletRequest request,
			SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		ResponseModel responseModel = null;
		try {
			
			if (surveyTaskAllocationFilterDAO.getLandIds() != null && surveyTaskAllocationFilterDAO.getLandIds().size() > 0) {
				List<UserLandAssignment> userLandAssigment=userLandAssignmentRepository.findByFarmlandIdInAndIsActiveAndIsDeletedAndSeason_SeasonIdAndStartingYearAndEndingYear(surveyTaskAllocationFilterDAO.getLandIds(), Boolean.TRUE, Boolean.FALSE,surveyTaskAllocationFilterDAO.getSeasonId(),surveyTaskAllocationFilterDAO.getStartYear(),surveyTaskAllocationFilterDAO.getEndYear());
//				List<UserLandAssignment> userLandAssigment=userLandAssignmentRepository.findByFarmlandIdInAndIsActiveAndIsDeletedAndStatus_StatusCodeAndSeason_SeasonIdAndYear(surveyTaskAllocationFilterDAO.getLandIds(), Boolean.TRUE, Boolean.FALSE,StatusEnum.PENDING.getValue(),surveyTaskAllocationFilterDAO.getSeasonId(),surveyTaskAllocationFilterDAO.getYear());
//				List<UserLandAssignment> userLandAssigment=userLandAssignmentRepository.findByFarmlandIdInAndIsActiveAndIsDeletedAndStatus_StatusCode(surveyTaskAllocationFilterDAO.getLandIds(), Boolean.TRUE, Boolean.FALSE,StatusEnum.PENDING.getValue());
				if (userLandAssigment != null && userLandAssigment.size() > 0) {
					userLandAssigment.forEach(action->{
						action.setIsActive(Boolean.FALSE);
						action.setIsDeleted(Boolean.TRUE);
						String userId=CustomMessages.getUserId(request, jwtTokenUtil);
						if(userId!=null ){
							action.setModifiedBy(userId);	
						}
					});
					userLandAssignmentRepository.saveAll(userLandAssigment);
				}	
			}

			return new ResponseModel(null, CustomMessages.TASK_REMOVED,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;

		}

	}
	
	/**
	 * Retrieves pending land assignments for the specified users based on the given filter criteria.
	 *
	 * @param request The HttpServletRequest object.
	 * @param surveyTaskAllocationFilterDAO The filter criteria for retrieving pending land assignments.
	 * @return The ResponseModel object containing the pending land assignments for the users.
	 */
	public ResponseModel getPendingLandByusers(HttpServletRequest request,
			SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		ResponseModel responseModel = null;
		try {
			
			List<UserLandCount> landDataList=new ArrayList<>();
			StatusMaster statusMaster=statusMasterRepository.findByIsDeletedFalseAndStatusCode(StatusEnum.PENDING.getValue());
			if(statusMaster!=null ) {
				 landDataList=userLandAssignmentRepository.getCountDetailsByVillageCodeWithfilterPendingUserId(surveyTaskAllocationFilterDAO.getSeasonId(), surveyTaskAllocationFilterDAO.getYear(), statusMaster.getStatusId(), surveyTaskAllocationFilterDAO.getUserList());
				
			}
					
           

			return new ResponseModel(landDataList, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;

		}

	}
	
	/**
	 * Removes land assignments by user filter.
	 *
	 * @param request The HttpServletRequest object.
	 * @param surveyTaskAllocationFilterDAO The filter criteria for removing land assignments.
	 * @return The ResponseModel object indicating the success of the operation.
	 */
	@Transactional
	public ResponseModel removeLandByUserFilter(HttpServletRequest request,
			SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		ResponseModel responseModel = null;
		try {
			
			if (surveyTaskAllocationFilterDAO.getUserList() != null && surveyTaskAllocationFilterDAO.getUserList().size() > 0) {
				List<UserLandAssignment> userLandAssigment=userLandAssignmentRepository.findByUser_UserIdInAndIsActiveAndIsDeletedAndStatus_StatusCodeAndSeason_SeasonIdAndYear(surveyTaskAllocationFilterDAO.getUserList(), Boolean.TRUE, Boolean.FALSE,StatusEnum.PENDING.getValue(),surveyTaskAllocationFilterDAO.getSeasonId(),surveyTaskAllocationFilterDAO.getYear());
				if (userLandAssigment != null && userLandAssigment.size() > 0) {
					userLandAssigment.forEach(action->{
						action.setIsActive(Boolean.FALSE);
						action.setIsDeleted(Boolean.TRUE);
					});
					userLandAssignmentRepository.saveAll(userLandAssigment);
				}	
			}

			return new ResponseModel(null, CustomMessages.TASK_REMOVED,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;

		}

	}

    /**
     * Retrieves surveyors by taluka code with the specified filter criteria.
     *
     * @param request The HttpServletRequest object.
     * @param surveyTaskAllocationFilterDAO The filter criteria for retrieving surveyors.
     * @return The ResponseModel object containing the surveyors.
     */
	public ResponseModel getSurveyorByTalukaCodeWithFilter(HttpServletRequest request, SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		ResponseModel responseModel = null;
		try {
			List<PRPaymentReleaseDAO> finalResult=new ArrayList<>();
			List<UserLandCount> count = userLandAssignmentRepository
					.getCountDetailsByUserWithfilter(surveyTaskAllocationFilterDAO.getSeasonId(), surveyTaskAllocationFilterDAO.getStartYear(), surveyTaskAllocationFilterDAO.getEndYear(),surveyTaskAllocationFilterDAO.getUserList());
			if(count!=null && count.size()>0) {
//				ConfigCode.ONE_USER_ONE_DEVICE
				List<ConfigCode> configCodes=new ArrayList<>();
				configCodes.add(ConfigCode.MIN_AMOUNT);
				configCodes.add(ConfigCode.MAX_AMOUNT);
				configCodes.add(ConfigCode.NA_AMOUNT);
				configCodes.add(ConfigCode.FALLOW_AMOUNT);
				configCodes.add(ConfigCode.PER_CROP_AMOUNT);
				
				List<ConfigurationMaster> configList=configurationRepository.findByIsActiveTrueAndIsDeletedFalseAndConfigCodeIn(configCodes);
				StatusMaster statusMaster=statusMasterRepository.findByIsDeletedFalseAndStatusCode(StatusEnum.SURVEY_PENDING.getValue());

				StatusMaster statusApprove=statusMasterRepository.findByIsDeletedFalseAndStatusCode(StatusEnum.APPROVED.getValue());
				Set<Long> userIds=count.stream().map(data->data.getUserId()).collect(Collectors.toSet());
				List<UserMaster> usermasterIds = userMasterRepository.findByUserIdInOrderByUserFullNameAsc(userIds);
				
				if (usermasterIds != null && usermasterIds.size() > 0) {
					Set<Long> vcodes=usermasterIds.stream().map(x->Long.valueOf(x.getUserVillageLGDCode())).collect(Collectors.toSet());
					List<VillageLgdMaster> villageLgdCodeDetails=villageLgdMasterRepository.findByVillageLgdCodeIn(vcodes);
					List<PrCountDAO> prCountDaoList=userLandAssignmentRepository.getPrPaymentCounts(surveyTaskAllocationFilterDAO.getSeasonId(), surveyTaskAllocationFilterDAO.getStartYear(), surveyTaskAllocationFilterDAO.getEndYear(), userIds);
					usermasterIds.forEach(action->{
						
						PRPaymentReleaseDAO prPaymentReleaseDAO=new PRPaymentReleaseDAO();
	
						configList.forEach(action2->{
							
							if(action2.getConfigKey().equals(ConfigCode.NA_AMOUNT.toString())) {
								prPaymentReleaseDAO.setNAAmountPerSurvey(Long.valueOf(action2.getConfigValue()));
							}else if(action2.getConfigKey().equals(ConfigCode.FALLOW_AMOUNT.toString())) {
								prPaymentReleaseDAO.setFallowAmountPerSurvey(Long.valueOf(action2.getConfigValue()));
							}else if(action2.getConfigKey().equals(ConfigCode.PER_CROP_AMOUNT.toString())) {
								prPaymentReleaseDAO.setCropAmountPerSurvey(Long.valueOf(action2.getConfigValue()));
							}else if(action2.getConfigKey().equals(ConfigCode.MIN_AMOUNT.toString())) {
								prPaymentReleaseDAO.setMinimumAmountPerSurvey(Long.valueOf(action2.getConfigValue()));
							}else if(action2.getConfigKey().equals(ConfigCode.MAX_AMOUNT.toString())) {
								prPaymentReleaseDAO.setMaximumAmountPerSurvey(Long.valueOf(action2.getConfigValue()));
							}
							
							
							
//							action2.get
						});
						prPaymentReleaseDAO.setCalculatedAmount(0l);
						List<PrCountDAO> prCountDaoFilterList=prCountDaoList.stream().filter(x->x.getUserId().equals(action.getUserId())).collect(Collectors.toList());
						if(prCountDaoFilterList!=null && prCountDaoFilterList.size()>0) {
							prCountDaoFilterList.forEach(countDao->{
								
								
								
								
							Long calculatedAmount=prPaymentReleaseDAO.getMinimumAmountPerSurvey()+(prPaymentReleaseDAO.getNAAmountPerSurvey()*countDao.getNaCount())+(prPaymentReleaseDAO.getFallowAmountPerSurvey()*countDao.getFallowCount())+((countDao.getCropCount()-1)*prPaymentReleaseDAO.getCropAmountPerSurvey());
							
							
							if(prPaymentReleaseDAO.getMaximumAmountPerSurvey()!=null && calculatedAmount.compareTo(prPaymentReleaseDAO.getMaximumAmountPerSurvey())<0) {
								prPaymentReleaseDAO.setCalculatedAmount(prPaymentReleaseDAO.getCalculatedAmount()+calculatedAmount);
							
							}else {
							
								prPaymentReleaseDAO.setCalculatedAmount(prPaymentReleaseDAO.getCalculatedAmount()+prPaymentReleaseDAO.getMaximumAmountPerSurvey());
							
								
							}
//								countDao.getCropCount();
							});
							prPaymentReleaseDAO.setNaCount(prCountDaoFilterList.get(0).getNaCount());
							prPaymentReleaseDAO.setFallowCount(prCountDaoFilterList.get(0).getFallowCount());
							prPaymentReleaseDAO.setCropCount(prCountDaoFilterList.get(0).getCropCount());
//							prPaymentReleaseDAO.setMoreThanOneCropCount(prCountDaoFilterList.getCropCount);
							
						}
						
						
						
						prPaymentReleaseDAO.setSurveyorId(action.getUserId());
						prPaymentReleaseDAO.setSurveyorPrId(action.getUserPrId());
						prPaymentReleaseDAO.setSurveyorFullName(action.getUserFullName());
						prPaymentReleaseDAO.setSurveyConductedPlotCount(0l);
						prPaymentReleaseDAO.setSurveyApprovedPlotCount(0l);
						prPaymentReleaseDAO.setReleasePaymentPlotCount(0);
						
//						
						if(action.getUserVillageLGDCode()!=null&& villageLgdCodeDetails!=null && villageLgdCodeDetails.size()>0) {
							Long userVillageLGDCode=Long.valueOf(action.getUserVillageLGDCode());
							List<VillageLgdMaster> vdatalist=villageLgdCodeDetails.stream().filter(x->x.getVillageLgdCode().equals(userVillageLGDCode)).collect(Collectors.toList());
							if(vdatalist!=null && vdatalist.size()>0) {
								
								prPaymentReleaseDAO.setVillageName(vdatalist.get(0).getVillageName());	
							}
//							
						}
						finalResult.add(prPaymentReleaseDAO);
						
					});
					finalResult.forEach(action->{
						count.forEach(action2->{
							if(action.getSurveyorId().equals(action2.getUserId())){
								action.setTotalAssignPlotCount(action2.getTotalCount());
								
							}
						});
					});
					
//					List<UserLandAssignment> userLandAssignMentList = userLandAssignmentRepository
//							.findByYearAndSeason_SeasonIdAndVillageLgdCode_villageLgdCodeAndIsActiveAndIsDeleted(surveyTaskAllocationFilterDAO.getYear(),surveyTaskAllocationFilterDAO.getSeasonId(),surveyTaskAllocationFilterDAO.getVillageLgdCode(),true,false);
					List<PRPaymentRelease> prList=prPaymentReleaseRepository.findByStartYearAndEndYearAndSeason_SeasonIdAndSurveyorId_UserIdInAndIsActiveAndIsDeleted(surveyTaskAllocationFilterDAO.getStartYear(), surveyTaskAllocationFilterDAO.getEndYear(), surveyTaskAllocationFilterDAO.getSeasonId(), surveyTaskAllocationFilterDAO.getUserList(), true, false);
					if(prList!=null && prList.size()>0) {
						finalResult.forEach(action->{
							prList.forEach(action2->{
								
							
							if(action.getSurveyorId().equals(action2.getSurveyorId().getUserId())){
								action.setPrId(action2.getPrId());
								
								action.setReleasePaymentPlotCount(action2.getTotalPlotForPayment());
								action.setRemarks(action2.getRemarks());
								action.setStatusName(action2.getPaymentStatus().getStatusName());
								action.setCalculatedAmount(action2.getCalculatedAmount());
								
							}
							});
						});
					}
					List<UserLandCount> assignLandCounts= userLandAssignmentRepository.getAssignLandListByUser(surveyTaskAllocationFilterDAO.getStartYear(), surveyTaskAllocationFilterDAO.getEndYear(), surveyTaskAllocationFilterDAO.getSeasonId(), surveyTaskAllocationFilterDAO.getUserList(),statusMaster.getStatusCode());					
					if(assignLandCounts!=null && assignLandCounts.size()>0) {
						
						finalResult.forEach(action->{
							assignLandCounts.forEach(action2->{
								
								
								if(action.getSurveyorId().equals(action2.getUserId())){
									action.setSurveyConductedPlotCount(action2.getTotalCount());
								}
							});
						});
		
						
					}
					List<UserLandCount> approveCounts= userLandAssignmentRepository.getAproveSurveyListByUser(surveyTaskAllocationFilterDAO.getStartYear(), surveyTaskAllocationFilterDAO.getEndYear(), surveyTaskAllocationFilterDAO.getSeasonId(), surveyTaskAllocationFilterDAO.getUserList(), statusApprove.getStatusCode());
					if(approveCounts!=null && approveCounts.size()>0) {
						
						finalResult.forEach(action->{
							approveCounts.forEach(action2->{
								
								
								if(action.getSurveyorId().equals(action2.getUserId())){
									action.setSurveyApprovedPlotCount(action2.getTotalCount());
								}
							});
						});
		
						
					}

				}
			}
			
	
			
			
	

			return CustomMessages.makeResponseModel(finalResult, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Removes land assignments for surveyors based on a filter.
	 *
	 * @param request The HttpServletRequest object.
	 * @param surveyTaskAllocationFilterDAO The filter criteria for removing land assignments.
	 * @return true if the land assignments are successfully removed, false otherwise.
	 */
	public Boolean removeSurveyorLandAssignment(HttpServletRequest request,
											  SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		ResponseModel responseModel = null;
		try {
			if (surveyTaskAllocationFilterDAO.getUserList() != null && surveyTaskAllocationFilterDAO.getUserList().size() > 0) {

				List<LandUserDAO> filterList=userLandAssignmentRepository.getfamlandAssignmentForSurveyor(surveyTaskAllocationFilterDAO.getStartYear(), surveyTaskAllocationFilterDAO.getEndYear(), surveyTaskAllocationFilterDAO.getSeasonId(),surveyTaskAllocationFilterDAO.getUserList());

				if(filterList!=null && filterList.size()>0) {
					List<String> farmlandIds=filterList.stream().map(x->x.getFarmLandId()).collect(Collectors.toList());
					List<UserLandAssignment> userLandAssigment=userLandAssignmentRepository.findByIsActiveAndIsDeletedAndSeason_SeasonIdAndStartingYearAndEndingYearAndFarmlandIdIn(true,false,surveyTaskAllocationFilterDAO.getSeasonId(),surveyTaskAllocationFilterDAO.getStartYear(), surveyTaskAllocationFilterDAO.getEndYear(),farmlandIds);
					if (userLandAssigment != null && userLandAssigment.size() > 0) {
						userLandAssigment.forEach(action->{
							action.setIsActive(Boolean.FALSE);
							action.setIsDeleted(Boolean.TRUE);
							String userId=CustomMessages.getUserId(request, jwtTokenUtil);
							if(userId!=null ){
								action.setModifiedBy(userId);
							}
						});
						userLandAssignmentRepository.saveAll(userLandAssigment);
					}

				}
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * @param request
	 * @param surveyTaskAllocationFilterDAO
	 * @return
	 */
	public ResponseModel getSurveyorByTalukaCodeWithFilterNew(HttpServletRequest request,
			SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		ResponseModel responseModel = null;
		try {
			
			String userIds = getJoinedValuefromList(surveyTaskAllocationFilterDAO.getUserList());
			String response = userLandAssignmentRepository
					.getCalculatePrPaymentDetails(userIds,  surveyTaskAllocationFilterDAO.getSeasonId().intValue(), surveyTaskAllocationFilterDAO.getStartYear(), surveyTaskAllocationFilterDAO.getEndYear());

			
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<Object> finalList = mapper.readValue(response,
					typeFactory.constructCollectionType(List.class, Object.class));
			

			
				return CustomMessages.makeResponseModel(finalList, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
						CustomMessages.SUCCESS);
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

	/**
     * Retrieves unable survey plot details.
     *
     * @param request The HttpServletRequest object.
     * @param requestDAO The filter criteria for retrieving data.
     * @return The ResponseModel object containing the unable survey plot details.
     */
	public ResponseModel getUnableToSurveyDetails(PaginationDao paginationDao, HttpServletRequest request) {
		
		try {

			String search=paginationDao.getSearch()!=null?paginationDao.getSearch():"";
			System.out.println("search "+search);
			
			String userId = CustomMessages.getUserId(request, jwtTokenUtil);

			userId = (userId != null && !userId.isEmpty()) ? userId : "0";
			String villageList = getJoinedValuefromList(paginationDao.getVillageLgdCodeList());
			String response = userLandAssignmentRepository.getUnableToSurveyDetails(Long.valueOf(userId),paginationDao.getStartingYear(), paginationDao.getEndingYear(), paginationDao.getSeasonId().intValue() ,villageList, paginationDao.getPage(), paginationDao.getLimit(), paginationDao.getSortField(),paginationDao.getSortOrder(), search);

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<Object> finalResult = mapper.readValue(response,
					typeFactory.constructCollectionType(List.class, Object.class));
			
			Map<String, Object> responseData = new HashMap();
			responseData.put("data", finalResult);
//			responseData.put("total");
			responseData.put("page", paginationDao.getPage());
			responseData.put("limit", paginationDao.getLimit());
			responseData.put("sortField", paginationDao.getSortField());
			responseData.put("sortOrder", paginationDao.getSortOrder());

			return new ResponseModel(responseData, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
     * Retrieves unable survey plot enable.
     *
     * @param request The HttpServletRequest object.
     * @param inputDAO The filter criteria for retrieving data.
     * @return The ResponseModel object containing the enable the unable survey plot.
     */
	public ResponseModel unableToSurveyEnable(UserInputDAO inputDAO) {
		ResponseModel responseModel = null;
		try {
			
			userLandAssignmentRepository.unableToSurveyEnable(inputDAO.getLpsmId());
		
		 responseModel =  CustomMessages.makeResponseModel(null, CustomMessages.RECORD_UPDATE,
				 CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			return responseModel;
		} catch (Exception e) {
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Retrieves assignment data based on the provided filter criteria.
	 *
	 * @param request The HttpServletRequest object.
	 * @param surveyTaskAllocationFilterDAO The filter criteria for retrieving the assignment data.
	 * @return The ResponseModel object containing the assignment data based on the filter criteria.
	 */
	public ResponseModel getAssignDataByFilterV2(HttpServletRequest request, SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		ResponseModel responseModel = null;
		try {
			
			String response=userLandAssignmentRepository.getAssignDataByvillagelgdcode(surveyTaskAllocationFilterDAO.getVillageLgdCode(),surveyTaskAllocationFilterDAO.getSeasonId(),surveyTaskAllocationFilterDAO.getStartYear(),surveyTaskAllocationFilterDAO.getEndYear());
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<Object> finalList = mapper.readValue(response,
					typeFactory.constructCollectionType(List.class, Object.class));
			

			return CustomMessages.makeResponseModel(finalList, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Retrieves surveyors assigned to a specific village based on the village code, with additional filtering options.
	 *
	 * @param request The HttpServletRequest object.
	 * @param surveyTaskAllocationFilterDAO The filter criteria for retrieving the surveyors.
	 * @return The ResponseModel object containing the surveyors assigned to the specified village, filtered by the given criteria.
	 */
	public ResponseModel getSurveyorByVillageCodeWithFilterV2(HttpServletRequest request,
			SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		ResponseModel responseModel = null;
		try {
		
				
				String response=userLandAssignmentRepository.getAssignSurveyorDataByvillagelgdcode(surveyTaskAllocationFilterDAO.getVillageLgdCode(),surveyTaskAllocationFilterDAO.getSeasonId(),surveyTaskAllocationFilterDAO.getStartYear(),surveyTaskAllocationFilterDAO.getEndYear());
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

				TypeFactory typeFactory = mapper.getTypeFactory();
				List<Object> finalList = mapper.readValue(response,
						typeFactory.constructCollectionType(List.class, Object.class));
				

				return CustomMessages.makeResponseModel(finalList, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
						CustomMessages.SUCCESS);
			} catch (Exception e) {
				e.printStackTrace();

				responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
						CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
				return responseModel;
			}

	}
	
}
