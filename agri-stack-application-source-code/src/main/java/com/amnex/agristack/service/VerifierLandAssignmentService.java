package com.amnex.agristack.service;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.*;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.*;
import com.amnex.agristack.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.amnex.agristack.Enum.RoleEnum;
import com.amnex.agristack.Enum.StatusEnum;
import com.amnex.agristack.Enum.VerifierReasonOfAssignmentEnum;
import com.amnex.agristack.utils.CustomMessages;

@Service
public class VerifierLandAssignmentService {

//	public static final String reasonOfAssignmentRandomPick = "Random Pick for Verification";
//	public static final String reasonOfAssignmentSupervisorRejection = "PR Survey Rejected by Supervisor";
//	public static final String reasonOfAssignmentFarmerObjection = "Objection Raised by Farmer";

	@Autowired
	private UserMasterRepository userMasterRepository;

	@Autowired
	private SeasonMasterRepository seasonMasterRepository;

	@Autowired
	private VillageLgdMasterRepository villageLgdMasterRepository;

	@Autowired
	private VerifierLandAssignmentRepository verifierLandAssignmentRepository;

	@Autowired
	private StatusMasterRepository statusMasterRepository;

	@Autowired
	private FarmlandPlotRegistryRepository farmlandPlotRegistryRepository;

	@Autowired
	private SurveyVerificationConfigurationMasterRepository surveyVerificationConfigurationMasterRepository;

	@Autowired
	private GeneralService generalService;

	@Autowired
	private NonAgriPlotMappingRepository nonAgriPlotMappingRepository;

	@Autowired
	private UserVillageMappingRepository userVillageMappingRepository;

	@Autowired
	private UserLandAssignmentRepository userLandAssignmentRepository;
	
	@Autowired
	private UserAvailabilityRepository userAvailabilityRepository;

	@Autowired
	private RoleMasterRepository roleRepository;
	
