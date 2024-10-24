package com.amnex.agristack.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class SurveyVerificationConfigurationMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Integer surveyVerificationConfigurationMasterId;

	private Double randomPickPercentageOfVillages;
	private Double randomPickPercentageOfPlots;
	private Integer randomPickMinimumNumberOfPlots;

	private Boolean surveyRejectedBySupervisorForSecondTime;
	private Boolean objectionRaisedByFarmerAndMarkedBySupervisor;

	private Boolean isActive;
	private Boolean isDeleted;
	private Boolean isApppliedNextSeason;
	
	@OneToOne
	@JoinColumn(name = "state_lgd_code", referencedColumnName = "state_lgd_code")
	private StateLgdMaster stateLgdMaster;
	
	@ManyToOne
	@JoinColumn(name = "applied_year", referencedColumnName = "id")
	private YearMaster appliedYear;
	
	@ManyToOne
	@JoinColumn(name = "applied_season", referencedColumnName = "season_id")
	private SowingSeason appliedSeason;
	
	private Date createdOn;
	
	@OneToOne
	@JoinColumn(name = "status_id")
	private StatusMaster status;
}
