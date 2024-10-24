package com.amnex.agristack.dao;

import lombok.Data;

@Data
public class UserLandAssignmentInputDAO {

	private Long userId;

	private String year;
	
	private Integer startingYear;
	private Integer endingYear;

	private Long seasonId;

	private String farmlandId;

	private String landParcelId;

	private Long villageLgdCode;

	private Boolean isBoundarySurvey;
	
	private Long parcelId;
}
