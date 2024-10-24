package com.amnex.agristack.dao;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class SurveyVerificationConfigurationMasterDAO {

	private Integer surveyVerificationConfigurationMasterId;

	private Double randomPickPercentageOfVillages;
	private Double randomPickPercentageOfPlots;
	private Integer randomPickMinimumNumberOfPlots;

	private Boolean surveyRejectedBySupervisorForSecondTime;
	private Boolean objectionRaisedByFarmerAndMarkedBySupervisor;

	private Boolean isActive;
	private Boolean isDeleted;

	private Long stateLgdCode;
	private List<Long> stateCodes;

	private Long appliedYearId;
	private Long appliedSeasonId;

	private String appliedYearName;
	private String appliedSeasonName;

	private Date createdOn;

	public SurveyVerificationConfigurationMasterDAO(Integer surveyVerificationConfigurationMasterId,
			Double randomPickPercentageOfVillages, Double randomPickPercentageOfPlots,
			Integer randomPickMinimumNumberOfPlots, Boolean surveyRejectedBySupervisorForSecondTime,
			Boolean objectionRaisedByFarmerAndMarkedBySupervisor, Boolean isActive, Boolean isDeleted,
			Long stateLgdCode, Long appliedYearId, Long appliedSeasonId, String appliedYearName,
			String appliedSeasonName, Date createdOn) {

		this.surveyVerificationConfigurationMasterId = surveyVerificationConfigurationMasterId;
		this.randomPickPercentageOfVillages = randomPickPercentageOfVillages;
		this.randomPickPercentageOfPlots = randomPickPercentageOfPlots;
		this.randomPickMinimumNumberOfPlots = randomPickMinimumNumberOfPlots;
		this.surveyRejectedBySupervisorForSecondTime = surveyRejectedBySupervisorForSecondTime;
		this.objectionRaisedByFarmerAndMarkedBySupervisor = objectionRaisedByFarmerAndMarkedBySupervisor;
		this.isActive = isActive;
		this.isDeleted = isDeleted;
		this.stateLgdCode = stateLgdCode;
		this.appliedYearId = appliedYearId;
		this.appliedSeasonId = appliedSeasonId;
		this.appliedYearName = appliedYearName;
		this.appliedSeasonName = appliedSeasonName;
		this.createdOn = createdOn;
	}
}
