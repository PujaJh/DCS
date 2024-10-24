package com.amnex.agristack.entity;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Type;

import lombok.Data;

/**
 * Entity class representing details of land parcels that were unable to be surveyed.
 * Each entry includes an identifier, parcel ID, start and end years of the season, season ID,
 * reason ID for being unable to survey, remarks, surveyor latitude and longitude,
 * survey date, user who conducted the survey, and status.
 */

@Entity
@Data
public class UnableToSurveyDetails extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long lpsmId;

	@OneToOne
	@JoinColumn(name = "parcel_id", referencedColumnName = "fpr_farmland_plot_registry_id")
	private FarmlandPlotRegistry parcelId;

	private Integer seasonStartYear;

	private Integer seasonEndYear;

	private Long seasonId;

	@OneToOne
	@JoinColumn(name = "reason_id", referencedColumnName = "reason_id")
	private UnableToSurveyReasonMaster reasonId;

	@Lob
	@Type(type = "text")
	private String remarks;

	private Double surveyorLat;
	private Double surveyorLong;

	private Timestamp surveyDate;

	private Long surveyBy;
	private Integer status;
}
