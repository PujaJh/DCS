/**
 *
 */
package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author kinnari.soni
 *
 */

@Entity
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@NoArgsConstructor
public class FarmerExtendedRegistry extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fer_farmer_extended_registry_id")
	private Long farmerExtendedRegistryId;

//	@ManyToOne
	@JsonBackReference
	@OneToOne
	@JoinColumn(name = "fer_fr_farmer_registry_id", referencedColumnName = "fr_farmer_registry_id")
	private FarmerRegistry farmerRegistryId;

	@ManyToOne
	@JoinColumn(name = "fer_ccm_caste_category_master_id", referencedColumnName = "cc_caste_category_master_id")
	private CasteCategoryMaster casteCategoryMasterId;

	@Column(name = "fer_caste_certificate_id",columnDefinition = "VARCHAR(100)")
	private String casteCertificateId;

	@Column(name = "fer_is_divyang")
	private Boolean isDivyang;

	@ManyToOne
	@JoinColumn(name = "fer_rm_religion_master_id", referencedColumnName = "rm_religion_master_id")
	private ReligionMaster religionMasterId;

	//	@Column(name = "fer_farmer_name_match_score_aadhaar")
	//	private Integer farmerNameMatchScoreAadhaar;

	@Column(name = "fer_is_minority")
	private Boolean isMinority;

	@Column(name = "fer_pmk_id",columnDefinition = "VARCHAR(50)")
	private String pmkId;

	@Column(name = "fer_is_bank_details_available")
	private Boolean isBankDetailsAvailable;

	@Column(name = "fer_is_kcc_details_available")
	private Boolean isKccDetailsAvailable;
	
	@Column(name = "fer_pan_number", columnDefinition = "VARCHAR(20)")
	private String panNumber;
	
	@Column(name = "fer_farmer_name_match_score_aadhaar")
	private Double farmerNameMatchScoreAadhaar;
	
	@Column(name = "fer_kisan_kcc_number")
	private String ferKisanKccNumber;
	
	@Column(name = "fer_kisan_kcc_amount")
	private Double ferKisanKccAmount;
	
	@Column(name = "fer_kisan_kcc_bank_name")
	private String ferKisanKccBankName;
	
	@Column(name = "fer_farmer_occupation")
	private String ferFarmerOccupation;
	
	@Column(name = "fer_farmer_photograph")
	private String ferFarmerPhotograph;
	
	@Column(name = "fer_farmer_disability_extent")
	private String ferFarmerDisabilityExtent;
}
