package com.amnex.agristack.dao;

public interface SurveyStatusDAO {

	String getFarmlandId();

	Long getFarmlandPlotRegistryId();

	Integer getSurveyOneStatusCode();

	String getSurveyOneStatus();

	Integer getSurveyTwoStatusCode();

	String getSurveyTwoStatus();

	Integer getMainStatusCode();

	String getMainStatus();
}
