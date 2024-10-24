package com.amnex.agristack.service;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.*;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.*;
import com.amnex.agristack.repository.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.amnex.agristack.utils.CustomMessages;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

@Service
public class FarmlandPlotService {

	@Autowired
	private FarmlandPlotRegistryRepository farmlandPlotRegistryRepository;
	@Autowired
	private GeneralService generalService;

	@Autowired
	private VillageLgdMasterRepository villageLgdMasterRepository;

	@Autowired
	private UserLandAssignmentRepository userLandAssignmentRepository;

	@Autowired
	private SeasonMasterRepository seasonMasterRepository;

	@Autowired
	private UserVillageMappingRepository userVillageMappingRepository;

	@Autowired
	private NonAgriPlotMappingRepository nonAgriPlotMappingRepository;

	@Autowired
	private EarlyLateVillageService earlyLateVillageService;

	@Autowired
	LandParcelSurveyMasterRespository landParcelSurveyMasterRespository;

	@Autowired
	private UserService userService;

	/**
	 * Get farm land plot
	 * 
	 * @param request
	 * @param ids
	 * @return
	 */
	public ResponseModel getFarmlandPlot(HttpServletRequest request, List<Integer> ids) {
		ResponseModel responseModel = null;
		try {
			List<FarmlandPlotRegistry> details = farmlandPlotRegistryRepository.findAll();

			if (details != null && details.size() > 0) {
				details.forEach(action -> {
					SowingSeason sowingSeason = generalService.getCurrentSeason();
					if (sowingSeason != null) {
						action.setSeason(sowingSeason);
					}
				});
			}

			List<FarmLandCount> farmLandcountDetails = farmlandPlotRegistryRepository.getCountDetails();
			List<FarmlandPlotRegistryDAO> finalResult = new ArrayList<>();
			if (farmLandcountDetails != null) {

				farmLandcountDetails.forEach(action -> {
					FarmlandPlotRegistryDAO farmlandPlotRegistryDAO = new FarmlandPlotRegistryDAO();
					SowingSeason sowingSeason = generalService.getCurrentSeason();
					farmlandPlotRegistryDAO.setVillageLgdCode(action.getVillageLgdCode());
					farmlandPlotRegistryDAO.setTotalCount(action.getTotalCount());
					farmlandPlotRegistryDAO.setTotalAssignedPlotsCount(0l);
					farmlandPlotRegistryDAO.setTotalUnAssignedPlotsCount(0l);
					farmlandPlotRegistryDAO.setTotalPlotsSurveyDataCollectedCount(0l);
					farmlandPlotRegistryDAO.setTaskAllocationComplete(0l);
					farmlandPlotRegistryDAO.setSurveyComplete(0l);
					farmlandPlotRegistryDAO.setNoOfSurveyorsAssociated(0l);

					if (sowingSeason != null) {
						farmlandPlotRegistryDAO.setSeason(sowingSeason);
					}
					finalResult.add(farmlandPlotRegistryDAO);

				});
				List<Long> villageLgdCodes = farmLandcountDetails.stream().map(mapper -> mapper.getVillageLgdCode())
						.collect(Collectors.toList());

				if (villageLgdCodes != null && villageLgdCodes.size() > 0) {
					List<VillageLgdMaster> villageList = villageLgdMasterRepository
							.findByVillageLgdCodeIn(villageLgdCodes);

					if (villageList != null) {

						finalResult.forEach(action -> {

							villageList.forEach(action2 -> {
								if (action.getVillageLgdCode().equals(action2.getVillageLgdCode())) {
									action.setVillageName(action2.getVillageName());
									action.setSubDistrictName(action2.getSubDistrictLgdCode().getSubDistrictName());

								}

							});

						});
					}
				}

				List<FarmLandCount> userLandDetails = userLandAssignmentRepository.getCountDetails();
				finalResult.forEach(action -> {

					if (userLandDetails != null && userLandDetails.size() > 0) {
						userLandDetails.forEach(action3 -> {
							if (action.getVillageLgdCode().equals(action3.getVillageLgdCode())) {

								action.setTotalAssignedPlotsCount(action3.getTotalCount());
								Long unAssignCount = action.getTotalCount() - action3.getTotalCount();
								action.setTotalUnAssignedPlotsCount(unAssignCount);
								Long taskAllocationComplete = (action3.getTotalCount() * 100) / action.getTotalCount();
								action.setTaskAllocationComplete(taskAllocationComplete);
							}

						});
					}

				});

				finalResult.forEach(action -> {
					if (action.getVillageLgdCode() != null) {
						List<UserLandCount> count = userLandAssignmentRepository
								.getCountDetailsByVillageCode(Long.valueOf(action.getVillageLgdCode()));
						if (count != null && count.size() > 0) {
							Set<Long> userIds = count.stream().map(data -> data.getUserId())
									.collect(Collectors.toSet());
							if (userIds != null && userIds.size() > 0) {
								action.setNoOfSurveyorsAssociated(Long.valueOf(userIds.size()));
							}
						}
					}
				});

			}
			return CustomMessages.makeResponseModel(finalResult, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Get farm land plot by village code
	 * 
	 * @param request
	 * @param villageCode
	 * @return
	 */
	public ResponseModel getFarmlandPlotByVillageCode(HttpServletRequest request, Integer villageCode) {
		ResponseModel responseModel = null;
		try {

			List<UserLandAssignment> userLandAssignMent = userLandAssignmentRepository
					.findByVillageLgdCode_villageLgdCode(Long.valueOf(villageCode));
			List<FarmlandPlotRegistry> details = farmlandPlotRegistryRepository
					.findByVillageLgdMasterVillageLgdCode(Long.valueOf(villageCode));
			if (userLandAssignMent != null && userLandAssignMent.size() > 0) {
				Set<String> farmLandids = userLandAssignMent.stream().map(mapper -> mapper.getFarmlandId())
						.collect(Collectors.toSet());
				details = farmlandPlotRegistryRepository
						.findByVillageLgdMasterVillageLgdCodeAndFarmlandIdNotIn(Long.valueOf(villageCode), farmLandids);
			}

			details.forEach(action -> {
				SowingSeason sowingSeason = generalService.getCurrentSeason();

				if (sowingSeason != null) {
					action.setSeason(sowingSeason);
				}

			});

			return CustomMessages.makeResponseModel(details, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Get farm land plots by village code
	 * 
	 * @param request
	 * @param villageCode
	 * @return
	 */
	public ResponseModel getFarmlandPlotsByVillageCode(HttpServletRequest request, Integer villageCode) {
		ResponseModel responseModel = null;
		try {
			// List<FarmlandPlotRegistry> details = farmlandPlotRegistryRepository
			// 		.findByVillageLgdMasterVillageLgdCodeAndPlotGeometryIsNotNull(Long.valueOf(villageCode));
			// 		System.out.println("details "+details.size());
			// List<FarmlandPlotRegistryDAO> finalList = new ArrayList<>();
			// List<FarmGeometry> geometryList = farmlandPlotRegistryRepository
			// 		.getGeometryDetails(Long.valueOf(villageCode));
			// 		//System.out.println("geometryList "+geometryList);
			// details.forEach(action -> {
			// 	FarmlandPlotRegistryDAO farmlandPlotRegistryDAO = new FarmlandPlotRegistryDAO();

			// 	farmlandPlotRegistryDAO.setFarmlandPlotRegistryId(action.getFarmlandPlotRegistryId());
			// 	farmlandPlotRegistryDAO.setLandParcelId(action.getLandParcelId());
			// 	farmlandPlotRegistryDAO.setSurveyNumber(action.getSurveyNumber());
			// 	farmlandPlotRegistryDAO.setSubSurveyNumber(action.getSubSurveyNumber());
			// 	if (action.getVillageLgdMaster() != null) {
			// 		farmlandPlotRegistryDAO.setVillageName(action.getVillageLgdMaster().getVillageName());

			// 		farmlandPlotRegistryDAO.setSubDistrictName(
			// 				action.getVillageLgdMaster().getSubDistrictLgdCode().getSubDistrictName());
			// 		farmlandPlotRegistryDAO.setDistrictName(action.getVillageLgdMaster().getSubDistrictLgdCode()
			// 				.getDistrictLgdCode().getDistrictName());

			// 		farmlandPlotRegistryDAO.setStateName(action.getVillageLgdMaster().getSubDistrictLgdCode()
			// 				.getDistrictLgdCode().getStateLgdCode().getStateName());
			// 		// farmlandPlotRegistryDAO.setVillageName(.getVillageLgdMaster().getVillageName())
			// 	}

			// 	geometryList.forEach(action2 -> {
			// 		if (action.getFarmlandPlotRegistryId().equals(action2.getId())) {
			// 			farmlandPlotRegistryDAO.setParcelGeoms(action2.getGeometry());
			// 		}
			// 	});
			// 	finalList.add(farmlandPlotRegistryDAO);
			// });
			List<FarmlandPlotRegistry> details = farmlandPlotRegistryRepository
        .findByVillageLgdMasterVillageLgdCodeAndPlotGeometryIsNotNull(Long.valueOf(villageCode));
System.out.println("details size: " + details.size());
			List<FarmlandPlotRegistryDAO> finalList = new ArrayList<>();
			Map<Long, FarmGeometry> geometryMap = new HashMap<>();
			Set<String> uniqueSurveyNumbers = new HashSet<>();

// Fetch geometry details once and store them in a map for efficient lookup
			farmlandPlotRegistryRepository.getGeometryDetails(Long.valueOf(villageCode))
					.forEach(geometry -> geometryMap.put(geometry.getId(), geometry));

			details.forEach(action -> {
				FarmlandPlotRegistryDAO farmlandPlotRegistryDAO = new FarmlandPlotRegistryDAO();

				farmlandPlotRegistryDAO.setFarmlandPlotRegistryId(action.getFarmlandPlotRegistryId());
				farmlandPlotRegistryDAO.setLandParcelId(action.getLandParcelId());
				farmlandPlotRegistryDAO.setSurveyNumber(action.getSurveyNumber());
				farmlandPlotRegistryDAO.setSubSurveyNumber(action.getSubSurveyNumber());

				if (action.getVillageLgdMaster() != null) {
					farmlandPlotRegistryDAO.setVillageName(action.getVillageLgdMaster().getVillageName());
					farmlandPlotRegistryDAO.setSubDistrictName(action.getVillageLgdMaster().getSubDistrictLgdCode().getSubDistrictName());
					farmlandPlotRegistryDAO.setDistrictName(action.getVillageLgdMaster().getSubDistrictLgdCode().getDistrictLgdCode().getDistrictName());
					farmlandPlotRegistryDAO.setStateName(action.getVillageLgdMaster().getSubDistrictLgdCode().getDistrictLgdCode().getStateLgdCode().getStateName());
				}

				// Set parcel geometries using the map for efficient lookup
				FarmGeometry geometry = geometryMap.get(action.getFarmlandPlotRegistryId());
				if (geometry != null) {
					farmlandPlotRegistryDAO.setParcelGeoms(geometry.getGeometry());
				}

				// Check if this survey number is unique, if not, skip adding it to the finalList
				if (uniqueSurveyNumbers.add(farmlandPlotRegistryDAO.getSurveyNumber())) {
					finalList.add(farmlandPlotRegistryDAO);
				}
			});


//List<FarmlandPlotRegistryDAO> finalList = new ArrayList<>();
//Map<Long, FarmGeometry> geometryMap = new HashMap<>();
//
//// Fetch geometry details once and store them in a map for efficient lookup
//farmlandPlotRegistryRepository.getGeometryDetails(Long.valueOf(villageCode))
//        .forEach(geometry -> geometryMap.put(geometry.getId(), geometry));
//
//details.forEach(action -> {
//    FarmlandPlotRegistryDAO farmlandPlotRegistryDAO = new FarmlandPlotRegistryDAO();
//
//    farmlandPlotRegistryDAO.setFarmlandPlotRegistryId(action.getFarmlandPlotRegistryId());
//    farmlandPlotRegistryDAO.setLandParcelId(action.getLandParcelId());
//    farmlandPlotRegistryDAO.setSurveyNumber(action.getSurveyNumber());
//    farmlandPlotRegistryDAO.setSubSurveyNumber(action.getSubSurveyNumber());
//
//    if (action.getVillageLgdMaster() != null) {
//        farmlandPlotRegistryDAO.setVillageName(action.getVillageLgdMaster().getVillageName());
//        farmlandPlotRegistryDAO.setSubDistrictName(action.getVillageLgdMaster().getSubDistrictLgdCode().getSubDistrictName());
//        farmlandPlotRegistryDAO.setDistrictName(action.getVillageLgdMaster().getSubDistrictLgdCode().getDistrictLgdCode().getDistrictName());
//        farmlandPlotRegistryDAO.setStateName(action.getVillageLgdMaster().getSubDistrictLgdCode().getDistrictLgdCode().getStateLgdCode().getStateName());
//    }
//
//    // Set parcel geometries using the map for efficient lookup
//    FarmGeometry geometry = geometryMap.get(action.getFarmlandPlotRegistryId());
//    if (geometry != null) {
//        farmlandPlotRegistryDAO.setParcelGeoms(geometry.getGeometry());
//    }
//
//    finalList.add(farmlandPlotRegistryDAO);
//});



// Now finalList contains optimized mapping of details with geometries

		//	System.out.println("finalList "+finalList);
			return CustomMessages.makeResponseModel(finalList, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Get farm land plot by filter
	 * 
	 * @param request
	 * @param surveyTaskAllocationFilterDAO
	 * @return
	 */
	public ResponseModel getFarmlandPlotByFilter(HttpServletRequest request,
			SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		ResponseModel responseModel = null;
		try {

			List<FarmlandPlotRegistryDAO> finalResult = new ArrayList<>();
			SowingSeason sowingSeason = seasonMasterRepository
					.findBySeasonId(surveyTaskAllocationFilterDAO.getSeasonId());

			if (surveyTaskAllocationFilterDAO.getSubDistrictLgdCodeList() != null
					&& surveyTaskAllocationFilterDAO.getSubDistrictLgdCodeList().size() > 0) {
				// List<VillageLgdMaster> villageList = villageLgdMasterRepository
				// .findBySubDistrictLgdCode_SubDistrictLgdCodeIn(
				// surveyTaskAllocationFilterDAO.getSubDistrictLgdCodeList());

				String userIdString = userService.getUserId(request);
				Long userId = Long.parseLong(userIdString);
				List<UserVillageMapping> userVillageList = userVillageMappingRepository
						.findByUserMaster_UserIdAndVillageLgdMaster_SubDistrictLgdCode_SubDistrictLgdCodeIn(userId,
								surveyTaskAllocationFilterDAO.getSubDistrictLgdCodeList());
				// List<VillageLgdMaster> villageList =

				if (userVillageList != null && userVillageList.size() > 0) {
					List<VillageLgdMaster> villageList = userVillageList.stream().map(x -> x.getVillageLgdMaster())
							.collect(Collectors.toList());

					if (sowingSeason.getIsCentralProvided() != null
							&& sowingSeason.getIsCentralProvided().equals(Boolean.FALSE)) {
						EarlyLateVillageInputDAO earlyLateVillageInputDAO = new EarlyLateVillageInputDAO();
						earlyLateVillageInputDAO.setSeasonId(surveyTaskAllocationFilterDAO.getSeasonId());
						earlyLateVillageInputDAO.setStartYear(surveyTaskAllocationFilterDAO.getStartYear());
						earlyLateVillageInputDAO.setEndYear(surveyTaskAllocationFilterDAO.getEndYear());
						earlyLateVillageInputDAO.setSeasonId(surveyTaskAllocationFilterDAO.getSeasonId());
						List<Integer> subDistrictLgdCode = surveyTaskAllocationFilterDAO.getSubDistrictLgdCodeList()
								.stream().map(Long::intValue).collect(Collectors.toList());
						if (subDistrictLgdCode != null && subDistrictLgdCode.size() > 0) {
							earlyLateVillageInputDAO.setSubDistrictLgdCodeList(subDistrictLgdCode);
						}

						// earlyLateVillageInputDAO.setVillageLGDCodes(villageLGDCodes);

						List<Long> earlyVillageCodesList = earlyLateVillageService
								.getVillageLGDCodesByEarlyAndLateVillageMapping(earlyLateVillageInputDAO);

						if (villageList != null && villageList.size() > 0 && earlyVillageCodesList != null
								&& earlyVillageCodesList.size() > 0) {
							List<Long> vCodes = new ArrayList<>();
							List<Long> list = villageList.stream().map(x -> x.getVillageLgdCode())
									.collect(Collectors.toList());
							// earlyVillageCodesList.stream().filter(x->
							// earlyVillageCodesListearlyVillageCodesList.)
							list.forEach(action1 -> {

								earlyVillageCodesList.forEach(action2 -> {

									if (action1.equals(action2)) {
										vCodes.add(action2);
									}
								});

							});

							if (vCodes != null && vCodes.size() > 0) {
								villageList = villageLgdMasterRepository.findByVillageLgdCodeIn(vCodes);
							}
						}

					}
					if (villageList != null && villageList.size() > 0) {
						List<Long> villagelgdCodes = villageList.stream().map(mapper -> mapper.getVillageLgdCode())
								.collect(Collectors.toList());

						List<NonAgriPlotMapping> naList = nonAgriPlotMappingRepository
								.findAllBySeasonIdStartYearEndYearAndVillageLGDCode(
										surveyTaskAllocationFilterDAO.getSeasonId(),
										surveyTaskAllocationFilterDAO.getStartYear(),
										surveyTaskAllocationFilterDAO.getEndYear(), villagelgdCodes);
						List<String> landIds = new ArrayList<>();
						List<FarmlandPlotRegistry> details = new ArrayList<>();
						if (naList != null && naList.size() > 0) {
							landIds = naList.stream().map(x -> x.getLandParcelId().getFarmlandId())
									.collect(Collectors.toList());
							details = farmlandPlotRegistryRepository
									.findByVillageLgdMaster_VillageLgdCodeInAndFarmlandIdNotIn(villagelgdCodes,
											landIds);
						} else {
							details = farmlandPlotRegistryRepository
									.findByVillageLgdMaster_VillageLgdCodeIn(villagelgdCodes);
						}

						// .findByVillageLgdMaster_VillageLgdCodeIn(villagelgdCodes);
						if (details != null && details.size() > 0) {

							details.forEach(action -> {
								// surveyTaskAllocationFilterDAO.getSeasonId()

								// SowingSeason sowingSeason = generalService.getCurrentSeason();

								if (sowingSeason != null) {
									action.setSeason(sowingSeason);
								}

							});
						}

						List<FarmLandCount> farmLandcountDetails = farmlandPlotRegistryRepository
								.getCountDetailsByVillageCode(villagelgdCodes);
						if (landIds != null && landIds.size() > 0) {
							farmLandcountDetails = farmlandPlotRegistryRepository
									.getCountDetailsByVillageCode(villagelgdCodes, landIds);
						}
						if (farmLandcountDetails != null) {
							farmLandcountDetails.forEach(action -> {
								FarmlandPlotRegistryDAO farmlandPlotRegistryDAO = new FarmlandPlotRegistryDAO();
								// SowingSeason sowingSeason = generalService.getCurrentSeason();

								farmlandPlotRegistryDAO.setVillageLgdCode(action.getVillageLgdCode());
								farmlandPlotRegistryDAO.setTotalCount(action.getTotalCount());
								farmlandPlotRegistryDAO.setTotalAssignedPlotsCount(0l);
								farmlandPlotRegistryDAO.setTotalUnAssignedPlotsCount(action.getTotalCount());
								farmlandPlotRegistryDAO.setTotalPlotsSurveyDataCollectedCount(0l);
								farmlandPlotRegistryDAO.setTaskAllocationComplete(0l);
								farmlandPlotRegistryDAO.setSurveyComplete(0l);
								farmlandPlotRegistryDAO.setNoOfSurveyorsAssociated(0l);

								if (sowingSeason != null) {
									farmlandPlotRegistryDAO.setSeason(sowingSeason);
								}
								finalResult.add(farmlandPlotRegistryDAO);

							});
							villageList.forEach(action2 -> {

								finalResult.forEach(action -> {
									if (action.getVillageLgdCode().equals(action2.getVillageLgdCode())) {
										action.setVillageName(action2.getVillageName());
										action.setSubDistrictName(action2.getSubDistrictLgdCode().getSubDistrictName());

									}

								});

							});
							List<Long> villagelgdCodeLong = villageList.stream()
									.map(mapper -> mapper.getVillageLgdCode()).collect(Collectors.toList());
							List<FarmLandCount> userLandDetails = userLandAssignmentRepository.getCountDetailsByFilter(
									surveyTaskAllocationFilterDAO.getSeasonId(), villagelgdCodeLong,
									surveyTaskAllocationFilterDAO.getYear());
							finalResult.forEach(action -> {

								if (userLandDetails != null && userLandDetails.size() > 0) {
									userLandDetails.forEach(action3 -> {
										if (action.getVillageLgdCode().equals(action3.getVillageLgdCode())) {

											action.setTotalAssignedPlotsCount(action3.getTotalCount());
											Long unAssignCount = action.getTotalCount() - action3.getTotalCount();
											action.setTotalUnAssignedPlotsCount(unAssignCount);
											Long taskAllocationComplete = (action3.getTotalCount() * 100)
													/ action.getTotalCount();
											action.setTaskAllocationComplete(taskAllocationComplete);
											action.setTaskAllocationCompleted(
													((double) action3.getTotalCount() * 100) / action.getTotalCount());
										}

									});
								}

							});

							if (finalResult != null && finalResult.size() > 0) {

								List<Long> vcodes = finalResult.stream().map(x -> x.getVillageLgdCode())
										.collect(Collectors.toList());
								List<UserCountDAO> completedCounts = userLandAssignmentRepository
										.getAssignVillageCodesByFilter(surveyTaskAllocationFilterDAO.getStartYear(),
												surveyTaskAllocationFilterDAO.getEndYear(),
												surveyTaskAllocationFilterDAO.getSeasonId(), vcodes);
								finalResult.forEach(action -> {
									completedCounts.forEach(action2 -> {

										if (action.getVillageLgdCode().equals(action2.getVillageCode())) {
											action.setTotalPlotsSurveyDataCollectedCount(action2.getTotalCount());

											if (action2.getTotalCount() != null) {
												action.setSurveyComplete((action2.getTotalCount() * 100)
														/ action.getTotalAssignedPlotsCount());
												Double count = (((double) action2.getTotalCount() * 100)
														/ action.getTotalAssignedPlotsCount());
												action.setSurveyCompleted(count);
											}
											// action.getTotalAssignedPlotsCount()
											//
										}
									});
								});

							}

							/**
							 * COMMENTED TO REMOVE NO. OF SURVEYOR COUNT
							 * 
							 * 22 08 2023 START
							 */
//							finalResult.forEach(action -> {
//								if (action.getVillageLgdCode() != null) {
//									List<UserLandCount> count = userLandAssignmentRepository
//											.getCountDetailsByVillageCode(Long.valueOf(action.getVillageLgdCode()),surveyTaskAllocationFilterDAO.getSeasonId(), surveyTaskAllocationFilterDAO.getYear());
//									if (count != null && count.size() > 0) {
//										Set<Long> userIds = count.stream().map(data -> data.getUserId())
//												.collect(Collectors.toSet());
//										if (userIds != null && userIds.size() > 0) {
//											action.setNoOfSurveyorsAssociated(Long.valueOf(userIds.size()));
//										}
//									}
//								}
//							});

							/**
							 * COMMENTED TO REMOVE NO. OF SURVEYOR COUNT
							 * 
							 * 22 08 2023 END
							 */

						}
					}
				}
			}

			// List<FarmlandPlotRegistry> details =
			// farmlandPlotRegistryRepository.findAll();
			//
			// if (details != null && details.size() > 0) {
			// details.forEach(action -> {
			// SowingSeason sowingSeason = generalService.getCurrentSeason();
			// if (sowingSeason != null) {
			// action.setSeason(sowingSeason);
			// }
			// });
			// }

			// List<FarmLandCount> farmLandcountDetails =
			// farmlandPlotRegistryRepository.getCountDetailsByVillageCode();

			// if (farmLandcountDetails != null) {
			//
			// farmLandcountDetails.forEach(action -> {
			// FarmlandPlotRegistryDAO farmlandPlotRegistryDAO = new
			// FarmlandPlotRegistryDAO();
			// SowingSeason sowingSeason = generalService.getCurrentSeason();
			// farmlandPlotRegistryDAO.setVillageLgdCode(action.getVillageLgdCode());
			// farmlandPlotRegistryDAO.setTotalCount(action.getTotalCount());
			// farmlandPlotRegistryDAO.setTotalAssignedPlotsCount(0l);
			// farmlandPlotRegistryDAO.setTotalUnAssignedPlotsCount(0l);
			// farmlandPlotRegistryDAO.setTotalPlotsSurveyDataCollectedCount(0l);
			// farmlandPlotRegistryDAO.setTaskAllocationComplete(0l);
			// farmlandPlotRegistryDAO.setSurveyComplete(0l);
			// farmlandPlotRegistryDAO.setNoOfSurveyorsAssociated(0l);
			//
			// if (sowingSeason != null) {
			// farmlandPlotRegistryDAO.setSeason(sowingSeason);
			// }
			// finalResult.add(farmlandPlotRegistryDAO);
			//
			// });
			// List<Long> villageLgdCodes = farmLandcountDetails.stream().map(mapper ->
			// mapper.getVillageLgdCode())
			// .collect(Collectors.toList());
			//
			// if (villageLgdCodes != null && villageLgdCodes.size() > 0) {
			// List<VillageLgdMaster> villageList = villageLgdMasterRepository
			// .findByVillageLgdCodeIn(villageLgdCodes);
			//
			// if (villageList != null) {
			//
			// finalResult.forEach(action -> {
			//
			// villageList.forEach(action2 -> {
			// if (action.getVillageLgdCode().equals(action2.getVillageLgdCode())) {
			// action.setVillageName(action2.getVillageName());
			// action.setSubDistrictName(action2.getSubDistrictLgdCode().getSubDistrictName());
			//
			// }
			//
			// });
			//
			// });
			// }
			// }
			//
			// List<FarmLandCount> userLandDetails =
			// userLandAssignmentRepository.getCountDetails();
			// finalResult.forEach(action -> {
			//
			// if (userLandDetails != null && userLandDetails.size() > 0) {
			// userLandDetails.forEach(action3 -> {
			// if (action.getVillageLgdCode().equals(action3.getVillageLgdCode())) {
			//
			// action.setTotalAssignedPlotsCount(action3.getTotalCount());
			// Long unAssignCount = action.getTotalCount() - action3.getTotalCount();
			// action.setTotalUnAssignedPlotsCount(unAssignCount);
			// Long taskAllocationComplete = (action3.getTotalCount() * 100) /
			// action.getTotalCount();
			// action.setTaskAllocationComplete(taskAllocationComplete);
			// }
			//
			// });
			// }
			//
			// });
			//
			// finalResult.forEach(action -> {
			// if (action.getVillageLgdCode() != null) {
			// List<UserLandCount> count = userLandAssignmentRepository
			// .getCountDetailsByVillageCode(Long.valueOf(action.getVillageLgdCode()));
			// if (count != null && count.size() > 0) {
			// Set<Long> userIds = count.stream().map(data -> data.getUserId())
			// .collect(Collectors.toSet());
			// if (userIds != null && userIds.size() > 0) {
			// action.setNoOfSurveyorsAssociated(Long.valueOf(userIds.size()));
			// }
			// }
			// }
			// });
			//
			// }
			return CustomMessages.makeResponseModel(finalResult, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Get unassign plot by filter
	 * 
	 * @param request
	 * @param surveyTaskAllocationFilterDAO
	 * @return
	 */
	public ResponseModel getUnassignPlotByfilter(HttpServletRequest request,
			SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		ResponseModel responseModel = null;
		try {
			List<Long> vCodes = new ArrayList<>();
			vCodes.add(surveyTaskAllocationFilterDAO.getVillageLgdCode());

			List<UserLandAssignment> userLandAssignMent = userLandAssignmentRepository
					.findByYearAndSeason_SeasonIdAndVillageLgdCode_villageLgdCodeAndIsActiveAndIsDeletedOrderByUser_UserFullNameAsc(
							surveyTaskAllocationFilterDAO.getYear(), surveyTaskAllocationFilterDAO.getSeasonId(),
							surveyTaskAllocationFilterDAO.getVillageLgdCode(), true, false);
			List<NonAgriPlotMapping> naList = nonAgriPlotMappingRepository
					.findAllBySeasonIdStartYearEndYearAndVillageLGDCode(surveyTaskAllocationFilterDAO.getSeasonId(),
							surveyTaskAllocationFilterDAO.getStartYear(), surveyTaskAllocationFilterDAO.getEndYear(),
							vCodes);
			List<FarmlandPlotRegistry> details = farmlandPlotRegistryRepository
					.findByVillageLgdMasterVillageLgdCodeOrderBySurveyNumberAsc(
							surveyTaskAllocationFilterDAO.getVillageLgdCode());
			if (naList != null && naList.size() > 0) {
				Set<String> landIds = naList.stream().map(x -> x.getLandParcelId().getFarmlandId())
						.collect(Collectors.toSet());
				details = farmlandPlotRegistryRepository
						.findByVillageLgdMasterVillageLgdCodeAndFarmlandIdNotInOrderBySurveyNumberAsc(
								surveyTaskAllocationFilterDAO.getVillageLgdCode(), landIds);
			}
			if (userLandAssignMent != null && userLandAssignMent.size() > 0) {
				Set<String> farmLandids = userLandAssignMent.stream().map(mapper -> mapper.getFarmlandId())
						.collect(Collectors.toSet());

				if (naList != null && naList.size() > 0) {
					Set<String> landIds = naList.stream().map(x -> x.getLandParcelId().getFarmlandId())
							.collect(Collectors.toSet());
					farmLandids.addAll(landIds);
				}
				details = farmlandPlotRegistryRepository
						.findByVillageLgdMasterVillageLgdCodeAndFarmlandIdNotInOrderBySurveyNumberAsc(
								surveyTaskAllocationFilterDAO.getVillageLgdCode(), farmLandids);
			}

			List<FarmlandPlotRegistryDAO> finalList = new ArrayList<FarmlandPlotRegistryDAO>();
			SowingSeason sowingSeason = generalService.getCurrentSeason();
			//

			details.forEach(action -> {

				FarmlandPlotRegistryDAO obj = new FarmlandPlotRegistryDAO();
				obj.setVillageLgdCode(action.getVillageLgdMaster().getVillageLgdCode());
				obj.setFarmlandId(action.getFarmlandId());
				obj.setLandParcelId(action.getLandParcelId());
				obj.setFarmlandPlotRegistryId(action.getFarmlandPlotRegistryId());

				if (sowingSeason != null) {
					obj.setSeason(sowingSeason);
				}
				obj.setSurveyNumber(action.getSurveyNumber());
				obj.setSubSurveyNumber(action.getSubSurveyNumber());
				finalList.add(obj);

			});

			return CustomMessages.makeResponseModel(finalList, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Get farm land plot for supervisor
	 * 
	 * @param request
	 * @param paginationDao
	 * @return
	 */
	public ResponseModel getFarmlandPlotForSupervisor(HttpServletRequest request, PaginationDao paginationDao) {
		ResponseModel responseModel = null;
		try {
			Pageable pageable = PageRequest.of(paginationDao.getPage(), paginationDao.getLimit(),
					Sort.by(paginationDao.getSortField()).descending());
			Map<String, Object> responseData = new HashMap();
			if (paginationDao.getSortOrder().equals("asc")) {
				pageable = PageRequest.of(paginationDao.getPage(), paginationDao.getLimit(),
						Sort.by(paginationDao.getSortField()).ascending());
			}
			List<FarmlandPlotRegistryDAO> finalResult = new ArrayList<>();
			if (paginationDao.getSubDistrictLgdCodeList() != null
					&& paginationDao.getSubDistrictLgdCodeList().size() > 0) {
				List<VillageLgdMaster> villageData = new ArrayList<>();
				if (paginationDao.getSearch() != null) {
					villageData = villageLgdMasterRepository
							.findBySubDistrictLgdCode_SubDistrictLgdCodeInAndVillageNameContaining(
									paginationDao.getSubDistrictLgdCodeList(), paginationDao.getSearch());
				} else {
					villageData = villageLgdMasterRepository
							.findBySubDistrictLgdCode_SubDistrictLgdCodeIn(paginationDao.getSubDistrictLgdCodeList());
				}
				List<VillageLgdMaster> villageList = villageData;
				// VillageNameContaining

				if (villageList != null && villageList.size() > 0) {
					List<Long> villagelgdCodes = villageList.stream().map(mapper -> mapper.getVillageLgdCode())
							.collect(Collectors.toList());

					// Page<FarmLandCount> farmLandcountDetails = farmlandPlotRegistryRepository
					// .getCountDetailsByVillageCode(villagelgdCodes,pageable);

					Page<FarmVillageCount> farmLandcountDetails = farmlandPlotRegistryRepository
							.getCountDetailsByVillageCodeWithName(villagelgdCodes, pageable);

					// villagelgdCodes
					List<UserVillageMapping> userDetails = userVillageMappingRepository
							.findByUserMaster_IsActiveAndUserMaster_IsDeletedAndUserMaster_RoleId_RoleNameIgnoreCaseAndVillageLgdMaster_VillageLgdCodeInAndIsActiveAndIsDeleted(
									true, false, "Supervisor", villagelgdCodes, true, false);
					if (farmLandcountDetails != null) {
						SowingSeason sowingSeason = seasonMasterRepository.findBySeasonId(paginationDao.getSeasonId());
						farmLandcountDetails.forEach(action -> {
							FarmlandPlotRegistryDAO farmlandPlotRegistryDAO = new FarmlandPlotRegistryDAO();
							// SowingSeason sowingSeason = generalService.getCurrentSeason();

							farmlandPlotRegistryDAO.setVillageLgdCode(action.getVillageLgdCode());
							farmlandPlotRegistryDAO.setTotalCount(action.getTotalCount());
							farmlandPlotRegistryDAO.setTotalAssignedPlotsCount(0l);
							farmlandPlotRegistryDAO.setTotalUnAssignedPlotsCount(action.getTotalCount());
							farmlandPlotRegistryDAO.setTotalPlotsSurveyDataCollectedCount(0l);
							farmlandPlotRegistryDAO.setTaskAllocationComplete(0l);
							farmlandPlotRegistryDAO.setSurveyComplete(0l);
							farmlandPlotRegistryDAO.setNoOfSurveyorsAssociated(0l);

							farmlandPlotRegistryDAO.setTotalReviewCompletedCount(0l);
							farmlandPlotRegistryDAO.setTotalRejectedCount(0l);

							userDetails.forEach(action2 -> {
								if (action2.getVillageLgdMaster().getVillageLgdCode()
										.equals(action.getVillageLgdCode())) {
									farmlandPlotRegistryDAO
											.setAssignedSupervisor(action2.getUserMaster().getUserName());
									farmlandPlotRegistryDAO.setUserFullName(action2.getUserMaster().getUserFullName());
								}
							});
							if (sowingSeason != null) {
								farmlandPlotRegistryDAO.setSeason(sowingSeason);
							}
							villageList.forEach(action2 -> {
								if (action.getVillageLgdCode().equals(action2.getVillageLgdCode())) {
									farmlandPlotRegistryDAO.setVillageName(action2.getVillageName());
									farmlandPlotRegistryDAO
											.setSubDistrictName(action2.getSubDistrictLgdCode().getSubDistrictName());

								}

							});
							finalResult.add(farmlandPlotRegistryDAO);

						});
						;
						responseData.put("total", farmLandcountDetails.getTotalElements());

					}
				}

			}

			responseData.put("data", finalResult);

			responseData.put("page", paginationDao.getPage());
			responseData.put("limit", paginationDao.getLimit());
			responseData.put("sortField", paginationDao.getSortField());
			responseData.put("sortOrder", paginationDao.getSortOrder());
			return CustomMessages.makeResponseModel(responseData, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Get farmland plot for supervisor by filter
	 * 
	 * @param request
	 * @param paginationDao
	 * @return
	 */
	public ResponseModel getFarmlandPlotForSupervisorByFilter(HttpServletRequest request, PaginationDao paginationDao) {
		ResponseModel responseModel = null;
		try {

			Pageable pageable = PageRequest.of(paginationDao.getPage(), paginationDao.getLimit(),
					Sort.by(paginationDao.getSortField()).descending());

			if (paginationDao.getSortOrder().equals("asc")) {
				pageable = PageRequest.of(paginationDao.getPage(), paginationDao.getLimit(),
						Sort.by(paginationDao.getSortField()).ascending());
			}
			Page<FarmlandPlotRegistry> details = farmlandPlotRegistryRepository
					.findByVillageLgdMasterVillageLgdCode(paginationDao.getVillageLgdCode(), pageable);
			List<FarmlandPlotRegistryDAO> finalResultList = new ArrayList<>();
			SowingSeason sowingSeason = seasonMasterRepository.findBySeasonId(paginationDao.getSeasonId());
			details.getContent().forEach(action -> {

				FarmlandPlotRegistryDAO farmlandPlotRegistryDAO = new FarmlandPlotRegistryDAO();

				farmlandPlotRegistryDAO.setVillageLgdCode(action.getVillageLgdMaster().getVillageLgdCode());
				farmlandPlotRegistryDAO.setTotalAssignedPlotsCount(0l);
				farmlandPlotRegistryDAO.setTotalPlotsSurveyDataCollectedCount(0l);
				farmlandPlotRegistryDAO.setTaskAllocationComplete(0l);
				farmlandPlotRegistryDAO.setSurveyComplete(0l);
				farmlandPlotRegistryDAO.setNoOfSurveyorsAssociated(0l);
				farmlandPlotRegistryDAO.setLandParcelId(action.getLandParcelId());
				farmlandPlotRegistryDAO.setFarmlandId(action.getFarmlandId());
				farmlandPlotRegistryDAO.setSurveyNumber(action.getSurveyNumber());
				farmlandPlotRegistryDAO.setSubSurveyNumber(action.getSubSurveyNumber());
				if (sowingSeason != null) {
					farmlandPlotRegistryDAO.setSeason(sowingSeason);
				}
				finalResultList.add(farmlandPlotRegistryDAO);

			});

			Map<String, Object> responseData = new HashMap();
			responseData.put("data", finalResultList);
			responseData.put("total", details.getTotalElements());
			responseData.put("page", paginationDao.getPage());
			responseData.put("limit", paginationDao.getLimit());
			responseData.put("sortField", paginationDao.getSortField());
			responseData.put("sortOrder", paginationDao.getSortOrder());

			return CustomMessages.makeResponseModel(responseData, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	/**
	 * Get farm land plot by filter
	 * 
	 * @param request
	 * @param surveyTaskAllocationFilterDAO
	 * @return
	 */
	public ResponseModel getFarmlandPlotByFilterNew(HttpServletRequest request,
			SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		ResponseModel responseModel = null;
		try {

			List<FarmlandPlotRegistryReturnDAO> finalResult = new ArrayList<>();
			SowingSeason sowingSeason = seasonMasterRepository
					.findBySeasonId(surveyTaskAllocationFilterDAO.getSeasonId());
		    System.out.println("sowingSeason 1"+sowingSeason);

			if (surveyTaskAllocationFilterDAO.getSubDistrictLgdCodeList() != null
					&& surveyTaskAllocationFilterDAO.getSubDistrictLgdCodeList().size() > 0) {

				String userIdString = userService.getUserId(request);
				Long userId = Long.parseLong(userIdString);

//				
//				if (sowingSeason.getIsCentralProvided() != null
//						&& sowingSeason.getIsCentralProvided().equals(Boolean.FALSE)) {
//					EarlyLateVillageInputDAO earlyLateVillageInputDAO = new EarlyLateVillageInputDAO();
//					earlyLateVillageInputDAO.setSeasonId(surveyTaskAllocationFilterDAO.getSeasonId());
//					earlyLateVillageInputDAO.setStartYear(surveyTaskAllocationFilterDAO.getStartYear());
//					earlyLateVillageInputDAO.setEndYear(surveyTaskAllocationFilterDAO.getEndYear());
//					earlyLateVillageInputDAO.setSeasonId(surveyTaskAllocationFilterDAO.getSeasonId());
//					List<Integer> subDistrictLgdCode = surveyTaskAllocationFilterDAO.getSubDistrictLgdCodeList()
//							.stream().map(Long::intValue).collect(Collectors.toList());
//					if (subDistrictLgdCode != null && subDistrictLgdCode.size() > 0) {
//						earlyLateVillageInputDAO.setSubDistrictLgdCodeList(subDistrictLgdCode);
//					}
//
//					List<Long> earlyVillageCodesList = earlyLateVillageService
//							.getVillageLGDCodesByEarlyAndLateVillageMapping(earlyLateVillageInputDAO);
//
//				}

				String subDistrictCodes = surveyTaskAllocationFilterDAO.getSubDistrictLgdCodeList().stream()
						.map(i -> i.toString()).collect(Collectors.joining(", "));

				String returnData = farmlandPlotRegistryRepository.getSurveyTaskCountFilter(userId.intValue(),
						surveyTaskAllocationFilterDAO.getStartYear(), surveyTaskAllocationFilterDAO.getEndYear(),
						surveyTaskAllocationFilterDAO.getSeasonId().intValue(), subDistrictCodes);
						System.out.println("subDistrictCodes "+subDistrictCodes);
						System.out.println("returnData "+returnData);
				
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

				TypeFactory typeFactory = mapper.getTypeFactory();
				 finalResult = mapper.readValue(returnData,
						typeFactory.constructCollectionType(List.class, FarmlandPlotRegistryReturnDAO.class));
				

//				 if(finalResult!=null && finalResult.size()>0) {
//					List<Long> codes=finalResult.stream().map(x->x.getVillageLgdCode()).collect(Collectors.toList());
//					List<CountReturnDAO> counData=farmlandPlotRegistryRepository.getCountVillageWise(codes, surveyTaskAllocationFilterDAO.getStartYear(), surveyTaskAllocationFilterDAO.getEndYear(),
//							surveyTaskAllocationFilterDAO.getSeasonId());
//					if(counData!=null && counData.size()>0) {
//						finalResult.parallelStream().forEach(action->{
//							counData.forEach(action2->{
//								if(action.getVillageLgdCode().equals(action2.getCode())) {
//									action.setTotalPlotsSurveyDataCollectedCount(action2.getSurvey());
//								}
//							});	
//						});
//					}
//				}
				finalResult.parallelStream().forEach((action)->{
					
					if(action.getTotalPlotsSurveyDataCollectedCount()==null) {
						action.setTotalPlotsSurveyDataCollectedCount(0l);
					}
					Long unAssignCount = action.getTotalCount()
							- action.getTotalAssignedPlotsCount();
					action.setTotalUnAssignedPlotsCount(unAssignCount);
					Long taskAllocationComplete = (action.getTotalAssignedPlotsCount() * 100)
							/ action.getTotalCount();
					action.setTaskAllocationComplete(taskAllocationComplete);
					action.setTaskAllocationCompleted(
							((double) action.getTotalAssignedPlotsCount() * 100)
									/ action.getTotalCount());

					action.setSeason(sowingSeason);
					Double count = (((double) action.getTotalPlotsSurveyDataCollectedCount() * 100)
							/ action.getTotalAssignedPlotsCount());
					
					if(count.isNaN()) {
						action.setSurveyCompleted(0d);
					}else {
						action.setSurveyCompleted(count);
					}
					
				});	

			}

			return CustomMessages.makeResponseModel(finalResult, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();

			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	public ResponseModel getUnassignPlotByfilterV2(HttpServletRequest request,
			SurveyTaskAllocationFilterDAO surveyTaskAllocationFilterDAO) {
		ResponseModel responseModel = null;
		try {
			String response=farmlandPlotRegistryRepository.getUnassignDataByvillagelgdcode(surveyTaskAllocationFilterDAO.getVillageLgdCode(),surveyTaskAllocationFilterDAO.getSeasonId(),surveyTaskAllocationFilterDAO.getStartYear(),surveyTaskAllocationFilterDAO.getEndYear());
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
	public ResponseModel getPlotDetailByVillageLgdCode(SurveyTaskAllocationFilterDAO inputDao, HttpServletRequest request) {
		ResponseModel responseModel = null;
		try {
			
			Integer statusCode = (inputDao.getStatusCode() != null) ? inputDao.getStatusCode() : 0;
			String response = farmlandPlotRegistryRepository.getPlotDetailByVillageLgdCode(inputDao.getVillageLgdCode(), inputDao.getStartYear(), inputDao.getEndYear(), inputDao.getSeasonId().intValue(),statusCode);
			
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<Object> resultObject = mapper.readValue(response,
					typeFactory.constructCollectionType(List.class, Object.class));
			return new ResponseModel(resultObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;		}
		
	}


	public ResponseModel getFarmerLandDetails(FarmerInputDAO farmerInput, HttpServletRequest request) {
		try {
			List<FarmerOutputDAO> farmerOutputDAOList = new ArrayList<>();
			farmerInput.getFarmerDetailInputDAO().stream().forEach(ele-> {
				String year = ele.getStartYear() + "-" + ele.getEndYear();
//				FarmerOutputDAO output = farmlandPlotRegistryRepository.getFarmLandIPlotDetails(ele.getVillageLgdCode(),ele.getSurveyNumber(),ele.getSubSurveyNumber());
//				Long seasonId, String year,Long villageLgdCode, String surveyNumber, String subSurveyNumber
				List<FarmlandPlotRegistry> farmLandPlotList = farmlandPlotRegistryRepository.getFarmLandIPlotDetailsWithSeasonYear(ele.getSeasonId(),year,ele.getVillageLgdCode(),ele.getSurveyNumber(),ele.getSubSurveyNumber());
				for (FarmlandPlotRegistry farmPlot: farmLandPlotList) {
					//f.farmlandPlotRegistryId,f.surveyNumber,f.subSurveyNumber,f.farmlandId,f.plotArea,f.villageLgdMaster.villageLgdCode
					FarmerOutputDAO output =  new FarmerOutputDAO(farmPlot.getFarmlandPlotRegistryId(), farmPlot.getSurveyNumber(),farmPlot.getSubSurveyNumber(),farmPlot.getFarmlandId(),farmPlot.getPlotArea(),farmPlot.getVillageLgdMaster().getVillageLgdCode());
					VillageLgdMaster village = villageLgdMasterRepository.findByVillageLgdCode(ele.getVillageLgdCode());
					if(Objects.nonNull(output) && !Objects.isNull(output.getFarmlandPlotRegistryId())) {
						output.setDistrictName(village.getDistrictLgdCode().getDistrictName());
						output.setSubDistrictName(village.getSubDistrictLgdCode().getSubDistrictName());
						output.setVillageName(village.getVillageName());
						List<Long> seasonList = landParcelSurveyMasterRespository.getSeasonNameForParcelId(output.getFarmlandPlotRegistryId());
						List<SeasonMasterOutputDAO> season = seasonMasterRepository.findSeasonOutputList(seasonList);
						if (!seasonList.isEmpty()) {
							output.setSeasonList(season);
							List<String> yearList = landParcelSurveyMasterRespository.getYearForParcelId(output.getFarmlandPlotRegistryId());
							if (!yearList.isEmpty()){
								List<YearDao> years = new ArrayList<>();
								yearList.stream().forEach(x-> years.add(new YearDao(x)));
								output.setYearList(years);
							}
						}
						output.setSfdbFarmlandId(ele.getSfdbFarmlandId());
						farmerOutputDAOList.add(output);
					}
				}
			});

			return new ResponseModel(farmerOutputDAOList, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e){
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

}
