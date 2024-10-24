package com.amnex.agristack.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.amnex.agristack.dao.FarmerRegistryCustomFieldDto;
import com.amnex.agristack.dao.FarmerRegistryDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * class: farmer registry
 */

@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Data
public class FarmerRegistry extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fr_farmer_registry_id")
	private Long farmerRegistryId;

	@Column(name = "fr_farmer_number", columnDefinition = "VARCHAR(11)")
	private String farmerRegistryNumber;

	@Column(name = "fr_farmer_enrollement_number")
	private String farmerEnrollementNumber;

	@Column(name = "farmer_aadhaar_hash", columnDefinition = "VARCHAR(255)")
	private String farmerAadhaarHash;
	@Column(name = "fr_aadhaar_vault_ref_id", columnDefinition = "VARCHAR(255)")
	private String farmerAadhaarValutRefId;
	@Column(name = "fr_aadhaar_vault_ref_source")
	private String farmerAadhaarValutRefSource;
	// @Column(name = "fr_ft_farmer_type_id")
	// private Integer farmerTypeId;
	@ManyToOne
	@JoinColumn(name = "fr_ft_farmer_type_id", referencedColumnName = "ft_farmer_type_master_id")
	private FarmerTypeMaster farmerType;

	@Column(name = "fr_dob")
	private Date farmerDob;
	@Column(name = "fr_age")
	private Integer farmerAge;
	// @Column(name = "fr_lt_location_type_id")
	// private Long farmerLocationTypeId;
	@ManyToOne
	@JoinColumn(name = "fr_lt_location_type_id", referencedColumnName = "lt_location_type_master_id")
	private LocationTypeMaster farmerLocationType;
	@Column(name = "fr_village_lgd_code")
	private Long farmerVillageLgdCode;
	@Column(name = "fr_ulb_lgd_code")
	private Long farmerUrbanLgdCode;
	@Column(name = "fr_ward_lgd_code")
	private Long farmerWardLgdCode;
	@Column(name = "fr_pin_code")
	private Long farmerPinCode;
	@Column(name = "fr_address_en", columnDefinition = "TEXT")
	private String farmerAddressEn;
	@Column(name = "fr_address_local", columnDefinition = "TEXT")
	private String farmerAddressLocal;
	@Column(name = "fr_name_en", columnDefinition = "TEXT")
	private String farmerNameEn;
	@Column(name = "fr_name_local", columnDefinition = "TEXT")
	private String farmerNameLocal;

	// @Column(name = "fr_g_gender_id")
	// private Long farmerGenederTypeId;
	@ManyToOne
	@JoinColumn(name = "fr_g_gender_id", referencedColumnName = "g_gender_master_id")
	private GenderMaster farmerGenederType;
	@Column(name = "fr_identifier_name_en", columnDefinition = "VARCHAR(100)")
	private String farmerIdentifierNameEn;
	@Column(name = "fr_identifier_name_local", columnDefinition = "VARCHAR(100)")
	private String farmerIdentifierNameLocal;

	// @Column(name = "fr_it_identifier_type_id")
	// private Long farmerIdentifierTypeId;
	@ManyToOne
	@JoinColumn(name = "fr_it_identifier_type_id", referencedColumnName = "it_identifier_type_master_id")
	private IdentifierTypeMaster farmerIdentifierType;

	// @Column(name = "fr_cc_caste_category_id")
	// private Long famerCasteCategoryId;
	@ManyToOne
	@JoinColumn(name = "fr_cc_caste_category_id", referencedColumnName = "cc_caste_category_master_id")
	private CasteCategoryMaster famerCasteCategory;
//	@Column(name = "fr_total_land_owned", columnDefinition = "DECIMAL(8,5)")
	@Column(name = "fr_total_land_owned", columnDefinition = "DOUBLE PRECISION")
	private Double farmerTotalLandOwned;

	// @Column(name = "fr_ut_unit_type_id")
	// private Long farmerLandUnitTypeId;
	@ManyToOne
	@JoinColumn(name = "fr_ut_unit_type_id", referencedColumnName = "ut_unit_type_master_id")
	private StateUnitTypeMaster farmerLandUnitType;

