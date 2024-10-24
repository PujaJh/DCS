package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.vividsolutions.jts.geom.Geometry;

import lombok.Data;

/**
 * Entity class representing the details of a segment.
 * Each segment detail has a unique identifier, geometry data,
 * area, village LGD code, survey number, sub-survey number,
 * segment number, segment unique ID, and description.
 */

@Entity
@Data
public class SegmentDetailMaster extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "geometry", columnDefinition = "GEOMETRY")
	private Geometry geometry;

	@Column(name = "area", columnDefinition = "numeric")
	private Double area;

	@Column(name = "village_lgd_code")
	private Integer villageLgdCode;

	@Column(name = "survey_number")
	private String surveyNumber;

	@Column(name = "sub_survey_number")
	private String subSurveyNumber;
	
	@Column(name = "segment_number")
	private String segmentNumber;
	
	@Column(name = "segment_unique_id")
	private String segmentUniqueId;
	
	@Column(name = "description")
	private String description;
}
