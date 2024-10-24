package com.amnex.agristack.dao;

import lombok.Data;

import java.util.List;

@Data
public class APISpecificationsRequestDao {
	public String village_lgd_code;
	public LandIdentifierRequestDao land_identifier;
	public List<SurveySeasonRequestDao> survey_season;
}
