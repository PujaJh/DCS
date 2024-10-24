package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entity class representing details of non-agricultural farm land plots.
 * Each instance corresponds to specific details including a unique identifier,
 * user ID, season ID, start year, end year, and availability status.
 */

@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data

public class NAFarmLandPlotDetails extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long Id;

	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "userId")
	private UserMaster userId;

	@Column(name = "season_id")
	private Long seasonId;

	@Column(name = "start_year")
	private Integer startYear;

	@Column(name = "end_year")
	private Integer endYear;

	@Column(name = "is_available")
	private Boolean isAvailable;
}
