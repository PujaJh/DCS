package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.vividsolutions.jts.geom.Geometry;

import lombok.Data;

@Entity
@Data
public class CadastralMapGeometry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@Column(name = "fpr_plot_geometry", columnDefinition = "GEOMETRY")
	private Geometry plotGeometry;

	@Column(name = "fpr_plot_area", columnDefinition = "DECIMAL(8,5)")
	private Double plotArea;

	@Column(name = "fpr_village_lgd_code")
	private Integer villageLgdCode;

	@Column(name = "fpr_survey_number")
	private String surveyNumber;

	@Column(name = "fpr_sub_survey_number")
	private String subSurveyNumber;

}
