package com.amnex.agristack.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonRequestDAO {

	private String userId;
	private Integer seasonId;
	private Integer startYear;
	private Integer endYear;

	private String startDate;
	private String endDate;

	private String surveyno;
	private String subSurveyNo;
	private Double surveyorLat;
	private Double surveyorLong;
	private Double nearestDistance;

	private List<Long> stateLgdCodeList;
	private List<Long> districtLgdCodeList;
	private List<Long> subDistrictLgdCodeList;
	private List<Long> villageLgdCodeList;
	private List<Long> statusCodeList;
	private Long villageLgdCode;

	private String page;

	private String limit;

	private String sortField;
	private String sortOrder;
	private String search;
	private String territoryLevel;
	private Long code;
	private Integer typeId;

	private String farmlandId;
	private String subSurveyNumber;
	private String surveyNumber;
	private String cropCode;
	private Integer startingYear;
	private Integer endingYear;
	private Timestamp last_sync_date;

	private String stateLgdCode;

}
