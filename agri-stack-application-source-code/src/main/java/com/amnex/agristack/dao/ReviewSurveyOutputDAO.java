package com.amnex.agristack.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties
public class ReviewSurveyOutputDAO {

	public Integer lpsmId;
	public Integer farmlandPlotRegistryId;
	public String farmlandId;
	public String farmlandParcelId;

	public String surveyNumber;
	public String subSurveyNumber;

	public String seasonName;
	public Integer startYear;
	public Integer endYear;
	public Integer villageLgdCode;
	public Integer districtLgdCode;
	public String districtName;
	public Integer subDistrictLgdCode;
	public String subDistrictName;
	public String villageName;
	public String surveyorName;
	public String surveyorId;
	public String farmerName;
	public String farmerId;
	public String departmentName;

	public Integer surveyOneStatusCode;
	public String surveyOneStatus;
	public Integer surveyTwoStatusCode;
	public String surveyTwoStatus;
	public Integer verifierStatusCode;
	public String verifierStatus;
	
	public String reviewBy1;
	public String reviewBy2;
	public String reviewBy3;

	public String surveyOneRejectionReason;
	public String surveyOneRejectionRemarks;
	public String surveyTwoRejectionReason;
	public String surveyTwoRejectionRemarks;
	public String verifierRejectionReason;
	public String verifierRejectionRemarks;
	
	public String inspectionOfficerStatusCode;
	public String inspectionOfficerStatus;

}
