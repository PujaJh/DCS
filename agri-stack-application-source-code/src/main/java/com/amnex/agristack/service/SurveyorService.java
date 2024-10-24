package com.amnex.agristack.service;

import com.amnex.agristack.Enum.DepartmentEnum;
import com.amnex.agristack.Enum.RoleEnum;
import com.amnex.agristack.Enum.StatusEnum;
import com.amnex.agristack.centralcore.dao.ACMMsgRequestDAO;
import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.dao.*;
import com.amnex.agristack.dao.common.Mail;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.*;
import com.amnex.agristack.notifications.NotificationThread;
import com.amnex.agristack.repository.*;
import com.amnex.agristack.scheduler.SeasonChangeScheduler;
import com.amnex.agristack.utils.CommonUtil;
import com.amnex.agristack.utils.Constants;
import com.amnex.agristack.utils.CustomMessages;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author majid.belim
 *
 */
@Service
public class SurveyorService {
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

    @Value(value = "${app.default.password}")
    private String defaultPassword;

    @Autowired
    UserLandAssignmentService userLandAssignmentService;

    @Autowired
    MessageConfigurationService messageConfigurationService;
    

	@Autowired
	private MessageCredentialRepository messageCredentialRepository;

	@Autowired
	private MessageConfigurationRepository messageConfigurationRepository;
	
	@Autowired
	private JavaMailSender mailSender;

    @Autowired
    private SeasonChangeScheduler seasonChangeScheduler;

    @Autowired
    private UserService userService;
    
    @Autowired
	FarmlandPlotRegistryRepository farmlandPlotRegistryRepository;
	
