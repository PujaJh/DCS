package com.amnex.agristack.dao;

import java.util.Date;

import com.amnex.agristack.entity.DepartmentMaster;
import com.amnex.agristack.entity.DesignationMaster;
import com.amnex.agristack.entity.UserBankDetail;
import com.amnex.agristack.entity.VillageLgdMaster;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
@Data
public class SurveyorOutputDAO {

	private Long userId;
	private String userName;
	private String userFirstName;
	private String userLastName;
	private String userFullName;
	private String departmentName;
	private String designationName;
	private Integer userStateLGDCode;
	private String stateName;
	private Integer userDistrictLGDCode;
	private String districtName;
	private Integer userTalukaLGDCode;
	private String talukaName;
	private Integer userVillageLGDCode;
	private String villageName;
	private String userAadhaarHash;
	private String userAadhaarVaultRefCentral;
	private String userStatus;
	private String userRejectionReason;
	private String userMobileNumber;
	private String userAlternateMobileNumber;
	private String userEmailAddress;
	private Boolean selfRegistered;
	private Boolean isEmailVerified;
	private Boolean isMobileVerified;
	private DesignationMaster designationId;
	private DepartmentMaster departmentId;
	private UserBankDetail userBankDetail;
	private String seasonName;
	private Boolean isAvailable;
	private String userPrId;
	private Date createdOn;
	private VillageLgdMaster villageMaster;
	private String governmentId;
	private Long assignCount;
	private Long bankId;
	private String userRoleName;
	private Long userRoleId;
}
