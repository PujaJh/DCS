package com.amnex.agristack.service;

import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.*;
import com.amnex.agristack.dao.common.Mail;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.*;
import com.amnex.agristack.repository.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.amnex.agristack.Enum.DepartmentEnum;
import com.amnex.agristack.Enum.RoleEnum;
import com.amnex.agristack.Enum.StatusEnum;
import com.amnex.agristack.Enum.VerifierReasonOfAssignmentEnum;
import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.notifications.NotificationThread;
import com.amnex.agristack.utils.CommonUtil;
import com.amnex.agristack.utils.Constants;
import com.amnex.agristack.utils.CustomMessages;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

@Service
public class VerifierService {

	@Autowired
	ExceptionLogService exceptionLogService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	UserMasterRepository userRepository;

	@Autowired
	UserAvailabilityRepository userAvailabilityRepository;

	@Autowired
	StateLgdMasterService stateService;

	@Autowired
	DistrictLgdMasterService districtService;
	@Autowired
	SubDistrictLgdMasterService subDistrictService;

	@Autowired
	VillageLgdMasterService villageService;

	@Autowired
	DepartmentRepository departmentRepository;

	@Autowired
	DesignationRepository designationRepository;

	@Autowired
	RoleMasterRepository roleRepository;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private BankRepository bankRepository;

	@Autowired
	private UserBankDetailRepository userBankDetailRepository;

	@Autowired
	private StatusMasterRepository statusRepository;

	@Autowired
	private GeneralService generalService;

	@Autowired
	private SeasonMasterRepository seasonMasterRepository;

	@Autowired
	private UserLandAssignmentRepository userLandAssignmentRepository;

	@Autowired
	MessageConfigurationService messageConfigurationService;

	@Autowired
	private MessageCredentialRepository messageCredentialRepository;

	@Autowired
	private MessageConfigurationRepository messageConfigurationRepository;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private VillageLgdMasterRepository villageLgdMasterRepository;

	@Autowired
	private UserVillageMappingRepository userVillageMappingRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private VerifierLandAssignmentRepository verifierLandAssignmentRepository;

	@Autowired
	private VerifierLandConfigurationRepository verifierLandConfigurationRepository;

	@Autowired
	private StatusMasterRepository statusMasterRepository;

	@Autowired
	private LandParcelSurveyMasterRespository landParcelSurveyMasterRespository;

     Logger logger= Logger.getLogger(VerifierService.class.getName());

