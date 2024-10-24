package com.amnex.agristack.dao;

import com.amnex.agristack.Enum.UserType;
import com.amnex.agristack.Enum.VerificationType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class OTPRequestDAO {
	private Long userId;
	private String userName;

	private String userMobileNumber;
	private String emailAddress;

	private String verificationSource;

	private String otp;

	private VerificationType verificationType; // EMAIL-MOBILE

	private String deviceToken;

	private String deviceType;

	private String language;

	private String appVersion;

	private String deviceName;

	private String userPassword;

	private String os;

	private String departmentCode;
	private UserType userType;
	private String userDeviceToken;

	private String type;
	private String imeiNumber;
	private String userLocalLanguage;
	private String isSigned;
	private String token;
	private String uniqueKey;
	private String captcha;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
