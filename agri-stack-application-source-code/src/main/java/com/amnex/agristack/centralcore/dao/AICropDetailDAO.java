package com.amnex.agristack.centralcore.dao;

import java.util.ArrayList;

import lombok.Data;

@Data
public class AICropDetailDAO {

	public Double cropArea;
	public String cropRemarks;
	public String cropName;
	public String areaType;
	public String sowingDate;
	public String irrigationSource;
	public String cropStage;
	public String cropClassName;
	public String nonAgriTypeName;
	public ArrayList<String> media;
	public String landType;
	public String unitName;
	
	public Long farmlandPlotRegistryId;
	public String surveyNumber;
	public String subSurveyNumber;
	public String farmlandId;
	
	public String year;
	public String season;
	
	public Long surveyCropId;
}
