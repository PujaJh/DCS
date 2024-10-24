package com.amnex.agristack.service;

import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import com.amnex.agristack.Enum.StatusEnum;
import com.amnex.agristack.dao.OTPRequestDAO;
import com.amnex.agristack.dao.SurveyorInputDAO;
import com.amnex.agristack.dao.common.Mail;
import com.amnex.agristack.entity.UserMaster;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.amnex.agristack.Enum.VerificationType;
import com.amnex.agristack.dao.UserInputDAO;
import com.amnex.agristack.entity.MessageConfigurationMaster;
import com.amnex.agristack.entity.MessageCredentialMaster;
import com.amnex.agristack.entity.OTPRegistration;
import com.amnex.agristack.notifications.NotificationThread;
import com.amnex.agristack.repository.MessageConfigurationRepository;
import com.amnex.agristack.repository.MessageCredentialRepository;
import com.amnex.agristack.utils.Constants;

/**
 * @author kinnari.soni
 *
 *         22 Feb 2023 10:39:25 am
 */

@Service
public class MessageConfigurationService {

	@Autowired
	private MessageCredentialRepository messageCredentialRepository;

	@Autowired
	private MessageConfigurationRepository messageConfigurationRepository;

	@Autowired
	JavaMailSender mailSender;

	@Autowired
	private Environment environment;

