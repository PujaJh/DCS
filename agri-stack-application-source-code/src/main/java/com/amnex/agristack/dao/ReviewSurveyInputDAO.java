package com.amnex.agristack.dao;

import java.util.List;

import lombok.Data;

@Data
public class ReviewSurveyInputDAO {

	private String userId;
	private Long masterId;
	private String seasonId;
	private String startYear;
	private String endYear;
	private String plotIds;

	private List<Long> stateLgdCodes;
	private List<Long> districtLgdCodes;
	private List<Long> subDistrictLgdCodes;
	private List<Long> villageLgdCodes;
	private List<Long> statusCodes;

	private String page;

	private String limit;

	private String sortField;
	private String sortOrder;
	private String search;
	
	private String startDate;
	private String endDate;
}