    /**
     * Adds a surveyor.
     *
     * @param surveyorInput {@code SurveyorInputDAO}
     * @param request   The HttpServletRequest object.
     * @return          The ResponseModel object containing the result of the operation.
     */
    @Transactional
    public ResponseModel addSurveyor(SurveyorInputDAO surveyorInput, HttpServletRequest request) {
        ResponseModel responseModel = new ResponseModel();
        try {
            if (Objects.nonNull(surveyorInput)) {
                Optional<UserMaster> opUserMob = userRepository.findByUserMobileNumber(surveyorInput.getUserMobileNumber());
                if(Objects.nonNull(surveyorInput.getUserAadhaarHash())) {
                    List<UserMaster> opUserAadhar = userRepository.findByUserAadhaarHash(surveyorInput.getUserAadhaarHash());
                    if (!opUserAadhar.isEmpty()) {
                        return new ResponseModel(opUserAadhar.get(0).getUserAadhaarHash(), "User " + CustomMessages.RECORD_ALREADY_EXIST, CustomMessages.GET_DATA_ERROR, CustomMessages.FAILED, CustomMessages.METHOD_POST);
                    }
                }
                Optional<UserMaster> opUserEmail = userRepository.findByUserEmailAddress(surveyorInput.getUserEmailAddress());
                if (opUserMob.isPresent()) {
                    return new ResponseModel(opUserMob.get().getUserMobileNumber(), "User " + CustomMessages.RECORD_ALREADY_EXIST, CustomMessages.GET_DATA_ERROR, CustomMessages.FAILED, CustomMessages.METHOD_POST);
                } else if (opUserEmail.isPresent()) {
                    return new ResponseModel(opUserEmail.get().getUserEmailAddress(), "User " + CustomMessages.RECORD_ALREADY_EXIST, CustomMessages.GET_DATA_ERROR, CustomMessages.FAILED, CustomMessages.METHOD_POST);
                } else {
                    UserMaster surveyor = new UserMaster();
                    BeanUtils.copyProperties(surveyorInput, surveyor);
                    // if mobile or  email verified
                    Optional<UserMaster> op= userRepository.findByGovernmentId(surveyorInput.getGovernmentId());
                    if(op.isPresent()) {
                    	return new ResponseModel(surveyorInput.getGovernmentId(), "User " + CustomMessages.RECORD_ALREADY_EXIST, CustomMessages.GET_DATA_ERROR, CustomMessages.FAILED, CustomMessages.METHOD_POST);
                    }
                    surveyor.setGovernmentId(surveyorInput.getGovernmentId());
                    surveyor.setIsEmailVerified(Objects.isNull(surveyorInput.getIsEmailVerified()) ? false : surveyorInput.getIsEmailVerified());
                    surveyor.setIsMobileVerified(Objects.isNull(surveyorInput.getIsMobileVerified()) ? false : surveyorInput.getIsMobileVerified());
                    surveyor.setIsAadhaarVerified(Objects.isNull(surveyorInput.getIsAadhaarVerified()) ? false : surveyorInput.getIsAadhaarVerified());

                    //set department and designation start
                    if(Objects.nonNull(surveyorInput.getDepartmentId()))  {
                        DepartmentMaster department = departmentRepository.findById(surveyorInput.getDepartmentId()).orElse(null);
                        surveyor.setDepartmentId(department);
                    }
                    if(Objects.nonNull(surveyorInput.getDesignationId()))  {
                        DesignationMaster designation = designationRepository.findById(surveyorInput.getDesignationId()).orElse(null);
                        surveyor.setDesignationId(designation);
                    }
                    // Assign Surveyor Role
                    assignSurveyorRole(surveyor);

                    String prId = getPrNumberforSurveyor(surveyor);
                    surveyor.setUserPrId(prId);

                    surveyor.setPinCode(surveyorInput.getPinCode());
                    surveyor.setUserFullName(surveyor.getUserFirstName() + " " + surveyor.getUserLastName());
                    surveyor.setUserName(prId);
                    surveyor.setIsActive(true);
                    surveyor.setIsDeleted(false);

                    surveyor.setCreatedOn(new Timestamp(new Date().getTime()));
                    surveyor.setCreatedIp(CommonUtil.getRequestIp(request));

                    if(!surveyorInput.getSelfRegistered()) {
                        surveyor.setCreatedBy(CustomMessages.getUserId(request, jwtTokenUtil));
                        surveyor.setUserStatus(statusRepository.findByIsDeletedFalseAndStatusCode(StatusEnum.APPROVED.getValue()).getStatusCode());
                    }

//					password change start
					String randomPassword = CommonUtil.GeneratePassword(8);
					surveyor.setUserPasswordText(randomPassword);
					surveyor.setUserPassword(encoder.encode(randomPassword));
					surveyor.setIsPasswordChanged(false);
					surveyor.setLastPasswordChangedDate(new Date());

					Gson gson = new Gson();
					JsonArray passwordHistory = new JsonArray();
					if (surveyor.getPasswordHistory() != null) {
						passwordHistory = gson.fromJson(surveyor.getPasswordHistory().toString(), JsonArray.class);
					}
					passwordHistory.add(surveyor.getUserPassword());
					if (passwordHistory.size() > 3) {
						passwordHistory.remove(0);
					}
                    System.out.println("surveyor "+surveyor);
//					password change end
                    surveyor = userRepository.save(surveyor);
                    addSurveyorBankDetail(surveyor,surveyorInput);

//                    messageConfigurationService.sendMessageToWebSurveyor("Crop Surveyor", "Mobile Number", surveyorInput.getUserMobileNumber(), Objects.isNull(surveyorInput.getUserPassword())? tempPassword : surveyorInput.getUserPassword(), surveyorInput.getUserMobileNumber());
                    
					if (surveyor.getUserMobileNumber() != null && surveyor.getUserEmailAddress() != null) {

						sendMobileMessage(surveyor);
						sendEmailMessage(surveyor);
					}


                    addCurrentSeasonAvailability(surveyor, request);
                    surveyorInput.setUserId(surveyor.getUserId());
                }
            }
            return new ResponseModel(surveyorInput.getUserId(), CustomMessages.RECORD_ADD, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
        } catch (Exception e) {
            e.printStackTrace();
            responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
                    CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
            return responseModel;
        }
    }

    
    /**
     * Sends a mobile message to the user.
     *
     * @param userMaster The user information.
     */
	public void sendMobileMessage(UserMaster userMaster) {

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

	/**
	 * Sends an email message to the user.
	 *
	 * @param userMaster The user information.
	 */
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

	/**
	 * Initializes the Velocity engine for processing email templates.
	 *
	 * @return The initialized VelocityEngine.
	 */
	private VelocityEngine initializeVelocity() {
		// Initialize the engine
		VelocityEngine velocityEngine = new VelocityEngine();
		velocityEngine.setProperty(Velocity.RESOURCE_LOADER, "string");
		velocityEngine.setProperty("resource.loader.string.class", StringResourceLoader.class.getName());
		velocityEngine.init();
		return velocityEngine;
	}
	
	/**
	 * Adds bank details for a surveyor.
	 *
	 * @param surveyor      The surveyor object.
	 * @param surveyorInput The surveyor input data.
	 * @return The updated surveyor object.
	 */
    @Transactional
    private UserMaster addSurveyorBankDetail(UserMaster surveyor, SurveyorInputDAO surveyorInput) {
        Optional<BankMaster> bank = bankRepository.findByBankIdAndIsDeletedFalse(surveyorInput.getUserBankId());
        if (bank.isPresent()) {
            BankMaster bankMaster = bank.get();
            UserBankDetail bankDetail = new UserBankDetail();
            BeanUtils.copyProperties(surveyorInput, bankDetail);
            bankDetail.setUserBankName(bankMaster.getBankName());
            bankDetail.setBankId(bankMaster);
            bankDetail.setUserId(surveyor);
            bankDetail.setCreatedIp(surveyor.getCreatedIp());
            bankDetail.setCreatedBy(surveyor.getCreatedBy());
            bankDetail.setIsActive(true);
            bankDetail.setIsDeleted(false);
            userBankDetailRepository.save(bankDetail);
        }
        return surveyor;
    }

    /**
     * Adds availability details for a surveyor.
     *
     * @param surveyor The surveyor object.
     * @param request  The HttpServletRequest object containing the request information.
     * @return The updated surveyor object.
     */
    @Transactional
    private UserMaster addSurveyorAvailability(UserMaster surveyor, HttpServletRequest request) {
        UserAvailability availability = new UserAvailability();
        availability.setUserId(surveyor);
        if(!surveyor.getIsAvailable()) {
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
        return surveyor;
    }

    
    /**
     * Adds availability details for a surveyor in the current season.
     *
     * @param surveyor The surveyor object.
     * @param request  The HttpServletRequest object containing the request information.
     * @return The updated surveyor object.
     */
    @Transactional
    private UserMaster addCurrentSeasonAvailability(UserMaster surveyor, HttpServletRequest request) {
        UserAvailability availability = new UserAvailability();
        availability.setUserId(surveyor);
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
        return surveyor;
    }

    /**
     * Updates a surveyor.
     *
     * @param surveyorInput The {@code SurveyorInputDAO} object containing surveyor details.
     * @param request   The HttpServletRequest object.
     * @return          The ResponseModel object containing the result of the operation.
     */
    @Transactional
    public ResponseModel updateSurveyor(SurveyorInputDAO surveyorInput, HttpServletRequest request) {
        ResponseModel responseModel = new ResponseModel();
        try {
            if (Objects.nonNull(surveyorInput)) {
                Optional<UserMaster> opUser = userRepository.findByUserId(surveyorInput.getUserId());
                if (opUser.isPresent()) {
                    UserMaster surveyor = opUser.get();
                    BeanUtils.copyProperties(surveyorInput, surveyor, "createdOn","userName", "userPrId","roleId","createdBy", "createdIp", "isDeleted", "isActive", "selfRegistered", "isMobileVerified", "isEmailVerified", "userPassword","userDeviceToken","appVersion","userDeviceType","userDeviceName", "userOs", "userLocalLangauge","isPasswordChanged","lastPasswordChangedDate","lastPasswordChangedDate","defaultPage","passwordHistory","rolePatternMappingId","userImage", "mediaId","isAadhaarVerified");
                    surveyor.setGovernmentId(surveyorInput.getGovernmentId());
                    // if mobile or email gets changed
                    surveyor.setIsEmailVerified((!surveyor.getUserEmailAddress().equals(surveyorInput.getUserEmailAddress())) ? false : true);
                    surveyor.setIsMobileVerified((!surveyor.getUserMobileNumber().equals(surveyorInput.getUserMobileNumber())) ? false : true);
                    //set department and designation start
                    DepartmentMaster department = departmentRepository.findById(surveyorInput.getDepartmentId()).orElse(null);
                    DesignationMaster designation = designationRepository.findById(surveyorInput.getDesignationId()).orElse(null);
                    surveyor.setDepartmentId(department);
                    surveyor.setDesignationId(designation);
                    //set department and designation end
                    surveyor.setUserFullName(surveyor.getUserFirstName() + " " + surveyor.getUserLastName());
                    surveyor.setModifiedOn(new Timestamp(new Date().getTime()));
                    surveyor.setModifiedIp(CommonUtil.getRequestIp(request));
                    surveyor.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
                    surveyor = userRepository.save(surveyor);
                    surveyorInput.setUserId(surveyor.getUserId());
                    updateSurveyorBankDetail(surveyor, surveyorInput);
//                    if (surveyor.getDepartmentId().getDepartmentType()
//                            .equals(DepartmentEnum.PRIVATE_RESIDENT.getValue())) {
//                        updateSurveyorBankDetail(surveyor, surveyorInput);
//                    }

                }
            }
            return new ResponseModel(surveyorInput.getUserId(), "Surveyor" + CustomMessages.RECORD_UPDATE, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
        } catch (Exception e) {
            e.printStackTrace();
            responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
                    CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
            return responseModel;
        }
    }

    /**
     * Retrieves the details of a surveyor.
     *
     * @param userId    The ID of the surveyor.
     * @param request   The HttpServletRequest object.
     * @return          The ResponseModel object containing the surveyor details.
     */
    public ResponseModel getSurveyorDetail(Long userId, HttpServletRequest request) {
        try {
            SurveyorOutputDAO userOutput = new SurveyorOutputDAO();

            Optional<UserMaster> opUser = userRepository.findByUserId(userId);
            if (opUser.isPresent()) {
                UserMaster surveyor = opUser.get();
                BeanUtils.copyProperties(surveyor, userOutput);
                if(Objects.nonNull(surveyor.getDepartmentId())) {
                    DepartmentMaster department = new DepartmentMaster(surveyor.getDepartmentId().getDepartmentId(), surveyor.getDepartmentId().getDepartmentName(), surveyor.getDepartmentId().getDepartmentType(), surveyor.getDepartmentId().getDepartmentCode());
                    surveyor.setDepartmentId(department);
                }
                if(Objects.nonNull(surveyor.getDesignationId())) {
                    DesignationMaster designation = new DesignationMaster(surveyor.getDesignationId().getDesignationId(), surveyor.getDesignationId().getDesignationName());
                    surveyor.setDesignationId(designation);
                }
                UserBankDetail bankDetail = userBankDetailRepository.findByUserId_UserId(surveyor.getUserId());
                if(Objects.nonNull(bankDetail)) {
                    userOutput.setUserBankDetail(new UserBankDetail(bankDetail.getUserBankName(), bankDetail.getUserBranchCode(), bankDetail.getUserIfscCode(), bankDetail.getUserBankAccountNumber(), bankDetail.getBankId().getBankId()));
                }
                userOutput.setGovernmentId(surveyor.getGovernmentId());
                userOutput = getSurveyorBoundaryUsingVillage(userOutput);
                return new ResponseModel(userOutput, "Surveyor " + CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
            } else {
                return new ResponseModel(null, CustomMessages.USER_ID_NOT_FOUND, CustomMessages.NO_DATA_FOUND, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR,
                    CustomMessages.FAILED, CustomMessages.METHOD_GET);
        }
    }

    
    /**
     * Retrieves the boundary details (state, district, taluka, and village names) for a surveyor
     * based on their LGD codes (state, district, taluka, and village).
     *
     * @param user The SurveyorOutputDAO object containing the user information.
     * @return     The updated SurveyorOutputDAO object with the boundary details.
     */
    public SurveyorOutputDAO getSurveyorBoundaryDetail(SurveyorOutputDAO user) {
        if(Objects.nonNull(user.getUserVillageLGDCode())) {
            VillageLgdMaster village = villageService.getVillageByLGDCode(user.getUserVillageLGDCode().longValue());
            user.setStateName(village.getStateLgdCode().getStateName());
            user.setDistrictName(village.getDistrictLgdCode().getDistrictName());
            user.setTalukaName(village.getSubDistrictLgdCode().getSubDistrictName());
            user.setVillageName(Objects.nonNull(village) ? village.getVillageName() : "");
        }

        return user;
    }

    /**
     * Retrieves the boundary details (state, district, taluka, and village names) for a surveyor
     * based on their village code.
     *
     * @param user The SurveyorOutputDAO object containing the user information.
     * @return     The updated SurveyorOutputDAO object with the boundary details.
     */
    public SurveyorOutputDAO getSurveyorBoundaryUsingVillage(SurveyorOutputDAO user) {
        if(Objects.nonNull(user.getUserVillageLGDCode())) {
            VillageLgdMaster village = villageService.getVillageByLGDCode(user.getUserVillageLGDCode().longValue());
            if (Objects.nonNull(village)) {
                user.setStateName(Objects.nonNull(village.getStateLgdCode().getStateId()) ? village.getStateLgdCode().getStateName() : "");
                user.setDistrictName(Objects.nonNull(village.getDistrictLgdCode().getDistrictId()) ? village.getDistrictLgdCode().getDistrictName() : "");
                user.setTalukaName(Objects.nonNull(village.getSubDistrictLgdCode().getSubDistrictId()) ? village.getSubDistrictLgdCode().getSubDistrictName() : "");
                user.setVillageName(Objects.nonNull(village) ? village.getVillageName() : "");
            }
        }
        return user;
    }

    /**
     * Delete Surveyor
     *
     * @param surveyorInputDAO The {@code SurveyorInputDAO} object containing surveyor details.
     * @param request The HttpServletRequest object.
     * @return {@link UserMaster}
     */
    public ResponseModel deleteSurveyor(SurveyorInputDAO surveyorInputDAO, HttpServletRequest request) {
        try {
            Optional<UserMaster> surveyor = userRepository.findByUserId(surveyorInputDAO.getUserId());
            if (surveyor.isPresent()) {
                UserMaster user = surveyor.get();
                user.setIsActive(false);
                user.setIsDeleted(true);
                user.setModifiedOn(new Timestamp(new Date().getTime()));
                user.setModifiedIp(CommonUtil.getRequestIp(request));
                user.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
                user = userRepository.save(user);
                surveyorInputDAO.setIsAvailable(false);
                updateUserAvailability(surveyorInputDAO,request);
                return new ResponseModel(user, "Surveyor " + CustomMessages.RECORD_UPDATE, CustomMessages.UPDATE_SUCCESSFULLY, CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
            } else {
                return new ResponseModel(null, CustomMessages.USER_ID_NOT_FOUND, CustomMessages.NO_DATA_FOUND, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
            }


        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR,
                    CustomMessages.FAILED, CustomMessages.METHOD_GET);
        }
    }

    /**
     * Update Surveyor Status
     *
     * @param surveyorInputDAO The {@code SurveyorInputDAO} object containing surveyor details.
     * @param request The HttpServletRequest object.
     * @return {@link UserMaster}
     */
    public ResponseModel updateSurveyorStatus(SurveyorInputDAO surveyorInputDAO, HttpServletRequest request) {
        try {
            Optional<UserMaster> surveyor = userRepository.findByUserId(surveyorInputDAO.getUserId());
            if (surveyor.isPresent()) {
                UserMaster user = surveyor.get();
                user.setIsActive(surveyorInputDAO.getIsActive());
                user.setModifiedOn(new Timestamp(new Date().getTime()));
                user.setModifiedIp(CommonUtil.getRequestIp(request));
                user.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
                user = userRepository.save(user);
                return new ResponseModel(user, "Surveyor " + CustomMessages.RECORD_UPDATE, CustomMessages.UPDATE_SUCCESSFULLY, CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
            } else {
                return new ResponseModel(null, CustomMessages.USER_ID_NOT_FOUND, CustomMessages.NO_DATA_FOUND, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR,
                    CustomMessages.FAILED, CustomMessages.METHOD_GET);
        }
    }

    /**
     * Assigns the "SURVEYOR" role to the given user.
     *
     * @param user  The UserMaster object.
     * @return      The updated UserMaster object with the assigned role.
     */
    @Transactional
    protected UserMaster assignSurveyorRole(UserMaster user) {
        RoleMaster role = roleRepository.findByCode("SURVEYOR").orElse(null);
        if (Objects.nonNull(role)) {
            user.setRoleId(role);
        }
        return user;
    }

    /**
     * Adds a self-registered surveyor from mobile.
     *
     * @param inputDAO The {@code SurveyorInputDAO} input data.
     * @param request  The HttpServletRequest object containing the request information.
     * @return The ResponseModel containing the response data.
     */
    @Transactional
    public ResponseModel addSelfRegisteredSurveyor(SurveyorInputDAO surveyorInput, HttpServletRequest request) {
        ResponseModel responseModel = new ResponseModel();
        try {
            UserMaster surveyor = new UserMaster();
            if (Objects.nonNull(surveyorInput)) {
                 Optional<UserMaster> opUserMob = userRepository.findByUserMobileNumber(surveyorInput.getUserMobileNumber());
                System.out.println("surveyorInput.getUserAadhaarHash() "+surveyorInput.getUserAadhaarHash());
//                 if(Objects.nonNull(surveyorInput.getUserAadhaarHash())) {
//                     Optional<UserMaster> opUserAadhar = userRepository.findByUserAadhaarHash(surveyorInput.getUserAadhaarHash());
//                     System.out.println("opUserAadhar "+opUserAadhar);
//                     if (opUserAadhar.isPresent()) {
//                         return new ResponseModel(opUserAadhar.get(), "User " + CustomMessages.RECORD_ALREADY_EXIST, CustomMessages.GET_DATA_ERROR, CustomMessages.FAILED, CustomMessages.METHOD_POST);
//                     }
//                 }
//                 Optional<UserMaster> opUserEmail = userRepository.findByUserEmailAddress(surveyorInput.getUserEmailAddress());
//                System.out.println("opUserMob "+opUserMob);
//                System.out.println("opUserEmail "+opUserEmail);
//                 if (opUserMob.isPresent()) {
//                     return new ResponseModel(opUserMob.get(), "User " + CustomMessages.RECORD_ALREADY_EXIST, CustomMessages.GET_DATA_ERROR, CustomMessages.FAILED, CustomMessages.METHOD_POST);
//                 } else if (opUserEmail.isPresent()) {
//                     return new ResponseModel(opUserEmail.get(), "User " + CustomMessages.RECORD_ALREADY_EXIST, CustomMessages.GET_DATA_ERROR, CustomMessages.FAILED, CustomMessages.METHOD_POST);
//                 } else {
                    BeanUtils.copyProperties(surveyorInput, surveyor);
                    surveyor.setUserStatus(statusRepository.findByIsDeletedFalseAndStatusCode(StatusEnum.PENDING.getValue()).getStatusCode());
                    // if mobile or  email verified
                    surveyor.setIsEmailVerified(Objects.isNull(surveyorInput.getIsEmailVerified()) ? false : surveyorInput.getIsEmailVerified());
                    surveyor.setIsMobileVerified(Objects.isNull(surveyorInput.getIsMobileVerified()) ? false : surveyorInput.getIsMobileVerified());
                    surveyor.setIsAadhaarVerified(Objects.isNull(surveyorInput.getIsAadhaarVerified()) ? false : surveyorInput.getIsAadhaarVerified());
                    //default PR
                    DepartmentMaster department =  departmentRepository.findByDepartmentType(DepartmentEnum.PRIVATE_RESIDENT.getValue()).orElse(null);
                    surveyor.setDepartmentId(department);
                    surveyor.setPinCode(surveyorInput.getPinCode());
                    assignSurveyorRole(surveyor);
                    String prId = getPrNumberforSurveyor(surveyor);
                    surveyor.setUserPrId(prId);
                    surveyor.setUserFullName(surveyorInput.getUserFirstName() + "" + surveyorInput.getUserLastName());
                    surveyor.setUserName(prId);
                    surveyor.setIsActive(true);
                    surveyor.setIsDeleted(false);

                    String tempPassword = generalService.createTempPassword();
                    surveyor.setUserPassword(Objects.isNull(surveyorInput.getUserPassword())? null : encoder.encode(surveyorInput.getUserPassword()));
                    surveyor.setIsPasswordChanged(true);

                    surveyor.setCreatedOn(new Timestamp(new Date().getTime()));
                    surveyor.setCreatedIp(CommonUtil.getRequestIp(request));
                    surveyor = userRepository.save(surveyor);
                    surveyor.setCreatedBy(surveyor.getUserId().toString());
                    surveyor = userRepository.save(surveyor);
                    if(surveyorInput.getIsAvailable()!=null && surveyorInput.getIsAvailable()) {
                        addCurrentSeasonAvailability(surveyor,request);
                    }
                     System.out.println("surveyor "+surveyor);
                    addSurveyorBankDetail(surveyor, surveyorInput);
                    surveyorInput.setUserId(surveyor.getUserId());
                }
          //  }
            return new ResponseModel(surveyorInput, CustomMessages.RECORD_ADD, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
        } catch (Exception e) {
            e.printStackTrace();
            responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
                    CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
            return responseModel;
        }
    }
    
    /**
     * Updates the availability status of a surveyor.
     *
     * @param surveyorInput  The {@code SurveyorInputDAO} object containing the surveyor details.
     * @param request        The HttpServletRequest object.
     * @return               The ResponseModel object indicating the success or failure of the operation.
     */
    @Transactional
    public ResponseModel surveyorAvailability(SurveyorInputDAO surveyorInput, HttpServletRequest request) {
        ResponseModel responseModel = new ResponseModel();
        try {
            
            if (Objects.nonNull(surveyorInput)) {
                Optional<UserMaster> opUser = userRepository.findByUserIdAndIsDeletedAndIsActive(surveyorInput.getUserId(), false,true);
                if (opUser.isPresent()) {
                	
                    if(surveyorInput.getIsAvailable()!=null &&  surveyorInput.getIsAvailable().equals(true)) {
                        addCurrentSeasonAvailability(opUser.get(),request);
                    }
                
                } else {
        			responseModel = CustomMessages.makeResponseModel(null, CustomMessages.USER_NOT_FOUND,
    						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
                }
            }
            return new ResponseModel(surveyorInput, CustomMessages.RECORD_UPDATE, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
        } catch (Exception e) {
            e.printStackTrace();
            responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
                    CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
            return responseModel;
        }
    }

    
    /**
     * get Surveyor list
     * @param requestInput  The {@code SurveyorInputDAO} object containing surveyor details.
     * @param request The HttpServletRequest object.
     * @return {@link SurveyorOutputDAO}
     */
    public ResponseModel getAllSurveyor(SurveyorInputDAO requestInput, HttpServletRequest request) {
        ResponseModel responseModel = new ResponseModel();
        List<SurveyorOutputDAO> surveyorList = new ArrayList<>();
        List<UserMaster> users = new ArrayList<>();
        SowingSeason season = new SowingSeason();
        try {
            RoleMaster role = roleRepository.findByCode(RoleEnum.SURVEYOR.toString()).orElse(null);
//           Start old code
//            DepartmentMaster department =  departmentRepository.findById(requestInput.getDepartmentId()).orElse(null);
//            List<UserMaster> userList = userRepository.findByDepartmentIdAndRoleIdAndIsActiveTrueAndIsDeletedFalse(department,role);
//          End old code
            
            List<UserMaster> userList = userRepository.findByDepartmentId_departmentIdInAndRoleIdAndIsActiveTrueAndIsDeletedFalse(requestInput.getDepartmentIds(),role);
            if(userList!=null && userList.size()>0) {
		            List<Long> userIds = userList.stream().distinct().map(UserMaster::getUserId).collect(Collectors.toList());
		            List<UserAvailability> userAvailabilities = userAvailabilityRepository.findUserAvailabilityInSeason(requestInput.getStartingYear(),requestInput.getEndingYear(), requestInput.getSeasonId(), userIds, true, false);
		            userList.stream().forEach(ele -> {
		                List<UserAvailability> userAvailability = userAvailabilities.stream().filter(x -> x.getUserId().getUserId().equals(ele.getUserId())).collect(Collectors.toList());
		                if(userAvailability.size() > 0) {
		                    ele.setIsAvailable(userAvailability.get(0).getIsAvailable());
		                } else {
		                    ele.setIsAvailable(false);
		                }
		            });
		           
		            List<UserMaster> surveyors = new ArrayList<>();
		            if(Objects.nonNull(requestInput.getVillageLgdCodeList())) {
		                userList.stream().forEach(userMaster -> {
		                    if(requestInput.getVillageLgdCodeList().contains(userMaster.getUserVillageLGDCode())) {
		                        surveyors.add(userMaster);
		                    }
		                });
		                surveyorList = getSurveyOutFromUserMaster(surveyors, season,Integer.parseInt(requestInput.getStartingYear()),Integer.parseInt(requestInput.getEndingYear()),requestInput.getSeasonId());
		            }
            }
            return new ResponseModel(surveyorList, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
        } catch (Exception e) {
            e.printStackTrace();
            responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
                    CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
            return responseModel;
        }
    }
    
    /**
     * Retrieves a list of SurveyorOutputDAO objects from a list of UserMaster objects.
     *
     * @param users         The list of UserMaster objects.
     * @param season        The SowingSeason object.
     * @param startingYear  The starting year of the season.
     * @param endingYear    The ending year of the season.
     * @param seasonId      The ID of the season.
     * @return              The list of SurveyorOutputDAO objects.
     */
    public List<SurveyorOutputDAO> getSurveyOutFromUserMaster(List<UserMaster> users, SowingSeason season,Integer startingYear,Integer endingYear,Long seasonId) {
        List<SurveyorOutputDAO> surveyorList = new ArrayList<>();
        for (UserMaster user : users) {
            if(!user.getIsDeleted()) {
                SurveyorOutputDAO  surveyor = new SurveyorOutputDAO();
                BeanUtils.copyProperties(user,surveyor);
                getSurveyorBoundaryDetail(surveyor);
                if(Objects.nonNull(surveyor.getDepartmentId())) {
                    DepartmentMaster department = new DepartmentMaster(surveyor.getDepartmentId().getDepartmentId(), surveyor.getDepartmentId().getDepartmentName(), surveyor.getDepartmentId().getDepartmentType(), surveyor.getDepartmentId().getDepartmentCode());
                    surveyor.setDepartmentId(department);
                }
                surveyor.setUserStatus(statusRepository.findByIsDeletedFalseAndStatusCode(user.getUserStatus()).getStatusName());
                surveyor.setIsAvailable(user.getIsAvailable());
                if(Objects.nonNull(surveyor.getDesignationId())) {
                    DesignationMaster designation = new DesignationMaster(surveyor.getDesignationId().getDesignationId(), surveyor.getDesignationId().getDesignationName());
                    surveyor.setDesignationId(designation);
                }
                UserBankDetail bankDetail = userBankDetailRepository.findByUserId_UserId(user.getUserId());
                if(Objects.nonNull(bankDetail)) {
                    surveyor.setBankId(bankDetail.getBankId().getBankId());
                    surveyor.setUserBankDetail(new UserBankDetail(bankDetail.getUserBankName(), bankDetail.getUserBranchCode(), bankDetail.getUserIfscCode(), bankDetail.getUserBankAccountNumber(),bankDetail.getBankId().getBankId()));
                }
                surveyor.setSeasonName(season.getSeasonName());
                surveyorList.add(surveyor);
            }
        }
        if(users!=null && users.size()>0) {
        	List<Long> userIds=users.stream().map(x->x.getUserId()).collect(Collectors.toList());
        	List<UserLandCount> counts=userLandAssignmentRepository.getCountDetailsByUserWithfilter(seasonId, startingYear, endingYear, userIds);
        	if(counts!=null && counts.size()>0) {
        		surveyorList.forEach(action1->{
        			counts.forEach(action2->{
        			if(action1.getUserId().equals(action2.getUserId())) {
        				
        				action1.setAssignCount(action2.getTotalCount());
        			}
            			
            		});	
        		});
        		
        		
        	}
        }
        return surveyorList;
    }

    /**
     * update user availability
     *
     * @param surveyorInput The {@link SurveyorInputDAO} object containing surveyor details.
     * @param request The HttpServletRequest object.
     * @return {@code UserAvailability}
     */
    public ResponseModel updateUserAvailability(SurveyorInputDAO surveyorInput, HttpServletRequest request) {
        try {
            UserAvailability currentAvailability = new UserAvailability();
            List<UserAvailability> surveyorAvailability = userAvailabilityRepository.findUserAvailability(surveyorInput.getStartingYear(),surveyorInput.getEndingYear(), surveyorInput.getSeasonId(), surveyorInput.getUserId());
           if(surveyorAvailability.size() > 0 && !surveyorInput.getIsAvailable()) {
                surveyorAvailability.forEach(ele ->{
                    ele.setIsActive(false);
                    ele.setIsDeleted(true);
                    ele.setIsAvailable(false);
                    ele.setModifiedOn(new Timestamp(new Date().getTime()));
                    ele.setModifiedIp(CommonUtil.getRequestIp(request));
                    ele.setModifiedBy(userService.getUserId(request));
                    userAvailabilityRepository.save(ele);
                });
               SurveyTaskAllocationFilterDAO surveyTaskAllocationRequest = new SurveyTaskAllocationFilterDAO();
               surveyTaskAllocationRequest.setSeasonId(surveyorInput.getSeasonId());
               surveyTaskAllocationRequest.setStartYear(Integer.parseInt(surveyorInput.getStartingYear()));
               surveyTaskAllocationRequest.setEndYear(Integer.parseInt(surveyorInput.getEndingYear()));
               List<Long> surveyorIds = new ArrayList<>();
               surveyorIds.add(surveyorInput.getUserId());
               surveyTaskAllocationRequest.setUserList(surveyorIds);
               userLandAssignmentService.removeSurveyorLandAssignment(request,surveyTaskAllocationRequest);
           } else if (surveyorAvailability.size() > 0 && surveyorInput.getIsAvailable())  {
               currentAvailability = surveyorAvailability.get(0);
               currentAvailability.setIsAvailable(surveyorInput.getIsAvailable());
               currentAvailability.setIsActive(true);
               currentAvailability.setIsDeleted(false);
               currentAvailability.setModifiedOn(new Timestamp(new Date().getTime()));
               currentAvailability.setModifiedIp(CommonUtil.getRequestIp(request));
               currentAvailability.setModifiedBy(userService.getUserId(request));
               userAvailabilityRepository.save(currentAvailability);
           } else {
               UserMaster user = userRepository.findByUserId(surveyorInput.getUserId()).orElse(null);
               if(user != null) {
                   addCurrentSeasonAvailability(user,request);
               }
            }
           return new ResponseModel(currentAvailability.getIsAvailable(), CustomMessages.RECORD_UPDATE, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseModel(e.getMessage(), e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR,
                    CustomMessages.FAILED, CustomMessages.FAILED);
        }
    }

    /**
     * Allows a user to become a surveyor.
     *
     * @param surveyorInput {@code SurveyorInputDAO}
     * @param request   The HttpServletRequest object.
     * @return          The ResponseModel object containing the result of the operation.
     */
    @Transactional
    public ResponseModel becomeSurveyor(SurveyorInputDAO surveyorInput, HttpServletRequest request) {
        ResponseModel responseModel = new ResponseModel();
        try {
            if (Objects.nonNull(surveyorInput)) {
                Optional<UserMaster> opUserMob = userRepository.findByUserMobileNumber(surveyorInput.getUserMobileNumber());
                if(Objects.nonNull(surveyorInput.getUserAadhaarHash())) {
                    List<UserMaster> opUserAadhar = userRepository.findByUserAadhaarHash(surveyorInput.getUserAadhaarHash());
                    if (!opUserAadhar.isEmpty()) {
                        return new ResponseModel(opUserAadhar.get(0).getUserAadhaarHash(), "User " + CustomMessages.RECORD_ALREADY_EXIST, CustomMessages.GET_DATA_ERROR, CustomMessages.FAILED, CustomMessages.METHOD_POST);
                    }
                }
                Optional<UserMaster> opUserEmail = userRepository.findByUserEmailAddress(surveyorInput.getUserEmailAddress());
                if (opUserMob.isPresent()) {
                    return new ResponseModel(opUserMob.get().getUserMobileNumber(), "User " + CustomMessages.RECORD_ALREADY_EXIST, CustomMessages.GET_DATA_ERROR, CustomMessages.FAILED, CustomMessages.METHOD_POST);
                } else if (opUserEmail.isPresent()) {
                    return new ResponseModel(opUserEmail.get().getUserEmailAddress(), "User " + CustomMessages.RECORD_ALREADY_EXIST, CustomMessages.GET_DATA_ERROR, CustomMessages.FAILED, CustomMessages.METHOD_POST);
                } else {
                    UserMaster surveyor = new UserMaster();
                    BeanUtils.copyProperties(surveyorInput, surveyor);
                    // if mobile or  email verified
                    surveyor.setIsEmailVerified(Objects.isNull(surveyorInput.getIsEmailVerified()) ? false : surveyorInput.getIsEmailVerified());
                    surveyor.setIsMobileVerified(Objects.isNull(surveyorInput.getIsMobileVerified()) ? false : surveyorInput.getIsMobileVerified());
                    surveyor.setIsAadhaarVerified(Objects.isNull(surveyorInput.getIsAadhaarVerified()) ? false : surveyorInput.getIsAadhaarVerified());
                    surveyor.setPinCode(surveyorInput.getPinCode());
                    surveyor.setUserFullName(surveyor.getUserFirstName() + " " + surveyor.getUserLastName());

                    //set department  PR
                    DepartmentMaster department =  departmentRepository.findByDepartmentType(DepartmentEnum.PRIVATE_RESIDENT.getValue()).orElse(null);
                    surveyor.setDepartmentId(department);

                    // Assign Surveyor Role
                    assignSurveyorRole(surveyor);

                    surveyor.setIsActive(true);
                    surveyor.setIsDeleted(false);

                    surveyor.setUserPassword(Objects.isNull(surveyorInput.getUserPassword())? null : encoder.encode(surveyorInput.getUserPassword()));
                    surveyor.setIsPasswordChanged(true);

                    surveyor.setCreatedOn(new Timestamp(new Date().getTime()));
                    surveyor.setCreatedIp(CommonUtil.getRequestIp(request));
                    surveyor.setUserStatus(statusRepository.findByIsDeletedFalseAndStatusCode(StatusEnum.PENDING.getValue()).getStatusCode());
                    String prId = getPrNumberforSurveyor(surveyor);
                    surveyor.setUserPrId(prId);
                    surveyor.setUserName(prId);
                    surveyor = userRepository.save(surveyor);
                    surveyor.setCreatedBy(surveyor.getUserId().toString());
                    surveyor = userRepository.save(surveyor);
                    addSurveyorBankDetail(surveyor,surveyorInput);
                    addCurrentSeasonAvailability(surveyor, request);
                    surveyorInput.setUserId(surveyor.getUserId());
                }
            }
            return new ResponseModel(surveyorInput.getUserId(), CustomMessages.RECORD_ADD, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
        } catch (Exception e) {
            e.printStackTrace();
            responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
                    CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
            return responseModel;
        }
    }

    @Transactional
    private UserMaster updateSurveyorBankDetail(UserMaster surveyor, SurveyorInputDAO surveyorInput) {
        UserBankDetail userBankDetail = userBankDetailRepository.findByUserId_UserId(surveyor.getUserId());
        Optional<BankMaster> bankOp = bankRepository.findByBankIdAndIsDeletedFalse(surveyorInput.getUserBankId());
        if (Objects.nonNull(userBankDetail) && Objects.nonNull(userBankDetail.getBankId()) && bankOp.isPresent() && Objects.nonNull(surveyorInput.getUserBankId())) {
            BankMaster bankMaster = bankOp.get();
            BeanUtils.copyProperties(surveyorInput, userBankDetail, "userBankDetailId", "createdBy", "createdIp",
                    "createdOn", "isDeleted", "isActive");
            userBankDetail.setBankId(bankMaster);
            userBankDetail.setUserBankName(bankMaster.getBankName());
            userBankDetail.setModifiedIp(surveyor.getModifiedIp());
            userBankDetail.setModifiedBy(surveyor.getModifiedBy());
            userBankDetail.setModifiedOn(new Timestamp(new Date().getTime()));
            userBankDetailRepository.save(userBankDetail);
        } else {
            addSurveyorBankDetail(surveyor, surveyorInput);
        }
        return surveyor;
    }



    /**
     * Generates a PR number for a surveyor.
     *
     * @param surveyor   The UserMaster object representing the surveyor.
     * @return           The generated PR number.
     */
    public String getPrNumberforSurveyor(UserMaster surveyor) {
        String prNumber = "SUR";
        
     String depCode = surveyor.getDepartmentId().getDepartmentCode();
//     Integer serialNumber =    userRepository. getUserCountForDepartmentAndVillage(surveyor.getDepartmentId().getDepartmentId(), surveyor.getUserVillageLGDCode());
     Integer serialNumber = userRepository.getUserCountForDepartmentAndVillageV2(depCode, surveyor.getUserVillageLGDCode(),surveyor.getRoleId().getRoleId());
     String department = (surveyor.getDepartmentId().getDepartmentCode());
     return prNumber+"_"+department+"_"+(serialNumber+1)+"_"+surveyor.getUserVillageLGDCode().toString();
    }

    /**
     * update surveyor status
     *
     * @param surveyorInput The {SurveyorInputDAO object containing surveyor details.
     * @param request The HttpServletRequest object.
     * @return
     */
    public ResponseModel updateBulkSurveyorStatus(SurveyorInputDAO surveyorInputDAO, HttpServletRequest request) {
        try {
            List<UserMaster> surveyors = userRepository.findByUserIdInOrderByUserFullNameAsc(surveyorInputDAO.getUserIds());
            if (surveyors.size()> 0) {
                for (UserMaster user: surveyors) {
                    user.setUserStatus(StatusEnum.valueOf(surveyorInputDAO.getUserStatus()).getValue());
                    if(surveyorInputDAO.getUserStatus().equals(StatusEnum.REJECTED.toString())) {
                        user.setUserRejectionReason(surveyorInputDAO.getUserRejectionReason());
                        SurveyTaskAllocationFilterDAO surveyTaskInput = new SurveyTaskAllocationFilterDAO();
                        surveyTaskInput.setSeasonId(surveyorInputDAO.getSeasonId());
                        surveyTaskInput.setYear(surveyorInputDAO.getEndingYear());
                        List<Long> userIds = new ArrayList<>();
                        userIds.add(user.getUserId());
                        surveyTaskInput.setUserList(userIds);
                        userLandAssignmentService.removeLandByUserFilter(request,surveyTaskInput);
                    } else{
                        user.setUserRejectionReason(null);
                    }
                    user.setModifiedOn(new Timestamp(new Date().getTime()));
                    user.setModifiedIp(CommonUtil.getRequestIp(request));
                    user.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
                    userRepository.save(user);
                }
                // Notify User with SMS + Email
                try {
                    messageConfigurationService.sendEmailToSurveyorForApprovalRejection(surveyors, surveyorInputDAO.getUserStatus());
                    messageConfigurationService.sendMessageToSurveyorForApprovalRejection(surveyors, surveyorInputDAO.getUserStatus(), "Crop Surveyor");
                } catch (Exception e){
                    return new ResponseModel(surveyors.size() +" updated.", e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
                }


                return new ResponseModel(surveyors.size() +" updated.", "Surveyor " + CustomMessages.RECORD_UPDATE, CustomMessages.UPDATE_SUCCESSFULLY, CustomMessages.SUCCESS, CustomMessages.METHOD_GET);
            } else {
                return new ResponseModel(null, CustomMessages.USER_ID_NOT_FOUND, CustomMessages.NO_DATA_FOUND, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR,
                    CustomMessages.FAILED, CustomMessages.METHOD_GET);
        }
    }

    /**
     * get Surveyor list
     * @param surveyorInput The {@link SurveyorInputDAO} object containing surveyor details.
     * @param request The HttpServletRequest object.
     * @return {@code SurveyorOutputDAO}
     */
    public ResponseModel getRegisteredSurveyor(SurveyorInputDAO requestInput, HttpServletRequest request) {
        ResponseModel responseModel = new ResponseModel();
        List<SurveyorOutputDAO> surveyorList = new ArrayList<>();
        List<UserMaster> users = new ArrayList<>();
        SowingSeason season = new SowingSeason();
        try {
            RoleMaster role = roleRepository.findByCode(RoleEnum.SURVEYOR.toString()).orElse(null);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            SowingSeason sowingSeason = generalService.getCurrentSeason();
            // Parse dates from the sowingSeason object
            Date startDate = simpleDateFormat.parse(sowingSeason.getStartingMonth());
            System.out.println(sowingSeason.getStartingMonth());
            Timestamp startTimestamp = new Timestamp(startDate.getTime());

            // Date endDate = simpleDateFormat.parse(sowingSeason.getEndingMonth());
            System.out.println("startTimestamp "+startTimestamp);


            users = userRepository.findAllByBoundarySelection(requestInput.getDepartmentId(),
                    requestInput.getVillageLgdCodeList(), requestInput.getSubDistrictLgdCodeList(), requestInput.getDistrictLgdCodeList(), requestInput.getStateLGDCodeList(), role,startTimestamp);
            List<Long> userIds = users.stream().distinct().map(UserMaster::getUserId).collect(Collectors.toList());
            List<UserAvailability> userAvailabilities = userAvailabilityRepository.findUserAvailabilityInSeason(requestInput.getStartingYear(),requestInput.getEndingYear(), requestInput.getSeasonId(), userIds, true, false);


            users.stream().forEach(ele -> {
                List<UserAvailability> userAvailability = userAvailabilities.stream().filter(x -> x.getUserId().getUserId().equals(ele.getUserId())).collect(Collectors.toList());
                if(userAvailability.size() > 0) {
                    ele.setIsAvailable(userAvailability.get(0).getIsAvailable());
                } else {
                    ele.setIsAvailable(false);
                }
            });
            Set<Long> availableUserIds = userAvailabilities.stream()
                    .map(ua -> ua.getUserId().getUserId())
                    .collect(Collectors.toSet());

            System.out.println("availableUserIds "+availableUserIds);
            users = users.stream()
                    .filter(user -> availableUserIds.contains(user.getUserId()))
                    .collect(Collectors.toList());
            System.out.println("users "+users);
            surveyorList = getSurveyOutFromUserMaster(users, season,Integer.parseInt(requestInput.getStartingYear()),Integer.parseInt(requestInput.getEndingYear()),requestInput.getSeasonId());
            return new ResponseModel(surveyorList, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
        } catch (Exception e) {
            e.printStackTrace();
            responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
                    CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
            return responseModel;
        }
    }
    
    /**
     * Checks if there has been a season change.
     *
     * @param request   The HttpServletRequest object.
     * @return          Boolean indicating if there has been a season change.
     */
    public Boolean checkSeasonChange(HttpServletRequest request) {
        seasonChangeScheduler.seasonChangeScheduler();
        return true;
    }
    /**
     * perform the clear user details by id.
     *
     * @param inputDAO The {@code UserInputDAO} object containing surveyor details.
     * @param request The HttpServletRequest object.
     * @return  The ResponseModel object representing the response.
     */
	public ResponseModel clearUserDetailsById(UserInputDAO inputDAO) {
		ResponseModel responseModel = null;
		try {
			
		farmlandPlotRegistryRepository.clearUserDetailsById(inputDAO.getUserId());
		userAvailabilityRepository.clearUserDetailsById(inputDAO.getUserId());
		
		 responseModel =  CustomMessages.makeResponseModel(null, CustomMessages.RECORD_UPDATE,
				 CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			return responseModel;
		} catch (Exception e) {
			responseModel = CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			return responseModel;
		}
	}

    public void sendSMSForConsentAccessReuqest(ACMMsgRequestDAO dao) {
        MessageCredentialMaster messageCredentialMaster = messageCredentialRepository
                .findByMessageCredentialType("MOBILE").get();

        // findByTemplateId
        MessageConfigurationMaster messageConfiguartionMaster = messageConfigurationRepository
                .findByTemplateId(Constants.FARMER_CONSENT_ACCESS_REQUEST_SMS_TEMPLATE_ID).get();

        if (!messageConfiguartionMaster.getTemplate().isEmpty()) {
            String template = messageConfiguartionMaster.getTemplate();
            String smsTemplate = template.replace("{$1}", dao.getFarmerName()).replace("{$2}", dao.getAiuName())
                    .replace("{$3}", dao.getAipName()).replace("{$4}", dao.getPurpose()).replace("{$5}", dao.getAcmUrl())
                    .replace("{$6}", dao.getAcmRequestId()).replace("{$7}", dao.getAcmName());

            messageConfigurationService.sendOTP(messageCredentialMaster.getHost(), messageCredentialMaster.getUserName(), messageCredentialMaster.getPassword(), dao.getFarmerMobileNo(), smsTemplate);
        }
    }


    /**
     * get Surveyor list V2
     * @param request
     * @return
     */
    public ResponseModel getAllSurveyorV2(SurveyorInputDAO requestInput, HttpServletRequest request) {
        ResponseModel responseModel = new ResponseModel();
        try {
            RoleMaster role = roleRepository.findByCode(RoleEnum.SURVEYOR.toString()).orElse(null);
            String userId = CustomMessages.getUserId(request, jwtTokenUtil);
            String departmentIds = getJoinedValueFromList(requestInput.getDepartmentIds());
            String response = userRepository.getAllSurveyorV2(Long.parseLong(userId), departmentIds, requestInput.getSeasonId(),
                    Integer.parseInt(requestInput.getStartingYear()),Integer.parseInt(requestInput.getEndingYear()));
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            TypeFactory typeFactory = mapper.getTypeFactory();
            List<Object> resultObject = mapper.readValue(response,
                    typeFactory.constructCollectionType(List.class, Object.class));

            return new ResponseModel(resultObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
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

    /**
     * perform the update user status.
     *
     * @param surveyorInput The {@code SurveyorInputDAO} object containing surveyor details.
     * @param request The HttpServletRequest object.
     * @return  The ResponseModel {@link UserMaster} object representing the response.
     */
    public ResponseModel updateUserStatus(SurveyorInputDAO surveyorInput, HttpServletRequest request) {
        try {
            Optional<UserMaster> user = userRepository.findByUserId(surveyorInput.getUserId());
            if(user.get() != null){
                if(surveyorInput.getStatus() == 1){ 
                    user.get().setIsActive(true);
                    user.get().setIsDeleted(false);
                }
                userRepository.save(user.get());
            }
           return new ResponseModel(user, CustomMessages.RECORD_UPDATE, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseModel(e.getMessage(), e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR,
                    CustomMessages.FAILED, CustomMessages.FAILED);
        }
    }

}
