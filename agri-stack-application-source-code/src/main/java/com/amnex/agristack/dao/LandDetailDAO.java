package com.amnex.agristack.dao;

import lombok.Data;

@Data
public class LandDetailDAO {
	public double farmerOwnedArea;
	public double farmerId;
	public double farmlandPlotRegistryId;
	public String surveyNumber;
	public String subSurveyNumber;
	public String farmlandId;
	public double farmTotalArea;
	public String actualGeom;
	public String villageName;
	public Integer villageLgdCode;
}
