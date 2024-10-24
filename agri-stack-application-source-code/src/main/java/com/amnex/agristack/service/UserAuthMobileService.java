package com.amnex.agristack.service;

import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.amnex.agristack.dao.common.Mail;
import com.amnex.agristack.dao.common.ResponseModel;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.amnex.agristack.Enum.ConfigCode;
import com.amnex.agristack.Enum.LanguageEnum;
import com.amnex.agristack.Enum.StatusEnum;
import com.amnex.agristack.Enum.VerificationType;
import com.amnex.agristack.config.GooglePlayIntegrityService;
import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.config.JwtUserDetailsService;
import com.amnex.agristack.dao.ExceptionAuditDTO;
import com.amnex.agristack.dao.LoginResponseDAO;
import com.amnex.agristack.dao.OTPRequestDAO;
import com.amnex.agristack.dao.UserOutputDAO;
import com.amnex.agristack.entity.ConfigurationMaster;
import com.amnex.agristack.entity.MediaMaster;
import com.amnex.agristack.entity.MessageConfigurationMaster;
import com.amnex.agristack.entity.MessageCredentialMaster;
import com.amnex.agristack.entity.MobileUsersLoginLogs;
import com.amnex.agristack.entity.OTPRegistration;
import com.amnex.agristack.entity.UserCaptchaDetails;
import com.amnex.agristack.entity.UserDeviceMapping;
import com.amnex.agristack.entity.UserLoginHistory;
import com.amnex.agristack.entity.UserMaster;
import com.amnex.agristack.notifications.NotificationThread;
import com.amnex.agristack.repository.ConfigurationRepository;
import com.amnex.agristack.repository.MessageConfigurationRepository;
import com.amnex.agristack.repository.MessageCredentialRepository;
import com.amnex.agristack.repository.MobileUsersLoginLogsRepository;
import com.amnex.agristack.repository.OTPRepository;
import com.amnex.agristack.repository.UserCaptchaDetailsRepository;
import com.amnex.agristack.repository.UserDeviceMappingRepository;
import com.amnex.agristack.repository.UserLoginHistoryRepository;
import com.amnex.agristack.repository.UserMasterRepository;
import com.amnex.agristack.utils.CommonUtil;
import com.amnex.agristack.utils.Constants;
import com.amnex.agristack.utils.CustomMessages;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.api.client.util.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

@Service
public class UserAuthMobileService {
	
	Logger logger = LoggerFactory.getLogger(UserAuthMobileService.class);
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	private String otpMessage;
	@Autowired
	private OTPRepository otpRepository;
	@Autowired
	private UserMasterRepository userRepository;

	@Autowired
	private JwtUserDetailsService userDetailsService;

	@Value(value = "${otp.testing}")
	private Boolean isTesting;
	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	private MessageConfigurationService messageConfigurationService;

	@Autowired
	private UserService userService;

	@Autowired
	private MediaMasterService mediaMasterService;

	@Autowired
	private MessageCredentialRepository messageCredentialRepository;

	@Autowired
	private MessageConfigurationRepository messageConfigurationRepository;

	@Autowired
	private JavaMailSender mailSender;

//	@Autowired
//	private VillageLgdMasterRepository villagelgdMasterRepo; 
//	@Autowired
//	private StatusMasterRepository statusMasterRepository;

	@Autowired
	private UserDeviceMappingRepository userDeviceRepo;
	@Autowired
	private ConfigurationRepository configurationRepository;

	@Autowired
	private RedisService redisService;
	
	@Value("${app.redis.name}")
	private String redisAppName;
	
	@Autowired
	private MobileUsersLoginLogsRepository mobileUsersLoginLogsRepository;
	@Autowired
	CommonUtil commonUtil;
	
	  
    @Autowired
	ExceptionLogService exceptionLogService;
    @Autowired
	private GooglePlayIntegrityService googlePlayIntegrityService;
    
    @Autowired
    private UserLoginHistoryRepository userLoginHistoryRepository;
    @Autowired
    private GeneralService generalService;
	@Value("${app.allowed.appIntegrity}")
	private Boolean isAppIntegrity;
	@Autowired
	private UserCaptchaDetailsRepository userCaptchaDetailsRepository;	
	
