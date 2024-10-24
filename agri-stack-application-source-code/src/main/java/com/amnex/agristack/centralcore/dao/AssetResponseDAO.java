package com.amnex.agristack.centralcore.dao;

import java.util.List;

import lombok.Data;

@Data
public class AssetResponseDAO {

	public AIFarmerDetailsDAO farmerDetails;
	public List<AILandDetailsDAO> landDetails;
	public AISurveyDetailsDAO survey_details;
	public List<AICropDetailDAO> crop_details;

}