//	@Column(name = "fr_total_land_tenanted", columnDefinition = "DECIMAL(8,5)")
	@Column(name = "fr_total_land_tenanted", columnDefinition = "DOUBLE PRECISION")
	private Double farmerTotalLandTenanted;
	@Column(name = "fr_number_of_lands_owned")
	private Integer farmerNumberOfLandOwned;
	// @Column(name = "fr_fc_farmer_category_id")
	// private Long farmerCategoryId;
	@ManyToOne
	@JoinColumn(name = "fr_fc_farmer_category_id", referencedColumnName = "fc_farmer_category_master_id")
	private FarmerCategoryMaster farmerCategory;
	// @Column(name = "fr_fs_farmer_status_id")
	// private Long farmerStatusId;
	@ManyToOne
	@JoinColumn(name = "fr_fs_farmer_status_id", referencedColumnName = "fs_farmer_status_master_id")
	private FarmerStatusMaster farmerStatus;
	@ManyToOne
	@JoinColumn(name = "fr_as_approval_status_id", referencedColumnName = "as_approval_status_master_id")
	private ApprovalStatusMaster farmerApprovalStatus;

	@Column(name = "fr_last_update_date")
	private Date farmerLastUpdateDate;
	@Column(name = "fr_synchronisation_date")
	private Date farmerSynchronisationDate;

	@Column(name = "fr_social_registry_linkage_type")
	private Long farmerSocialRegistryLinkageType;
	// from Family ID assigned to a farmer as per the Ration Card system
	@Column(name = "fr_pds_family_id", columnDefinition = "VARCHAR(100)")
	private String farmerPdsFamilyId;
	@Column(name = "fr_farmer_member_resident_id", columnDefinition = "VARCHAR(20)")
	private String farmerMemberResidentId;
	@Column(name = "fr_email_id")
	private String farmerEmailId;
	@Column(name = "fr_mobile_number", columnDefinition = "VARCHAR(12)")
	private String farmerMobileNumber;

	@ManyToOne
	@JoinColumn(name = "fr_st_state_lgd_code", referencedColumnName = "state_lgd_code")
	private StateLgdMaster stateLgdMaster;
	@ManyToOne
	@JoinColumn(name = "fr_dis_state_lgd_code", referencedColumnName = "district_lgd_code")
	private DistrictLgdMaster districtLgdMaster;
	@ManyToOne
	@JoinColumn(name = "fr_sub_dis_state_lgd_code", referencedColumnName = "sub_district_lgd_code")
	private SubDistrictLgdMaster subDistrictLgdMaster;
	@ManyToOne
	@JoinColumn(name = "fr_vill_village_lgd_code", referencedColumnName = "village_lgd_code")
	private VillageLgdMaster villageLgdMaster;
	@Column(name = "fr_is_email_verified")
	private Boolean isEmailVerified = false;
	@Column(name = "fr_is_mobile_verified")
	private Boolean isMobileVerified = false;
	@Column(name = "fr_is_aadhar_verified")
	private Boolean aadharVerified = false;
	@Column(name = "fr_is_drafted")
	private Boolean isDrafted = false;

	@ManyToMany
	private List<FarmerOccuptationMaster> farmerOccuptationMasters;

	@Column(name = "fr_aadhaar_name_match_score")
	private Integer aadhaarNameMatchScore;

	@Column(name = "fr_identifier_name_match_score")
	private Integer identifierNameMatchScore;

	@Transient
	@JsonManagedReference
	private List<FarmerLandOwnershipRegistry> landOwnershipRegistries;

	@Transient
	private FarmerExtendedRegistry farmerExtendedRegistry;

	@Transient
	private FarmerBankDetails farmerBankDetails;

	//	@Transient
	//private WorkFlowFarmerRegistryCategoryMaster workFlowFarmerRegistryCategoryMaster;

	//	@Transient
	//	private FarmerRegistryWorkFlowEscalations farmerRegistryWorkFlowEscalations;

	/*@Transient
	@JsonManagedReference
	private List<FarmerRegistryApprovalWorkflowTransaction> farmerRegistryApprovalWorkflowTransactions;*/


	@JsonManagedReference
	@Transient
	private List<FarmerDisabilityMapping> farmerDisabilityMappings;

	@OneToOne
	private UserMaster userMaster;


	private String insertedBy;

	@Transient
	private Map<String, FarmerRegistryCustomFieldDto> farmerRegistryCustomFielMap;


	public FarmerRegistry() {

	}

	public FarmerRegistry(FarmerRegistryDto farmerRegistryDto) {

		// this.farmerRegistryId=farmerRegistryDto.getFarmerRegistryId();
		this.farmerAadhaarHash = farmerRegistryDto.getFarmerAadhaarHash();
		// this.farmerAadhaarValutRefId =
		// farmerRegistryDto.getFarmerAadhaarValutRefId();
		// this.farmerAadhaarValutRefSource =
		// farmerRegistryDto.getFarmerAadhaarValutRefSource();
		this.farmerLocationType = farmerRegistryDto.getFarmerLocationType();
		this.farmerDob = farmerRegistryDto.getFarmerDob();
		this.farmerAge = farmerRegistryDto.getFarmerAge();
		// this.farmerLocationTypeId = farmerRegistryDto.getFarmerLocationTypeId();
		// this.farmerVillageLgdCode = farmerRegistryDto.getFarmerVillageLgdCode();
		// this.farmerUrbanLgdCode = farmerRegistryDto.getFarmerUrbanLgdCode();
		// this.farmerWardLgdCode = farmerRegistryDto.getFarmerWardLgdCode();
		this.farmerPinCode = farmerRegistryDto.getFarmerPinCode();
		this.farmerAddressEn = farmerRegistryDto.getFarmerAddressEn();
		this.farmerAddressLocal = farmerRegistryDto.getFarmerAddressLocal();
		this.farmerNameEn = farmerRegistryDto.getFarmerNameEn();
		this.farmerNameLocal = farmerRegistryDto.getFarmerNameLocal();
		this.farmerGenederType = farmerRegistryDto.getFarmerGenederType();

		if (farmerRegistryDto.getFarmerIdentifierType() != null) {
			this.farmerIdentifierType = farmerRegistryDto.getFarmerIdentifierType();
			this.farmerIdentifierNameEn = farmerRegistryDto.getFarmerIdentifierNameEn();
			this.farmerIdentifierNameLocal = farmerRegistryDto.getFarmerIdentifierNameLocal();
		}
		this.famerCasteCategory = farmerRegistryDto.getFamerCasteCategory();
		this.farmerTotalLandOwned = farmerRegistryDto.getFarmerTotalLandOwned();
		// this.farmerLandUnitType = farmerRegistryDto.getFarmerLandUnitType();
		this.farmerTotalLandTenanted = farmerRegistryDto.getFarmerTotalLandTenanted();
		this.farmerNumberOfLandOwned = farmerRegistryDto.getFarmerNumberOfLandOwned();
		this.farmerCategory = farmerRegistryDto.getFarmerCategory();
		if (farmerRegistryDto.getFarmerStatus() != null) {
			this.farmerStatus = farmerRegistryDto.getFarmerStatus();
		}
		if (farmerRegistryDto.getFarmerApprovalStatus() != null) {
			this.farmerApprovalStatus = farmerRegistryDto.getFarmerApprovalStatus();
		}
		if (farmerRegistryDto.getFarmerSocialRegistryLinkageType() != null) {
			this.farmerSocialRegistryLinkageType = farmerRegistryDto.getFarmerSocialRegistryLinkageType();
		}
		if (farmerRegistryDto.getFarmerPdsFamilyId() != null) {
			this.farmerPdsFamilyId = farmerRegistryDto.getFarmerPdsFamilyId();
		}
		if (farmerRegistryDto.getFarmerMemberResidentId() != null) {
			this.farmerMemberResidentId = farmerRegistryDto.getFarmerMemberResidentId();
		}
		if (farmerRegistryDto.getFarmerEmailId() != null) {
			this.farmerEmailId = farmerRegistryDto.getFarmerEmailId();
		}
		if (farmerRegistryDto.getFarmerMobileNumber() != null) {
			this.farmerMobileNumber = farmerRegistryDto.getFarmerMobileNumber();
		}
		if (farmerRegistryDto.getFarmerType() != null) {
			this.farmerType = farmerRegistryDto.getFarmerType();
		}
		this.stateLgdMaster = farmerRegistryDto.getStateLgdMaster();
		this.districtLgdMaster = farmerRegistryDto.getDistrictLgdMaster();
		this.subDistrictLgdMaster = farmerRegistryDto.getSubDistrictLgdMaster();
		this.villageLgdMaster = farmerRegistryDto.getVillageLgdMaster();

		if (farmerRegistryDto.getAadhaarNameMatchScore() != null) {
			this.aadhaarNameMatchScore = farmerRegistryDto.getAadhaarNameMatchScore();
		}

		if (farmerRegistryDto.getIdentifierNameMatchScore() != null) {
			this.identifierNameMatchScore = farmerRegistryDto.getIdentifierNameMatchScore();
		}
	}
}
