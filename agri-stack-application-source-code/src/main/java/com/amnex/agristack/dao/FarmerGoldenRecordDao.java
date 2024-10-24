package com.amnex.agristack.dao;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
public class FarmerGoldenRecordDao {
	
	private Integer startYear;
	
	private Integer endYear;
	
	private Integer seasonId;
	
	private Integer villageLgdCode;

	private String farmerNameLocal;
	
	private String farmerNameEnglish;
	
	private Long farmerId;
	
	private String surveyNumber;
	
	private List<Integer> surveyNumbers;
	
	private String subSurveyNumber;
	
	private Long farmLandPlotId;
	
	public FarmerGoldenRecordDao() {}


	public FarmerGoldenRecordDao(String farmerNameLocal,String farmerNameEnglish, Long farmerId) {
		this.farmerNameLocal = farmerNameLocal;
		this.farmerNameEnglish = farmerNameEnglish;
		this.farmerId = farmerId;
	}
	public FarmerGoldenRecordDao(Long farmLandPlotId,String surveyNumber, String subSurveyNumber) {
		this.surveyNumber = surveyNumber;
		this.subSurveyNumber = subSurveyNumber;
		this.farmLandPlotId=farmLandPlotId;
	}
	
	
}
