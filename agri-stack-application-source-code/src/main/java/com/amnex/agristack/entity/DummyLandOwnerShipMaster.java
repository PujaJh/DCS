package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "uploaded_land_ownership_master")
@Data
public class DummyLandOwnerShipMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "survey_no")
	private String surveyNumber;
	@Column(name = "hissa_no")

	private String subSurveyNumber;
	@Column(name = "owner_no", columnDefinition = "TEXT")
	private String ownerNo;
	@Column(name = "main_owner_no", columnDefinition = "TEXT")
	private String mainOwnerNo;
	@Column(name = "owner_name", columnDefinition = "TEXT")
	private String ownerName;
	@Column(name = "father", columnDefinition = "TEXT")
	private String identifierName;
	@Column(name = "ext_acre", columnDefinition = "TEXT")
	private String extAcre;
	@Column(name = "ext_gunta", columnDefinition = "TEXT")
	private String extGunta;
	@Column(name = "ext_fgunta", columnDefinition = "TEXT")
	private String extfGunta;
	@Column(name = "gunta_cent", columnDefinition = "TEXT")
	private String guntaCent;
	@Column(name = "total_ha", columnDefinition = "TEXT")
	private String totalHa;
	@Column(name = "village_code")
	private Long villageLgdCode;
	@Column(name = "identifer_type", columnDefinition = "TEXT")
	private String identifierType;
	@Column(name = "geom_str", columnDefinition = "TEXT")
	private String geomStr;
	private String aadharNo;
	private String mobileNo;
	@Column(name = "area_unit")
	private String areaUnit;
	
	@Column(name = "unique_code")
	private String uniqueCode;

	@Column(name = "owner_extent", columnDefinition = "TEXT")
	private Double ownerExtent;
}
