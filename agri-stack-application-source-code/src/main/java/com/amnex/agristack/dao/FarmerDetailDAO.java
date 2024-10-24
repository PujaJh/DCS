package com.amnex.agristack.dao;

import lombok.Data;

import java.util.ArrayList;

@Data
public class FarmerDetailDAO {
	public double farmerId;
	public double farmlandPlotRegistryId;
	public String farmerAadharHash;
	public String farmerEnrollmentId;
	public String farmerNameEn;
	public String farmerNameLocal;
	public double farmTotalArea;
	public String farmerPhoto;
	public String villageName;
	public String subDistrictName;
	public String districtName;
	public String stateName;
	public String farmerIdentifierName;
	public String pmKisanStatus;
	public String pmfbyStatus;
	public String farmerNumber;
}
