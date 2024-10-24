package com.amnex.agristack.dao;

import lombok.Data;

@Data
public class UnableToSurveyRequestDAO {

	private String userId;
	private Long seasonId;
	private Integer startYear;
	private Integer endYear;
	private Integer parcelId;
	private Integer reasonId;
	
	private String remarks;
	private Long surveyBy;
	
	private Double surveyorLat;
	private Double surveyorLong;
	

}
