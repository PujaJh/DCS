package com.amnex.agristack.service;

import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amnex.agristack.Enum.RoleEnum;
import com.amnex.agristack.Enum.StatusEnum;
import com.amnex.agristack.Enum.VerifierReasonOfAssignmentEnum;
import com.amnex.agristack.dao.FarmlandPlotVerifierDAO;
import com.amnex.agristack.dao.SurveyVerificationConfigurationMasterDAO;
import com.amnex.agristack.dao.YearCountDAO;
import com.amnex.agristack.entity.NonAgriPlotMapping;
import com.amnex.agristack.entity.RoleMaster;
import com.amnex.agristack.entity.SowingSeason;
import com.amnex.agristack.entity.StateLgdMaster;
import com.amnex.agristack.entity.StatusMaster;
import com.amnex.agristack.entity.SurveyVerificationConfigurationMaster;
import com.amnex.agristack.entity.UserAvailability;
import com.amnex.agristack.entity.UserMaster;
import com.amnex.agristack.entity.VerifierLandAssignment;
import com.amnex.agristack.entity.VerifierLandConfiguration;
import com.amnex.agristack.entity.VillageLgdMaster;
import com.amnex.agristack.entity.YearMaster;
import com.amnex.agristack.utils.CustomMessages;

@Service
public class SurveyVerificationConfigurationService {

	@Autowired
	private SurveyVerificationConfigurationMasterRepository surveyVerificationConfigurationMasterRepository;

	@Autowired
	private StateLgdMasterRepository stateLgdMasterRepository;

	@Autowired
	private YearRepository yearRepository;

	@Autowired
	private SeasonMasterRepository seasonMasterRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private UserVillageMappingRepository userVillageMappingRepository;
	
	@Autowired
	private VerifierLandConfigurationRepository verifierLandConfigurationRepository;
	
	@Autowired
	private VillageLgdMasterRepository villageLgdMasterRepository;
	
	@Autowired
	private FarmlandPlotRegistryRepository farmlandPlotRegistryRepository;
	@Autowired
	private StatusMasterRepository statusMasterRepository;
	@Autowired
	private RoleMasterRepository roleRepository;
	@Autowired
	private UserAvailabilityRepository  userAvailabilityRepository;
	@Autowired
	private VerifierLandAssignmentRepository verifierLandAssignmentRepository;
	@Autowired
	private GeneralService generalService;
	@Autowired
	private NonAgriPlotMappingRepository nonAgriPlotMappingRepository;

	@Autowired
	private ConfigurationRepository configurationRepository;

