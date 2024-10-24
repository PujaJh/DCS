package com.amnex.agristack.dao;

import java.util.List;

import lombok.Data;

@Data
public class SurveyMasterOutputDAO {

	public Integer lpsmId;
	public Integer surveyOneStatusCode;
	public String surveyOneStatus;
	public Integer surveyTwoStatusCode;
	public String surveyTwoStatus;
	public String verifierStatusCode;
	public String verifierStatus;
	public Integer surveyModeId;
	public String surveyMode;
	public Integer flexibleSurveyReasonId;
	public String flexibleSurveyReason;
	public String inspectionOfficerStatusCode;
	public String inspectionOfficerStatus;
	public List<SurveyDetailOutputDAO> surveyDetails;
}
