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
 * Entity class representing mappings of non-agricultural plots.
 * Each instance corresponds to a specific mapping with its unique identifier,
 * season, start year, end year, and land parcel ID.
 */

@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data

public class NonAgriPlotMapping extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long naPlotId;

	@OneToOne
	@JoinColumn(name = "season_id", referencedColumnName = "season_id")
	private SowingSeason season;

	@Column(name = "start_year")
	private Integer startYear;

	@Column(name = "end_year")
	private Integer endYear;

	@OneToOne
	@JoinColumn(name = "land_parcel_id", referencedColumnName = "fpr_farmland_plot_registry_id")
	private FarmlandPlotRegistry landParcelId;
}
