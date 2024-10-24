package com.amnex.agristack.dao;

import com.amnex.agristack.entity.SowingSeason;
import com.amnex.agristack.entity.SurveyActivityMaster;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;


/**
 * Survey Activity Input DAO
 * @author krupali.jogi
 *
 * */

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class SurveyActivityOutputDAO {
	private Long id;
	private Long activityId;
	private SurveyActivityMaster surveyActivity;
	private String activityName;
	private Long year;
	private Date startDate;
	private Date endDate;
	private Boolean isActive;
	private Long seasonId;
	private SowingSeason season;
	private String seasonName;
	private Integer days;
	private List<Long> activityIds;
}