	@Autowired
	private VerifierLandConfigurationRepository verifierLandConfigurationRepository;
	public ResponseModel getFarmlandPlotByFilter(HttpServletRequest request, VerifierTaskAllocationDAO dao) {
		ResponseModel responseModel = null;
		try {

			Pageable pageable = PageRequest.of(dao.getPage(), dao.getLimit(), Sort.by(dao.getSortField()).descending());

			if (dao.getSortOrder().equals("asc")) {
				pageable = PageRequest.of(dao.getPage(), dao.getLimit(), Sort.by(dao.getSortField()).ascending());
			}
			Map<String, Object> responseData = new HashMap();
			responseData.put("page", dao.getPage());
			responseData.put("limit", dao.getLimit());
			responseData.put("sortField", dao.getSortField());
			responseData.put("sortOrder", dao.getSortOrder());
			List<FarmlandPlotRegistryDAO> finalResult = new ArrayList<>();
			SowingSeason sowingSeason = generalService.getSeasonStartEndYear(dao.getSeasonId());

			if (dao.getSubDistrictLgdCodeList() != null && dao.getSubDistrictLgdCodeList().size() > 0) {
				List<VillageLgdMaster> villageList = villageLgdMasterRepository
						.findBySubDistrictLgdCode_SubDistrictLgdCodeIn(dao.getSubDistrictLgdCodeList());
				if (villageList != null && villageList.size() > 0) {
					List<Long> villagelgdCodes = villageList.stream().map(mapper -> mapper.getVillageLgdCode())
							.collect(Collectors.toList());
					List<FarmlandPlotRegistry> villageWisePlots = farmlandPlotRegistryRepository
							.findByVillageLgdMaster_VillageLgdCodeIn(villagelgdCodes);

					List<String> plotsToExclude = new ArrayList<>();
					// exclude NA (Non Agriculture) Plots
					List<NonAgriPlotMapping> naPlotList = nonAgriPlotMappingRepository
							.findAllBySeasonIdStartYearEndYearAndVillageLGDCode(dao.getSeasonId(),
									dao.getStartingYear(), dao.getEndingYear(), villagelgdCodes);

					if (naPlotList != null && naPlotList.size() > 0) {
						plotsToExclude.addAll(naPlotList.stream().map(x -> x.getLandParcelId().getFarmlandId())
								.collect(Collectors.toList()));
					}

					List<FarmlandPlotRegistry> plotsToConsider = villageWisePlots.stream()
							.filter(plot -> !plotsToExclude.contains(plot.getFarmlandId()))
							.collect(Collectors.toList());

					if (plotsToConsider != null && plotsToConsider.size() > 0) {
						plotsToConsider.forEach(plot -> {

							if (sowingSeason != null) {
								plot.setSeason(sowingSeason);
							}
						});
					}

					Page<FarmLandCount> farmLandcountDetails = farmlandPlotRegistryRepository
							.getCountDetailsByVillageCodeByPagination(villagelgdCodes, pageable);
//					.getCountDetailsByVillageCode(villagelgdCodes);
					responseData.put("total", farmLandcountDetails.getTotalElements());
					if (farmLandcountDetails != null) {

						farmLandcountDetails.forEach(count -> {
							FarmlandPlotRegistryDAO farmlandPlotRegistryDAO = new FarmlandPlotRegistryDAO();

							farmlandPlotRegistryDAO.setVillageLgdCode(count.getVillageLgdCode());
							farmlandPlotRegistryDAO.setTotalAssignedPlotsCount(0l);
							farmlandPlotRegistryDAO.setTotalPlotVerificationCompleteCount(0l);

							if (sowingSeason != null) {
								farmlandPlotRegistryDAO.setSeason(sowingSeason);
							}
							finalResult.add(farmlandPlotRegistryDAO);

						});

						if (finalResult != null && finalResult.size() > 0) {
							List<Long> vcodes = finalResult.stream().map(x -> x.getVillageLgdCode())
									.collect(Collectors.toList());
							List<UserCountDAO> completedCounts = userLandAssignmentRepository
									.getSurveyorApproveCountByFilter(dao.getStartingYear(), dao.getEndingYear(),
											dao.getSeasonId(), vcodes);
							finalResult.forEach(action -> {
								completedCounts.forEach(action2 -> {

									if (action.getVillageLgdCode().equals(action2.getVillageCode())) {
										action.setTotalPlotVerificationCompleteCount(action2.getTotalCount());
//										
									}
								});
							});
						}
						finalResult.forEach(action -> {

							villageList.forEach(action2 -> {
								if (action.getVillageLgdCode().equals(action2.getVillageLgdCode())) {
									action.setVillageName(action2.getVillageName());
									action.setSubDistrictName(action2.getSubDistrictLgdCode().getSubDistrictName());

									List<UserLandCount> count = verifierLandAssignmentRepository
											.getCountDetailsByVillageCode(action.getVillageLgdCode(), dao.getSeasonId(),
													dao.getStartingYear(), dao.getEndingYear());

									if (count != null && count.size() > 0) {
										UserLandCount verifierCount = count.get(0);
										action.setTotalAssignedPlotsCount(verifierCount.getTotalCount());
										Optional<UserMaster> verifier = userMasterRepository
												.findByUserId(verifierCount.getUserId());
										if (verifier.isPresent()) {
											String firstName = verifier.get().getUserFirstName();
											String lastName = verifier.get().getUserLastName();
											String fullName = firstName != null ? firstName
													: "" + " " + lastName != null ? lastName : "";
											action.setVerifierName(fullName);
											action.setVerifierId(verifier.get().getUserId());
										}
									}
								}

							});

						});

					}
				}

			}

			responseData.put("data", finalResult);

			return CustomMessages.makeResponseModel(responseData, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	public ResponseModel getAssignedDataByFilter(HttpServletRequest request, VerifierTaskAllocationDAO dao) {
		ResponseModel responseModel = null;
		try {
			List<VerifierLandAssignment> verifierLandAssignMent = verifierLandAssignmentRepository
					.findByStartingYearAndEndingYearAndSeason_SeasonIdAndVillageLgdCode_villageLgdCodeAndIsActiveAndIsDeletedAndVerifier_UserId(
							dao.getStartingYear(), dao.getEndingYear(), dao.getSeasonId(), dao.getVillageLgdCode(),
							Boolean.TRUE, Boolean.FALSE, dao.getVerifierId());

			return CustomMessages.makeResponseModel(verifierLandAssignMent, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	public ResponseModel getAvailableVerifiers(HttpServletRequest request, VerifierTaskAllocationDAO dao) {
		try {

			List<Long> unavailableVerifierList = verifierLandAssignmentRepository
					.findByYearAndSeason(dao.getStartingYear(), dao.getEndingYear(), dao.getSeasonId());

			if (unavailableVerifierList == null || unavailableVerifierList.isEmpty()) {
				unavailableVerifierList.add(0l);
			}

			List<Object[]> verifierList = userMasterRepository.findAllVerifiers(RoleEnum.VERIFIER.toString(),
					unavailableVerifierList, dao.getUserId());
			List<UserMasterReturnDAO> availableVerifierList = new ArrayList<>();

			for (Object obj[] : verifierList) {
				UserMasterReturnDAO userDao = new UserMasterReturnDAO();
				userDao.setUserId(Long.parseLong(String.valueOf(obj[0])));
				userDao.setUserFirstName(obj[1] != null ? (String) obj[1] : "");
				userDao.setUserLastName(obj[2] != null ? (String) obj[2] : "");
				userDao.setUserPrId(obj[3] != null ? (String) obj[3] : "");
				availableVerifierList.add(userDao);
			}

			return CustomMessages.makeResponseModel(availableVerifierList, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	public ResponseModel assignTaskToVerifier(HttpServletRequest request, VerifierTaskAllocationDAO dao) {
		try {

			
			SowingSeason currentSeason = generalService.getCurrentSeason();
			List<VerifierLandAssignment> plotsToExclude = new ArrayList<>();
			if ((Long.valueOf(RoleEnum.INSPECTION_OFFICER.getValue())).equals(dao.getUserType())) {

					plotsToExclude = verifierLandAssignmentRepository
							.findByStartingYearAndEndingYearAndSeason_SeasonIdAndVillageLgdCode_villageLgdCodeAndIsActiveAndIsDeleted(dao.getStartingYear(),
									dao.getEndingYear(),
									dao.getSeasonId(),
									dao.getVillageLgdCode());

			}else {

					plotsToExclude = verifierLandAssignmentRepository
							.findByStartingYearAndEndingYearAndSeason_SeasonIdAndVillageLgdCode_villageLgdCodeAndIsActiveAndIsDeletedAndIsSecondTimeRejected(dao.getStartingYear(),
									dao.getEndingYear(),
									dao.getSeasonId(),
									dao.getVillageLgdCode(), true, false, true);
			}
			List<VerifierLandConfiguration> landConfigList=new ArrayList<>();
			List<FarmlandPlotVerifierDAO> farmlandIdList = new ArrayList<>();
			List<FarmlandPlotRegistry> fprList = new ArrayList<>();
			if(plotsToExclude!=null && plotsToExclude.size()>0) {
				List<String> plotsToExcludeList =plotsToExclude.stream().map(x->x.getFarmlandId()).collect(Collectors.toList());
				List<FarmlandPlotRegistry> farmLandPlotsExclude = farmlandPlotRegistryRepository.findAllByFarmLandPlotIds(plotsToExcludeList);
				List<Long> plotsToExcludeId = farmLandPlotsExclude.stream().map(FarmlandPlotRegistry::getFarmlandPlotRegistryId).collect(Collectors.toList());
//				landConfigList=verifierLandConfigurationRepository.findByConfigId_StateLgdMaster_StateLgdCodeAndConfigId_IsActiveAndConfigId_IsDeletedAndIsActiveAndIsDeletedAndSeason_SeasonIdAndStartingYearAndEndingYearAndVillageLgdCode_VillageLgdCodeAndLandPlot_FarmlandIdNotIn
//						(dao.getStateLgdCode(), true, false, true, false, dao.getSeasonId(), dao.getStartingYear(), dao.getEndingYear(),dao.getVillageLgdCode(), plotsToExcludeList);
				if ((Long.valueOf(RoleEnum.INSPECTION_OFFICER.getValue())).equals(dao.getUserType())) {
//					landConfigList = verifierLandConfigurationRepository.findByConfigId_StateLgdMaster_StateLgdCodeAndConfigId_IsActiveAndConfigId_IsDeletedAndIsActiveAndIsDeletedAndSeason_SeasonIdAndStartingYearAndEndingYearAndVillageLgdCode_VillageLgdCodeAndLandPlotsNotIn
//							(dao.getStateLgdCode(), true, false, true, false, dao.getSeasonId(), dao.getStartingYear(), dao.getEndingYear(), dao.getVillageLgdCode(), plotsToExcludeId);
						List<Long> landIds = verifierLandConfigurationRepository.findByConfigId_StateLgdMaster_StateLgdCodeAndConfigId_IsActiveAndConfigId_IsDeletedAndIsActiveAndIsDeletedAndSeason_SeasonIdAndStartingYearAndEndingYearAndVillageLgdCode_VillageLgdCodeAndFarmlandNotInV2(
							dao.getStateLgdCode(), dao.getSeasonId(), dao.getStartingYear(), dao.getEndingYear(),dao.getVillageLgdCode(),plotsToExcludeId);
					farmlandIdList = farmlandPlotRegistryRepository.getFarmLandIdsDetailsByids(landIds);
				}else {
					fprList = verifierLandAssignmentRepository.findSecondTimeRejectedSurveyLandDetailsByVillageLgdCodeWithPlotExcludeV2
							(dao.getSeasonId(), dao.getStartingYear(), dao.getEndingYear(), dao.getVillageLgdCode(),plotsToExcludeId);
				}

			}else {

				if ((Long.valueOf(RoleEnum.INSPECTION_OFFICER.getValue())).equals(dao.getUserType())) {
//					landConfigList = verifierLandConfigurationRepository.findByConfigId_StateLgdMaster_StateLgdCodeAndConfigId_IsActiveAndConfigId_IsDeletedAndIsActiveAndIsDeletedAndSeason_SeasonIdAndStartingYearAndEndingYearAndVillageLgdCode_VillageLgdCode
//							(dao.getStateLgdCode(), true, false, true, false, dao.getSeasonId(), dao.getStartingYear(), dao.getEndingYear(), dao.getVillageLgdCode());
					List<Long> landIds = verifierLandConfigurationRepository.findByConfigId_StateLgdMaster_StateLgdCodeAndConfigId_IsActiveAndConfigId_IsDeletedAndIsActiveAndIsDeletedAndSeason_SeasonIdAndStartingYearAndEndingYearAndVillageLgdCode_VillageLgdCodeV2(
							dao.getStateLgdCode(), dao.getSeasonId(), dao.getStartingYear(), dao.getEndingYear(),dao.getVillageLgdCode());
					farmlandIdList = farmlandPlotRegistryRepository.getFarmLandIdsDetailsByids(landIds);
				} else {
					fprList = verifierLandAssignmentRepository.findSecondTImeRejectedSurveyLandDetailsByVillageLgdCodeV2(dao.getSeasonId(), dao.getStartingYear(), dao.getEndingYear(),dao.getVillageLgdCode());
				}
			}
			Optional<UserMaster> opuser=userMasterRepository.findByUserId(dao.getVerifierId());
			StatusMaster statusMaster=statusMasterRepository.findByIsDeletedFalseAndStatusCode(StatusEnum.PENDING.getValue());
			List<VerifierLandAssignment> verifierLandAssignmentList=new ArrayList<>();

			if ((Long.valueOf(RoleEnum.INSPECTION_OFFICER.getValue())).equals(dao.getUserType())) {
				if (farmlandIdList != null && farmlandIdList.size() > 0) {

					farmlandIdList.forEach(action2 -> {
//							action.getLandPlots().forEach(action2->{
						VerifierLandAssignment verifierLandAssignment = new VerifierLandAssignment();
						if (opuser.isPresent()) {
							verifierLandAssignment.setVerifier(opuser.get());
						}
						VillageLgdMaster villageLgdMaster = villageLgdMasterRepository.findByVillageLgdCode(dao.getVillageLgdCode());
						verifierLandAssignment.setVillageLgdCode(villageLgdMaster);
						verifierLandAssignment.setSeason(currentSeason);
						if (statusMaster != null) {
							verifierLandAssignment.setStatus(statusMaster);
						}


						verifierLandAssignment.setFarmlandId(action2.getFarmlandId());
						verifierLandAssignment.setLandParcelId(action2.getLandParcelId());


						verifierLandAssignment.setStartingYear(dao.getStartingYear());
						verifierLandAssignment.setEndingYear(dao.getEndingYear());

						verifierLandAssignment.setIsActive(Boolean.TRUE);
						verifierLandAssignment.setIsDeleted(Boolean.FALSE);


						verifierLandAssignment.setReasonOfAssignment(VerifierReasonOfAssignmentEnum.RANDOM_PICK.getValue());
						verifierLandAssignment.setReasonOfAssignmentType(VerifierReasonOfAssignmentEnum.RANDOM_PICK);
						verifierLandAssignmentList.add(verifierLandAssignment);
					});
				}


			}else{
				fprList.forEach(action -> {
//							action.getLandPlots().forEach(action2->{
								VerifierLandAssignment verifierLandAssignment = new VerifierLandAssignment();
								if (opuser.isPresent()) {
									verifierLandAssignment.setVerifier(opuser.get());
								}

								verifierLandAssignment.setVillageLgdCode(action.getVillageLgdMaster());
								verifierLandAssignment.setSeason(currentSeason);
								if (statusMaster != null) {
									verifierLandAssignment.setStatus(statusMaster);
								}


								verifierLandAssignment.setFarmlandId(action.getFarmlandId());
								verifierLandAssignment.setLandParcelId(action.getLandParcelId());


								verifierLandAssignment.setStartingYear(dao.getStartingYear());
								verifierLandAssignment.setEndingYear(dao.getEndingYear());

								verifierLandAssignment.setIsActive(Boolean.TRUE);
								verifierLandAssignment.setIsDeleted(Boolean.FALSE);


								verifierLandAssignment.setReasonOfAssignment(VerifierReasonOfAssignmentEnum.SUPERVISOR_REJECTION.getValue());
								verifierLandAssignment.setReasonOfAssignmentType(VerifierReasonOfAssignmentEnum.SUPERVISOR_REJECTION);
								verifierLandAssignment.setIsSecondTimeRejected(Boolean.TRUE);

								verifierLandAssignmentList.add(verifierLandAssignment);
				});
			}
			
			if(verifierLandAssignmentList!=null && verifierLandAssignmentList.size()>0) {
				verifierLandAssignmentRepository.saveAll(verifierLandAssignmentList);
			}
//			SurveyVerificationConfigurationMasterDAO configDao = surveyVerificationConfigurationMasterRepository
//					.findByStateLgdCode(dao.getStateLgdCode());
//
//			Double plotAssignmentPercentage = configDao.getRandomPickPercentageOfPlots();
//
//			Integer plotAssignmentCount = configDao.getRandomPickMinimumNumberOfPlots();
//
//			List<FarmlandPlotRegistry> villageWisePlotList = farmlandPlotRegistryRepository
//					.findByVillageLgdMaster_VillageLgdCodeIn(dao.getVillageLdgCodeList());
//
//			// exclude plots which are already assigned
//			List<String> plotsToExclude = verifierLandAssignmentRepository
//					.findPreviouslyAllocatedPlots(generalService.getCurrentSeason().getEndingYear());
//			// exclude NA (Non Agriculture) Plots
//			List<NonAgriPlotMapping> naPlotList = nonAgriPlotMappingRepository
//					.findAllBySeasonIdStartYearEndYearAndVillageLGDCode(currentSeason.getSeasonId(),
//							currentSeason.getStartingYear(), currentSeason.getEndingYear(),
//							dao.getVillageLdgCodeList());
//
//			if (naPlotList != null && !naPlotList.isEmpty()) {
//				plotsToExclude.addAll(
//						naPlotList.stream().map(x -> x.getLandParcelId().getFarmlandId()).collect(Collectors.toList()));
//			}
//
//			List<FarmlandPlotRegistry> plotsToConsiderForAllocation = villageWisePlotList.stream()
//					.filter(plot -> !plotsToExclude.contains(plot.getFarmlandId())).collect(Collectors.toList());
//
//			Integer totalPlots = plotsToConsiderForAllocation.size(), noOfPlotsToAssign = 0;
//			Double plotPercentageWiseCount = (totalPlots * plotAssignmentPercentage) / 100;
//
//			if (plotAssignmentCount > plotPercentageWiseCount) {
//				noOfPlotsToAssign = plotAssignmentCount;
//			} else {
//				noOfPlotsToAssign = plotPercentageWiseCount.intValue();
//			}
//
//			if (noOfPlotsToAssign > totalPlots) {
//				noOfPlotsToAssign = totalPlots;
//			}
//
//			List<FarmlandPlotRegistry> listOfPlotsToAssign = plotsToConsiderForAllocation.subList(0, noOfPlotsToAssign);
//
//			listOfPlotsToAssign.forEach((plot) -> {
//				VerifierLandAssignment verifierLandAssignment = new VerifierLandAssignment();
//
//				verifierLandAssignment.setVerifier(userMasterRepository.findByUserId(dao.getVerifierId()).get());
//				verifierLandAssignment.setVillageLgdCode(plot.getVillageLgdMaster());
//				verifierLandAssignment.setSeason(currentSeason);
//				verifierLandAssignment.setStatus(
//						statusMasterRepository.findByIsDeletedFalseAndStatusCode(StatusEnum.PENDING.getValue()));
//
//				verifierLandAssignment.setFarmlandId(plot.getFarmlandId());
//				verifierLandAssignment.setLandParcelId(plot.getLandParcelId());
//
//				verifierLandAssignment.setStartingYear(currentSeason.getStartingYear());
//				verifierLandAssignment.setEndingYear(currentSeason.getEndingYear());
//
//				verifierLandAssignment.setIsActive(Boolean.TRUE);
//				verifierLandAssignment.setIsDeleted(Boolean.FALSE);
//				verifierLandAssignment.setReasonOfAssignment(VerifierReasonOfAssignmentEnum.RANDOM_PICK.getValue());
//				verifierLandAssignment.setReasonOfAssignmentType(VerifierReasonOfAssignmentEnum.RANDOM_PICK);
//
//				verifierLandAssignmentRepository.save(verifierLandAssignment);
//			});

			return CustomMessages.makeResponseModel("", CustomMessages.RECORD_ADD, CustomMessages.ADD_SUCCESSFULLY,
					CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	public ResponseModel getFarmlandPlotByVillageCode(HttpServletRequest request, VerifierTaskAllocationDAO dao) {
		ResponseModel responseModel = null;
		try {

//			List<FarmlandPlotRegistry> villageWisePlotList = farmlandPlotRegistryRepository
//					.findByVillageLgdMasterVillageLgdCode(Long.valueOf(dao.getVillageLgdCode()));
//			List<VerifierLandAssignment>
			List<VerifierLandAssignment> finalData = new ArrayList<>();
			if ((Long.valueOf(RoleEnum.INSPECTION_OFFICER.getValue())).equals(dao.getUserType())) {
				finalData=verifierLandAssignmentRepository.findLandAssignmentWithoutSecondRejected( dao.getStartingYear(),
						dao.getEndingYear(),dao.getSeasonId(), dao.getVillageLgdCode(),true,false);

			}else{
				finalData=verifierLandAssignmentRepository.findLandAssignmentWithSecondRejected( dao.getStartingYear(),
						dao.getEndingYear(),dao.getSeasonId(), dao.getVillageLgdCode(),true,false);
			}

//			List<VerifierLandAssignment> finalData=verifierLandAssignmentRepository.findByStartingYearAndEndingYearAndSeason_SeasonIdAndVillageLgdCode_villageLgdCodeAndIsActiveAndIsDeleted( dao.getStartingYear(),
//					dao.getEndingYear(),dao.getSeasonId(), dao.getVillageLgdCode(),true,false);

			
			// exclude NA (Non Agriculture) Plots
			List<NonAgriPlotMapping> naPlotList = nonAgriPlotMappingRepository
					.findAllBySeasonIdStartYearEndYearAndVillageLgdCode(dao.getSeasonId(), dao.getStartingYear(),
							dao.getEndingYear(), dao.getVillageLgdCode());

//			List<String> plotsToExclude = new ArrayList<>();
//			if (naPlotList != null && !naPlotList.isEmpty()) {
//				plotsToExclude.addAll(
//						naPlotList.stream().map(x -> x.getLandParcelId().getFarmlandId()).collect(Collectors.toList()));
//			}
//
//			List<FarmlandPlotRegistry> plotsToDisplay = villageWisePlotList.stream()
//					.filter(plot -> !plotsToExclude.contains(plot.getFarmlandId())).collect(Collectors.toList());

			if(naPlotList!=null && naPlotList.size()>0) {
				List<String> plotsToExclude=naPlotList.stream().map(x -> x.getLandParcelId().getFarmlandId()).collect(Collectors.toList());
				finalData.stream().filter(plot -> !plotsToExclude.contains(plot.getFarmlandId())).collect(Collectors.toList());
			}
			
			List<String> farmlandids=finalData.stream().map(x->x.getFarmlandId()).collect(Collectors.toList());
			if(farmlandids!=null && farmlandids.size()>0) {
			List<FarmlandPlotRegistry> flprList=farmlandPlotRegistryRepository.findByFarmlandIdIn(farmlandids);
			finalData.forEach(action->{
				List<FarmlandPlotRegistry> filter=flprList.stream().filter(x->x.getFarmlandId().equals(action.getFarmlandId())).collect(Collectors.toList());
				if(filter!=null && filter.size()>0) {
					action.setSurveyNumber(filter.get(0).getSurveyNumber());
					action.setSubSurveyNumber(filter.get(0).getSubSurveyNumber());
				}				
			});

		}
			
			return CustomMessages.makeResponseModel(finalData, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	public ResponseModel getVillageListForVerifierTaskAllocation(HttpServletRequest request,
			VerifierTaskAllocationDAO dao) {
		try {
			List<VillageLgdMaster> accessibleVillages = new ArrayList<>();
			List<Long> userAccessibleVillageCodes = new ArrayList<>();

			SurveyVerificationConfigurationMasterDAO configDao = surveyVerificationConfigurationMasterRepository
					.findByStateLgdCode(dao.getStateLgdCode());

			Double villageAssignmentPercentage = configDao.getRandomPickPercentageOfVillages();

			List<UserVillageMapping> userVillageMapping = userVillageMappingRepository
					.findByUserMaster_UserIdAndVillageLgdMaster_SubDistrictLgdCode_SubDistrictLgdCodeIn(dao.getUserId(),
							dao.getSubDistrictLgdCodeList());

			// need to exclude villages which are already assigned
			List<Long> villageCodesToExclude = verifierLandAssignmentRepository
					.findPreviouslyAllocatedVillages(generalService.getCurrentSeason().getEndingYear());

			List<UserVillageMapping> villagesToConsiderForAllocation = userVillageMapping.stream().filter(
					village -> !villageCodesToExclude.contains(village.getVillageLgdMaster().getVillageLgdCode()))
					.collect(Collectors.toList());

			Integer totalVillages = villagesToConsiderForAllocation.size();
			Double percentageWiseVillageCount = (totalVillages * villageAssignmentPercentage) / 100;

			if (percentageWiseVillageCount > totalVillages) {
				percentageWiseVillageCount = Double.parseDouble(String.valueOf(totalVillages));
			}

			List<UserVillageMapping> villageCodesToAdd = villagesToConsiderForAllocation.subList(0,
					percentageWiseVillageCount.intValue());

			accessibleVillages.addAll(villageCodesToAdd.stream().map(UserVillageMapping::getVillageLgdMaster)
					.collect(Collectors.toList()));

			userAccessibleVillageCodes.addAll(
					accessibleVillages.stream().map(VillageLgdMaster::getVillageLgdCode).collect(Collectors.toList()));

			if (userAccessibleVillageCodes.size() > 0) {
				accessibleVillages = villageLgdMasterRepository.findByVillageLgdCodeIn(userAccessibleVillageCodes);
			}

			return CustomMessages.makeResponseModel(accessibleVillages, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (

		Exception e) {
			e.printStackTrace();

			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	public ResponseModel unassignVerifierTaskAllocation(HttpServletRequest request, VerifierTaskAllocationDAO dao) {
		try {
//			List<LandUserDAO> filterList = verifierLandAssignmentRepository.getfamlandIdByFilter(dao.getStartingYear(),
//					dao.getEndingYear(), dao.getSeasonId(), dao.getVillageLgdCode(), dao.getVerifierId());
			List<LandUserDAO> filterList = new ArrayList<>();
			List<String> completedSurveyFarmlandId = new ArrayList<>();
			if ((Long.valueOf(RoleEnum.INSPECTION_OFFICER.getValue())).equals(dao.getUserType())){

				completedSurveyFarmlandId = userLandAssignmentRepository.getSurveyCompleteFarmlandByFilterWithoutSecondTimeRejected(
						dao.getStartingYear(),dao.getEndingYear(),dao.getSeasonId(), Collections.singletonList(dao.getVillageLgdCode()));

				if (!completedSurveyFarmlandId.isEmpty()){
					filterList = verifierLandAssignmentRepository.getFarmlandIdByFilterWithOutUserIdWithoutSecondRejectedFarmlandNotIn(dao.getStartingYear(),
							dao.getEndingYear(), dao.getSeasonId(), dao.getVillageLgdCode(),completedSurveyFarmlandId);
				}else {
					filterList = verifierLandAssignmentRepository.getfamlandIdByFilterWithOutUserIdWithoutSecondRejected(dao.getStartingYear(),
							dao.getEndingYear(), dao.getSeasonId(), dao.getVillageLgdCode());
				}
			}else{

				completedSurveyFarmlandId = userLandAssignmentRepository.getSurveyCompleteFarmlandByFilterWithSecondTimeRejected(
						dao.getStartingYear(),dao.getEndingYear(),dao.getSeasonId(), Collections.singletonList(dao.getVillageLgdCode()));

				if (!completedSurveyFarmlandId.isEmpty()){
					filterList = verifierLandAssignmentRepository.getFarmlandIdByFilterWithOutUserIdWithSecondRejectedFarmlandNotIn(dao.getStartingYear(),
							dao.getEndingYear(), dao.getSeasonId(), dao.getVillageLgdCode(),completedSurveyFarmlandId);
				}else {
					filterList = verifierLandAssignmentRepository.getfamlandIdByFilterWithOutUserIdWithSecondRejected(dao.getStartingYear(),
							dao.getEndingYear(), dao.getSeasonId(), dao.getVillageLgdCode());
				}
			}
//			List<LandUserDAO> filterList = verifierLandAssignmentRepository.getfamlandIdByFilterWithOutUserId(dao.getStartingYear(),
//					dao.getEndingYear(), dao.getSeasonId(), dao.getVillageLgdCode());
			if (filterList != null && filterList.size() > 0) {
				List<String> farmlandIds = filterList.stream().map(x -> x.getFarmLandId()).collect(Collectors.toList());

//				verifierLandAssignmentRepository.unassignVerifierTaskAllocation(dao.getVillageLgdCode(),
//						dao.getSeasonId(), dao.getStartingYear(), dao.getEndingYear(), dao.getVerifierId(),
//						farmlandIds);
				verifierLandAssignmentRepository.unassignVerifierTaskAllocationWithoutUserId(dao.getVillageLgdCode(),
						dao.getSeasonId(), dao.getStartingYear(), dao.getEndingYear(), farmlandIds);
				
			}

			return CustomMessages.makeResponseModel("", CustomMessages.RECORD_UPDATE,
					CustomMessages.UPDATE_SUCCESSFULLY, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}
	
	public ResponseModel getAvailableVerifiersByFilter(HttpServletRequest request, VerifierTaskAllocationDAO dao) {
		try {
//			dao.getSubDistrictLgdCodeList()
			RoleMaster role = new RoleMaster();
			if ((Long.valueOf(RoleEnum.INSPECTION_OFFICER.getValue())).equals(dao.getUserType())) {
				role = roleRepository.findByCode(RoleEnum.INSPECTION_OFFICER.toString()).orElse(null);
			} else if ((Long.valueOf(RoleEnum.VERIFIER.getValue())).equals(dao.getUserType())){
				role = roleRepository.findByCode(RoleEnum.VERIFIER.toString()).orElse(null);
			}

			List<UserVillageMapping> userVillageMappingList=userVillageMappingRepository.findByUserMaster_RoleId_RoleIdAndVillageLgdMaster_SubDistrictLgdCode_SubDistrictLgdCodeInAndIsActiveAndIsDeleted(role!=null ? role.getRoleId() : dao.getUserType(), dao.getSubDistrictLgdCodeList(), true, false);
			
			List<UserAvailability> dataList=new ArrayList<>();
			if(userVillageMappingList!=null && userVillageMappingList.size()>0) {
				List<Long> userIds=userVillageMappingList.stream().map(x->x.getUserMaster().getUserId()).collect(Collectors.toList());
				if(userIds!=null && userIds.size()>0) {
					dataList = userAvailabilityRepository.findByUserId_RoleId_RoleIdAndUserId_UserStatusAndIsAvailableAndSeasonStartYearAndSeasonEndYearAndSeasonIdAndUserId_UserIdInOrderByUserId_UserFullNameAsc(
							role.getRoleId(), StatusEnum.APPROVED.getValue(), true,
							dao.getStartingYear()+"",dao.getEndingYear()+"", dao.getSeasonId(),
							userIds);
			
				}
			}
//			List<UserAvailability> dataList = userAvailabilityRepository.findByUserId_RoleId_RoleIdAndUserId_UserStatusAndIsAvailableAndSeasonStartYearAndSeasonEndYearAndSeasonIdAndUserId_VerificationVillageLGDCodeOrderByUserId_UserFullNameAsc(
//							role.getRoleId(), StatusEnum.APPROVED.getValue(), true,
//							dao.getStartingYear()+"",dao.getEndingYear()+"", dao.getSeasonId(),
//							dao.getVillageLgdCode().intValue());
			
			List<UserMasterReturnDAO> availableVerifierList = new ArrayList<>();

			
			dataList.forEach(action->{
				UserMasterReturnDAO userDao = new UserMasterReturnDAO();
				userDao.setUserId(action.getUserId().getUserId());
				userDao.setUserFirstName(action.getUserId().getUserFirstName());
				userDao.setUserLastName(action.getUserId().getUserLastName());
				userDao.setUserPrId(action.getUserId().getUserPrId());
				userDao.setUserFullName(action.getUserId().getUserFullName());
				userDao.setUserMobileNumber(action.getUserId().getUserMobileNumber());
				userDao.setUserName(action.getUserId().getUserName());
				userDao.setUserEmailAddress(action.getUserId().getUserEmailAddress());
				availableVerifierList.add(userDao);
			});

			return CustomMessages.makeResponseModel(availableVerifierList, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}
	
	public ResponseModel getVerifierByVillageCodeWithFilter(HttpServletRequest request, VerifierTaskAllocationDAO dao) {
		ResponseModel responseModel = null;
		try {
			List<UserLandCount>  verifierLandAssignMent = new ArrayList<UserLandCount>();
			
			List<NonAgriPlotMapping> naPlotList = nonAgriPlotMappingRepository
					.findAllBySeasonIdStartYearEndYearAndVillageLgdCode(dao.getSeasonId(), dao.getStartingYear(),
							dao.getEndingYear(), dao.getVillageLgdCode());
			
			if(naPlotList!=null && naPlotList.size()>0) {
				List<String> plotsToExclude=naPlotList.stream().map(x -> x.getLandParcelId().getFarmlandId()).collect(Collectors.toList());

//				verifierLandAssignMent = verifierLandAssignmentRepository
//						.getCountDetailsByVillageCodeWithfilterNAPlots(dao.getSeasonId(), dao.getVillageLgdCode(), dao.getStartingYear(), dao.getEndingYear(), plotsToExclude);

				if ((Long.valueOf(RoleEnum.INSPECTION_OFFICER.getValue())).equals(dao.getUserType())) {
					verifierLandAssignMent = verifierLandAssignmentRepository
							.getCountDetailsByVillageCodeWithFilterNAPlotsWithoutSecondRejected(dao.getSeasonId(), dao.getVillageLgdCode(), dao.getStartingYear(), dao.getEndingYear(), plotsToExclude);
				} else {
					verifierLandAssignMent = verifierLandAssignmentRepository
							.getCountDetailsByVillageCodeWithFilterNAPlotsWithSecondRejected(dao.getSeasonId(), dao.getVillageLgdCode(), dao.getStartingYear(), dao.getEndingYear(), plotsToExclude);
				}
			}else {
//				verifierLandAssignMent = verifierLandAssignmentRepository
//						.getCountDetailsByVillageCodeWithfilter(dao.getSeasonId(), dao.getVillageLgdCode(), dao.getStartingYear(), dao.getEndingYear());
				if ((Long.valueOf(RoleEnum.INSPECTION_OFFICER.getValue())).equals(dao.getUserType())) {
					verifierLandAssignMent = verifierLandAssignmentRepository
							.getCountDetailsByVillageCodeWithFilterWithoutSecondRejected(dao.getSeasonId(), dao.getVillageLgdCode(), dao.getStartingYear(), dao.getEndingYear());
				}else{
					verifierLandAssignMent = verifierLandAssignmentRepository
							.getCountDetailsByVillageCodeWithFilterWithSecondRejected(dao.getSeasonId(), dao.getVillageLgdCode(), dao.getStartingYear(), dao.getEndingYear());
				}

			}
			
			List<UserDAO> userFinalList=new ArrayList<>();
			if(verifierLandAssignMent!=null && verifierLandAssignMent.size()>0) {
				List<Long> userIds=verifierLandAssignMent.stream().map(x->x.getUserId()).collect(Collectors.toList());
				List<UserMaster> userList=userMasterRepository.findByUserIdInAndIsDeletedAndIsActive(userIds, false, true);
				
				verifierLandAssignMent.forEach(action->{
					UserDAO userDAO=new UserDAO();
					userDAO.setUserId(action.getUserId());
					userDAO.setFarmAssignCount(action.getTotalCount());
					List<UserMaster> tmpUserList=userList.stream().filter(x->x.getUserId().equals(action.getUserId())).collect(Collectors.toList());
					if(tmpUserList!=null && tmpUserList.size()>0) {
						userDAO.setUserName(tmpUserList.get(0).getUserName());
						userDAO.setUserFullName(tmpUserList.get(0).getUserFullName());
						userDAO.setUserFirstName(tmpUserList.get(0).getUserFirstName());
						userDAO.setUserLastName(tmpUserList.get(0).getUserLastName());
					}
					userFinalList.add(userDAO);
				});
			}
			

		
			return CustomMessages.makeResponseModel(userFinalList, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

}
