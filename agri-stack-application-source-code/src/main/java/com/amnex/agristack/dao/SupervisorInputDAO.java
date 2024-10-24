package com.amnex.agristack.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
@Data
public class SupervisorInputDAO {

	private Long userId;
	private String userName;
	private String userFirstName;
	private String userLastName;
	private String userFullName;
	private Long departmentId;
	private Long designationId;
	private String addressLine1;
	private String addressLine2;
	private String pinCode;
	private String userPassword;
	private Integer userCountryLGDCode;
	private Integer userStateLGDCode;
	private Integer userDistrictLGDCode;
	private Integer userTalukaLGDCode;
	private Integer userVillageLGDCode;
	private String userAadhaarHash;
	private String userAadhaarVaultRefCentral;
	private String userStatus;
	private String userRejectionReason;
	private String userMobileNumber;
	private String userAlternateMobileNumber;
	private String userEmailAddress;
	private String userBankName;
	private Long userBankId;
	private String userBranchCode;
	private String userIfscCode;
	private String userBankAccountNumber;
	private Long userBankDetailId;
	private Boolean selfRegistered;
	private Boolean isEmailVerified;
	private Boolean isMobileVerified;
	private Boolean isActive;
	private Boolean forCurrentSeason;
	private Long seasonId;
	private String startingYear;
	private String endingYear;
	private Boolean isAadhaarVerified;
	private Boolean isAvailable;
}
