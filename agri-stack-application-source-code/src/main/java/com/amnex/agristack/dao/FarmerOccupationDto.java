package com.amnex.agristack.dao;

import com.amnex.agristack.entity.FarmerOccuptationMaster;
import lombok.Data;

@Data
public class FarmerOccupationDto {

	private String farmerAadhaarHash;

	private String occupationName;

	private FarmerOccuptationMaster farmerOccupation;

	private StringBuilder  errorMessage = new StringBuilder();

	private Boolean isValidData = true;
}