     public ResponseModel addVerifier(VerifierInputDAO inputDAO, HttpServletRequest request) {
		ResponseModel responseModel = new ResponseModel();
		try {
			if (Objects.nonNull(inputDAO)) {
				Optional<UserMaster> opUserMob = userRepository.findByUserMobileNumber(inputDAO.getUserMobileNumber());
				if (Objects.nonNull(inputDAO.getUserAadhaarHash())) {
					List<UserMaster> opUserAadhar = userRepository
							.findByUserAadhaarHash(inputDAO.getUserAadhaarHash());
					if (!opUserAadhar.isEmpty()) {
						return new ResponseModel(opUserAadhar.get(0).getUserAadhaarHash(),
								"User " + CustomMessages.RECORD_ALREADY_EXIST, CustomMessages.GET_DATA_ERROR,
								CustomMessages.FAILED, CustomMessages.METHOD_POST);
					}
				}
				Optional<UserMaster> opUserEmail = userRepository
						.findByUserEmailAddress(inputDAO.getUserEmailAddress());
				if (opUserMob.isPresent()) {
					return new ResponseModel(opUserMob.get().getUserMobileNumber(),
							"User " + CustomMessages.RECORD_ALREADY_EXIST, CustomMessages.GET_DATA_ERROR,
							CustomMessages.FAILED, CustomMessages.METHOD_POST);
				} else if (opUserEmail.isPresent()) {
					return new ResponseModel(opUserMob.get().getUserEmailAddress(),
							"User " + CustomMessages.RECORD_ALREADY_EXIST, CustomMessages.GET_DATA_ERROR,
							CustomMessages.FAILED, CustomMessages.METHOD_POST);
				} else {
					UserMaster verifier = new UserMaster();
					BeanUtils.copyProperties(inputDAO, verifier);
					// if mobile or email verified
					verifier.setIsEmailVerified(
							Objects.isNull(inputDAO.getIsEmailVerified()) ? false : inputDAO.getIsEmailVerified());
					verifier.setIsMobileVerified(
							Objects.isNull(inputDAO.getIsMobileVerified()) ? false : inputDAO.getIsMobileVerified());
					verifier.setIsAadhaarVerified(
							Objects.isNull(inputDAO.getIsAadhaarVerified()) ? false : inputDAO.getIsAadhaarVerified());

					// set department and designation start
					if (Objects.nonNull(inputDAO.getDepartmentId())) {
						DepartmentMaster department = departmentRepository.findById(inputDAO.getDepartmentId())
								.orElse(null);
						verifier.setDepartmentId(department);
					}
					if (Objects.nonNull(inputDAO.getDesignationId())) {
						DesignationMaster designation = designationRepository.findById(inputDAO.getDesignationId())
								.orElse(null);
						verifier.setDesignationId(designation);
					}
					// Assign verifier Role
//					assignVerifierRole(verifier);
					if(inputDAO.getUserTypeId()!=null) {
						RoleMaster role = roleRepository.findByRoleId(inputDAO.getUserTypeId());
						if (Objects.nonNull(role)) {
							verifier.setRoleId(role);
						}
					}else {
						RoleMaster role = roleRepository.findByCode(RoleEnum.VERIFIER.toString()).orElse(null);
						if (Objects.nonNull(role)) {
							verifier.setRoleId(role);
						}
					}
					verifier.setGovernmentId(inputDAO.getGovernmentId());
//					String uniqueUsername = generateVerifierUniqueUserName(verifier);
					String prId = generateVerifierUniqueUserName(verifier);
					verifier.setUserName(prId);
//					String tempPassword = generalService.createTempPassword();
//					verifier.setUserPassword(Objects.isNull(inputDAO.getUserPassword())? encoder.encode(tempPassword) : encoder.encode(inputDAO.getUserPassword()));
//					verifier.setIsPasswordChanged(false);
					verifier.setPinCode(inputDAO.getPinCode());
					verifier.setUserFullName(verifier.getUserFirstName() + " " + verifier.getUserLastName());
					verifier.setIsActive(Boolean.TRUE);
					verifier.setIsDeleted(Boolean.FALSE);
					verifier.setCreatedOn(new Timestamp(new Date().getTime()));
					verifier.setCreatedIp(CommonUtil.getRequestIp(request));
					if (!inputDAO.getSelfRegistered()) {
						verifier.setCreatedBy(CustomMessages.getUserId(request, jwtTokenUtil));
						verifier.setUserStatus(statusRepository
								.findByIsDeletedFalseAndStatusCode(StatusEnum.APPROVED.getValue()).getStatusCode());
					}


//					password change start
					String randomPassword = CommonUtil.GeneratePassword(8);
					verifier.setUserPasswordText(randomPassword);
					verifier.setUserPassword(encoder.encode(randomPassword));
					verifier.setIsPasswordChanged(false);
					verifier.setLastPasswordChangedDate(new Date());

					Gson gson = new Gson();
					JsonArray passwordHistory = new JsonArray();
					if (verifier.getPasswordHistory() != null) {
						passwordHistory = gson.fromJson(verifier.getPasswordHistory().toString(), JsonArray.class);
					}
					passwordHistory.add(verifier.getUserPassword());
					if (passwordHistory.size() > 3) {
						passwordHistory.remove(0);
					}
//					password change end
					UserMaster newVerifier = userRepository.save(verifier);

					if(inputDAO.getVerificationVillageLGDCode()!=null && inputDAO.getVerificationVillageLGDCode().size()>0) {

						List<VillageLgdMaster> villageCodes=villageLgdMasterRepository.findByVillageLgdCodeIn(inputDAO.getVerificationVillageLGDCode());
						if(villageCodes!=null && villageCodes.size()>0) {
							List<UserVillageMapping> userVillageMappingList=new ArrayList<>();
							villageCodes.forEach(action->{
								UserVillageMapping userVillageMapping=new UserVillageMapping();
								userVillageMapping.setIsActive(Boolean.TRUE);
								userVillageMapping.setIsDeleted(Boolean.FALSE);
								userVillageMapping.setCreatedIp(CommonUtil.getRequestIp(request));
								userVillageMapping.setModifiedIp(CommonUtil.getRequestIp(request));
								userVillageMapping.setUserMaster(newVerifier);
								userVillageMapping.setVillageLgdMaster(action);
								userVillageMappingList.add(userVillageMapping);
//								verifier

							});
							userVillageMappingRepository.saveAll(userVillageMappingList);

						}
//

//						Start assign task to verifier
						List<VerifierLandAssignment> finalList=new ArrayList<>();

						new Thread() {

							public void run() {
								try {

									StatusMaster statusMaster=statusMasterRepository.findByIsDeletedFalseAndStatusCode(StatusEnum.PENDING.getValue());

									List<VerifierLandConfiguration> verifierLandConfigurationList=verifierLandConfigurationRepository.
											findByConfigId_IsActiveAndConfigId_IsDeletedAndIsActiveAndIsDeletedAndConfigId_StateLgdMaster_StateLgdCodeAndVillageLgdCode_VillageLgdCodeIn
											(true, false, true, false,inputDAO.getVerificationStateLGDCode().longValue() ,inputDAO.getVerificationVillageLGDCode());

									if(verifierLandConfigurationList!=null && verifierLandConfigurationList.size()>0) {
										List<Long> verifierLandConfigurationIds=verifierLandConfigurationList.stream().map(x->x.getVerifierLandConfigurationId()).collect(Collectors.toList());

										List<YearDistinctDAO>	 yearSeasonDistinct=verifierLandConfigurationRepository.getYearAndSeasonDistinct(verifierLandConfigurationIds);


										if(yearSeasonDistinct!=null && yearSeasonDistinct.size()>0) {
											List<UserAvailability> userAvailabilityList=new ArrayList<>();
											yearSeasonDistinct.forEach(action->{
												UserAvailability availability = new UserAvailability();
												availability.setUserId(newVerifier);
												availability.setIsAvailable(true);
												availability.setIsActive(true);
												availability.setIsDeleted(false);
												availability.setSeasonId(action.getSeasonId());
												availability.setSeasonStartYear(action.getStartYear()+"");
												availability.setSeasonEndYear(action.getEndYear()+"");
												availability.setCreatedIp(CommonUtil.getRequestIp(request));
												availability.setCreatedBy(CustomMessages.getUserId(request, jwtTokenUtil));
												userAvailabilityList.add(availability);
											});
											if(userAvailabilityList!=null && userAvailabilityList.size()>0) {
												userAvailabilityRepository.saveAll(userAvailabilityList);
												logger.info("availability Added");
											}else {
												addCurrentSeasonAvailability(newVerifier, request);
											}


										}

//

									}else {
										addCurrentSeasonAvailability(newVerifier, request);
									}


									List<VerifierLandAssignment> landAssignmentList=verifierLandAssignmentRepository.findByVillageLgdCode_villageLgdCodeInAndIsActiveAndIsDeleted(inputDAO.getVerificationVillageLGDCode(), true, false);



									verifierLandConfigurationList.forEach(action->{

//										Set<String> plots=action.getLandPlot().stream().map(x->x.getFarmlandId()).collect(Collectors.toSet());
//										logger.info(action.getSeason().getSeasonName()+" all plots "+plots);
//										List<String> ids=landAssignmentList.stream().map(v->(v.getFarmlandId())).collect(Collectors.toList());
//										logger.info("landAssignment plots "+ids);
//
////										landAssignmentList.stream().filter(v->!plots.contains(v.getLandParcelId())).collect(Collectors.toList());
//										List<String> remaningPlots=plots.stream().filter(v->!ids.contains(v)).collect(Collectors.toList());
//										logger.info("remaningPlots "+remaningPlots);
//
//										if(remaningPlots!=null && remaningPlots.size()>0) {
//
//
//
//											remaningPlots.forEach(rPlot->{
//
//												VerifierLandAssignment verifierLandAssignment = new VerifierLandAssignment();
//
//												verifierLandAssignment.setVerifier(newVerifier);
//												verifierLandAssignment.setVillageLgdCode(action.getVillageLgdCode());
//												verifierLandAssignment.setSeason(action.getSeason());
//												verifierLandAssignment.setStatus(statusMaster);
//
//												verifierLandAssignment.setFarmlandId(rPlot);
//												List<FarmlandPlotRegistry> parcelIds=action.getLandPlot().stream().filter(x->x.getFarmlandId().equals(rPlot)).collect(Collectors.toList());
//												if(parcelIds!=null && parcelIds.size()>0) {
//													verifierLandAssignment.setLandParcelId(parcelIds.get(0).getLandParcelId());
//												}
//
//
//												verifierLandAssignment.setStartingYear(action.getStartingYear());
//												verifierLandAssignment.setEndingYear(action.getEndingYear());
//
////
////												Date date1 = null;
////												try {
////													date1 = new SimpleDateFormat("dd-MM-yyyy").parse(action.getSeason().getStartingMonth());
////												} catch (ParseException e) {
////													// TODO Auto-generated catch block
////													e.printStackTrace();
////												}
////												if(date1!=null && (date1.getMonth()+1)>6) {
////													verifierLandAssignment.setStartingYear(Integer.valueOf(action.getStartingYear())+1);
////													verifierLandAssignment.setEndingYear(Integer.valueOf(action.getEndingYear())+1 );
////												}else {
////													verifierLandAssignment.setStartingYear(Integer.valueOf(action.getStartingYear()));
////													verifierLandAssignment.setEndingYear(Integer.valueOf(action.getEndingYear()));
////												}
//
//												verifierLandAssignment.setIsActive(Boolean.TRUE);
//												verifierLandAssignment.setIsDeleted(Boolean.FALSE);
//												verifierLandAssignment.setReasonOfAssignment(VerifierReasonOfAssignmentEnum.RANDOM_PICK.getValue());
//												verifierLandAssignment.setReasonOfAssignmentType(VerifierReasonOfAssignmentEnum.RANDOM_PICK);
//												finalList.add(verifierLandAssignment);
//											});
//										}
//										logger.info("===========================================");
//

										if(inputDAO.getUserTypeId()!=null &&
												!(Long.valueOf(RoleEnum.INSPECTION_OFFICER.getValue())).equals(inputDAO.getUserTypeId())) {
											List<LandParcelSurveyMaster> lpsmList = landParcelSurveyMasterRespository.findLandParcelOfSecondRejectSurvey(action.getSeason().getSeasonId(), action.getStartingYear(),
													action.getEndingYear(), action.getVillageLgdCode().getVillageLgdCode());

											if (!lpsmList.isEmpty()) {
												List<VerifierLandAssignment> verifierLandAssignments = new ArrayList<>();
												for (LandParcelSurveyMaster lpsm : lpsmList) {
													Boolean isExistVla = verifierLandAssignmentRepository.findByFarmlandIdAndSeasonAndStartYearAndEndYear(lpsm.getParcelId().getFarmlandId(),
															action.getSeason().getSeasonId(), action.getStartingYear(), action.getEndingYear());
													if (Boolean.FALSE.equals(isExistVla)) {
														VerifierLandAssignment verifierLandAssignment = new VerifierLandAssignment();
														verifierLandAssignment.setVerifier(newVerifier);
														verifierLandAssignment.setVillageLgdCode(action.getVillageLgdCode());
														verifierLandAssignment.setSeason(action.getSeason());
														verifierLandAssignment.setStatus(statusMaster);
														verifierLandAssignment.setStartingYear(action.getStartingYear());
														verifierLandAssignment.setEndingYear(action.getEndingYear());
														verifierLandAssignment.setIsActive(true);
														verifierLandAssignment.setIsDeleted(false);
														verifierLandAssignment.setReasonOfAssignment(VerifierReasonOfAssignmentEnum.SUPERVISOR_REJECTION.getValue());
														verifierLandAssignment.setReasonOfAssignmentType(VerifierReasonOfAssignmentEnum.SUPERVISOR_REJECTION);
														verifierLandAssignment.setCreatedOn(new Timestamp(new Date().getTime()));
														verifierLandAssignment.setModifiedOn(new Timestamp(new Date().getTime()));
														verifierLandAssignment.setLandParcelId(lpsm.getParcelId().getFarmlandPlotRegistryId() + "");
														verifierLandAssignment.setFarmlandId(lpsm.getParcelId().getFarmlandId());
														verifierLandAssignment.setIsSecondTimeRejected(Boolean.TRUE);
														verifierLandAssignments.add(verifierLandAssignment);
													}
												}
												if (!verifierLandAssignments.isEmpty()) {
													verifierLandAssignmentRepository.saveAll(verifierLandAssignments);
												}
											}

										}

									});



									if(finalList!=null && finalList.size()>0) {
										verifierLandAssignmentRepository.saveAll(finalList);
										logger.info("Assign all plots successfully.");
									}
								}catch (Exception e) {

									e.printStackTrace();

									}

							}
						}.start();

//						End assign task to verifier
					}
					if (inputDAO.getSelfRegistered()) {
						verifier.setUserStatus(statusRepository
								.findByIsDeletedFalseAndStatusCode(StatusEnum.APPROVED.getValue()).getStatusCode());
						verifier.setCreatedBy(verifier.getUserId().toString());
//						addVerifierBankDetail(verifier, inputDAO);
						DepartmentMaster department = departmentRepository
								.findByDepartmentType(DepartmentEnum.PRIVATE_RESIDENT.getValue()).orElse(null);
						verifier.setDepartmentId(department);
						verifier = userRepository.save(verifier);
					}
//					addCurrentSeasonAvailability(verifier, request);
//					messageConfigurationService.sendMessageToWebSurveyor("Verifier", "Mobile Number", inputDAO.getUserMobileNumber(), Objects.isNull(inputDAO.getUserPassword())? tempPassword : inputDAO.getUserPassword(), inputDAO.getUserMobileNumber());
					if (verifier.getUserMobileNumber() != null && verifier.getUserEmailAddress() != null) {

						sendMobileMessage(verifier);
						sendEmailMessage(verifier);
					}
					inputDAO.setUserId(verifier.getUserId());


				}
			}
			return new ResponseModel(inputDAO.getUserId(), CustomMessages.RECORD_ADD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			exceptionLogService.addException(e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName(),
					e, null);
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}
	private void sendMobileMessage(UserMaster userMaster) {

		MessageCredentialMaster messageCredentialMaster = messageCredentialRepository
				.findByMessageCredentialType("MOBILE").get();
		// findByTemplateId
//		MessageConfigurationMaster messageConfiguartionMaster = messageConfigurationRepository
//				.findByTemplateId(Constants.USERID_SMS_TEMPLATE_ID).get();

		MessageConfigurationMaster messageConfiguartionMaster = messageConfigurationRepository
				.findByTemplateId(Constants.NEW_USERID_SMS_TEMPLATE_ID).get();

		// DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy
		// HH:mm:ss");

		String template = messageConfiguartionMaster.getTemplate();
//		String test = template.replace("{$1}", userMaster.getUserName());
		String test = template.replace("{$1}", userMaster.getUserMobileNumber());
		String test1 = test.replace("{$2}", userMaster.getUserPasswordText());
//		String sendOTPurl = messageCredentialMaster.getHost() + "authkey=" + messageCredentialMaster.getPassword()
//				+ "&mobiles=" + userMaster.getUserMobileNumber() + "&message=" + test1 + "&sender="
//				+ messageCredentialMaster.getUserName() + "&route=" + messageCredentialMaster.getRoute() + "&unicode="
//				+ messageCredentialMaster.getUniCode();
//
//		String response = new RestTemplate().getForObject(sendOTPurl, String.class);
//		System.out.print(response);


		messageConfigurationService.sendOTP(messageCredentialMaster.getHost(), messageCredentialMaster.getUserName(), messageCredentialMaster.getPassword(), userMaster.getUserMobileNumber(), test1);
	}

	public void sendEmailMessage(UserMaster userMaster) {
		try {
			// Get credential for username
			Optional<MessageCredentialMaster> credentialDetails = messageCredentialRepository
					.findByMessageCredentialType(Constants.EMAIL_TEMPLATE);

			// Get configuration details
			Optional<MessageConfigurationMaster> configuraitonDetails = messageConfigurationRepository
					.findByTemplateType(Constants.NEW_EMAIL_PASSWORD_TEMPLATE);

			// Initialize velocity engine
			VelocityEngine velocityEngine = initializeVelocity();

			String template = configuraitonDetails.get().getTemplate();
			String test = template.replace("{$1}", userMaster.getUserMobileNumber());
			String test1 = test.replace("{$2}", userMaster.getUserPasswordText());

			// Add template to repository
			StringResourceRepository repository = StringResourceLoader.getRepository();
			repository.putStringResource("NEW_EMAIL_PASSWORD_TEMPLATE", test1);

			// Set parameters
			VelocityContext context = new VelocityContext();
			context.put("userName", userMaster.getUserFullName());
			context.put("PASSWORD", userMaster.getUserPasswordText());

			// Process the template
			StringWriter writer = new StringWriter();
			velocityEngine.getTemplate("NEW_EMAIL_PASSWORD_TEMPLATE").merge(context, writer);

			String[] emailTo = new String[] { userMaster.getUserEmailAddress() };
			String[] emailcc = new String[] {};
			if (configuraitonDetails.get().getCcList() != null) {
				emailcc = configuraitonDetails.get().getCcList().trim().split(",");
			}

			Mail mail = new Mail();
			mail.setMailFrom(credentialDetails.get().getUserName());
			mail.setMailTo(emailTo);
			mail.setMailCc(emailcc);
			mail.setMailSubject(configuraitonDetails.get().getEmailSubject());
			mail.setMailContent(writer.toString());
			try {
				new NotificationThread(mail, mailSender).start();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private VelocityEngine initializeVelocity() {
		// Initialize the engine
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(Velocity.RESOURCE_LOADER, "string");
		velocityEngine.setProperty("resource.loader.string.class", StringResourceLoader.class.getName());
		velocityEngine.init();
		return velocityEngine;
	}

	@Transactional
	private UserMaster addVerifierBankDetail(UserMaster verifier, VerifierInputDAO verifierInput) {
		Optional<BankMaster> bank = bankRepository.findByBankIdAndIsDeletedFalse(verifierInput.getUserBankId());
		if (bank.isPresent()) {
			BankMaster bankMaster = bank.get();
			UserBankDetail bankDetail = new UserBankDetail();
			BeanUtils.copyProperties(verifierInput, bankDetail);
			bankDetail.setUserBankName(bankMaster.getBankName());
			bankDetail.setBankId(bankMaster);
			bankDetail.setUserId(verifier);
			bankDetail.setCreatedIp(verifier.getCreatedIp());
			bankDetail.setCreatedBy(verifier.getCreatedBy());
			bankDetail.setIsActive(true);
			bankDetail.setIsDeleted(false);
			userBankDetailRepository.save(bankDetail);
		}
		return verifier;
	}

	@Transactional
	private UserMaster addVerifierAvailability(UserMaster verifier, HttpServletRequest request) {
		UserAvailability availability = new UserAvailability();
		availability.setUserId(verifier);
		if (!verifier.getIsAvailable()) {
			availability.setIsAvailable(false);
			availability.setIsActive(false);
			availability.setIsDeleted(true);
		} else {

		}
		SowingSeason season = generalService.getCurrentSeason();
		availability.setSeasonId(Objects.nonNull(season) ? season.getSeasonId() : null);
		availability.setSeasonStartYear(season.getStartingYear().toString());
		availability.setSeasonEndYear(season.getEndingYear().toString());
		availability.setCreatedIp(CommonUtil.getRequestIp(request));
		availability.setCreatedBy(CustomMessages.getUserId(request, jwtTokenUtil));
		userAvailabilityRepository.save(availability);
		return verifier;
	}

	@Transactional
	protected UserMaster assignVerifierRole(UserMaster user) {
		RoleMaster role = roleRepository.findByCode(RoleEnum.VERIFIER.toString()).orElse(null);
		if (Objects.nonNull(role)) {
			user.setRoleId(role);
		}
		return new UserMaster();
	}

	@Transactional
	private UserMaster addCurrentSeasonAvailability(UserMaster verifier, HttpServletRequest request) {
		UserAvailability availability = new UserAvailability();
		availability.setUserId(verifier);
		availability.setIsAvailable(true);
		availability.setIsActive(true);
		availability.setIsDeleted(false);
		SowingSeason season = generalService.getCurrentSeason();
		availability.setSeasonId(Objects.nonNull(season) ? season.getSeasonId() : null);
		availability.setSeasonStartYear(season.getStartingYear().toString());
		availability.setSeasonEndYear(season.getEndingYear().toString());
		availability.setCreatedIp(CommonUtil.getRequestIp(request));
		availability.setCreatedBy(CustomMessages.getUserId(request, jwtTokenUtil));
		userAvailabilityRepository.save(availability);
		return verifier;
	}

	public ResponseModel getAllVerifier(VerifierInputDAO requestInput, HttpServletRequest request) {
		ResponseModel responseModel = new ResponseModel();
		List<VerifierOutputDAO> verifierList = new ArrayList<>();
		List<UserMaster> users = new ArrayList<>();
		SowingSeason season = new SowingSeason();
		try {

//			List<UserAvailability> availabilities = userAvailabilityRepository
//					.findAllVerifierSeasonStartYearAndSeasonEndYearAndSeasonId(requestInput.getStartingYear(),
//							requestInput.getEndingYear(), requestInput.getSeasonId(),
//							Long.valueOf(RoleEnum.VERIFIER.getValue()));
			List<Long> departmentIds = ( requestInput.getDepartmentIds() == null || requestInput.getDepartmentIds().isEmpty()
					) ? null : requestInput.getDepartmentIds();

//			List<UserAvailability> availabilities = userAvailabilityRepository
//					.findAllVerifierSeasonStartYearAndSeasonEndYearAndSeasonIdAndDepartmentIds(
//							requestInput.getStartingYear(), requestInput.getEndingYear(), requestInput.getSeasonId(),
//							Long.valueOf(RoleEnum.VERIFIER.getValue()), departmentIds);
			List<Long> roleIds=new ArrayList<>();
			if(requestInput.getUserTypeIds()!=null && requestInput.getUserTypeIds().size()>0) {
				roleIds.addAll(requestInput.getUserTypeIds());
			}else {
				roleIds.add(Long.valueOf(RoleEnum.VERIFIER.getValue()));
				roleIds.add(Long.valueOf(RoleEnum.INSPECTION_OFFICER.getValue()));
			}

			List<UserAvailability> availabilities = userAvailabilityRepository
					.findAllVerifierSeasonStartYearAndSeasonEndYearAndRoleIdInAndSeasonIdAndDepartmentIds(
							requestInput.getStartingYear(), requestInput.getEndingYear(), requestInput.getSeasonId(),
							roleIds, departmentIds);

			if (Objects.nonNull(availabilities) && availabilities.size() > 0) {
				season = seasonMasterRepository.findBySeasonId(requestInput.getSeasonId());
				List<UserMaster> tempuserList = new ArrayList<>();
				availabilities.stream().forEach(ele -> {
					if (Objects.nonNull(ele.getUserId().getDepartmentId())
//							&& (
////										requestInput.getDepartmentId().equals(ele.getUserId().getDepartmentId().getDepartmentId()) ||
//					requestInput.getDepartmentIds().contains(ele.getUserId().getDepartmentId().getDepartmentId()))
					) {
						tempuserList.add(ele.getUserId());
						ele.getUserId().setIsAvailable(ele.getIsAvailable());
					}
				});
				users = tempuserList.stream().distinct().collect(Collectors.toList());
			}
			List<UserMaster> verifiers = new ArrayList<>();
			String userId = userService.getUserId(request);
			Long id = Long.parseLong(userId);
			List<Long> vCodes=userVillageMappingRepository.getVillageCodesByUserId(id);
			if(vCodes!=null && vCodes.size()>0) {
				requestInput.setVillageLgdCodeList(vCodes.stream().map(x->x.intValue()).collect(Collectors.toList()));
			}
			if(Objects.nonNull(requestInput.getVillageLgdCodeList())) {
				users.stream().forEach(userMaster -> {
					if(requestInput.getVillageLgdCodeList().contains(userMaster.getUserVillageLGDCode())) {
						verifiers.add(userMaster);
					}
				});
				verifierList = getVerifierFromUserMaster(verifiers, season);
			}
//			verifierList = getVerifierFromUserMaster(users, season);
			return new ResponseModel(verifierList, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	public List<VerifierOutputDAO> getVerifierFromUserMaster(List<UserMaster> users, SowingSeason season) {
		List<VerifierOutputDAO> verifierList = new ArrayList<>();
		for (UserMaster user : users) {
			if (!user.getIsDeleted()) {
				VerifierOutputDAO verifier = new VerifierOutputDAO();
				BeanUtils.copyProperties(user, verifier);
				getVerifierBoundaryDetail(verifier);
				if (Objects.nonNull(verifier.getDepartmentId())) {
					DepartmentMaster department = new DepartmentMaster(verifier.getDepartmentId().getDepartmentId(),
							verifier.getDepartmentId().getDepartmentName(),
							verifier.getDepartmentId().getDepartmentType());
					verifier.setDepartmentId(department);
				}
				verifier.setUserStatus(
						statusRepository.findByIsDeletedFalseAndStatusCode(user.getUserStatus()).getStatusName());
				verifier.setIsAvailable(user.getIsAvailable());
				if (Objects.nonNull(verifier.getDesignationId())) {
					DesignationMaster designation = new DesignationMaster(
							verifier.getDesignationId().getDesignationId(),
							verifier.getDesignationId().getDesignationName());
					verifier.setDesignationId(designation);
				}
				UserBankDetail bankDetail = userBankDetailRepository.findByUserId_UserId(user.getUserId());
				if (Objects.nonNull(bankDetail)) {
					verifier.setUserBankDetail(
							new UserBankDetail(bankDetail.getUserBankName(), bankDetail.getUserBranchCode(),
									bankDetail.getUserIfscCode(), bankDetail.getUserBankAccountNumber(),bankDetail.getBankId().getBankId() ));
				}
				verifier.setSeasonName(season.getSeasonName());
				verifier.setRoleId(user.getRoleId().getRoleId());
				verifierList.add(verifier);
			}
		}

		return verifierList;
	}

	public VerifierOutputDAO getVerifierBoundaryDetail(VerifierOutputDAO user) {
		if (Objects.nonNull(user.getUserStateLGDCode())) {
			StateLgdMaster state = stateService.getStateByLGDCode(Long.valueOf(user.getUserStateLGDCode().longValue()));
			DistrictLgdMaster district = districtService
					.getDistrictByLGDCode(user.getUserDistrictLGDCode().longValue());
			SubDistrictLgdMaster subDistrict = subDistrictService
					.getSubDistrictByLGDCode(user.getUserTalukaLGDCode().longValue());
			VillageLgdMaster village = villageService.getVillageByLGDCode(user.getUserVillageLGDCode().longValue());
			user.setStateName(Objects.nonNull(state) ? state.getStateName() : "");
			user.setDistrictName(Objects.nonNull(district) ? district.getDistrictName() : "");
			user.setStateName(Objects.nonNull(subDistrict) ? subDistrict.getSubDistrictName() : "");
			user.setStateName(Objects.nonNull(village) ? village.getVillageName() : "");
		}

		return user;
	}

	public VerifierOutputDAO getVerifierBoundaryUsingVillage(VerifierOutputDAO user) {
		if (Objects.nonNull(user.getUserVillageLGDCode())) {
			VillageLgdMaster village = villageService.getVillageByLGDCode(user.getUserVillageLGDCode().longValue());
			if (Objects.nonNull(village)) {
				user.setStateName(Objects.nonNull(village.getStateLgdCode().getStateId())
						? village.getStateLgdCode().getStateName()
						: "");
				user.setDistrictName(Objects.nonNull(village.getDistrictLgdCode().getDistrictId())
						? village.getDistrictLgdCode().getDistrictName()
						: "");
				user.setTalukaName(Objects.nonNull(village.getSubDistrictLgdCode().getSubDistrictId())
						? village.getSubDistrictLgdCode().getSubDistrictName()
						: "");
				user.setVillageName(Objects.nonNull(village) ? village.getVillageName() : "");
			}
		}
		return user;
	}

	public ResponseModel deleteVerifier(Long verifierId, HttpServletRequest request) {
		try {
			Optional<UserMaster> verifier = userRepository.findByUserId(verifierId);

			if (verifier.isPresent()) {
				UserMaster user = verifier.get();

				user.setIsActive(Boolean.FALSE);
				user.setIsDeleted(Boolean.TRUE);
				user.setModifiedOn(new Timestamp(new Date().getTime()));
				user.setModifiedIp(CommonUtil.getRequestIp(request));
				user.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
				user = userRepository.save(user);


				List<UserAvailability> verfiierAvailability=userAvailabilityRepository.findByUserId_UserIdAndIsActiveAndIsDeleted(user.getUserId(), true, false);
				if(verfiierAvailability!=null && verfiierAvailability.size()>0) {
					UserAvailability currentAvailability = verfiierAvailability.get(0);
					currentAvailability.setIsAvailable(false);
					userAvailabilityRepository.save(currentAvailability);
				}
					verifierLandAssignmentRepository.UpdateByUserId(user.getUserId());




				return new ResponseModel(user, "Verifier " + CustomMessages.RECORD_UPDATE,
						CustomMessages.UPDATE_SUCCESSFULLY, CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
			} else {
				return new ResponseModel(null, CustomMessages.USER_ID_NOT_FOUND, CustomMessages.NO_DATA_FOUND,
						CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	public ResponseModel addSelfRegisteredVerifier(VerifierInputDAO verifierInput, HttpServletRequest request) {
		ResponseModel responseModel = new ResponseModel();
		try {
			UserMaster verifier = new UserMaster();
			if (Objects.nonNull(verifierInput)) {
				Optional<UserMaster> opUser = userRepository.findByUserMobileNumberAndUserAadhaarHash(
						verifierInput.getUserMobileNumber(), verifierInput.getUserAadhaarHash());
				if (opUser.isPresent()) {
					return new ResponseModel(opUser.get().getUserMobileNumber(),
							"User " + CustomMessages.RECORD_ALREADY_EXIST, CustomMessages.GET_DATA_ERROR,
							CustomMessages.FAILED, CustomMessages.METHOD_POST);
				} else {
					BeanUtils.copyProperties(verifierInput, verifier);
					verifier.setUserStatus(statusRepository
							.findByIsDeletedFalseAndStatusCode(StatusEnum.APPROVED.getValue()).getStatusCode());
					// if mobile or email verified
					verifier.setIsEmailVerified(Objects.isNull(verifierInput.getIsEmailVerified()) ? false
							: verifierInput.getIsEmailVerified());
					verifier.setIsMobileVerified(Objects.isNull(verifierInput.getIsMobileVerified()) ? false
							: verifierInput.getIsMobileVerified());
					verifier.setIsAadhaarVerified(Objects.isNull(verifierInput.getIsAadhaarVerified()) ? false
							: verifierInput.getIsAadhaarVerified());
					// default PR
					DepartmentMaster department = departmentRepository
							.findByDepartmentType(DepartmentEnum.PRIVATE_RESIDENT.getValue()).orElse(null);
					verifier.setDepartmentId(department);
					verifier.setPinCode(verifierInput.getPinCode());
					assignVerifierRole(verifier);

					String prId = generateVerifierUniqueUserName(verifier);
					verifier.setUserName(prId);
					verifier.setUserPrId(prId);
					verifier.setUserFullName(verifier.getUserFirstName() + " " + verifier.getUserLastName());
					verifier.setIsActive(true);
					verifier.setIsDeleted(false);
					verifier.setUserPassword(encoder.encode(verifierInput.getUserPassword()));
					verifier.setCreatedOn(new Timestamp(new Date().getTime()));
					verifier.setCreatedIp(CommonUtil.getRequestIp(request));
					verifier = userRepository.save(verifier);
					verifier.setCreatedBy(verifier.getUserId().toString());
					verifier = userRepository.save(verifier);
					if (verifierInput.getIsAvailable()) {
						addCurrentSeasonAvailability(verifier, request);
					}
					addVerifierBankDetail(verifier, verifierInput);
					verifierInput.setUserId(verifier.getUserId());
				}
			}
			return new ResponseModel(verifierInput, CustomMessages.RECORD_ADD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	public String generateVerifierUniqueUserName(UserMaster verifier) {

		String prNumber = "";
		if(verifier.getRoleId().getRoleName().equalsIgnoreCase(RoleEnum.VERIFIER.toString())) {
			prNumber = "VER";
		}else {
			prNumber = "IO";
		}


		String depCode = verifier.getDepartmentId().getDepartmentCode();
		Integer serialNumber = userRepository.getUserCountForDepartmentAndVillageV2(depCode, verifier.getUserVillageLGDCode(),verifier.getRoleId().getRoleId());
		String department = (verifier.getDepartmentId().getDepartmentCode());
		return prNumber+"_"+department+"_"+(serialNumber+1)+"_"+verifier.getUserVillageLGDCode().toString();
	}

	public ResponseModel updateVerifierStatus(VerifierInputDAO verifierInputDAO, HttpServletRequest request) {
		try {
			Optional<UserMaster> verifier = userRepository.findByUserId(verifierInputDAO.getUserId());
			if (verifier.isPresent()) {
				UserMaster user = verifier.get();
				user.setIsActive(verifierInputDAO.getIsActive());
				user.setModifiedOn(new Timestamp(new Date().getTime()));
				user.setModifiedIp(CommonUtil.getRequestIp(request));
				user.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
				user = userRepository.save(user);
				return new ResponseModel(user, "Verifier " + CustomMessages.RECORD_UPDATE,
						CustomMessages.UPDATE_SUCCESSFULLY, CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
			} else {
				return new ResponseModel(null, CustomMessages.USER_ID_NOT_FOUND, CustomMessages.NO_DATA_FOUND,
						CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	public ResponseModel getVerifierDetail(Long userId, HttpServletRequest request) {
		try {
			VerifierOutputDAO userOutput = new VerifierOutputDAO();

			Optional<UserMaster> opUser = userRepository.findByUserId(userId);
			if (opUser.isPresent()) {
				UserMaster verfiier = opUser.get();
				BeanUtils.copyProperties(verfiier, userOutput);
				if (Objects.nonNull(verfiier.getDepartmentId())) {
					DepartmentMaster department = new DepartmentMaster(verfiier.getDepartmentId().getDepartmentId(),
							verfiier.getDepartmentId().getDepartmentName(),
							verfiier.getDepartmentId().getDepartmentType());
					verfiier.setDepartmentId(department);
				}
				if (Objects.nonNull(verfiier.getDesignationId())) {
					DesignationMaster designation = new DesignationMaster(
							verfiier.getDesignationId().getDesignationId(),
							verfiier.getDesignationId().getDesignationName());
					verfiier.setDesignationId(designation);
				}
				UserBankDetail bankDetail = userBankDetailRepository.findByUserId_UserId(verfiier.getUserId());
				if (Objects.nonNull(bankDetail)) {
					userOutput.setUserBankDetail(
							new UserBankDetail(bankDetail.getUserBankName(), bankDetail.getUserBranchCode(),
									bankDetail.getUserIfscCode(), bankDetail.getUserBankAccountNumber(),bankDetail.getBankId().getBankId()));
				}

				userOutput = getVerifierBoundaryUsingVillage(userOutput);

				userOutput.setRoleId(verfiier.getRoleId()!=null ? verfiier.getRoleId().getRoleId() : null);

				List<Long> villageCodes=userVillageMappingRepository.getVillageCodesByUserId(userId);
				userOutput.setVerificationVillageLGDCode(villageCodes);
				return new ResponseModel(userOutput, "Verifier " + CustomMessages.GET_RECORD,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
			} else {
				return new ResponseModel(null, CustomMessages.USER_ID_NOT_FOUND, CustomMessages.NO_DATA_FOUND,
						CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	public ResponseModel markUnAvailable(VerifierInputDAO inputDAO, HttpServletRequest request) {
		try {
			UserAvailability currentAvailability = new UserAvailability();
			List<UserLandAssignment> userLandAssignment = userLandAssignmentRepository.findUserLandAssignment(
					inputDAO.getUserId(), inputDAO.getSeasonId(), inputDAO.getStartingYear(), inputDAO.getEndingYear());
			if (Objects.nonNull(userLandAssignment)) {
				if (userLandAssignment.size() == 0) {
					/* LOGIC WHEN LAND ASSIGNED TO USER */

				}


			}

			List<UserAvailability> verfiierAvailability = userAvailabilityRepository.findUserAvailability(
					inputDAO.getStartingYear(), inputDAO.getEndingYear(), inputDAO.getSeasonId(),
					inputDAO.getUserId());
			if(verfiierAvailability!=null && verfiierAvailability.size()>0) {
				currentAvailability = verfiierAvailability.get(0);
				currentAvailability.setIsAvailable(inputDAO.getIsAvailable());
				userAvailabilityRepository.save(currentAvailability);
			}


			List<LandUserDAO> filterList = verifierLandAssignmentRepository.getfamlandIds(Integer.valueOf(inputDAO.getStartingYear()),
					Integer.valueOf(inputDAO.getEndingYear()), inputDAO.getSeasonId(), inputDAO.getUserId());
			if (filterList != null && filterList.size() > 0) {
				List<String> farmlandIds = filterList.stream().map(x -> x.getFarmLandId()).collect(Collectors.toList());

				verifierLandAssignmentRepository.unassignVerifierTaskAllocationWithoutUserId(
						inputDAO.getSeasonId(), Integer.valueOf(inputDAO.getStartingYear()), Integer.valueOf(inputDAO.getEndingYear()), farmlandIds,inputDAO.getUserId());
			}

			return new ResponseModel(currentAvailability.getIsAvailable(), CustomMessages.RECORD_UPDATE,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(e.getMessage(), e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED, CustomMessages.FAILED);
		}
	}

	public ResponseModel updateVerifier(VerifierInputDAO inputDAO, HttpServletRequest request) {
		ResponseModel responseModel = new ResponseModel();
		try {
			if (Objects.nonNull(inputDAO)) {
				Optional<UserMaster> opUser = userRepository.findByUserId(inputDAO.getUserId());
				if (opUser.isPresent()) {
					UserMaster surveyor = opUser.get();
					BeanUtils.copyProperties(inputDAO, surveyor, "createdOn","userName", "userPrId","roleId","createdBy", "createdIp", "isDeleted", "isActive", "selfRegistered", "isMobileVerified", "isEmailVerified", "userPassword","userDeviceToken","appVersion","userDeviceType","userDeviceName", "userOs", "userLocalLangauge","isPasswordChanged","lastPasswordChangedDate","lastPasswordChangedDate","defaultPage","passwordHistory","rolePatternMappingId","userImage", "mediaId","isAadhaarVerified");

					// if mobile or email gets changed
					surveyor.setIsEmailVerified(
							(!surveyor.getUserEmailAddress().equals(inputDAO.getUserEmailAddress())) ? false : true);
					surveyor.setIsMobileVerified(
							(!surveyor.getUserMobileNumber().equals(inputDAO.getUserMobileNumber())) ? false : true);
					// set department and designation start
					DepartmentMaster department = departmentRepository.findById(inputDAO.getDepartmentId())
							.orElse(null);
					DesignationMaster designation = designationRepository.findById(inputDAO.getDesignationId())
							.orElse(null);
					surveyor.setDepartmentId(department);
					surveyor.setDesignationId(designation);
					surveyor.setGovernmentId(inputDAO.getGovernmentId());
					// set department and designation end
					surveyor.setUserFullName(surveyor.getUserFirstName() + " " + surveyor.getUserLastName());
					surveyor.setModifiedOn(new Timestamp(new Date().getTime()));
					surveyor.setModifiedIp(CommonUtil.getRequestIp(request));
					surveyor.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
					UserMaster newVerifier = userRepository.save(surveyor);
					inputDAO.setUserId(surveyor.getUserId());

					if(inputDAO.getVerificationVillageLGDCode()!=null && inputDAO.getVerificationVillageLGDCode().size()>0) {
//						newVerifier

						List<UserVillageMapping> removeList=userVillageMappingRepository.findByUserMaster_UserIdAndIsActiveAndIsDeleted(newVerifier.getUserId(), true, false);
						removeList.forEach(action->{
							action.setIsActive(false);
							action.setIsDeleted(true);
						});
						userVillageMappingRepository.saveAll(removeList);
						List<VillageLgdMaster> villageCodes=villageLgdMasterRepository.findByVillageLgdCodeIn(inputDAO.getVerificationVillageLGDCode());
						if(villageCodes!=null && villageCodes.size()>0) {
							List<UserVillageMapping> userVillageMappingList=new ArrayList<>();
							villageCodes.forEach(action->{
								UserVillageMapping userVillageMapping=new UserVillageMapping();
								userVillageMapping.setIsActive(Boolean.TRUE);
								userVillageMapping.setIsDeleted(Boolean.FALSE);
								userVillageMapping.setCreatedIp(CommonUtil.getRequestIp(request));
								userVillageMapping.setModifiedIp(CommonUtil.getRequestIp(request));
								userVillageMapping.setUserMaster(newVerifier);
								userVillageMapping.setVillageLgdMaster(action);
								userVillageMappingList.add(userVillageMapping);
//								verifier

							});
							userVillageMappingRepository.saveAll(userVillageMappingList);

						}
//
					}
				}
			}
			return new ResponseModel(inputDAO.getUserId(), "Verifier" + CustomMessages.RECORD_UPDATE,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	public ResponseModel getVilagelist(UserInputDAO userInputDAO,HttpServletRequest request) {
		try {
//			SowingSeason season = generalService.getCurrentSeason();
			String userId = userService.getUserId(request);
			Long id = Long.parseLong(userId);
			List<VillageLgdMaster> accessibleVillages = new ArrayList<>();
//			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
//			Integer currentYear = cal.get(Calendar.YEAR);
//			List<VerifierLandConfiguration> configList=verifierLandConfigurationRepository.
//					findByConfigId_IsActiveAndConfigId_IsDeletedAndIsActiveAndIsDeletedAndConfigId_StateLgdMaster_StateLgdCodeInAndEndingYearAndSeason_SeasonId(true, false, true, false,userInputDAO.getStateLGDCodeList() ,currentYear,season.getSeasonId());
//
//			if(configList!=null && configList.size()>0) {
//
//			}


					List<Long> removeVillageLgdCode=new ArrayList<>();
//					currentYear, season.getSeasonId()
			List<FarmLandCount> verifierLandCount = new ArrayList<>();

			if ((Long.valueOf(RoleEnum.INSPECTION_OFFICER.getValue())).equals(userInputDAO.getUserTypeId())){
				verifierLandCount=verifierLandAssignmentRepository.getVerifierLandAssignmentVillageCountWithoutSecondRejected();
			}else if ((Long.valueOf(RoleEnum.VERIFIER.getValue())).equals(userInputDAO.getUserTypeId())){
				verifierLandCount=verifierLandAssignmentRepository.getVerifierLandAssignmentVillageCountWithSecondRejected();
			}else{
				verifierLandCount=verifierLandAssignmentRepository.getVerifierLandAssigmentVillageCount();
			}

//					List<FarmLandCount> verifierLandCount=verifierLandAssignmentRepository.getVerifierLandAssigmentVillageCount();
					List<UserVillageMapping> villagelgdCodes=new ArrayList<UserVillageMapping>();
					if(verifierLandCount!=null && verifierLandCount.size()>0) {
							List<Long> codes=verifierLandCount.stream().map(x->x.getVillageLgdCode()).collect(Collectors.toList());

							List<VerifierLandConfiguration> configList=verifierLandConfigurationRepository.
									findByConfigId_IsActiveAndConfigId_IsDeletedAndIsActiveAndIsDeletedAndConfigId_StateLgdMaster_StateLgdCodeInAndVillageLgdCode_VillageLgdCodeIn(true, false, true, false,userInputDAO.getStateLGDCodeList() ,codes);

							if(configList!=null && configList.size()>0) {
								verifierLandCount.forEach(action->{
									configList.forEach(action2->{

											if(action.getVillageLgdCode().equals(action2.getVillageLgdCode().getVillageLgdCode())) {
												 int obj = Long.compare( action.getTotalCount(), action2.getLandPlots().size());
												 if(obj>0 || obj==0) {
													 removeVillageLgdCode.add(action.getVillageLgdCode());
												 }
											}
									});
								});
							}



//							List<FarmLandCount> plotCountVillageWise=verifierLandAssignmentRepository.getFarmLandPlotRegistry(codes);
//
//							verifierLandCount.forEach(action->{
//								plotCountVillageWise.forEach(action2->{
//
//										if(action.getVillageLgdCode().equals(action2.getVillageLgdCode())) {
//											 int obj = Long.compare( action.getTotalCount(), action2.getTotalCount());
//											 if(obj>0 || obj==0) {
//												 removeVillageLgdCode.add(action.getVillageLgdCode());
//											 }
//										}
//								});
//							});
							if(removeVillageLgdCode!=null && removeVillageLgdCode.size()>0) {
								villagelgdCodes = userVillageMappingRepository.findByUserMaster_UserIdAndVillageLgdMaster_SubDistrictLgdCode_SubDistrictLgdCodeInAndVillageLgdMaster_VillageLgdCodeNotInOrderByVillageLgdMaster_VillageNameAsc(
										id, userInputDAO.getSubDistrictLgdCodeList(),removeVillageLgdCode);
							}else {
								villagelgdCodes = userVillageMappingRepository
										.findByUserMaster_UserIdAndVillageLgdMaster_SubDistrictLgdCode_SubDistrictLgdCodeInOrderByVillageLgdMaster_VillageNameAsc(
												id, userInputDAO.getSubDistrictLgdCodeList());
							}




					}
					else {
						villagelgdCodes = userVillageMappingRepository
								.findByUserMaster_UserIdAndVillageLgdMaster_SubDistrictLgdCode_SubDistrictLgdCodeInOrderByVillageLgdMaster_VillageNameAsc(
										id, userInputDAO.getSubDistrictLgdCodeList());

					}

					if (villagelgdCodes != null && villagelgdCodes.size() > 0) {

								accessibleVillages = villagelgdCodes.stream().map(x -> x.getVillageLgdMaster())
																		.collect(Collectors.toList());
						}


			return new ResponseModel(accessibleVillages, "Verifier Village data " + CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_GET);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	public ResponseModel getUserCountVerifierInspection(HttpServletRequest request) {
		ResponseModel responseModel = null;
		try {
			String requestTokenHeader = request.getHeader("Authorization");
			String jwtToken = requestTokenHeader.substring(7);
			String userIdString = CustomMessages.getUserId(request, jwtTokenUtil);
			Long userId = userIdString!=null ? Long.parseLong(userIdString) : null;
			String result=verifierLandConfigurationRepository.getUserCountVerifierInspection(userId);
			ObjectMapper jacksonObjMapper = new ObjectMapper();
			return responseModel = CustomMessages.makeResponseModel(jacksonObjMapper.readTree(result), CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}


	public ResponseModel getAllVerifierV2(VerifierInputDAO requestInput, HttpServletRequest request) {
		ResponseModel responseModel = new ResponseModel();
		try {

			if (requestInput.getStartingYear()==null || requestInput.getEndingYear() == null){
				YearMaster yearMaster =  generalService.getCurrentYear();
				requestInput.setStartingYear(yearMaster.getStartYear());
				requestInput.setEndingYear(yearMaster.getEndYear());
			}

			List<Long> departmentIds = ( requestInput.getDepartmentIds() == null || requestInput.getDepartmentIds().isEmpty()
			) ? null : requestInput.getDepartmentIds();


			List<Long> roleIds=new ArrayList<>();
			if(requestInput.getUserTypeIds()!=null && requestInput.getUserTypeIds().size()>0) {
				roleIds.addAll(requestInput.getUserTypeIds());
			}else {
				roleIds.add(Long.valueOf(RoleEnum.VERIFIER.getValue()));
				roleIds.add(Long.valueOf(RoleEnum.INSPECTION_OFFICER.getValue()));
			}

			String roleIdList = getJoinedValueFromList(roleIds);
			String departments = getJoinedValueFromList(departmentIds);

			String userId = userService.getUserId(request);
			Long id = Long.parseLong(userId);

			String response = userAvailabilityRepository
					.getAllVerifierBySeasonIdAndSeasonYear(requestInput.getSeasonId(),
							requestInput.getStartingYear(), requestInput.getEndingYear(), id,
                            roleIdList, departments);

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<Object> verifierList = mapper.readValue(response,
					typeFactory.constructCollectionType(List.class, Object.class));

			return new ResponseModel(verifierList, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

	private String getJoinedValueFromList(List<Long> list) {
		try {
			return list.stream().map(Object::toString).collect(Collectors.joining(", "));
		} catch (Exception e) {
			return "";
		}
	}

}
