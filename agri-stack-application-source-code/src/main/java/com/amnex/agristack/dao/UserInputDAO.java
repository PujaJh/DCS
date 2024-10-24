package com.amnex.agristack.dao;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
@Data
public class UserInputDAO {
	private Long userId;
	private String oldPassword;
	private String newPassword;
	private String userFullName;
	private String firstName;
	private String lastName;
	private Long roleId;
	private String userEmailAddress;

	private String userMobileNumber;
	private String userAadhaarHash;

	private String userName;
	private String userPassword;
	private Boolean isDeleted;

	private Boolean isActive;
	private Boolean isFarmerGrievance;

	//	private String geographicalArea;
	private Long boundaryId;
	private String boundaryType;
	private List<GeographicalAreaDAO> geographicalArea;
	private List<Long> stateLGDCodeList;
	private List<Long> districtLgdCodeList;
	private List<Long> subDistrictLgdCodeList;
	private List<Long> villageLgdCodeList;
	
	private List<Long> codes;


	//	private String geographicalArea;

	private Long stateCodeToBeAssign;
	private String token;
	private String deviceToken;
	private String governmentId;
	private String territoryLevel;
	private Long lpsmId;


	// for centralCore
	private String password;
	private String uniqueKey;
	private String captcha;

	private Long userTypeId;
	private String bankAccountNo;
}
