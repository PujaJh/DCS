package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

/**
 * Entity class representing the mapping of segments to farmland plots.
 * Each mapping has a unique identifier, village LGD code, survey number,
 * sub-survey number, segment number, segment unique ID, segment ID,
 * farmland plot registry ID, farmland ID, and a flag indicating the validity of the geometry.
 */

@Entity
@Data
public class SegmentPlotMapping extends BaseEntity{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "segment_plot_mapping_id")
	private Long segmentPlotMappingId;
	
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
	
	@Column(name = "segment_id")
	private Long segmentId;
	
	@Column(name = "fpr_farmland_plot_registry_id")
	private Long farmlandPlotRegistryId;
	
	@Column(name = "fpr_farmland_id", columnDefinition = "VARCHAR(50)")
	private String farmlandId;
	
	@Column(name = "valid_geometry")
	private Boolean validGeometry;
	
}
