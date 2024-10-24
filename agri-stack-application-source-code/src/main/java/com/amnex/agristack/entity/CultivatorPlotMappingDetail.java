package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity class representing details of cultivator plot mapping.
 * Each instance corresponds to a specific mapping between a cultivator and a plot, 
 * including information such as season, parcel ID, cultivator ID, surveyor, 
 * cultivator type, area cultivated, and remarks.
 */

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class CultivatorPlotMappingDetail extends BaseEntity  {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cpmdId;

	private Long seasonId;

	private Integer seasonStartYear;

	private Integer seasonEndYear;

	private Long parcelId;

	private Long cultivatorId;

	private Long surveyBy;

	private Integer cultivatorTypeId;

	private Double cultivatorArea;

	@Column
	@Type(type = "text")
	private String remarks;

}
