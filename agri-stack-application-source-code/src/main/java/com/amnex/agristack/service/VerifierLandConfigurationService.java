/**
 * 
 */
package com.amnex.agristack.service;

import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.Enum.RoleEnum;
import com.amnex.agristack.dao.FarmLandCount;
import com.amnex.agristack.dao.UserCountDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.*;
import com.amnex.agristack.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.amnex.agristack.Enum.VerifierReasonOfAssignmentEnum;
import com.amnex.agristack.dao.PaginationDao;
import com.amnex.agristack.dao.VerifierLandConfigurationDAO;
import com.amnex.agristack.utils.CustomMessages;

/**
 * @author majid.belim
 *
 */
@Service
public class VerifierLandConfigurationService {

	@Autowired
	private VerifierLandConfigurationRepository verifierLandConfigurationRepository;
	
	@Autowired
	private VillageLgdMasterRepository villageLgdMasterRepository;
	@Autowired
	private VerifierLandAssignmentRepository verifierLandAssignmentRepository;
	@Autowired
	private UserLandAssignmentRepository userLandAssignmentRepository;

	@Autowired
	private SeasonMasterRepository seasonMasterRepository;


	public ResponseModel getVerifierConfigCalenderByConfigId(PaginationDao paginationDao, HttpServletRequest request) {

		try {

			Boolean isSortingOnRandomPlotsCount = Boolean.FALSE;
			if (paginationDao.getSortField() != null && "randomPlotsPickedCount".equals(paginationDao.getSortField())){
				isSortingOnRandomPlotsCount = Boolean.TRUE;
				paginationDao.setSortField("VillageLgdCode_VillageName");
			}

			Pageable pageable = PageRequest.of(paginationDao.getPage(), paginationDao.getLimit(),
					Sort.by(paginationDao.getSortField()).descending());

			if (paginationDao.getSortOrder().equals("asc")) {
				pageable = PageRequest.of(paginationDao.getPage(), paginationDao.getLimit(),
						Sort.by(paginationDao.getSortField()).ascending());
			}
			
//			Page<VerifierLandConfiguration> finalData = verifierLandConfigurationRepository
//					.findByConfigId_SurveyVerificationConfigurationMasterIdAndIsActiveAndIsDeletedAndSeason_SeasonIdAndStartingYearAndEndingYearAndVillageLgdCode_SubDistrictLgdCode_SubDistrictLgdCodeInOrderByVillageLgdCode_VillageName(
//							paginationDao.getId(), true, false,paginationDao.getSeasonId(),paginationDao.getStartingYear(),paginationDao.getEndingYear(),paginationDao.getSubDistrictLgdCodeList(), pageable);
			Page<VerifierLandConfiguration> finalData = verifierLandConfigurationRepository
					.findByConfigId_SurveyVerificationConfigurationMasterIdAndIsActiveAndIsDeletedAndSeason_SeasonIdAndStartingYearAndEndingYearAndVillageLgdCode_SubDistrictLgdCode_SubDistrictLgdCodeIn(
							paginationDao.getId(), true, false,paginationDao.getSeasonId(),paginationDao.getStartingYear(),paginationDao.getEndingYear(),paginationDao.getSubDistrictLgdCodeList(), pageable);
			
		
			List<VerifierLandConfigurationDAO> finalDataList = new ArrayList<>();
			List<FarmLandCount> listFarmLandcount=new ArrayList<>();
			List<FarmLandCount> listFarmLandRejectcount=new ArrayList<>();
			List<FarmLandCount> listFarmLandFarmObjectioncount=new ArrayList<>();
			if(finalData!=null && finalData.getContent()!=null && finalData.getContent().size()>0) {
				List<Long> vCodes=finalData.stream().map(x->x.getVillageLgdCode().getVillageLgdCode()).collect(Collectors.toList());
				
				listFarmLandcount=verifierLandAssignmentRepository.getCountDetailsByFilter
						(paginationDao.getSeasonId(), vCodes,paginationDao.getStartingYear(),paginationDao.getEndingYear(),VerifierReasonOfAssignmentEnum.RANDOM_PICK.getValue());
				listFarmLandRejectcount=verifierLandAssignmentRepository.getCountDetailsByFilter
						(paginationDao.getSeasonId(), vCodes,paginationDao.getStartingYear(),paginationDao.getEndingYear(),VerifierReasonOfAssignmentEnum.SUPERVISOR_REJECTION.getValue());
				listFarmLandFarmObjectioncount=verifierLandAssignmentRepository.getCountDetailsByFilter
						(paginationDao.getSeasonId(), vCodes,paginationDao.getStartingYear(),paginationDao.getEndingYear(),VerifierReasonOfAssignmentEnum.FARMER_OBJECTION.getValue());
				
			}
			List<FarmLandCount> randomfarmLandcounts=listFarmLandcount;
			List<FarmLandCount> rejectfarmLandcounts=listFarmLandRejectcount;
			List<FarmLandCount> farmObjectionLandcounts=listFarmLandFarmObjectioncount;
			
//			Set<Long> villages=finalData.stream().map(x->x.getVillageLgdCode()).collect(Collectors.toSet());
			finalData.getContent().forEach(action -> {
				VerifierLandConfigurationDAO verifierLandConfigurationDAO = new VerifierLandConfigurationDAO();
				verifierLandConfigurationDAO.setSeasonId(action.getSeason().getSeasonId());
				verifierLandConfigurationDAO.setSeasonName(action.getSeason().getSeasonName());
				verifierLandConfigurationDAO.setStateLgdCode(action.getConfigId().getStateLgdMaster().getStateLgdCode());
				verifierLandConfigurationDAO.setVillageLgdCode(action.getVillageLgdCode().getVillageLgdCode());
				verifierLandConfigurationDAO.setVillageName(action.getVillageLgdCode().getVillageName());
				verifierLandConfigurationDAO.setTalukaName(action.getVillageLgdCode().getSubDistrictLgdCode().getSubDistrictName());
				verifierLandConfigurationDAO.setRandomPlotsPickedCount(0l);
				verifierLandConfigurationDAO.setTotalAssignedPlotsCount(0l);
				verifierLandConfigurationDAO.setRejectedBySupervisorCount(0l);
				verifierLandConfigurationDAO.setObjectionsMarkedForVerificationSurveyBySupervisorCount(0l);
//				if(action.getLandPlot()!=null && action.getLandPlot().size()>0) {
//					verifierLandConfigurationDAO.setRandomPlotsPickedCount((long) action.getLandPlot().size());
//				}
				if(randomfarmLandcounts!=null && randomfarmLandcounts.size()>0) {
					List<FarmLandCount> totalAssignPlotCount=randomfarmLandcounts.stream().filter(x->x.getVillageLgdCode().equals(action.getVillageLgdCode().getVillageLgdCode())).collect(Collectors.toList());
				
					if(totalAssignPlotCount!=null && totalAssignPlotCount.size()>0) {
						verifierLandConfigurationDAO.setTotalAssignedPlotsCount(totalAssignPlotCount.get(0).getTotalCount());
						verifierLandConfigurationDAO.setRandomPlotsPickedCount(totalAssignPlotCount.get(0).getTotalCount());
					}
				}else {
					if(action.getLandPlot()!=null && action.getLandPlot().size()>0) {
					verifierLandConfigurationDAO.setRandomPlotsPickedCount((long) action.getLandPlot().size());
					}
				}
				
				if(rejectfarmLandcounts!=null && rejectfarmLandcounts.size()>0) {
					List<FarmLandCount> totalAssignPlotCount=rejectfarmLandcounts.stream().filter(x->x.getVillageLgdCode().equals(action.getVillageLgdCode().getVillageLgdCode())).collect(Collectors.toList());
					if(totalAssignPlotCount!=null && totalAssignPlotCount.size()>0) {
						verifierLandConfigurationDAO.setRejectedBySupervisorCount(totalAssignPlotCount.get(0).getTotalCount());
					}
				}
				
				if(farmObjectionLandcounts!=null && farmObjectionLandcounts.size()>0) {
					List<FarmLandCount> totalAssignPlotCount=farmObjectionLandcounts.stream().filter(x->x.getVillageLgdCode().equals(action.getVillageLgdCode().getVillageLgdCode())).collect(Collectors.toList());
					if(totalAssignPlotCount!=null && totalAssignPlotCount.size()>0) {
						verifierLandConfigurationDAO.setObjectionsMarkedForVerificationSurveyBySupervisorCount(totalAssignPlotCount.get(0).getTotalCount());
					}
					
				}
//				verifierLandConfigurationDAO.setRejectedBySupervisorCount(0l);
//				verifierLandConfigurationDAO.setObjectionsMarkedForVerificationSurveyBySupervisorCount(0l);
				
				verifierLandConfigurationDAO.setTotalPlotVerificationCompleteCount(0l);
				
				finalDataList.add(verifierLandConfigurationDAO);

			});
			
			if (finalDataList != null && finalDataList.size() > 0) {
				List<Long> vcodes = finalDataList.stream().map(x -> x.getVillageLgdCode())
						.collect(Collectors.toList());
				List<UserCountDAO> completedCounts = userLandAssignmentRepository
						.getSurveyorApproveCountByFilter(paginationDao.getStartingYear(), paginationDao.getEndingYear(),
								paginationDao.getSeasonId(), vcodes);
				finalDataList.forEach(action -> {
					completedCounts.forEach(action2 -> {

						if (action.getVillageLgdCode().equals(action2.getVillageCode())) {
							action.setTotalPlotVerificationCompleteCount(action2.getTotalCount());
//							
						}
					});
				});
			}  
//			if(villages!=null && villages.size()>0) {
//				List<VillageLgdMaster> data=villageLgdMasterRepository.findByVillageLgdCodeIn(villages);
//				finalDataList.forEach(action->{
//					data.forEach(action2->{
//						if(action.getVillageLgdCode().equals(action2.getVillageLgdCode())) {
//							action.setVillageName(action2.getVillageName());
//							action.setTalukaName(action2.getSubDistrictLgdCode().getSubDistrictName());
//						}
//					});
//					
//				});
//				
//			}

			if (Boolean.TRUE.equals(isSortingOnRandomPlotsCount)){
				if (paginationDao.getSortOrder().equals("asc")) {
					Collections.sort(finalDataList,Comparator.comparing(VerifierLandConfigurationDAO::getRandomPlotsPickedCount));
				}else{
					Collections.sort(finalDataList,Comparator.comparing(VerifierLandConfigurationDAO::getRandomPlotsPickedCount).reversed());
				}
			}
	

			Map<String, Object> responseData = new HashMap();
			responseData.put("data", finalDataList);
			responseData.put("total", finalData.getTotalElements());
			responseData.put("page", paginationDao.getPage());
			responseData.put("limit", paginationDao.getLimit());
			responseData.put("sortField", paginationDao.getSortField());
			responseData.put("sortOrder", paginationDao.getSortOrder());

			return new ResponseModel(responseData, CustomMessages.SUCCESS, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILURE, CustomMessages.METHOD_POST);
		}
	}
	public ResponseModel getVerifierConfigCalender(PaginationDao paginationDao, HttpServletRequest request) {
		try {
			List<VerifierLandConfigurationDAO> finalDataList = new ArrayList<>();

			Pageable pageable = PageRequest.of(paginationDao.getPage(), paginationDao.getLimit(),
					Sort.by(paginationDao.getSortField()).descending());

			if (paginationDao.getSortOrder().equals("asc")) {
				pageable = PageRequest.of(paginationDao.getPage(), paginationDao.getLimit(),
						Sort.by(paginationDao.getSortField()).ascending());
			}

			Integer offset = paginationDao.getLimit() * paginationDao.getPage();
			List<VerifierLandConfigurationDAO> finalDataListTemp = new ArrayList<>();
//			Page<VerifierLandConfiguration> finalData = verifierLandConfigurationRepository
//					.findByConfigId_SurveyVerificationConfigurationMasterIdAndIsActiveAndIsDeletedAndSeason_SeasonIdAndStartingYearAndEndingYearAndVillageLgdCode_SubDistrictLgdCode_SubDistrictLgdCodeInOrderByVillageLgdCode_VillageName(
//							paginationDao.getId(), true, false,paginationDao.getSeasonId(),paginationDao.getStartingYear(),paginationDao.getEndingYear(),paginationDao.getSubDistrictLgdCodeList(), pageable);
			
			Page<VerifierLandConfiguration> finalData = null;
			List<FarmlandPlotRegistry> finalVerifierData = new ArrayList<>();

			if ((Long.valueOf(RoleEnum.INSPECTION_OFFICER.getValue())).equals(paginationDao.getUserType())) {
				if (paginationDao.getSearch() != null) {
					finalData = verifierLandConfigurationRepository
							.findByConfigId_StateLgdMaster_StateLgdCodeInAndConfigId_IsActiveAndConfigId_IsDeletedAndIsActiveAndIsDeletedAndSeason_SeasonIdAndStartingYearAndEndingYearAndVillageLgdCode_SubDistrictLgdCode_SubDistrictLgdCodeInAndVillageLgdCode_VillageNameContaining(
									paginationDao.getStateLgdCodeList(), true, false, true, false, paginationDao.getSeasonId(), paginationDao.getStartingYear(), paginationDao.getEndingYear(), paginationDao.getSubDistrictLgdCodeList(), paginationDao.getSearch(), pageable);

				} else {
					finalData = verifierLandConfigurationRepository
							.findByConfigId_StateLgdMaster_StateLgdCodeInAndConfigId_IsActiveAndConfigId_IsDeletedAndIsActiveAndIsDeletedAndSeason_SeasonIdAndStartingYearAndEndingYearAndVillageLgdCode_SubDistrictLgdCode_SubDistrictLgdCodeIn(
									paginationDao.getStateLgdCodeList(), true, false, true, false, paginationDao.getSeasonId(), paginationDao.getStartingYear(), paginationDao.getEndingYear(), paginationDao.getSubDistrictLgdCodeList(), pageable);
				}

				//			UsernameContaining
//				List<VerifierLandConfigurationDAO> finalDataList = new ArrayList<>();
				List<FarmLandCount> listFarmLandcount = new ArrayList<>();
				if (finalData != null && finalData.getContent() != null && finalData.getContent().size() > 0) {
					List<Long> vCodes = finalData.stream().map(x -> x.getVillageLgdCode().getVillageLgdCode()).collect(Collectors.toList());

					listFarmLandcount = verifierLandAssignmentRepository.getCountDetailsByFilterWithoutSecondTimeRejected
							(paginationDao.getSeasonId(), vCodes, paginationDao.getStartingYear(), paginationDao.getEndingYear());

				}
				List<FarmLandCount> farmLandcounts = listFarmLandcount;

				finalData.getContent().forEach(action -> {
					VerifierLandConfigurationDAO verifierLandConfigurationDAO = new VerifierLandConfigurationDAO();
					verifierLandConfigurationDAO.setSeasonId(action.getSeason().getSeasonId());
					verifierLandConfigurationDAO.setSeasonName(action.getSeason().getSeasonName());
					verifierLandConfigurationDAO.setStateLgdCode(action.getConfigId().getStateLgdMaster().getStateLgdCode());
					verifierLandConfigurationDAO.setVillageLgdCode(action.getVillageLgdCode().getVillageLgdCode());
					verifierLandConfigurationDAO.setVillageName(action.getVillageLgdCode().getVillageName());
					verifierLandConfigurationDAO.setTalukaName(action.getVillageLgdCode().getSubDistrictLgdCode().getSubDistrictName());
					verifierLandConfigurationDAO.setSubDistrictLgdCode(action.getVillageLgdCode().getSubDistrictLgdCode().getSubDistrictLgdCode());
					verifierLandConfigurationDAO.setRandomPlotsPickedCount(0l);
					verifierLandConfigurationDAO.setTotalAssignedPlotsCount(0l);
					if (action.getLandPlots() != null && action.getLandPlots().size() > 0) {
						verifierLandConfigurationDAO.setRandomPlotsPickedCount((long) action.getLandPlots().size());
					}
					List<FarmLandCount> totalAssignPlotCount = farmLandcounts.stream().filter(x -> x.getVillageLgdCode().equals(action.getVillageLgdCode().getVillageLgdCode())).collect(Collectors.toList());
					if (totalAssignPlotCount != null && totalAssignPlotCount.size() > 0) {
						verifierLandConfigurationDAO.setTotalAssignedPlotsCount(totalAssignPlotCount.get(0).getTotalCount());
					}
					verifierLandConfigurationDAO.setRejectedBySupervisorCount(0l);
					verifierLandConfigurationDAO.setObjectionsMarkedForVerificationSurveyBySupervisorCount(0l);

					verifierLandConfigurationDAO.setTotalPlotVerificationCompleteCount(0l);

					finalDataList.add(verifierLandConfigurationDAO);

				});

				if (finalDataList != null && finalDataList.size() > 0) {
					List<Long> vcodes = finalDataList.stream().map(x -> x.getVillageLgdCode())
							.collect(Collectors.toList());
					List<UserCountDAO> completedCounts = userLandAssignmentRepository
							.getSurveyorApproveCountByFilterWithoutSecondTimeRejected(paginationDao.getStartingYear(), paginationDao.getEndingYear(),
									paginationDao.getSeasonId(), vcodes);
					finalDataList.forEach(action -> {
						completedCounts.forEach(action2 -> {

							if (action.getVillageLgdCode().equals(action2.getVillageCode())) {
								action.setTotalPlotVerificationCompleteCount(action2.getTotalCount());
//							
							}
						});
					});
				}
			} else if ((Long.valueOf(RoleEnum.VERIFIER.getValue())).equals(paginationDao.getUserType())){

				Pageable pageableVerifier = PageRequest.of(paginationDao.getPage(), paginationDao.getLimit());

				if (paginationDao.getSearch() != null) {
					finalVerifierData = verifierLandAssignmentRepository.findSecondTImeRejectedSurveyLandDetailsBySubDistrictLgdCodeWithSearchV2
							(paginationDao.getSeasonId(), paginationDao.getStartingYear(), paginationDao.getEndingYear(), paginationDao.getSubDistrictLgdCodeList(),paginationDao.getSearch());
				}else {
					finalVerifierData = verifierLandAssignmentRepository.findSecondTImeRejectedSurveyLandDetailsBySubDistrictLgdCodeV2
							(paginationDao.getSeasonId(), paginationDao.getStartingYear(), paginationDao.getEndingYear(), paginationDao.getSubDistrictLgdCodeList());
				}

//				List<VerifierLandConfigurationDAO> finalDataList = new ArrayList<>();
				List<FarmLandCount> listFarmLandcount = new ArrayList<>();
				if (!finalVerifierData.isEmpty()) {
					List<Long> vCodes = finalVerifierData.stream().map(f->f.getVillageLgdMaster().getVillageLgdCode()).collect(Collectors.toList());

					listFarmLandcount = verifierLandAssignmentRepository.getCountDetailsByFilterWithSecondTimeRejected
							(paginationDao.getSeasonId(), vCodes, paginationDao.getStartingYear(), paginationDao.getEndingYear());

				}
				List<FarmLandCount> farmLandCounts = listFarmLandcount;
				List<FarmlandPlotRegistry> farmlandPlotRegistries = finalVerifierData;
				SowingSeason season = seasonMasterRepository.findBySeasonId(paginationDao.getSeasonId());

				finalVerifierData.forEach(action -> {
					if (finalDataListTemp.isEmpty() || (!finalDataListTemp.isEmpty() &&
							finalDataListTemp.stream().noneMatch(f -> f.getVillageLgdCode().equals(action.getVillageLgdMaster().getVillageLgdCode())))) {
						VerifierLandConfigurationDAO verifierLandConfigurationDAO = new VerifierLandConfigurationDAO();
						verifierLandConfigurationDAO.setSeasonId(season!=null ? season.getSeasonId() : null);
						verifierLandConfigurationDAO.setSeasonName(season!=null ? season.getSeasonName() : null);
						verifierLandConfigurationDAO.setStateLgdCode(action.getVillageLgdMaster().getStateLgdCode().getStateLgdCode());
						verifierLandConfigurationDAO.setVillageLgdCode(action.getVillageLgdMaster().getVillageLgdCode());
						verifierLandConfigurationDAO.setVillageName(action.getVillageLgdMaster().getVillageName());
						verifierLandConfigurationDAO.setTalukaName(action.getVillageLgdMaster().getSubDistrictLgdCode().getSubDistrictName());
						verifierLandConfigurationDAO.setSubDistrictLgdCode(action.getVillageLgdMaster().getSubDistrictLgdCode().getSubDistrictLgdCode());
						verifierLandConfigurationDAO.setRandomPlotsPickedCount(0l);
						verifierLandConfigurationDAO.setTotalAssignedPlotsCount(0l);

						Long totalSecondTimeRejectedPlots = farmlandPlotRegistries.stream().filter(f->f.getVillageLgdMaster().getVillageLgdCode().equals(action.getVillageLgdMaster().getVillageLgdCode())).count();
						verifierLandConfigurationDAO.setRandomPlotsPickedCount(totalSecondTimeRejectedPlots!=null? totalSecondTimeRejectedPlots : 0L);

						List<FarmLandCount> totalAssignPlotCount = farmLandCounts.stream().filter(x -> x.getVillageLgdCode().equals(action.getVillageLgdMaster().getVillageLgdCode())).collect(Collectors.toList());
						if (totalAssignPlotCount != null && totalAssignPlotCount.size() > 0) {
							verifierLandConfigurationDAO.setTotalAssignedPlotsCount(totalAssignPlotCount.get(0).getTotalCount());
						}
						verifierLandConfigurationDAO.setRejectedBySupervisorCount(0l);
						verifierLandConfigurationDAO.setObjectionsMarkedForVerificationSurveyBySupervisorCount(0l);

						verifierLandConfigurationDAO.setTotalPlotVerificationCompleteCount(0l);

						finalDataListTemp.add(verifierLandConfigurationDAO);
					}
				});

				if (finalDataListTemp != null && finalDataListTemp.size() > 0) {
					List<Long> vcodes = finalDataListTemp.stream().map(VerifierLandConfigurationDAO::getVillageLgdCode)
							.collect(Collectors.toList());
					List<UserCountDAO> completedCounts = userLandAssignmentRepository
							.getSurveyorApproveCountByFilterWithSecondTimeRejected(paginationDao.getStartingYear(), paginationDao.getEndingYear(),
									paginationDao.getSeasonId(), vcodes);
					finalDataListTemp.forEach(action -> {
						completedCounts.forEach(action2 -> {

							if (action.getVillageLgdCode().equals(action2.getVillageCode())) {
								action.setTotalPlotVerificationCompleteCount(action2.getTotalCount());
//
							}
						});
					});

					if (paginationDao.getSortOrder().equals("asc")) {
						Collections.sort(finalDataListTemp, Comparator.comparing(VerifierLandConfigurationDAO::getVillageName));
					}else{
						Collections.sort(finalDataListTemp, Comparator.comparing(VerifierLandConfigurationDAO::getVillageName).reversed());
					}
					Integer toLimit = Math.min(finalDataListTemp.size(), (offset + paginationDao.getLimit()));
					finalDataList.addAll(finalDataListTemp.subList(offset, toLimit ));

				}

			}
//			if(villages!=null && villages.size()>0) {
//				List<VillageLgdMaster> data=villageLgdMasterRepository.findByVillageLgdCodeIn(villages);
//				finalDataList.forEach(action->{
//					data.forEach(action2->{
//						if(action.getVillageLgdCode().equals(action2.getVillageLgdCode())) {
//							action.setVillageName(action2.getVillageName());
//							action.setTalukaName(action2.getSubDistrictLgdCode().getSubDistrictName());
//						}
//					});
//					
//				});
//				
//			}
			
	

			Map<String, Object> responseData = new HashMap();
			responseData.put("data", finalDataList);
			if ((Long.valueOf(RoleEnum.INSPECTION_OFFICER.getValue())).equals(paginationDao.getUserType())) {
				responseData.put("total", finalData.getTotalElements());
			} else if ((Long.valueOf(RoleEnum.VERIFIER.getValue())).equals(paginationDao.getUserType())){
				responseData.put("total", finalDataListTemp.size());
			}
			responseData.put("page", paginationDao.getPage());
			responseData.put("limit", paginationDao.getLimit());
			responseData.put("sortField", paginationDao.getSortField());
			responseData.put("sortOrder", paginationDao.getSortOrder());

			return new ResponseModel(responseData, CustomMessages.SUCCESS, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILURE, CustomMessages.METHOD_POST);
		}
	}

}
