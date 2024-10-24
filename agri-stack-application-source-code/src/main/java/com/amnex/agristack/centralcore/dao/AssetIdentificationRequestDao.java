package com.amnex.agristack.centralcore.dao;

import java.util.List;

import lombok.Data;

@Data
public class AssetIdentificationRequestDao {

	String farmer_id;
	List<AssetSeasonDao> survey_season;
	
	Long villageLgdCode;
	Long subDistrictLgdCode;
	Long districtLgdCode;
	
	String aadharNo;
	String surveyNo;
	String subSurveyNo;
}
