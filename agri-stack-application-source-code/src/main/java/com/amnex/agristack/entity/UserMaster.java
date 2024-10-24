package com.amnex.agristack.entity;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import com.amnex.agristack.dao.GeographicalAreaOutputDAO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author majid.belim
 *
 *         Table containing user data with role allocation
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "user_name"}) })
public class UserMaster extends BaseEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(name = "user_name",columnDefinition = "VARCHAR(200)",unique=true)
	private String userName;

	@Column(columnDefinition = "VARCHAR(100)")
	private String userPassword;

	@Column(columnDefinition = "TEXT")
	private String userToken;
	@Column(columnDefinition = "VARCHAR(200)")
	private String userFirstName;

	@Column(columnDefinition = "VARCHAR(200)")
	private String userLastName;

	@Column(columnDefinition = "VARCHAR(200)")
	private String userFullName;

	@Column(name = "user_country_lgd_code")
	private Integer userCountryLGDCode;

	@Column(name = "user_state_lgd_code")
	private Integer userStateLGDCode;

	@Column(name = "user_district_lgd_code")
	private Integer userDistrictLGDCode;

	@Column(name = "user_taluka_lgd_code")
	private Integer userTalukaLGDCode;

	@Column(name = "user_village_lgd_code")
	private Integer userVillageLGDCode;

	@Column(name = "verification_country_lgd_code")
	private Integer verificationCountryLGDCode;

	@Column(name = "verification_state_lgd_code")
	private Integer verificationStateLGDCode;

	@Column(name = "verification_district_lgd_code")
	private Integer verificationDistrictLGDCode;

	@Column(name = "verification_taluka_lgd_code")
	private Integer verificationTalukaLGDCode;

	@Column(name = "verification_village_lgd_code")
	private Integer verificationVillageLGDCode;

	@Column(columnDefinition = "VARCHAR(255)")
	private String userAadhaarHash;

	@Column(columnDefinition = "VARCHAR(255)")
	private String userAadhaarVaultRefCentral;

	@Column(columnDefinition = "VARCHAR(12)")
	private Integer userStatus;

	@Column(columnDefinition = "VARCHAR(100)")
	private String userRejectionReason;

	@Column(columnDefinition = "VARCHAR(13)")
	private String userMobileNumber;

	@Column(columnDefinition = "VARCHAR(13)")
	private String userAlternateMobileNumber;

	@Column(columnDefinition = "VARCHAR(100)")
//	@Column(columnDefinition = "TEXT")
	private String userEmailAddress;

	//	@ManyToMany
	//	@JoinTable(name = "user_role_mapping", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	//	private Set<RoleMaster> role;
	@OneToOne
	@JoinColumn(name = "role_id")
	private RoleMaster roleId;
	@Column(columnDefinition = "TEXT")
	private String geographicalArea;
	@OneToOne
	@JoinColumn(name = "department_id", referencedColumnName = "department_id")
	private DepartmentMaster departmentId;
	@OneToOne
	@JoinColumn(name = "designation_id", referencedColumnName = "designation_id")
	private DesignationMaster designationId;
	@Column(name = "address_line1", columnDefinition = "TEXT")
	String addressLine1;
	@Column(name = "address_line2", columnDefinition = "TEXT")
	String addressLine2;
	@Column(name = "user_device_token", columnDefinition = "TEXT")
	private String userDeviceToken;
	@Column(name = "user_device_type", columnDefinition = "VARCHAR(30)")
	private String userDeviceType;
	@Column(name = "app_version", columnDefinition = "VARCHAR(10)")
	private String appVersion;
	@Column(name = "user_device_name", columnDefinition = "VARCHAR(25)")
	private String userDeviceName;
	@Column(name = "user_os", columnDefinition = "VARCHAR(20)")
	private String userOs;
	@Column(name = "user_local_langauge", columnDefinition = "VARCHAR(20)")
	private String userLocalLangauge;
	@Column(name = "is_email_verified")
	private Boolean isEmailVerified;
	@Column(name = "is_mobile_verified")
	private Boolean isMobileVerified;

	@Column(name = "user_pr_id")
	private String userPrId;

	@Column(name = "pin_code", columnDefinition = "VARCHAR(10)")
	private String pinCode;

	@Column(name = "is_aadhaar_verified")
	private Boolean isAadhaarVerified;

	@Transient
	private GeographicalAreaOutputDAO geographicalAreaOutputDAO;

	@Transient
	private Long farmAssignCount;

	@Transient
	private Long farmPendingCount;

	@Transient
	private Long completedSurveyNumber;

	@Transient
	private Boolean isAvailable;

	@Transient
	private String userPasswordText;

	@Transient
	private Long surveyApprovedCount;

	@Column(name = "is_password_changed")
	private Boolean isPasswordChanged;

	@Column(name = "last_password_changed_date")
	private Date lastPasswordChangedDate;

	@Column(name = "default_page", columnDefinition = "VARCHAR(300)")
	private String defaultPage;

	@Column(name = "password_history", columnDefinition = "TEXT")
	private String passwordHistory;

	@OneToOne
	@JoinColumn(name = "role_pattern_mapping_id")
	private RolePatternMapping rolePatternMappingId;

	@Column(name = "user_image", columnDefinition = "TEXT")
	private String userImage;

	@Column(name = "media_id")
	private String mediaId;

	@Column(columnDefinition = "TEXT")
	private String deviceToken;
	@Column(columnDefinition = "TEXT")
	private String governmentId;

	@Column(name = "imei_number",columnDefinition = "TEXT")
	private String imeiNumber;

	@OneToOne
	@JoinColumn(name = "default_language_id")
	private LanguageMaster defaultLanguageMaster;
	
	@Transient
	private Set<Long> stateLGDCodeList;
	@Transient
	private Set<Long> districtLgdCodeList;
	@Transient
	private Set<Long> subDistrictLgdCodeList;
	@Transient
	private List<Long> villageLgdCodeList;
	

}
