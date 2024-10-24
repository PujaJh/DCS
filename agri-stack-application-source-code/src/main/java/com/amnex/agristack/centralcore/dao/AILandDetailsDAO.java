package com.amnex.agristack.centralcore.dao;

import lombok.Data;

@Data
public class AILandDetailsDAO {

	public Double farmerOwnedArea;
	public Long farmerId;
	public Long farmlandPlotRegistryId;
	public String surveyNumber;
	public String subSurveyNumber;
	public String farmlandId;
	public Double farmTotalArea;
	public String villageName;
	public Long villageLgdCode;

}