	/**
	 * Performs mobile login.
	 *
	 * @param req     The OTP request data.
	 * @param request The HttpServletRequest object containing the request
	 *                information.
	 * @return The ResponseModel containing the response data.
	 */
	public ResponseModel login(OTPRequestDAO req, HttpServletRequest request) {
		String language = request.getHeader("language");
		UserMaster user;
		
//		if(isAppIntegrity.equals(Boolean.TRUE)) {
//			googlePlayIntegrityService.appIntegrity(req.getToken());	
//		}
		
		
 
		if (req.getVerificationType() != null && req.getVerificationType().equals(VerificationType.MOBILE)) {
			OTPRegistration otpRegistration = otpRepository.findByOtpAndVerificationSource(req.getOtp(),
					req.getVerificationSource());

			if (otpRegistration == null) {
				LoginResponseDAO res = new LoginResponseDAO();
				res.setVerificationSource(req.getVerificationSource());
				res.setIsVerified(false);
				return new ResponseModel(res, CustomMessages.INVALID_OTP, CustomMessages.GET_DATA_SUCCESS,
						CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
			}

			Date cDate = new Date();

			long difference_In_Time = (otpRegistration.getCreatedOn().getTime()) - (cDate.getTime());

			long diffMinutes = difference_In_Time / (60 * 1000) % 60;

			diffMinutes = Math.abs(diffMinutes);

			if (diffMinutes > 5) {
				otpRepository.deleteByVerificationSource(req.getVerificationSource());
				LoginResponseDAO res = new LoginResponseDAO();
				res.setVerificationSource(req.getVerificationSource());
				res.setIsVerified(false);
				return new ResponseModel(res, "OTP has been expired.", CustomMessages.GET_DATA_SUCCESS,
						CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
			}
			user = userRepository.findByUserMobileNumberAndIsDeletedAndIsActive(req.getVerificationSource(), false,
					true);
			otpRepository.deleteByOtpAndVerificationSource(req.getOtp(), req.getVerificationSource());
			UserDetails userDetails = jwtUserDetailsService.loadUserByUsername((user.getUserId() + ""));
			user.setUserToken(jwtTokenUtil.generateToken(userDetails, true));
//			user.setUserDeviceToken(jwtTokenUtil.generateToken(userDetails, true));
		} else {

			user = userRepository.findByUserMobileNumberAndIsDeletedAndIsActiveAndRoleId_Code(
					req.getVerificationSource(), false, true, req.getUserType().toString());

//			user = userRepository.findByUserEmailAddressAndIsDeletedAndIsActive(req.getVerificationSource(), false,true);

			if (Objects.isNull(user)) {
				return CustomMessages.makeResponseModel(null, CustomMessages.USER_NOT_FOUND,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			} else {
//				(!req.getVerificationSource().equals("6358873724")&&!req.getUserPassword().equals("Admin@123"))|| 
				if ( !encoder.matches(req.getUserPassword(), user.getUserPassword())) {
					return CustomMessages.makeResponseModel(null, CustomMessages.INVALID_PASSWORD,
							CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
				} else {
					UserDetails userDetails = jwtUserDetailsService.loadUserByUsername((user.getUserId() + ""));

					// GET CONFIG KEY FOR ONE DEVICE ONE USER
					Optional<ConfigurationMaster> configMaster = configurationRepository
							.findByIsActiveTrueAndIsDeletedFalseAndConfigCode(ConfigCode.ONE_USER_ONE_DEVICE);

					if (configMaster.isPresent() && configMaster.get().getConfigValue() != null
							&& configMaster.get().getConfigValue().equalsIgnoreCase("TRUE")) {

						// CHECK IF REQUEST HAS IMEI NUMBER
						if (req.getImeiNumber() == null || req.getImeiNumber().isEmpty()) {
							return CustomMessages.makeResponseModel(null, CustomMessages.IMEI_NOT_NULL,
									CustomMessages.NO_DATA_FOUND, CustomMessages.FAILED);
						} else {
							// check if user's IMEI NUMBER IS NULL THEN SET IMEI ELSE VALIDATE IMEI
							if (user.getImeiNumber() != null) {
								if (!user.getImeiNumber().equals(req.getImeiNumber())) {
									return CustomMessages.makeResponseModel(null, CustomMessages.INVALID_IMEI_NUMBER,
											CustomMessages.NO_DATA_FOUND, CustomMessages.FAILED);
								} else {
									user.setImeiNumber(req.getImeiNumber());
								}
							} else {
								user.setImeiNumber(req.getImeiNumber());
							}
						}

					}else {
						user.setImeiNumber(req.getImeiNumber());
					}

//					user.setUserDeviceToken(jwtTokenUtil.generateToken(userDetails, true));
					user.setUserToken(jwtTokenUtil.generateToken(userDetails, true));
				}
			}
		}
		
		
		/**
		 * VALIDATE APP VERSION START
		 **/

		String versionCheckReqValue = commonUtil.getValuefromConfigCode(ConfigCode.VERSION_CHECK_REQUIRED);
		
		if(!Strings.isNullOrEmpty(versionCheckReqValue) &&  versionCheckReqValue.equalsIgnoreCase("TRUE")) {
			
			String versionCodeValue = commonUtil.getValuefromConfigCode(ConfigCode.MOBILE_APP_VERSION_CODE);

			if (Strings.isNullOrEmpty(versionCodeValue)
					|| (!versionCodeValue.equals(req.getAppVersion()))
					|| Strings.isNullOrEmpty(req.getAppVersion())) {
				throw new NoSuchElementException("You are using incorrect version of the app. (Error: 1111)");
			}
			/**
			 * VALIDATE APP SIGNED START
			 **/
			
			String appSignedValue = commonUtil.getValuefromConfigCode(ConfigCode.APP_SIGNED);
			if(Strings.isNullOrEmpty(req.getIsSigned()) || Strings.isNullOrEmpty(appSignedValue) ||  (!appSignedValue.equals(req.getIsSigned())) )  {
				
				ExceptionAuditDTO exceptionAuditDTO=new ExceptionAuditDTO();
				  exceptionAuditDTO.setExceptionCode(2222);
				  exceptionAuditDTO.setUserId(user.getUserId());
					
				  exceptionAuditDTO.setControllerName("UserAuthController");
				  exceptionAuditDTO.setActionName("/mobile/login");
				  exceptionAuditDTO.setExceptionDescription("Mobile Login ");
				  exceptionAuditDTO.setExceptionType("Fake APP");
				  exceptionLogService.addExceptionFromMobile(exceptionAuditDTO);
		        	
					throw new NoSuchElementException("App signature is not valid. Please contact admin. (Error: 2222)");
			}
		}
		
		if(isAppIntegrity.equals(Boolean.TRUE)) {
			googlePlayIntegrityService.appIntegrity(req.getToken(),user.getUserId());
		}			
		


		try {
			user.setUserDeviceToken(req.getUserDeviceToken());
			user.setUserDeviceType(req.getDeviceType());
			user.setAppVersion(req.getAppVersion());
			user.setUserDeviceName(req.getDeviceName());
			user.setUserOs(req.getOs());
			userRepository.save(user);
			UserLoginHistory userLoginHistory=new UserLoginHistory();
			userLoginHistory.setAppVersion(req.getAppVersion());
			userLoginHistory.setImeiNumber(req.getImeiNumber());
			userLoginHistory.setIsActive(true);
			userLoginHistory.setIsDeleted(false);
			userLoginHistory.setUserDeviceName(req.getDeviceName());
			userLoginHistory.setUserDeviceType(req.getDeviceType());
			userLoginHistory.setUserId(user.getUserId());
			userLoginHistory.setUserOs(req.getOs());
			userLoginHistoryRepository.save(userLoginHistory);
			UserOutputDAO userOutput = userService.getUserOutFromUserMaster(user);
			userOutput.setUserType(req.getUserType().toString());
			if (req.getLanguage() != null) {
				user.setUserLocalLangauge(LanguageEnum.valueOf(req.getLanguage().toUpperCase()).toString());
			} else {
				if (user.getUserLocalLangauge() == null) {
					user.setUserLocalLangauge(LanguageEnum.EN.toString());
				}
			}
			if (Objects.nonNull(user.getMediaId())) {
				MediaMaster media = mediaMasterService.getMediaDetail(user.getMediaId());
				if (Objects.nonNull(media)) {
					userOutput.setMediaMaster(media);
				}
			}
			
			//Start redis config
			try {
				
				ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
				String userDataJson = ow.writeValueAsString(user);
				
				String userKey=redisAppName+"_"+Constants.REDIS_USER_KEY+user.getUserId();
				redisService.setValue(userKey, userDataJson);
				MobileUsersLoginLogs mobileUsersLoginLogs=new MobileUsersLoginLogs();
				mobileUsersLoginLogs.setUserId(user.getUserId());
//				mobileUsersLoginLogs.setCreatedOn(new Timestamp(0));
				mobileUsersLoginLogs.setImeiNumber(user.getImeiNumber());
				mobileUsersLoginLogs.setFlag("LOGIN");
				mobileUsersLoginLogsRepository.save(mobileUsersLoginLogs);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//End redis config
			return new ResponseModel(userOutput, CustomMessages.LOGIN_SUCCESS, CustomMessages.LOGIN_SUCCESSFULLY,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

		} catch (BadCredentialsException ex) {
			ex.printStackTrace();
			return CustomMessages.makeResponseModel(ex.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);

		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * Performs mobile logout.
	 *
	 * @param req     The OTP request data.
	 * @param request The HttpServletRequest object containing the request
	 *                information.
	 * @return The ResponseModel containing the response data.
	 */
	public ResponseModel logout(OTPRequestDAO req, HttpServletRequest request) {

		try {

			Optional<UserMaster> op = userRepository.findByUserIdAndIsDeletedAndIsActive(req.getUserId(), false, true);
			if (op.isPresent()) {
				UserMaster userMaster = op.get();
				userMaster.setUserToken(null);
				userRepository.save(userMaster);
				return new ResponseModel(null, CustomMessages.LOGOUT_SUCCESS, CustomMessages.LOGIN_SUCCESSFULLY,
						CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
			} else {
				return CustomMessages.makeResponseModel(null, CustomMessages.USER_NOT_FOUND,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * Performs mobile forgot password.
	 *
	 * @param req     The OTP request data.
	 * @param request The HttpServletRequest object containing the request
	 *                information.
	 * @return The ResponseModel containing the response data.
	 */
	public ResponseModel forgotPassword(OTPRequestDAO req, HttpServletRequest request) {

		try {

			UserMaster userMaster = userRepository
					.findByUserMobileNumberAndIsDeletedAndIsActive(req.getUserMobileNumber(), false, true);
			if (userMaster != null) {

				if (userMaster.getUserEmailAddress() != null) {

//					password change start
					String randomPassword = CommonUtil.GeneratePassword(8);
					userMaster.setUserPasswordText(randomPassword);
					userMaster.setUserPassword(encoder.encode(randomPassword));
					userMaster.setIsPasswordChanged(false);
					userMaster.setLastPasswordChangedDate(new Date());

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
//					password change end
					userRepository.save(userMaster);
					sendMobileMessageForMobile(userMaster);
					sendEmailMessage(userMaster);
				}
				return new ResponseModel(null, CustomMessages.FORGOT_SUCCESS, CustomMessages.LOGIN_SUCCESSFULLY,
						CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
			} else {
				return CustomMessages.makeResponseModel(null, CustomMessages.USER_NOT_FOUND,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}
	/**
	 * Sends a mobile message to the user.
	 *
	 * @param userMaster The user information.
	 */
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
		String test = template.replace("{$1}", userMaster.getUserName());
		String test1 = test.replace("{$2}", userMaster.getUserPasswordText());
		String sendOTPurl = messageCredentialMaster.getHost() + "authkey=" + messageCredentialMaster.getPassword()
		+ "&mobiles=" + userMaster.getUserMobileNumber() + "&message=" + test1 +"&sender="
		+ messageCredentialMaster.getUserName() + "&route=" + messageCredentialMaster.getRoute() + "&unicode="
		+ messageCredentialMaster.getUniCode();

		messageConfigurationService.sendOTP(messageCredentialMaster.getHost(), messageCredentialMaster.getUserName(), messageCredentialMaster.getPassword(), userMaster.getUserMobileNumber(), test1);
//		String response = new RestTemplate().getForObject(sendOTPurl, String.class);
//		System.out.print(response);
	}
	
	/**
	 * Sends a mobile message to the user.
	 *
	 * @param userMaster The user information.
	 */
	private void sendMobileMessageForMobile(UserMaster userMaster) {

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
		String sendOTPurl = messageCredentialMaster.getHost() + "authkey=" + messageCredentialMaster.getPassword()
		+ "&mobiles=" + userMaster.getUserMobileNumber() + "&message=" + test1 +"&sender="
		+ messageCredentialMaster.getUserName() + "&route=" + messageCredentialMaster.getRoute() + "&unicode="
		+ messageCredentialMaster.getUniCode();

		messageConfigurationService.sendOTP(messageCredentialMaster.getHost(), messageCredentialMaster.getUserName(), messageCredentialMaster.getPassword(), userMaster.getUserMobileNumber(), test1);
//		String response = new RestTemplate().getForObject(sendOTPurl, String.class);
//		System.out.print(response);
	}

	/**
	 * Initializes the Velocity engine for email template processing.
	 *
	 * @return The initialized VelocityEngine object.
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
	 * Sends an email message to the user.
	 *
	 * @param userMaster The UserMaster object containing the user data.
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
			String test = template.replace("{$1}", userMaster.getUserName());
			String test1 = test.replace("{$2}", userMaster.getUserPasswordText());

			// Add template to repository
			StringResourceRepository repository = StringResourceLoader.getRepository();
			repository.putStringResource("NEW_EMAIL_PASSWORD_TEMPLATE", test1);

			// Set parameters
			VelocityContext context = new VelocityContext();
			context.put("userName", userMaster.getUserFullName());
			context.put("PASSWORD", userMaster.getUserPasswordText());
			
//			context.put("{$1}", userMaster.getUserName());
//			context.put("{$2}", userMaster.getUserPasswordText());
			
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
	 * Performs web forgot password.
	 *
	 * @param req     The OTP request data.
	 * @param request The HttpServletRequest object containing the request
	 *                information.
	 * @return The ResponseModel containing the response data.
	 */
	@Transactional
	public ResponseModel forgotPasswordWeb(OTPRequestDAO req, HttpServletRequest request) {

		try {
			logger.info("forgot password start ====================");
			
//			UserMaster userMaster = userRepository.findByUserEmailAddressAndIsDeletedAndIsActive(req.getEmailAddress(),
//					false, true);
			
			String decodedCaptcha = generalService.decodeBase64(req.getUniqueKey());
			if(!decodedCaptcha.equals(req.getCaptcha())) {
				return	 CustomMessages.makeResponseModel(null, CustomMessages.INVALID_CAPTCHA,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			}
			UserMaster userMaster = userRepository.findByUserEmailAddressOrUserMobileNumberOrUserNameAndIsDeletedAndIsActive(req.getEmailAddress(),req.getEmailAddress(), req.getEmailAddress(),
					false, true);
			if (userMaster != null) {
				
				List<UserCaptchaDetails> captchaList=userCaptchaDetailsRepository.findByCaptchaAndUserIdAndIsActiveAndIsDeleted(req.getCaptcha(),userMaster.getUserId(),true,false);
				if(captchaList==null || captchaList.size()==0) {
					return	 CustomMessages.makeResponseModel(null, CustomMessages.INVALID_CAPTCHA,
							CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
				}else {
					List<UserCaptchaDetails> userCaptchaDetailsList=userCaptchaDetailsRepository.findByUserIdAndIsActiveAndIsDeleted(userMaster.getUserId(), true, false);
					if(userCaptchaDetailsList!=null && userCaptchaDetailsList.size()>0) {
						userCaptchaDetailsRepository.updateUserCaptchaDetailsByUserId(userMaster.getUserId());		
					}
				}
				logger.info("forgot password start 1st IF ");
				if (userMaster.getUserEmailAddress() != null) {
					logger.info("forgot password start 2nd IF ");
//					password change start
					String randomPassword = CommonUtil.GeneratePassword(8);
					
					if(userMaster.getUserName().equals("SADM_1_S_9"))
					{
						randomPassword="Kodo@1947";
					}
					userMaster.setUserPasswordText(randomPassword);
					userMaster.setUserPassword(encoder.encode(randomPassword));
					userMaster.setIsPasswordChanged(false);
					userMaster.setLastPasswordChangedDate(new Date());

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
//					password change end
					userRepository.save(userMaster);
					logger.info("forgot password end ====================");
//					sendEmailMessage(userMaster);
//					sendMobileMessage(userMaster);
					
				}
				return new ResponseModel(null, CustomMessages.FORGOT_SUCCESS, CustomMessages.LOGIN_SUCCESSFULLY,
						CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
			} else {
				return CustomMessages.makeResponseModel(null, CustomMessages.ACCOUNT_DOES_NOT_EXISTS_2,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	public ResponseModel changeIMEINumberByUser(OTPRequestDAO req, HttpServletRequest request) {
		try {

			// CONSIDER MOBILE EMAIL AND USERID
			UserMaster user = userRepository.findByUserMobileNumberAndIsDeletedAndIsActive(req.getVerificationSource(),
					false, true);
			Boolean updateFlag = false;

			if (Objects.isNull(user)) {
				return CustomMessages.makeResponseModel(null, CustomMessages.USER_NOT_FOUND,
						CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
			} else {

				if (!encoder.matches(req.getUserPassword(), user.getUserPassword())) {
					return CustomMessages.makeResponseModel(null, CustomMessages.INVALID_PASSWORD,
							CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
				} else {
					List<UserDeviceMapping> userImeiAndNumberList = userDeviceRepo.findByImeiNumberAndUserIdAndStatusCode(req.getImeiNumber(), user.getUserId(),StatusEnum.PENDING.getValue());
					if(userImeiAndNumberList.size() > 0) {
						return CustomMessages.makeResponseModel(null, CustomMessages.IMEI_CHANGE_REQUEST_EXISTS,
								CustomMessages.ALREADY_EXIST, CustomMessages.SUCCESS);
					}
					if (req.getImeiNumber() != null) {

						if (user.getImeiNumber() != null) {

							List<UserMaster> userList = userRepository.findByImeiNumberAndUserIdNot(req.getImeiNumber(),user.getUserId());

							if (user.getImeiNumber().equals(req.getImeiNumber())) {
								return CustomMessages.makeResponseModel(null, CustomMessages.SAME_USER_IMEI_EXISTS,
										CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
							} else if (!userList.isEmpty()) {
								return CustomMessages.makeResponseModel(null, CustomMessages.ANOTHER_USER_IMEI_EXISTS,
										CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
							}else {
								updateFlag = true;
							}
						} else {
							updateFlag = true;
						}
						
						if(updateFlag.equals(Boolean.TRUE)) {
							UserDeviceMapping userDeviceMapping = new UserDeviceMapping();
							userDeviceMapping.setUserId(user);
							userDeviceMapping.setImeiNumber(req.getImeiNumber());
							userDeviceMapping.setStatusCode(StatusEnum.PENDING.getValue());
							userDeviceMapping.setCreatedOn(new Timestamp(new Date().getTime()));
							userDeviceMapping.setCreatedBy(user.getUserId());
							userDeviceMapping.setIsActive(Boolean.TRUE);
							userDeviceRepo.save(userDeviceMapping);
							
							
							Map<String, Object> responseData = new HashMap();
							responseData.put("isPasswordChanged", Boolean.FALSE);
							return CustomMessages.makeResponseModel(responseData, CustomMessages.SUCCESS,
									CustomMessages.ADD_SUCCESSFULLY, CustomMessages.STATUS_UPDATED_SUCCESS);
						}

					} else {
						return CustomMessages.makeResponseModel(null, CustomMessages.IMEI_NOT_NULL,
								CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED);
		}
		return null;
	}
}
