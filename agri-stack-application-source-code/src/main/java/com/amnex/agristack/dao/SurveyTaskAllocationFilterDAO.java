package com.amnex.agristack.dao;

import java.util.List;

import lombok.Data;

@Data
public class SurveyTaskAllocationFilterDAO {

	private Long seasonId;
	private List<Long> subDistrictLgdCodeList;
	private String year;
	private Long villageLgdCode;
	private String boundaryType;
	
	private List<Long> userList;
	private List<String> landIds;
	
	private Integer startYear;
	private Integer endYear;
	private List<Long> assignVillageLGDcodes;
	
	private Integer statusCode;

}
