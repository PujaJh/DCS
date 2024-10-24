package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.amnex.agristack.Enum.VerifierReasonOfAssignmentEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entity class representing the assignment of land to verifiers.
 * It stores information such as the assignment ID, verifier details, year of assignment,
 * starting and ending years, associated season, farmland ID, land parcel ID, village LGD code,
 * status details, assignment count for farms, reason of assignment, reason of assignment type,
 * survey number, and sub-survey number.
 */

@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
public class VerifierLandAssignment extends BaseEntity {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long verifierLandAssignmentId;

	@OneToOne
	@JoinColumn(name = "verifier_id")
	private UserMaster verifier;

	@Column
	private String year;

	@Column
	private Integer startingYear;

	@Column
	private Integer endingYear;

	@OneToOne
	@JoinColumn(name = "season_id")
	private SowingSeason season;

	private String farmlandId;
	private String landParcelId;
	private Boolean isSecondTimeRejected;

	@OneToOne
	@JoinColumn(referencedColumnName = "village_lgd_code")
	private VillageLgdMaster villageLgdCode;

	@OneToOne
	@JoinColumn(name = "status_id")
	private StatusMaster status;
	
	@Transient
	private Long farmAssignCount;
	
	private String reasonOfAssignment;
	
	@Column(columnDefinition = "TEXT")
	private VerifierReasonOfAssignmentEnum reasonOfAssignmentType;
	@Transient
	private String surveyNumber;
	@Transient
	private String subSurveyNumber;


}
