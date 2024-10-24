package com.amnex.agristack.dao;

import lombok.Data;

@Data
public class FarmerDetailsOutputDao {

	private Long farmerId;
	
	private Long farmlandPlotRegistryId;
	
	private Double farmerOwnedArea;
	
	private Double farmerTotalArea;
	
	private String farmerAadharHash;
	
	private String farmerEnrollmentId;
	
	private String farmerNameEn;
	
	private String farmerNameLocal;
	
	private String surveyNumber;
	
	private String subSurveyNumber;
	
	private String farmlandId;
	
	private Double farmTotalArea;
	
	private String farmerPhoto;
	
	private String villageName;
	
	private String subDistrictName;
	
	private String districtName;
	
	private String stateName;
	
	private String actualGeom;
	
	private String farmerIdentifierName;
	
}