	Logger logger= Logger.getLogger(SurveyVerificationConfigurationService.class.getName());
	public ResponseModel getSurveyVerificationConfiguration(Long stateLgdCode) {
		try {
			SurveyVerificationConfigurationMasterDAO configDao = surveyVerificationConfigurationMasterRepository
					.findByStateLgdCode(stateLgdCode);

			return new ResponseModel(configDao, CustomMessages.SUCCESS, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public ResponseModel getAllSurveyVerificationConfiguration(HttpServletRequest request) {
		try {

			String userId = userService.getUserId(request);
			Long userIdLong = Long.parseLong(userId);

			List<Long> assignedStateCodes = userVillageMappingRepository.getStateCodesById(userIdLong);

			List<SurveyVerificationConfigurationMaster> configList = surveyVerificationConfigurationMasterRepository
					.findAll(assignedStateCodes);

			return new ResponseModel(configList, CustomMessages.SUCCESS, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public ResponseModel addSurveyVerificationConfigurationV2(SurveyVerificationConfigurationMasterDAO dao,
			HttpServletRequest request) {
		try {

			String userId = userService.getUserId(request);
			Long userIdLong = Long.parseLong(userId);

			List<Long> assignedStateCodes = userVillageMappingRepository.getStateCodesById(userIdLong);
			
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("IST"));
			
			Integer currentYear = cal.get(Calendar.YEAR);
			SowingSeason currentSeason = generalService.getCurrentSeason();
			for (Long stateLgdCode : assignedStateCodes) {

				SurveyVerificationConfigurationMaster surveyVerificationConfigurationMasterOld = surveyVerificationConfigurationMasterRepository
						.findMasterDataByStateLgdCode(stateLgdCode);
				SurveyVerificationConfigurationMaster surveyVerificationConfigurationMaster = new SurveyVerificationConfigurationMaster();
				surveyVerificationConfigurationMaster.setIsApppliedNextSeason(false);
				surveyVerificationConfigurationMaster.setIsActive(Boolean.TRUE);
				List<Long> oldVillageList=new ArrayList<>();
				if (surveyVerificationConfigurationMasterOld != null) {
					// if state wise entry already exists then deactivate previous entry and add new
					// one
//					surveyVerificationConfigurationMasterOld.setIsActive(Boolean.FALSE);
//					surveyVerificationConfigurationMasterRepository.save(surveyVerificationConfigurationMasterOld);
					
					
					if(currentSeason!=null ) {
						oldVillageList=verifierLandConfigurationRepository.getVillagesByFilter(surveyVerificationConfigurationMasterOld.getSurveyVerificationConfigurationMasterId(), currentYear, currentSeason.getSeasonId());	
					}
					
					 Optional<SurveyVerificationConfigurationMaster> surveyOp=surveyVerificationConfigurationMasterRepository.findByStateLgdMaster_StateLgdCodeAndIsDeletedAndIsApppliedNextSeason(stateLgdCode, false, true);
					 if(surveyOp.isPresent()) {
						 SurveyVerificationConfigurationMaster surveyVerificationConfigurationMasterIsApppliedNextSeason=surveyOp.get();
						 
						 surveyVerificationConfigurationMasterIsApppliedNextSeason.setIsApppliedNextSeason(false);
						 surveyVerificationConfigurationMasterRepository.save(surveyVerificationConfigurationMasterIsApppliedNextSeason);
					 }
					surveyVerificationConfigurationMaster.setIsApppliedNextSeason(true);
					surveyVerificationConfigurationMaster.setIsActive(Boolean.FALSE);
					
					List<Long> seasonIds=seasonMasterRepository.getSeasonIdsNotIn(surveyVerificationConfigurationMasterOld.getAppliedSeason().getSeasonId());
					if(seasonIds!=null && seasonIds.size()>0) {
						List<SowingSeason> upComingSeasons=seasonMasterRepository.findBySeasonIdIn(seasonIds);
						if(seasonIds!=null && seasonIds.size()>0) {
							surveyVerificationConfigurationMaster.setAppliedSeason(upComingSeasons.get(0));	
						}
							
					}
					
					
				}else {
					SowingSeason appliedSeason = seasonMasterRepository.findBySeasonId(dao.getAppliedSeasonId());
					surveyVerificationConfigurationMaster.setAppliedSeason(appliedSeason);
				}
				List<Long> villageLgdCodeOld=oldVillageList;

				surveyVerificationConfigurationMaster.setRandomPickPercentageOfVillages(dao.getRandomPickPercentageOfVillages());
				surveyVerificationConfigurationMaster.setRandomPickPercentageOfPlots(dao.getRandomPickPercentageOfPlots());
				surveyVerificationConfigurationMaster.setRandomPickMinimumNumberOfPlots(dao.getRandomPickMinimumNumberOfPlots());

				surveyVerificationConfigurationMaster.setSurveyRejectedBySupervisorForSecondTime(dao.getSurveyRejectedBySupervisorForSecondTime());
				surveyVerificationConfigurationMaster.setObjectionRaisedByFarmerAndMarkedBySupervisor(
						dao.getObjectionRaisedByFarmerAndMarkedBySupervisor());

				StateLgdMaster stateLgdMaster = stateLgdMasterRepository.findByStateLgdCode(stateLgdCode);
				surveyVerificationConfigurationMaster.setStateLgdMaster(stateLgdMaster);

				Optional<YearMaster> appliedYear = yearRepository.findById(dao.getAppliedYearId());
				surveyVerificationConfigurationMaster.setAppliedYear(appliedYear.get());

//				Year start
				
				Integer currentMonth = cal.get(Calendar.MONTH) + 1;
				Integer currentYeaTemp=currentYear;
				if(currentMonth>6) {
					currentYeaTemp=currentYear+1;
				}
				YearMaster yearMaster=yearRepository.findByEndYear(String.valueOf(currentYeaTemp));
				if(yearMaster==null)  {
					 yearMaster=yearRepository.findByEndYear(String.valueOf(cal.get(Calendar.YEAR)));
				}
				surveyVerificationConfigurationMaster.setAppliedYear(yearMaster);
//				Year end
				surveyVerificationConfigurationMaster.setCreatedOn(new Date());
				
				surveyVerificationConfigurationMaster.setIsDeleted(Boolean.FALSE);
				StatusMaster status =statusMasterRepository.findByIsDeletedFalseAndStatusCode(StatusEnum.PENDING.getValue());
				surveyVerificationConfigurationMaster.setStatus(status);
				SurveyVerificationConfigurationMaster surveyVerificationConfigurationMasterAdd=surveyVerificationConfigurationMasterRepository.save(surveyVerificationConfigurationMaster);
				Double villageAssignmentPercentage = surveyVerificationConfigurationMaster.getRandomPickPercentageOfVillages();
				Long plotAssignmentPercentage =Math.round( surveyVerificationConfigurationMaster.getRandomPickPercentageOfPlots());

				Integer plotAssignmentCount = surveyVerificationConfigurationMaster.getRandomPickMinimumNumberOfPlots();
//				Start config
//				List<UserVillageMapping> villagecods = userVillageMappingRepository.findByUserMaster_UserIdAndIsActiveAndIsDeleted(userIdLong,true,false);
				
				/// add state filter also
//				List<Long> villagecode = userVillageMappingRepository.getVillageCodesByUserId(userIdLong);
				
				new Thread() {

					public void run() {
						try {			
							
//				start	change 08-08-2023
				List<Long> subDistrictList=userVillageMappingRepository.getSubDistrictCodesById(userIdLong);
//				end	change 08-08-2023
				List<Long> villageCodes=userVillageMappingRepository.getVillageForPlotsGreaterZero(userIdLong);
				
//				Due to large amount of village we use subdistict wise data before we use village wise data
//				List<Long> villagecode = userVillageMappingRepository.getVillageCodesByUserIdStateCode(userIdLong,stateLgdCode);
//				List<VillageLgdMaster> villagecodes=villageLgdMasterRepository.findByVillageLgdCodeIn(villagecode);
//				start	change 08-08-2023
//				List<VillageLgdMaster> villagecodes=villageLgdMasterRepository.findBySubDistrictLgdCode_SubDistrictLgdCodeInOrderByVillageLgdCodeAsc(subDistrictList);
//				end	change 08-08-2023
				
				List<VillageLgdMaster> villagecodes = new ArrayList<>();
				if (villageCodes.size() < 2100) {
					villagecodes=villageLgdMasterRepository.findByVillageLgdCodeIn(villageCodes);
				} else {
					Integer parts = 2100;
					Integer startIndex = 0;
					Integer totalSize = villageCodes.size();

					while (startIndex < totalSize) {
						List<Long> idList = new ArrayList<>();

						if ((startIndex + parts) < totalSize) {
							idList = villageCodes.subList(startIndex, (startIndex + parts));
						} else {
							idList = villageCodes.subList(startIndex, totalSize);
						}
						villagecodes
								.addAll(villageLgdMasterRepository.findByVillageLgdCodeIn(idList));

						startIndex += parts;
					}

				}
				if(villagecodes!=null && villagecodes.size()>0) {
					if(villageLgdCodeOld!=null && villageLgdCodeOld.size()>0 ) {
						List<VillageLgdMaster> tmpVillageList=villagecodes.stream().filter(v->villageLgdCodeOld.contains(v.getVillageLgdCode())).collect(Collectors.toList());
						villagecodes.removeAll(tmpVillageList);
						villagecodes.addAll(tmpVillageList);
					}
					
			
					
//					System.out.println("villageAssignmentPercentage "+villageAssignmentPercentage);
					int years=(int) (100/villageAssignmentPercentage);
//					System.out.println("years "+years);
//					System.out.println("RandomPickPercentageOfPlots % "+master.getRandomPickPercentageOfPlots());
//					System.out.println("RandomPickMinimumNumberOfPlots Number "+master.getRandomPickMinimumNumberOfPlots());
					
					Integer totalVillages = villagecodes.size();
					logger.info("total SIZE "+totalVillages);
					Double percentageWiseVillageCount = (totalVillages * villageAssignmentPercentage) / 100;
					logger.info("percentageWiseVillageCount  "+percentageWiseVillageCount);
					
					if (percentageWiseVillageCount > totalVillages) {
						percentageWiseVillageCount = Double.parseDouble(String.valueOf(totalVillages));
					}
					
					Long percentageWiseVillageCountRound =Math.round(percentageWiseVillageCount);
					logger.info("percentageWiseVillageCountRound  "+percentageWiseVillageCountRound);
//					System.out.println("percentageWiseVillageCount  "+percentageWiseVillageCount);
//					System.out.println("percentageWiseVillageCount  round "+percentageWiseVillageCountRound);
					

					
//					
					List<YearCountDAO> yearList=yearRepository.getAllYears(currentYear+"");
					
					if(yearList!=null && yearList.size()>0) {
						
						List<YearCountDAO> yearsWithLimit=yearList.stream().limit(years).collect(Collectors.toList());
						List<String> endYearList=yearsWithLimit.stream().map(x->x.getEndYear()).collect(Collectors.toList());
						RoleMaster role = roleRepository.findByCode(RoleEnum.VERIFIER.toString()).orElse(null);
						List<UserAvailability> userList=userAvailabilityRepository.findByIsAvailableAndSeasonEndYearInAndIsActiveAndIsDeletedAndUserId_RoleId_RoleId(true, endYearList, true, false,role.getRoleId());
//						Due to large amount of village we use subdistict wise data before we use village wise data
//						List<NonAgriPlotMapping> 	naList=nonAgriPlotMappingRepository.findAllByVillageLGDCodeIn(villagecode);
						List<NonAgriPlotMapping> 	naList=nonAgriPlotMappingRepository.findAllBySubDistirctIn(subDistrictList);
						
						Long tmpRound=percentageWiseVillageCountRound;
						int j=0;
//						List<VerifierLandConfiguration> finalList=new ArrayList<>();
//						Due to large amount of village we use subdistict wise data before we use village wise data
//						List<FarmlandPlotRegistry> villageWisePlotList = farmlandPlotRegistryRepository.findByVillageLgdMaster_VillageLgdCodeIn(villagecode);
//						start old code change 07-08-2023
//						List<FarmlandPlotRegistry> villageWisePlotList = farmlandPlotRegistryRepository.findByVillageLgdMaster_SubDistrictLgdCode_SubDistrictLgdCodeIn(subDistrictList);
//						end  old code change 07-08-2023
						
						List<Long> seasonIds=seasonMasterRepository.getSeasonIds();
//						List<SowingSeason> upComingSeasons=seasonMasterRepository.getUpComingSeasonIds();
						List<SowingSeason> upComingSeasons=seasonMasterRepository.findBySeasonIdIn(seasonIds);
						List<SowingSeason>  seasonAll=seasonMasterRepository.findByIsDeletedFalseAndIsActiveTrueOrderBySeasonNameAsc();
						StatusMaster statusMaster=statusMasterRepository.findByIsDeletedFalseAndStatusCode(StatusEnum.PENDING.getValue());
//						seasonMasterRepository.getSeasonIds()
//						for (int i = 0; i < yearsWithLimit.size(); i++) {
							for (int i = 0; i < 1; i++) {
							

							logger.info("==========================================");
							
							logger.info( new Date()+" year "+yearsWithLimit.get(i).getEndYear());
							logger.info("percentageWise Village Count "+percentageWiseVillageCountRound);
							logger.info("start village sequnce  "+j);
							logger.info("end village sequnce  "+tmpRound);
							
							
							List<VerifierLandConfiguration> finalList=new ArrayList<>();
							List<VerifierLandAssignment> landAssignMentList=new ArrayList<>();
//							for (int k = j; k < villagecodes.size(); k++) {
								
//								if(tmpRound==k) {
//									
//									tmpRound=tmpRound+percentageWiseVillageCountRound;
//									j=k;
//									logger.info("******************************");
//									logger.info("new end village sequnce  "+tmpRound);
//									logger.info("******************************");
//									break;
//								}else {
							AtomicInteger index = new AtomicInteger();
							villagecodes.parallelStream().forEach(k->{

//									start plot assignment
//									VillageLgdMaster villageMaster=villagecodes.get(k);
									VillageLgdMaster villageMaster=k;
									logger.info("******************************");
									logger.info("start village lgd code :: "+ index.incrementAndGet() + ":: "+k.getVillageLgdCode());
//									start old code change 07-08-2023
//									List<FarmlandPlotRegistry> tmpVilageWisePlotList=	villageWisePlotList.stream().filter(x->x.getVillageLgdMaster().getVillageLgdCode().equals(villageMaster.getVillageLgdCode())).collect(Collectors.toList());
//									start old code change 07-08-2023
//									List<FarmlandPlotRegistry> tmpVillageWisePlotList=	farmlandPlotRegistryRepository.findByVillageLgdMaster_VillageLgdCode(villageMaster.getVillageLgdCode());
									List<FarmlandPlotVerifierDAO> tmpVillageWisePlotList=	farmlandPlotRegistryRepository.getFarmLandIdsVillageLgdMaster_VillageLgdCode(villageMaster.getVillageLgdCode());

//									start NA PLots remove
									if(tmpVillageWisePlotList!=null && tmpVillageWisePlotList.size()>0 && naList!=null && naList.size()>0) {
										
										List<NonAgriPlotMapping> 	tmpNaList=naList.stream().filter(v->v.getLandParcelId().getVillageLgdMaster().getVillageLgdCode().equals(villageMaster.getVillageLgdCode())).collect(Collectors.toList());
										if(tmpNaList!=null && tmpNaList.size()>0) {
											
											List<Long> naPlotIds=tmpNaList.stream().map(x->x.getLandParcelId().getFarmlandPlotRegistryId()).collect(Collectors.toList());
											tmpVillageWisePlotList=tmpVillageWisePlotList.stream().filter(v->!naPlotIds.contains(v.getFarmlandPlotRegistryId())).collect(Collectors.toList());
										}

									}
//									end NA PLots remove
//									List<FarmlandPlotRegistry> tmpVillageWisePlotList = farmlandPlotRegistryRepository.findByVillageLgdMasterVillageLgdCode(villagecodes.get(k).getVillageLgdCode());
									Integer totalPlots=tmpVillageWisePlotList.size();
									Integer noOfPlotsToAssign=0;
									Long plotPercentageWiseCount = (totalPlots * plotAssignmentPercentage) / 100;
									if (plotAssignmentCount > plotPercentageWiseCount) {
										noOfPlotsToAssign = plotAssignmentCount;
									} else {
										noOfPlotsToAssign = plotPercentageWiseCount.intValue();
									}

									
									if (noOfPlotsToAssign > totalPlots) {
										noOfPlotsToAssign = totalPlots;
									}
									
								
									if(tmpVillageWisePlotList!=null && tmpVillageWisePlotList.size()>0 && noOfPlotsToAssign>tmpVillageWisePlotList.size()) {
										noOfPlotsToAssign = tmpVillageWisePlotList.size();
									}
//									List<FarmlandPlotRegistry> listOfPlotsToAssign =new ArrayList<>();
									List<FarmlandPlotVerifierDAO> listOfPlotsToAssign =new ArrayList<>();
									if(tmpVillageWisePlotList!=null && tmpVillageWisePlotList.size()>0) {
										listOfPlotsToAssign = tmpVillageWisePlotList.subList(0, noOfPlotsToAssign);
									}
									
									
									
//									logger.info("remaningPlots "+remaningPlots);
//									end plot assignment
									List<SowingSeason>  seasonList=new ArrayList<>();
//									if(villageLgdCodeOld!=null && villageLgdCodeOld.size()>0 && currentYear.equals(Integer.valueOf(yearsWithLimit.get(i).getEndYear()))) {
//										List<FarmlandPlotRegistry> remaningPlots=tmpVillageWisePlotList.stream().filter(v->!villageLgdCodeOld.contains(v.getVillageLgdMaster().getVillageLgdCode())).collect(Collectors.toList());
//										if(remaningPlots!=null && remaningPlots.size()>0 && noOfPlotsToAssign>remaningPlots.size()) {
//											noOfPlotsToAssign = remaningPlots.size();
//										}
//										
//										if(remaningPlots!=null && remaningPlots.size()>0) {
//											listOfPlotsToAssign = remaningPlots.subList(0, noOfPlotsToAssign);
//										}
//										
//										
//									}
//									if(currentYear.equals(Integer.valueOf(yearsWithLimit.get(i).getEndYear()))) {
									if(currentYear.equals(Integer.valueOf(yearsWithLimit.get(0).getEndYear()))) {
										
//										seasonList=seasonMasterRepository.findBySeasonIdIn(seasonIds);
										seasonList=upComingSeasons;
										
										
									}else {
										
										seasonList=seasonAll;
									}
//									List<FarmlandPlotRegistry> yearWiseLandAssign=listOfPlotsToAssign;
									List<FarmlandPlotVerifierDAO> yearWiseLandAssign=listOfPlotsToAssign;
									
									String startYear=yearsWithLimit.get(0).getStartYear();
									String endYear=yearsWithLimit.get(0).getEndYear();
//	start 1 season config
									
									
									
											VerifierLandConfiguration verifierLandConfiguration=new VerifierLandConfiguration();
											
											verifierLandConfiguration.setConfigId(surveyVerificationConfigurationMasterAdd);
											verifierLandConfiguration.setIsActive(true);
											verifierLandConfiguration.setIsDeleted(false);
//											
											Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("IST"));
											Integer currentMonth = cal.get(Calendar.MONTH) + 1;
//											System.out.println(currentMonth);
											
											if(currentMonth>6) {
												verifierLandConfiguration.setStartingYear(Integer.valueOf(startYear)+1);
												verifierLandConfiguration.setEndingYear(Integer.valueOf(endYear)+1);
											}else {
												verifierLandConfiguration.setStartingYear(Integer.valueOf(startYear));
												verifierLandConfiguration.setEndingYear(Integer.valueOf(endYear));	
											}
//											
											
											verifierLandConfiguration.setStateLgdCode(stateLgdCode);
											
											verifierLandConfiguration.setCreatedBy(userId);
											verifierLandConfiguration.setModifiedBy(userId);
											
											try {
												verifierLandConfiguration.setCreatedIp(InetAddress.getLocalHost().getHostAddress());
												verifierLandConfiguration.setModifiedIp(InetAddress.getLocalHost().getHostAddress());
											}catch(Exception e) {
												e.printStackTrace();
											}
											
											verifierLandConfiguration.setVillageLgdCode(villageMaster);
											verifierLandConfiguration.setSeason(currentSeason);
//											verifierLandConfiguration.setLandPlot(yearWiseLandAssign);
											verifierLandConfiguration.setLandPlots(null);
											List<Long> landplots=yearWiseLandAssign.stream().map(x->x.getFarmlandPlotRegistryId()).collect(Collectors.toList());

											verifierLandConfiguration.setLandPlots(landplots);
											if(userList!=null && userList.size()>0) {
												List<UserAvailability> userAvailableList=userList.stream().filter(x->x.getUserId().getVerificationVillageLGDCode()!=null&&x.getUserId().getVerificationVillageLGDCode().equals(villageMaster.getVillageLgdCode())&&x.getSeasonId().equals(currentSeason.getSeasonId()) && x.getSeasonEndYear().equals(endYear)).collect(Collectors.toList());
												if(userAvailableList!=null && userAvailableList.size()>0) {
													UserMaster userMaster=userAvailableList.get(0).getUserId();
													if(yearWiseLandAssign!=null && yearWiseLandAssign.size()>0) {
														yearWiseLandAssign.parallelStream().forEach(assignPlot->{
															VerifierLandAssignment verifierLandAssignment = new VerifierLandAssignment();
	
															verifierLandAssignment.setVerifier(userMaster);
															verifierLandAssignment.setVillageLgdCode(villageMaster);
															verifierLandAssignment.setSeason(currentSeason);
															verifierLandAssignment.setStatus(statusMaster);
	
															verifierLandAssignment.setFarmlandId(assignPlot.getFarmlandId());
															verifierLandAssignment.setLandParcelId(assignPlot.getLandParcelId());
															
															Date date1 = null;
															try {
																date1 = new SimpleDateFormat("dd-MM-yyyy").parse(currentSeason.getStartingMonth());
															} catch (ParseException e) {
																// TODO Auto-generated catch block
																e.printStackTrace();
															}
															if(date1!=null && (date1.getMonth()+1)>6) {
																verifierLandAssignment.setStartingYear(Integer.valueOf(startYear)+1);
																verifierLandAssignment.setEndingYear(Integer.valueOf(endYear)+1 );
															}else {
																verifierLandAssignment.setStartingYear(Integer.valueOf(startYear));
																verifierLandAssignment.setEndingYear(Integer.valueOf(endYear));	
															}
															
															
	
															verifierLandAssignment.setIsActive(Boolean.TRUE);
															verifierLandAssignment.setIsDeleted(Boolean.FALSE);
															verifierLandAssignment.setReasonOfAssignment(VerifierReasonOfAssignmentEnum.RANDOM_PICK.getValue());
															verifierLandAssignment.setReasonOfAssignmentType(VerifierReasonOfAssignmentEnum.RANDOM_PICK);
															landAssignMentList.add(verifierLandAssignment);
														});
										
													}
														
														
													
											
												}	
											}
											
											finalList.add(verifierLandConfiguration);
									
//			end 1 season config 						
									
//										start	old  multiple season allocation 
//									seasonList.forEach(action2->{
////										
//										if(villageLgdCodeOld!=null && villageLgdCodeOld.size()>0 &&currentYear.equals(Integer.valueOf(endYear)) && currentSeason.getSeasonId().equals(action2.getSeasonId())) {
//
//										}else {
//												VerifierLandConfiguration verifierLandConfiguration=new VerifierLandConfiguration();
//												
//												verifierLandConfiguration.setConfigId(surveyVerificationConfigurationMasterAdd);
//												verifierLandConfiguration.setIsActive(true);
//												verifierLandConfiguration.setIsDeleted(false);
////												
//												Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("IST"));
//												Integer currentMonth = cal.get(Calendar.MONTH) + 1;
////												System.out.println(currentMonth);
//												
//												if(currentMonth>6) {
//													verifierLandConfiguration.setStartingYear(Integer.valueOf(startYear)+1);
//													verifierLandConfiguration.setEndingYear(Integer.valueOf(endYear)+1);
//												}else {
//													verifierLandConfiguration.setStartingYear(Integer.valueOf(startYear));
//													verifierLandConfiguration.setEndingYear(Integer.valueOf(endYear));	
//												}
////												
//												
//												verifierLandConfiguration.setStateLgdCode(stateLgdCode);
//												
//												verifierLandConfiguration.setCreatedBy(userId);
//												verifierLandConfiguration.setModifiedBy(userId);
//												try {
//													verifierLandConfiguration.setCreatedIp(InetAddress.getLocalHost().getHostAddress());
//													verifierLandConfiguration.setModifiedIp(InetAddress.getLocalHost().getHostAddress());
//												}catch(Exception e) {
//													e.printStackTrace();
//													
//												}
//												verifierLandConfiguration.setVillageLgdCode(villageMaster);
//												verifierLandConfiguration.setSeason(action2);
//												verifierLandConfiguration.setLandPlot(yearWiseLandAssign);
//												
//												if(userList!=null && userList.size()>0) {
//													List<UserAvailability> userAvailableList=userList.stream().filter(x->x.getUserId().getVerificationVillageLGDCode()!=null&&x.getUserId().getVerificationVillageLGDCode().equals(villageMaster.getVillageLgdCode())&&x.getSeasonId().equals(action2) && x.getSeasonEndYear().equals(endYear)).collect(Collectors.toList());
//													if(userAvailableList!=null && userAvailableList.size()>0) {
//														UserMaster userMaster=userAvailableList.get(0).getUserId();
//														if(yearWiseLandAssign!=null && yearWiseLandAssign.size()>0) {
//															yearWiseLandAssign.forEach(assignPlot->{
//																VerifierLandAssignment verifierLandAssignment = new VerifierLandAssignment();
//		
//																verifierLandAssignment.setVerifier(userMaster);
//																verifierLandAssignment.setVillageLgdCode(villageMaster);
//																verifierLandAssignment.setSeason(action2);
//																verifierLandAssignment.setStatus(statusMaster);
//		
//																verifierLandAssignment.setFarmlandId(assignPlot.getFarmlandId());
//																verifierLandAssignment.setLandParcelId(assignPlot.getLandParcelId());
//																
//																Date date1 = null;
//																try {
//																	date1 = new SimpleDateFormat("dd-MM-yyyy").parse(action2.getStartingMonth());
//																} catch (ParseException e) {
//																	// TODO Auto-generated catch block
//																	e.printStackTrace();
//																}
//																if(date1!=null && (date1.getMonth()+1)>6) {
//																	verifierLandAssignment.setStartingYear(Integer.valueOf(startYear)+1);
//																	verifierLandAssignment.setEndingYear(Integer.valueOf(endYear)+1 );
//																}else {
//																	verifierLandAssignment.setStartingYear(Integer.valueOf(startYear));
//																	verifierLandAssignment.setEndingYear(Integer.valueOf(endYear));	
//																}
//																
//																
//		
//																verifierLandAssignment.setIsActive(Boolean.TRUE);
//																verifierLandAssignment.setIsDeleted(Boolean.FALSE);
//																verifierLandAssignment.setReasonOfAssignment(VerifierReasonOfAssignmentEnum.RANDOM_PICK.getValue());
//																verifierLandAssignment.setReasonOfAssignmentType(VerifierReasonOfAssignmentEnum.RANDOM_PICK);
//																landAssignMentList.add(verifierLandAssignment);
//															});
//											
//														}
//															
//															
//														
//												
//													}	
//												}
//												
//												finalList.add(verifierLandConfiguration);
//										}
//									});
//									end	old  multiple season allocation
									logger.info("end village lgd code "+villageMaster.getVillageLgdCode());
							});
									
//								}
							
//							}
							if(finalList!=null && finalList.size()>0) {
								
								verifierLandConfigurationRepository.saveAll(finalList);
								logger.info("Save ALL Configuration "+yearsWithLimit.get(i).getEndYear());
							}
							if(landAssignMentList!=null && landAssignMentList.size()>0) {
								
								verifierLandAssignmentRepository.saveAll(landAssignMentList);
								logger.info("Save ALL verifier Land assignment "+landAssignMentList.stream().map(x->x.getVerifier().getUserId()));
							}
							
						}
						

						StatusMaster status =statusMasterRepository.findByIsDeletedFalseAndStatusCode(StatusEnum.COMPLETED.getValue());
						logger.info("StatusName "+status.getStatusName()+" "+ new Date());
						surveyVerificationConfigurationMasterAdd.setStatus(status);
						surveyVerificationConfigurationMasterRepository.save(surveyVerificationConfigurationMasterAdd);


						
				
					}
					
				}
						}catch (Exception e) {

							e.printStackTrace();

							}

					}
				}.start();
//				End config

//				surveyVerificationConfigurationMasterRepository.save(master);

			}
			return new ResponseModel(dao, CustomMessages.SUCCESS, CustomMessages.ADD_SUCCESSFULLY,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILURE, CustomMessages.METHOD_POST);
		}
	}

	public ResponseModel updateSurveyVerificationConfiguration(SurveyVerificationConfigurationMasterDAO dao) {
		try {
			SurveyVerificationConfigurationMaster master = surveyVerificationConfigurationMasterRepository
					.findBySurveyVerificationConfigurationMasterId(dao.getSurveyVerificationConfigurationMasterId());

			master.setRandomPickPercentageOfVillages(dao.getRandomPickPercentageOfVillages());
			master.setRandomPickPercentageOfPlots(dao.getRandomPickPercentageOfPlots());
			master.setRandomPickMinimumNumberOfPlots(dao.getRandomPickMinimumNumberOfPlots());

			master.setSurveyRejectedBySupervisorForSecondTime(dao.getSurveyRejectedBySupervisorForSecondTime());
			master.setObjectionRaisedByFarmerAndMarkedBySupervisor(
					dao.getObjectionRaisedByFarmerAndMarkedBySupervisor());

			StateLgdMaster stateLgdMaster = stateLgdMasterRepository.findByStateLgdCode(dao.getStateLgdCode());
			master.setStateLgdMaster(stateLgdMaster);

			Optional<YearMaster> appliedYear = yearRepository.findById(dao.getAppliedYearId());
			master.setAppliedYear(appliedYear.get());

			SowingSeason appliedSeason = seasonMasterRepository.findBySeasonId(dao.getAppliedSeasonId());
			master.setAppliedSeason(appliedSeason);

			SurveyVerificationConfigurationMaster updated = surveyVerificationConfigurationMasterRepository
					.save(master);

			return new ResponseModel(updated, CustomMessages.SUCCESS, CustomMessages.UPDATE_SUCCESSFULLY,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILURE, CustomMessages.METHOD_POST);
		}
	}

	public ResponseModel deleteSurveyVerificationConfiguration(Integer surveyVerificationConfigurationMasterId) {
		try {
			SurveyVerificationConfigurationMaster master = surveyVerificationConfigurationMasterRepository
					.findBySurveyVerificationConfigurationMasterId(surveyVerificationConfigurationMasterId);

			if(master!=null) {
				master.setIsDeleted(Boolean.TRUE);
				SurveyVerificationConfigurationMaster updated = surveyVerificationConfigurationMasterRepository
						.save(master);
				updated.getStateLgdMaster().getStateLgdCode();
				Optional<SurveyVerificationConfigurationMaster>  op=surveyVerificationConfigurationMasterRepository.findByStateLgdMaster_StateLgdCodeAndIsDeletedAndIsActive(updated.getStateLgdMaster().getStateLgdCode(), false,true);
				if(!op.isPresent()) {
					Integer previousEntryId = surveyVerificationConfigurationMasterRepository
							.getPreviousInactiveEntryForGivenState(master.getStateLgdMaster().getStateLgdCode());

					if (previousEntryId != null) {
						surveyVerificationConfigurationMasterRepository
								.activateSurveyVerificationConfiguration(previousEntryId);
					}					
				}

			}


			return new ResponseModel(null, CustomMessages.SUCCESS, CustomMessages.UPDATE_SUCCESSFULLY,
					CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILURE, CustomMessages.METHOD_GET);
		}
	}

	public ResponseModel addSurveyVerificationConfiguration(SurveyVerificationConfigurationMasterDAO dao,
															HttpServletRequest request) {
		try {

			String userId = userService.getUserId(request);
			Long userIdLong = Long.parseLong(userId);

//			List<Long> assignedStateCodes = userVillageMappingRepository.getStateCodesById(userIdLong);
			List<Long> assignedStateCodes = new ArrayList<>();
			String stateLgdCodeConfig = configurationRepository.findByConfigKey("STATE_LGD_CODE").isPresent() ?
					configurationRepository.findByConfigKey("STATE_LGD_CODE").get().getConfigValue() : null;
			if (stateLgdCodeConfig != null) {
				assignedStateCodes.add(Long.valueOf(stateLgdCodeConfig));
			}else {
				return new ResponseModel(null, CustomMessages.CONFIGURATION_ERROR, CustomMessages.GET_DATA_ERROR,
						CustomMessages.CONFIGURATION_ERROR, CustomMessages.METHOD_POST);
			}
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("IST"));

			Integer currentYear = cal.get(Calendar.YEAR);
			SowingSeason currentSeason = generalService.getCurrentSeason();

			for (Long stateLgdCode : assignedStateCodes) {

				SurveyVerificationConfigurationMaster surveyVerificationConfigurationMasterOld = surveyVerificationConfigurationMasterRepository
						.findMasterDataByStateLgdCode(stateLgdCode);
				SurveyVerificationConfigurationMaster surveyVerificationConfigurationMaster = new SurveyVerificationConfigurationMaster();
				surveyVerificationConfigurationMaster.setIsApppliedNextSeason(false);
				surveyVerificationConfigurationMaster.setIsActive(Boolean.TRUE);
				List<Long> oldVillageList=new ArrayList<>();
				if (surveyVerificationConfigurationMasterOld != null) {
					// if state wise entry already exists then deactivate previous entry and add new
					// one
//					surveyVerificationConfigurationMasterOld.setIsActive(Boolean.FALSE);
//					surveyVerificationConfigurationMasterRepository.save(surveyVerificationConfigurationMasterOld);


					if(currentSeason!=null ) {
						oldVillageList=verifierLandConfigurationRepository.getVillagesByFilter(surveyVerificationConfigurationMasterOld.getSurveyVerificationConfigurationMasterId(), currentYear, currentSeason.getSeasonId());
					}

					Optional<SurveyVerificationConfigurationMaster> surveyOp=surveyVerificationConfigurationMasterRepository.findByStateLgdMaster_StateLgdCodeAndIsDeletedAndIsApppliedNextSeason(stateLgdCode, false, true);
					if(surveyOp.isPresent()) {
						SurveyVerificationConfigurationMaster surveyVerificationConfigurationMasterIsApppliedNextSeason=surveyOp.get();

						surveyVerificationConfigurationMasterIsApppliedNextSeason.setIsApppliedNextSeason(false);
						surveyVerificationConfigurationMasterRepository.save(surveyVerificationConfigurationMasterIsApppliedNextSeason);
					}
					surveyVerificationConfigurationMaster.setIsApppliedNextSeason(true);
					surveyVerificationConfigurationMaster.setIsActive(Boolean.FALSE);

					List<Long> seasonIds=seasonMasterRepository.getSeasonIdsNotIn(surveyVerificationConfigurationMasterOld.getAppliedSeason().getSeasonId());
					if(seasonIds!=null && seasonIds.size()>0) {
						List<SowingSeason> upComingSeasons=seasonMasterRepository.findBySeasonIdIn(seasonIds);
						if(seasonIds!=null && seasonIds.size()>0) {
							surveyVerificationConfigurationMaster.setAppliedSeason(upComingSeasons.get(0));
						}

					}


				}else {
					SowingSeason appliedSeason = seasonMasterRepository.findBySeasonId(dao.getAppliedSeasonId());
					surveyVerificationConfigurationMaster.setAppliedSeason(appliedSeason);
				}
				List<Long> villageLgdCodeOld=oldVillageList;

				surveyVerificationConfigurationMaster.setRandomPickPercentageOfVillages(dao.getRandomPickPercentageOfVillages());
				surveyVerificationConfigurationMaster.setRandomPickPercentageOfPlots(dao.getRandomPickPercentageOfPlots());
				surveyVerificationConfigurationMaster.setRandomPickMinimumNumberOfPlots(dao.getRandomPickMinimumNumberOfPlots());

				surveyVerificationConfigurationMaster.setSurveyRejectedBySupervisorForSecondTime(dao.getSurveyRejectedBySupervisorForSecondTime());
				surveyVerificationConfigurationMaster.setObjectionRaisedByFarmerAndMarkedBySupervisor(
						dao.getObjectionRaisedByFarmerAndMarkedBySupervisor());

				StateLgdMaster stateLgdMaster = stateLgdMasterRepository.findByStateLgdCode(stateLgdCode);
				surveyVerificationConfigurationMaster.setStateLgdMaster(stateLgdMaster);

				Optional<YearMaster> appliedYear = yearRepository.findById(dao.getAppliedYearId());
				surveyVerificationConfigurationMaster.setAppliedYear(appliedYear.get());

//				Year start

				Integer currentMonth = cal.get(Calendar.MONTH) + 1;
				Integer currentYeaTemp=currentYear;
				if(currentMonth>6) {
					currentYeaTemp=currentYear+1;
				}
				YearMaster yearMaster=yearRepository.findByEndYear(String.valueOf(currentYeaTemp));
				if(yearMaster==null)  {
					yearMaster=yearRepository.findByEndYear(String.valueOf(cal.get(Calendar.YEAR)));
				}
				surveyVerificationConfigurationMaster.setAppliedYear(yearMaster);
//				Year end
				surveyVerificationConfigurationMaster.setCreatedOn(new Date());

				surveyVerificationConfigurationMaster.setIsDeleted(Boolean.FALSE);
				StatusMaster status =statusMasterRepository.findByIsDeletedFalseAndStatusCode(StatusEnum.PENDING.getValue());
				surveyVerificationConfigurationMaster.setStatus(status);
				SurveyVerificationConfigurationMaster surveyVerificationConfigurationMasterAdd=surveyVerificationConfigurationMasterRepository.save(surveyVerificationConfigurationMaster);
				Double villageAssignmentPercentage = surveyVerificationConfigurationMaster.getRandomPickPercentageOfVillages();
				Long plotAssignmentPercentage =Math.round( surveyVerificationConfigurationMaster.getRandomPickPercentageOfPlots());

				Integer plotAssignmentCount = surveyVerificationConfigurationMaster.getRandomPickMinimumNumberOfPlots();
//				Start config
//				List<UserVillageMapping> villagecods = userVillageMappingRepository.findByUserMaster_UserIdAndIsActiveAndIsDeleted(userIdLong,true,false);

				/// add state filter also
//				List<Long> villagecode = userVillageMappingRepository.getVillageCodesByUserId(userIdLong);

				new Thread() {

					public void run() {
						try {
							String createdModifiedIp = null;
							try {
								createdModifiedIp = InetAddress.getLocalHost().getHostAddress();
							}catch(Exception e) {
								e.printStackTrace();
							}

							logger.info("-----------------------------------------------------------------------------------");
							logger.info("surveyVerificationConfigMaster id : " + surveyVerificationConfigurationMasterAdd.getSurveyVerificationConfigurationMasterId());
							logger.info("userId : " + userIdLong);
							logger.info("villageAssignmentPercentage : "+villageAssignmentPercentage);
							logger.info("plotAssignmentPercentage : "+plotAssignmentPercentage);
							logger.info("plotAssignmentCount : "+plotAssignmentCount);
							logger.info("createdModifiedIp : "+createdModifiedIp);
							logger.info("startYear : "+surveyVerificationConfigurationMasterAdd.getAppliedSeason().getStartingYear());
							logger.info("endYear : "+surveyVerificationConfigurationMasterAdd.getAppliedSeason().getEndingYear());
							logger.info("seasonId : "+surveyVerificationConfigurationMasterAdd.getAppliedSeason().getSeasonId());
							logger.info("-----------------------------------------------------------------------------------");

							//	start survey verification config add process

							Object status = verifierLandConfigurationRepository.addSurveyVerifierConfiguration(
									surveyVerificationConfigurationMasterAdd.getSurveyVerificationConfigurationMasterId(), userIdLong,
									villageAssignmentPercentage,plotAssignmentPercentage,plotAssignmentCount,createdModifiedIp,
									Integer.parseInt(generalService.getCurrentYear().getStartYear()),
									Integer.parseInt(generalService.getCurrentYear().getEndYear()),
									surveyVerificationConfigurationMasterAdd.getAppliedSeason().getSeasonId()
							);
							logger.info("process ends");

							//	end survey verification config add process


						}catch (Exception e) {

							e.printStackTrace();

						}

					}
				}.start();
//				End config

			}
			return new ResponseModel(dao, CustomMessages.SUCCESS, CustomMessages.ADD_SUCCESSFULLY,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILURE, CustomMessages.METHOD_POST);
		}
	}
}
