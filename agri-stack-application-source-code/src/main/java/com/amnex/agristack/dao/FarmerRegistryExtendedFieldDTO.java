package com.amnex.agristack.dao;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class FarmerRegistryExtendedFieldDTO {

	private Integer frExtendedFieldMasterId;
	private String frExtendedFieldName;
	private String frExtendedFieldColumnName;
	private String frExtendedFieldType;
	
	private Long stateLgdCode;
	private Boolean isMandatory;

	private String fer_caste_certificate_id;
	private String fer_pan_number;
	private String fer_is_divyang;
	private String fer_is_minority;
	private Double fer_farmer_name_match_score_aadhaar;
	private String fer_pmk_id;
	private String fer_is_kcc_details_available;
	private String fer_is_bank_details_available;
	private Long fer_rm_religion_master_id;
	
	private String fer_kisan_kcc_number;
	private Double fer_kisan_kcc_amount;
	private String fer_kisan_kcc_bank_name;
	private String fer_farmer_occupation;
	
	private String fer_farmer_photograph;
	private String fer_farmer_disability_extent;
	
	private MultipartFile farmerPhotograph;
	private byte[] farmerPhotographInByte;
	
	private String fbd_bank_name;
	private String fbd_branch_code;
	private String fbd_account_number;
	private String fbd_ifsc_code;
}
