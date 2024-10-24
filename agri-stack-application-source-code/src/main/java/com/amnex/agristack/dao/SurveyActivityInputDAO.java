package com.amnex.agristack.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
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
public class SurveyActivityInputDAO {
	private Long id;
	private Long activityId;
	private Long year;
	private String startDate;
	private String endDate;
	private Boolean isActive;
	private Long seasonId;
	private Integer days;
	private List<Long> activityIds;
}
