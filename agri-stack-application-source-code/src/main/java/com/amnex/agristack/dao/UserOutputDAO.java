package com.amnex.agristack.dao;

import java.util.Date;
import java.util.List;

import com.amnex.agristack.entity.DepartmentMaster;
import com.amnex.agristack.entity.DesignationMaster;
import com.amnex.agristack.entity.LanguageMaster;
import com.amnex.agristack.entity.MediaMaster;
import com.amnex.agristack.entity.UserBankDetail;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * @author kinnari.soni
 *
 * 22 Feb 2023 5:07:37 pm
 */
/**
 * @author kinnari.soni
 *
 *         22 Feb 2023 5:12:51 pm
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
@Data
public class UserOutputDAO {
	private Long userId;
	private String userName;
	private String userToken;
	private String userFirstName;
	private String userType;

	private String userLastName;

	private String userFullName;

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

	private RoleOutputDAO role;

	private Long roleId;
	private String roleName;

	private List<Long> assignStateLGDcodes;

	private String departmentName;
	private String designationName;
	private String stateName;
	private String districtName;
	private String talukaName;
	private String villageName;
	private Boolean isEmailVerified;
	private Boolean isMobileVerified;
	private DesignationMaster designationId;
	private DepartmentMaster departmentId;
	private UserBankDetail userBankDetail;
	private String seasonName;
	private Boolean isAvailable;
	private String userPrId;

	private Boolean isPasswordChanged;

	private Date lastPasswordChangedDate;

	private String userDeviceToken;

	private String defaultPage;

	Boolean isActive;

	private Long bankId;

	private List<Long> assignSubDistrictLGDcodes;

	private List<Long> assignDistrictLGDcodes;

	private List<Long> assignVillageLGDcodes;

	private Long defaultLanguageId;
	private LanguageMaster defaultLanguageMaster;

	private MediaMaster mediaMaster;
	private String territoryLevel;

	private List<MenuOutputDAO> menuList;
	private Boolean isDefault;
}
