package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.vividsolutions.jts.geom.Geometry;

import lombok.Data;

@Entity
@Data
@Table(name = "uploaded_plot_details")
public class DummyLandRecords {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long gid;

	// private Long villageLgdCode;
	// private String talukaName;
	// private String districtName;
	// private String villageName;
	// private String surveyNumber;
	@Column(name = "hissano")
	private String subSurveyNumber;
	@Column(columnDefinition = "GEOMETRY", name = "geom")
	private Geometry geom;
	@Column(name = "lgd_villag")
	private Long villageLgdCode;
	@Column(name = "surveynumb")
	private String surveyNumber;
	@Column(name = "total_area")
	private String totalArea;

	@Column(name = "area_unit")
	private String areaUnit;

	@Column(name = "unique_code")
	private String uniqueCode;

	@Column(name = "land_usage_type")
	private String landUsageType;

	@Column(name = "ownership_type")
	private String ownershipType;
}
