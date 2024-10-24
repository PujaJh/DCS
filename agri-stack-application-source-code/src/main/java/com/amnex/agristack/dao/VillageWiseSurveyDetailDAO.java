package com.amnex.agristack.dao;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class VillageWiseSurveyDetailDAO {
	private Long recordId;
	private Long stateLgdCode;
	private Long villageLgdCode;
	private String year;
	private String season;
	private String cropCode;
	private String irrigationType;
	private Integer noOfFarmer;
	private Integer noOfPlots;
	private BigDecimal sownArea;
	private String sownAreaUnit;
	private String surveyStatus;
	private String action;
}
