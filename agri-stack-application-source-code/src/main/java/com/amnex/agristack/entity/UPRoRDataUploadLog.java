package com.amnex.agristack.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
public class UPRoRDataUploadLog {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "village_lgd_code")
	private Long villageLgdCode;
	
	@Column(name = "district_lgd_code")
	private Long districtLgdCode;
	
	@Column(name = "sub_district_lgd_code")
	private Long subDistrictLgdCode;
	
	@Column(name = "state_lgd_code")
	private Long stateLgdCode;
	
	@Column(name = "priority")
	private Long priority;
	
	@Column
	private Boolean isUploaded;
	
	@Column
	private Boolean isActive;
	
	@Column
	private Integer count;
	
	@Column
	private Timestamp uploadedOn;
	
	@Column
	private Integer talukCode;
	
	@Column
	private Boolean reUpload;
	
	@Column
	private Boolean isReUploadDone;

	@Column
	private Boolean isNaMark;

	@Column
	private Boolean isMarkNADone;

	@Column
	private Integer naPlotCount;

	@Column
	private Timestamp naPlotMarkOn;

	@Column
	private Integer landOwnershipCount;

	@Column
	private Integer userLandAssignmentCount;

	@Column
	private Integer surveyConductCount;

	@Column
	private Boolean fetchCensusData;

	@Column
	private Boolean censusDataUploaded;
	@Column
	private Integer censusCount;

	@Column
	private Long villageCensusCode;

}
