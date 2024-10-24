package com.amnex.agristack.ror.dao;

import java.util.List;

import lombok.Data;

@Data
public class FRPlotRegistryDAO {
	
	public Integer villageLgdCode;
	public String surveyNumber;
	public String subSurveyNumber;
	public String famrlandPlotId;
	public String plotGeometry;
	public Double plotArea;
	public String plotAreaUnit;
	public Integer projectionCode;
	public List<FRPlotOwnerDAO> owner;

}