	/**
	 * Initializes the bean after instantiation and dependency injection.
	 * This method is annotated with @PostConstruct to indicate that it should be executed
	 * once the bean is fully initialized.
	 */
	@PostConstruct
	public void init() {
		try {
			// Get credential for email (username, password, host and port)
			Optional<MessageCredentialMaster> credentialDetails = messageCredentialRepository
					.findByMessageCredentialType(Constants.EMAIL_TEMPLATE);

			JavaMailSenderImpl ms = (JavaMailSenderImpl) mailSender;
			ms.setHost(credentialDetails.get().getHost());
			ms.setPort(credentialDetails.get().getPort());

			ms.setUsername(credentialDetails.get().getUserName());
			ms.setPassword(credentialDetails.get().getPassword());

			Properties props = ms.getJavaMailProperties();
			props.put("mail.smtp.auth", credentialDetails.get().getAuth());
			props.put("mail.smtp.starttls.enable", credentialDetails.get().getStartTLS());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends OTP via mobile and email based on the provided OTPRegistration and OTPRequestDAO.
	 *
	 * @param otpRegistration The OTPRegistration object containing the OTP details.
	 * @param otpRequestDAO The OTPRequestDAO object containing the OTP request details.
	 */
	public void sendOTPMobilEmail(OTPRegistration otpRegistration,OTPRequestDAO otpRequestDAO) {
		init();
		if (otpRegistration.getVerificationType().equals(VerificationType.EMAIL)) {
			sendEmailOTP(otpRegistration,otpRequestDAO);
		} else if (otpRegistration.getVerificationType().equals(VerificationType.MOBILE)) {
			sendMobileOTP(otpRegistration);
		}
	}

	/**
	 * Sends update OTP via mobile and email based on the provided OTPRegistration.
	 *
	 * @param otpRegistration The OTPRegistration object containing the OTP details.
	 */
	public void sendUserUpdateOTPMobilEmail(OTPRegistration otpRegistration) {
		if (otpRegistration.getVerificationType().equals(VerificationType.EMAIL)) {
			sendUserUpdateEmailOTP(otpRegistration);
		} else if (otpRegistration.getVerificationType().equals(VerificationType.MOBILE)) {
			sendUserUpdateMobileOTP(otpRegistration);
		}
	}

	/**
	 * Sends OTP via mobile based on the provided OTPRegistration.
	 *
	 * @param otpRegistration The OTPRegistration object containing the OTP details.
	 */
	private void sendMobileOTP(OTPRegistration otpRegistration) {

		MessageCredentialMaster messageCredentialMaster = messageCredentialRepository
				.findByMessageCredentialType("MOBILE").get();
		// findByTemplateId
		MessageConfigurationMaster messageConfiguartionMaster = messageConfigurationRepository
				.findByTemplateId(Constants.OTP_VERIFICATION_TEMPLATE_ID).get();
		// DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy
		// HH:mm:ss");
		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String dateString = simpleDateFormat.format(date);
		String template = messageConfiguartionMaster.getTemplate();
		String test = template.replace("{$1}", otpRegistration.getVerificationType().toString())
				.replace("{$2}", otpRegistration.getOtp()).replace("{$3}", "Crop Survey")
				.replace("{$4}", dateString).replace("{$5}", "5");
//		String sendOTPurl = messageCredentialMaster.getHost() + "authkey=" + messageCredentialMaster.getPassword()
//				+ "&mobiles=" + otpRegistration.getVerificationSource() + "&message=" + test + "&sender="
//				+ messageCredentialMaster.getUserName() + "&route=" + messageCredentialMaster.getRoute() + "&unicode="
//				+ messageCredentialMaster.getUniCode();
//
//		String response = new RestTemplate().getForObject(sendOTPurl, String.class);
//		System.out.print(response);
		sendOTP(messageCredentialMaster.getHost(), messageCredentialMaster.getUserName(), messageCredentialMaster.getPassword(), otpRegistration.getVerificationSource(), test);
		
	}

	/**
	 * Sends OTP via email based on the provided OTPRegistration and OTPRequestDAO.
	 *
	 * @param otpRegistration The OTPRegistration object containing the OTP details.
	 * @param otpRequestDAO The OTPRequestDAO object containing the OTP request details.
	 */
	public void sendEmailOTP(OTPRegistration otpRegistration,OTPRequestDAO otpRequestDAO) {
		try {
			// Get credential for username
			Optional<MessageCredentialMaster> credentialDetails = messageCredentialRepository
					.findByMessageCredentialType(Constants.EMAIL_TEMPLATE);

			// Get configuration details
			Optional<MessageConfigurationMaster> configuraitonDetails = messageConfigurationRepository
					.findByTemplateType(Constants.VERIFY_EMAIL_OTP_CS_TEMPLATE);
			if(otpRequestDAO.getType()!=null && otpRequestDAO.getType().equals("cs")) {
//				VERIFY_EMAIL_OTP_CS_TEMPLATE
				configuraitonDetails = messageConfigurationRepository
						.findByTemplateType(Constants.VERIFY_EMAIL_OTP_CS_TEMPLATE);
			}

			// Initialize velocity engine
			VelocityEngine velocityEngine = initializeVelocity();

			// Add template to repository
			StringResourceRepository repository = StringResourceLoader.getRepository();
			repository.putStringResource("email_template", configuraitonDetails.get().getTemplate());

			// Set parameters
			Date date = new Date();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			String dateString = simpleDateFormat.format(date);

			VelocityContext context = new VelocityContext();
			context.put("userName", " ");
			context.put("otp", otpRegistration.getOtp());
			context.put("applicationName", "Crop Survey");
			context.put("dateTime", dateString);
			context.put("validMin", "5");

			// Process the template
			StringWriter writer = new StringWriter();
			velocityEngine.getTemplate("email_template").merge(context, writer);

			String[] emailTo = new String[] { otpRegistration.getVerificationSource() };
			String[] emailcc = new String[] {};
			emailcc = configuraitonDetails.get().getCcList().trim().split(",");

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
	 * Sends update OTP via mobile based on the provided OTPRegistration.
	 *
	 * @param otpRegistration The OTPRegistration object containing the OTP details.
	 */
	private void sendUserUpdateMobileOTP(OTPRegistration otpRegistration) {

		MessageCredentialMaster messageCredentialMaster = messageCredentialRepository
				.findByMessageCredentialType("MOBILE").get();
		// findByTemplateId
		MessageConfigurationMaster messageConfiguartionMaster = messageConfigurationRepository
				.findByTemplateId(Constants.OTP_VERIFICATION_TEMPLATE_ID).get();
		// DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy
		// HH:mm:ss");
		Date date = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String dateString = simpleDateFormat.format(date);
		String template = messageConfiguartionMaster.getTemplate();
		String test = template.replace("{$1}", otpRegistration.getVerificationType().toString())
				.replace("{$2}", otpRegistration.getOtp()).replace("{$3}", "AgriStack").replace("{$4}", dateString)
				.replace("{$5}", "5");
		
//		new DEV
		 String url = "https://mkisan.gov.in/api/SMSOtp";  // Replace with your API endpoint URL

		   RestTemplate restTemplate = new RestTemplate();
		   
		  HttpHeaders headers = new HttpHeaders();
		  headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED.toString());
		  headers.set("AuthKey", messageCredentialMaster.getPassword());
		  MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
			 Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		        System.out.println(timestamp);   
		        System.out.println(timestamp.getTime()); 
		    	String smsId = "";
				Random random = new Random();
				smsId = String.format("%02d", 10+random.nextInt(90));
				smsId="O"+timestamp.getTime()+smsId;
				System.out.println(smsId); 
	        requestBody.add("MobileNumber", otpRegistration.getVerificationSource());
	        requestBody.add("SMSText", test);
	        requestBody.add("SMSId",smsId);
	        requestBody.add("UserID", messageCredentialMaster.getUserName());
	        HttpEntity formEntity = new HttpEntity<MultiValueMap<String, String>>(requestBody, headers);
		     restTemplate.exchange(url, HttpMethod.POST, formEntity, Object.class);
	}
	
	public void sendOTP(String hostURL,String userName,String password,String mobileNumber,String template) {
		 
		try {
			  RestTemplate restTemplate = new RestTemplate();
		   
			  HttpHeaders headers = new HttpHeaders();
			  headers.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED.toString());
			  headers.set("AuthKey", password);
			  MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
			  Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			  String smsId = "";
			  Random random = new Random();
			  smsId = String.format("%02d", 10+random.nextInt(90));
			  smsId="O"+timestamp.getTime()+smsId;
		 
		      requestBody.add("MobileNumber", mobileNumber);
		      requestBody.add("SMSText", template);
		      requestBody.add("SMSId",smsId);
		      requestBody.add("UserID", userName);
		      HttpEntity formEntity = new HttpEntity<MultiValueMap<String, String>>(requestBody, headers);
			  ResponseEntity<Object> res=restTemplate.exchange(hostURL, HttpMethod.POST, formEntity, Object.class);
			  
			}catch (Exception e) {
//				e.printStackTrace();
			}
	}
	
//OLD
//	/**
//	 * Sends update OTP via mobile based on the provided OTPRegistration.
//	 *
//	 * @param otpRegistration The OTPRegistration object containing the OTP details.
//	 */
//	private void sendUserUpdateMobileOTP(OTPRegistration otpRegistration) {
//
//		MessageCredentialMaster messageCredentialMaster = messageCredentialRepository
//				.findByMessageCredentialType("MOBILE").get();
//		// findByTemplateId
//		MessageConfigurationMaster messageConfiguartionMaster = messageConfigurationRepository
//				.findByTemplateId(Constants.OTP_VERIFICATION_TEMPLATE_ID).get();
//		// DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy
//		// HH:mm:ss");
//		Date date = new Date();
//		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//		String dateString = simpleDateFormat.format(date);
//		String template = messageConfiguartionMaster.getTemplate();
//		String test = template.replace("{$1}", otpRegistration.getVerificationType().toString())
//				.replace("{$2}", otpRegistration.getOtp()).replace("{$3}", "AgriStack").replace("{$4}", dateString)
//				.replace("{$5}", "5");
//		String sendOTPurl = messageCredentialMaster.getHost() + "authkey=" + messageCredentialMaster.getPassword()
//				+ "&mobiles=" + otpRegistration.getVerificationSource() + "&message=" + test + "&sender="
//				+ messageCredentialMaster.getUserName() + "&route=" + messageCredentialMaster.getRoute() + "&unicode="
//				+ messageCredentialMaster.getUniCode();
//
//		String response = new RestTemplate().getForObject(sendOTPurl, String.class);
//		System.out.print(response);
//	}

	/**
	 * Sends update OTP via email based on the provided OTPRegistration.
	 *
	 * @param otpRegistration The OTPRegistration object containing the OTP details.
	 */
	public void sendUserUpdateEmailOTP(OTPRegistration otpRegistration) {
		try {
			// Get credential for username
			Optional<MessageCredentialMaster> credentialDetails = messageCredentialRepository
					.findByMessageCredentialType(Constants.EMAIL_TEMPLATE);

			// Get configuration details
			Optional<MessageConfigurationMaster> configuraitonDetails = messageConfigurationRepository
					.findByTemplateType(Constants.VERIFY_USER_EMAIL_OTP_TEMPLATE);

			// Initialize velocity engine
			VelocityEngine velocityEngine = initializeVelocity();

			// Add template to repository
			StringResourceRepository repository = StringResourceLoader.getRepository();
			repository.putStringResource("email_template", configuraitonDetails.get().getTemplate());

			// Set parameters
			Date date = new Date();
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			String dateString = simpleDateFormat.format(date);

			VelocityContext context = new VelocityContext();
			context.put("userName", " ");
			context.put("otp", otpRegistration.getOtp());
			context.put("applicationName", "AgriStack");
			context.put("dateTime", dateString);
			context.put("validMin", "5");

			// Process the template
			StringWriter writer = new StringWriter();
			velocityEngine.getTemplate("email_template").merge(context, writer);

			String[] emailTo = new String[] { otpRegistration.getVerificationSource() };
			String[] emailcc = new String[] {};
			emailcc = configuraitonDetails.get().getCcList().trim().split(",");

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
	 * Initializes and returns the VelocityEngine for email template rendering.
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
	 * Sends an email to the user based on the provided UserInputDAO.
	 *
	 * @param userDto The UserInputDAO object containing the user details.
	 */
	public void sendEmailToUser(UserInputDAO userDto) {
		try {
			// send notification to client on registered email
			Optional<MessageCredentialMaster> credentialDetails = messageCredentialRepository
					.findByMessageCredentialType(Constants.EMAIL_TEMPLATE);

			String[] emailTo = new String[] { userDto.getUserEmailAddress() };
			String[] emailcc = new String[] { environment.getProperty("app.ccmailid1") };

			VelocityEngine ve = new VelocityEngine();
			ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			ve.init();

			VelocityContext context = new VelocityContext();
			// context.put("firstName", Encode.forHtml(userDto.getFirstName()));
			// context.put("lastName", Encode.forHtml(userDto.getLastName()));
			context.put("userName", Encode.forHtml(userDto.getUserName()));
			context.put("password", Encode.forHtml(userDto.getUserPassword()));
//			context.put("loginPageLink", environment.getProperty("app.ui.url"));
			context.put("loginPageLink", "http://10.195.82.111/FarmerRegistry/#/");
			
			Template t = new Template();
			t = ve.getTemplate("templates/UserCreationEmail.vm");

			StringWriter writer = new StringWriter();
			t.merge(context, writer);

			Mail mail = new Mail();
			mail.setMailFrom(credentialDetails.get().getUserName());
			mail.setMailTo(emailTo);
			mail.setMailCc(emailcc);
			mail.setMailSubject("Welcome to AgriStack");
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
	 * Sends a password changed email to the user.
	 *
	 * @param userName The username of the user.
	 * @param email The email address of the user.
	 * @param reset The reset parameter.
	 * @return A boolean value indicating whether the email was sent successfully.
	 */
	public Boolean sendPasswordChangedEmail(String userName, String email, String reset) {
		// send notification to client on registered email
		try {

			Optional<MessageCredentialMaster> credentialDetails = messageCredentialRepository
					.findByMessageCredentialType(Constants.EMAIL_TEMPLATE);
			String[] emailTo = new String[] { email };
			String[] emailcc = new String[] { environment.getProperty("app.ccmailid1") };

			VelocityEngine ve = new VelocityEngine();
			ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
			ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
			ve.init();
			VelocityContext context = new VelocityContext();
			context.put("userName", Encode.forHtml(userName));
			Date date = new Date();
			SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");
			String datetime = formatter.format(date);
			context.put("datetime", datetime);
			context.put("reset", reset);
			Template t = ve.getTemplate("templates/PasswordChanged.vm");
			StringWriter writer = new StringWriter();
			t.merge(context, writer);

			Mail mail = new Mail();
			mail.setMailFrom(credentialDetails.get().getUserName());
			mail.setMailTo(emailTo);
			mail.setMailCc(emailcc);
			mail.setMailSubject("Password Changed");
			mail.setMailContent(writer.toString());
			try {
				new NotificationThread(mail, mailSender).start();
				return true;
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return false;
			}
		} catch (Exception e) {
			return false;
		}
	}


	/**
	 * Sends a message to the surveyor based on the provided users, season name, and year.
	 *
	 * @param users The list of UserMaster objects representing the surveyors.
	 * @param seaosonName The name of the season.
	 * @param year The year of the season.
	 */
	public void sendMessageToSurveyor(List<UserMaster> users, String seaosonName, String year) {

		MessageCredentialMaster messageCredentialMaster = messageCredentialRepository
				.findByMessageCredentialType("MOBILE").get();
		// findByTemplateId
		MessageConfigurationMaster messageConfiguartionMaster = messageConfigurationRepository
				.findByTemplateId(Constants.AVAILABILITY_FOR_UPCOMING_SEASON_MOBILE).get();
		String template = messageConfiguartionMaster.getTemplate();
		for (UserMaster user : users) {
                String test = template.replace("{$1}", user.getUserFirstName()).replace("{$2}", seaosonName).replace("{$3}", year);
                String sendOTPurl = messageCredentialMaster.getHost() + "authkey=" + messageCredentialMaster.getPassword()
                        + "&sender="+ messageCredentialMaster.getUserName() + "&route=" + messageCredentialMaster.getRoute() + "&unicode="
                        + messageCredentialMaster.getUniCode()+ "&mobiles=" + user.getUserMobileNumber() + "&message=" + test;
                System.out.print(sendOTPurl);
				String response = new RestTemplate().getForObject(sendOTPurl, String.class);
				System.out.print(response);
            }
	}

	/**
	 * Sends an email to the surveyors based on the provided users, season name, and year.
	 *
	 * @param users The list of UserMaster objects representing the surveyors.
	 * @param seaosonName The name of the season.
	 * @param year The year of the season.
	 * @return A boolean value indicating whether the email was sent successfully.
	 */
	public Boolean sendEmailToSurveyors(List<UserMaster> users, String seaosonName, String year) {
		Boolean notificationSend;
		try {
			notificationSend = false;
			Optional<MessageCredentialMaster> credentialDetails = messageCredentialRepository
					.findByMessageCredentialType(Constants.EMAIL_TEMPLATE);
			for (UserMaster user : users) {
			if(!Objects.isNull( user.getUserEmailAddress()) &&  user.getUserEmailAddress() != "") {

					// receiver email address
					String[] emailTo = new String[] { user.getUserEmailAddress() };

					// Get configuration details
					Optional<MessageConfigurationMaster> configuraitonDetails = messageConfigurationRepository
							.findByTemplateType(Constants.AVAILABILITY_FOR_UPCOMING_SEASON);

					// Initialize velocity engine
					VelocityEngine velocityEngine = initializeVelocity();

					// Add template to repository
					StringResourceRepository repository = StringResourceLoader.getRepository();
					repository.putStringResource("AVAILABILITY_FOR_UPCOMING_SEASON", configuraitonDetails.get().getTemplate());

					VelocityContext context = new VelocityContext();
					context.put("userName", Encode.forHtml(user.getUserFirstName()));
					context.put("seasonName", Encode.forHtml(seaosonName));
					context.put("year", Encode.forHtml(year));

					// Process the template
					StringWriter writer = new StringWriter();
					velocityEngine.getTemplate("AVAILABILITY_FOR_UPCOMING_SEASON").merge(context, writer);
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
						notificationSend = true;
					} catch (Exception e) {
						System.out.println(e.getMessage());
						notificationSend = false;
					}
				}
			}

		} catch (Exception e) {
			return false;
		}
		return notificationSend;
	}


	/**
	 * Sends a message to the surveyor for approval or rejection based on the provided users, status, and role.
	 *
	 * @param users The list of UserMaster objects representing the surveyors.
	 * @param status The status of the approval or rejection.
	 * @param role The role of the surveyor.
	 */
	public void sendMessageToSurveyorForApprovalRejection(List<UserMaster> users, String status, String role) {

		MessageCredentialMaster messageCredentialMaster = messageCredentialRepository
				.findByMessageCredentialType("MOBILE").get();

		// find templateId
		MessageConfigurationMaster messageConfiguartionMaster = new MessageConfigurationMaster();

		if(status.equals(StatusEnum.REJECTED.toString())) {
			messageConfiguartionMaster = messageConfigurationRepository
					.findByTemplateId(Constants.PR_REJECTION).get();
		} else if(status.equals(StatusEnum.APPROVED.toString())) {
			messageConfiguartionMaster = messageConfigurationRepository
					.findByTemplateId(Constants.PR_APPROVAL).get();
		}

		String template = messageConfiguartionMaster.getTemplate();
		for (UserMaster user : users) {
				String test = template.replace("{$1}", user.getUserFirstName()).replace("{$2}", role);
				String sendOTPurl = messageCredentialMaster.getHost() + "authkey=" + messageCredentialMaster.getPassword()
						+ "&sender="+ messageCredentialMaster.getUserName() + "&route=" + messageCredentialMaster.getRoute() + "&unicode="
						+ messageCredentialMaster.getUniCode()+ "&mobiles=" + user.getUserMobileNumber() + "&message=" + test;
				String response = new RestTemplate().getForObject(sendOTPurl, String.class);
		}
	}

	/**
	 * Sends an email to the surveyor for approval or rejection based on the provided users and status.
	 *
	 * @param users The list of UserMaster objects representing the surveyors.
	 * @param status The status of the approval or rejection.
	 * @return A boolean value indicating whether the email was sent successfully.
	 */

	public Boolean sendEmailToSurveyorForApprovalRejection(List<UserMaster> users, String status) {
		Boolean notificationSend;
		try {
			notificationSend = false;
			Optional<MessageCredentialMaster> messageCredentialMaster = messageCredentialRepository
					.findByMessageCredentialType(Constants.EMAIL_TEMPLATE);

			for (UserMaster user : users) {
				// Initialize velocity engine
				VelocityEngine velocityEngine = initializeVelocity();
				VelocityContext context = new VelocityContext();
				// Process the template
				StringWriter writer = new StringWriter();
				// receiver email address
				String[] emailTo = new String[] { user.getUserEmailAddress() };
				MessageConfigurationMaster messageConfiguartionMaster = new MessageConfigurationMaster();

				if (status.equals(StatusEnum.REJECTED.toString())) {
					messageConfiguartionMaster = messageConfigurationRepository
							.findByTemplateType(Constants.SURVEYOR_REJECTED).get();

					// Add template to repository
					context.put("userName", Encode.forHtml(user.getUserFirstName()));
					StringResourceRepository repository = StringResourceLoader.getRepository();
					repository.putStringResource("SURVEYOR_REJECTED", messageConfiguartionMaster.getTemplate());
					velocityEngine.getTemplate("SURVEYOR_REJECTED").merge(context, writer);

				} else if (status.equals(StatusEnum.APPROVED.toString())) {
					messageConfiguartionMaster = messageConfigurationRepository
							.findByTemplateType(Constants.SURVEYOR_APPROVAL).get();
					context.put("userName", Encode.forHtml(user.getUserFirstName()));

					// Add template to repository
					StringResourceRepository repository = StringResourceLoader.getRepository();
					repository.putStringResource("SURVEYOR_APPROVAL", messageConfiguartionMaster.getTemplate());
					// Process the template
					velocityEngine.getTemplate("SURVEYOR_APPROVAL").merge(context, writer);
				}
				String[] emailcc = new String[] {};
				if (messageConfiguartionMaster.getCcList() != null) {
					emailcc = messageConfiguartionMaster.getCcList().trim().split(",");
				}
				Mail mail = new Mail();
				mail.setMailFrom(messageCredentialMaster.get().getUserName());
				mail.setMailTo(emailTo);
				mail.setMailCc(emailcc);
				mail.setMailSubject(messageConfiguartionMaster.getEmailSubject());
				mail.setMailContent(writer.toString());
				try {
					new NotificationThread(mail, mailSender).start();
					notificationSend = true;
				} catch (Exception e) {
					System.out.println(e.getMessage());
					notificationSend = false;
				}
			}
		} catch (Exception e) {
			return false;
		}
		return notificationSend;
	}

	public void sendEmailForCropUpdate(String stateName, String action, String cropName) {
		try {
			// Get credential for username
			Optional<MessageCredentialMaster> credentialDetails = messageCredentialRepository
					.findByMessageCredentialType(Constants.EMAIL_TEMPLATE);

			// Get configuration details
			Optional<MessageConfigurationMaster> configuraitonDetails = messageConfigurationRepository
					.findByTemplateType(Constants.CROP_UPDATE);

			if(configuraitonDetails.isPresent()) {
				MessageConfigurationMaster messageConfigurationMaster = configuraitonDetails.get();
				String[] emailcc = new String[] { };

				String[] emailTo = new String[] { messageConfigurationMaster.getReceiverList() };

				// Initialize velocity engine
				VelocityEngine velocityEngine = initializeVelocity();
				VelocityContext context = new VelocityContext();
				// Process the template
				StringWriter writer = new StringWriter();
				// receiver email address
				// Add template to repository
				context.put("stateName", Encode.forHtml(stateName));
				context.put("action", Encode.forHtml(action));
				context.put("cropName", Encode.forHtml(cropName));

				StringResourceRepository repository = StringResourceLoader.getRepository();
				repository.putStringResource("CROP_UPDATE", messageConfigurationMaster.getTemplate());
				velocityEngine.getTemplate("CROP_UPDATE").merge(context, writer);

				Mail mail = new Mail();
				mail.setMailFrom(credentialDetails.get().getUserName());
				mail.setMailTo(emailTo);
				mail.setMailCc(emailcc);
				mail.setMailSubject(messageConfigurationMaster.getEmailSubject());
				mail.setMailContent(writer.toString());
				try {
					new NotificationThread(mail, mailSender).start();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	public void sendEmailForDownloadFile(String downloadUri) {
		try {
			// Get credential for username
			Optional<MessageCredentialMaster> credentialDetails = messageCredentialRepository
					.findByMessageCredentialType(Constants.EMAIL_TEMPLATE);

			// Get configuration details
			Optional<MessageConfigurationMaster> configuraitonDetails = messageConfigurationRepository
					.findByTemplateType(Constants.DOWNLOAD_MEDIA_TEMPLATE);

			// Initialize velocity engine
			VelocityEngine velocityEngine = initializeVelocity();

			// Add template to repository
			StringResourceRepository repository = StringResourceLoader.getRepository();
			repository.putStringResource("email_template", configuraitonDetails.get().getTemplate());

			VelocityContext context = new VelocityContext();
			context.put("userName", " ");
			context.put("otp", downloadUri);

			// Process the template
			StringWriter writer = new StringWriter();
			velocityEngine.getTemplate("email_template").merge(context, writer);

			String[] emailTo = new String[] { "abdul3@amnex.com" };
			String[] emailcc = new String[] {};
			emailcc = configuraitonDetails.get().getCcList().trim().split(",");

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

}
