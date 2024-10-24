package com.amnex.agristack.centralcore.dao;

import lombok.Data;

@Data
public class AIFarmerDetailsDAO {

	public Long farmerId;
	public String farmerAadharHash;
	public String farmerEnrollmentId;
	public String farmerNameEn;
	public String farmerNumber;
	public String farmerNameLocal;
	public String villageName;
	public Long villageLgdCode;
	public String subDistrictName;
	public String districtName;
	public String stateName;
	public String farmerIdentifierName;
}
