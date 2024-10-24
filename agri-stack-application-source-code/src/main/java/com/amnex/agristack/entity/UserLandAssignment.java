package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.amnex.agristack.Enum.StatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entity class representing the assignment of land to users.
 * It stores information such as the assignment ID, user details, year of assignment,
 * starting and ending years, associated season, farmland ID, land parcel ID, village LGD code,
 * boundary survey status, assignment count for farms, status details, survey status codes and names,
 * and parcel ID.
 */

@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
public class UserLandAssignment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userLandAssignmentId;

	@OneToOne
	@JoinColumn(name = "user_id")
	private UserMaster user;

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

	@OneToOne
	@JoinColumn(referencedColumnName = "village_lgd_code")
	private VillageLgdMaster villageLgdCode;

	private Boolean isBoundarySurvey;
	@Transient
	private Long farmAssignCount;

	@OneToOne
	@JoinColumn(name = "status_id")
	private StatusMaster status;

	@Transient
	Integer surveyOneStatusCode;
	@Transient
	String surveyOneStatus;
	@Transient
	Integer surveyTwoStatusCode;
	@Transient
	String surveyTwoStatus;
	@Transient
	Integer mainStatusCode;
	@Transient
	String mainStatus;
	@Transient
	String surveyNumber;
	@Transient
	String subSurveyNumber;
	private Long parcelId;

}
