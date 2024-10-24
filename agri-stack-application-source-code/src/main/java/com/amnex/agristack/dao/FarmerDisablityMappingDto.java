package com.amnex.agristack.dao;

import com.amnex.agristack.entity.DisabilityTypeMaster;
import lombok.Data;

@Data
public class FarmerDisablityMappingDto {
	private Long disabilityMappingId;
	private DisabilityTypeMaster disabilityTypeMaster;
	private Double disabilityExtent;

	private String farmerAadhaarHash;

	private StringBuilder  errorMessage= new StringBuilder();

	private Boolean isValidData = true;
}
