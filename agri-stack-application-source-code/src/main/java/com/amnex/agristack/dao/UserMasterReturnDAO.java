package com.amnex.agristack.dao;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
@Data
public class UserMasterReturnDAO {
	private Long userId;

	private String userName;

	private String userPassword;

	private String userToken;

	private String userFirstName;

	private String userLastName;

	private String userFullName;

	private Integer userCountryLGDCode;

	private Integer userStateLGDCode;

	private Integer userDistrictLGDCode;

	private Integer userTalukaLGDCode;

	private Integer userVillageLGDCode;

	private String userAadhaarHash;

	private String userAadhaarVaultRefCentral;

	private Integer userStatus;

	private String userRejectionReason;

	private String userMobileNumber;

	private String userAlternateMobileNumber;

	private String userEmailAddress;

	private Long roleId;
	private String roleName;

	String addressLine1;

	String addressLine2;

	private String userDeviceToken;

	private String userDeviceType;

	private String appVersion;

	private String userDeviceName;

	private String userOs;

	private String userLocalLangauge;

	private Boolean isEmailVerified;

	private Boolean isMobileVerified;

	private String userPrId;

	private String pinCode;

	private Boolean isAadhaarVerified;

	private GeographicalAreaOutputDAO geographicalAreaOutputDAO;

	private Long farmAssignCount;

	private Long farmPendingCount;

	private Boolean isAvailable;

	private Boolean isPasswordChanged;

	private Date lastPasswordChangedDate;

	private String defaultPage;

	private String passwordHistory;
}
