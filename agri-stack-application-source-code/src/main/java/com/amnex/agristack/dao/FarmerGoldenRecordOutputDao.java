package com.amnex.agristack.dao;

import java.util.List;

import lombok.Data;


public class FarmerGoldenRecordOutputDao {
	
	public FarmerDetailsOutputDao farmerDetails;
	
	public List<OwnerDetailsDao> ownerDetails;
	
	public SurveyDetailOutputDAO surveyDetails;
	
	public List<FarmerDetailsOutputDao> landDetails;

}
