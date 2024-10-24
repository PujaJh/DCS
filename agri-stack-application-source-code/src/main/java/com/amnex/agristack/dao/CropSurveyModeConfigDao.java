package com.amnex.agristack.dao;

import lombok.Data;

public class CropSurveyModeConfigDao {
	Long id;
	Long villageLgdCode;
	Long subDistrictLgdCode;
	Long districtLgdCode;
	Long stateLgdCode;
	Long mode;
	String stateName;
	String districtName;
	String subDistrictName;
	String villageName;
	String modeName;
	Boolean isFlexible;

	public CropSurveyModeConfigDao(Long villageLgdCode, Boolean isFlexible) {
		this.villageLgdCode = villageLgdCode;
		this.isFlexible = isFlexible;
	}

	public CropSurveyModeConfigDao(Long id, Long villageLgdCode, Long subDistrictLgdCode, Long districtLgdCode,
			Long stateLgdCode, Long mode, String stateName, String districtName, String subDistrictName,
			String villageName, String modeName) {
		this.id = id;
		this.villageLgdCode = villageLgdCode;
		this.subDistrictLgdCode = subDistrictLgdCode;
		this.districtLgdCode = districtLgdCode;
		this.stateLgdCode = stateLgdCode;
		this.mode = mode;
		this.stateName = stateName;
		this.districtName = districtName;
		this.subDistrictName = subDistrictName;
		this.villageName = villageName;
		this.modeName = modeName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getVillageLgdCode() {
		return villageLgdCode;
	}

	public void setVillageLgdCode(Long villageLgdCode) {
		this.villageLgdCode = villageLgdCode;
	}

	public Long getSubDistrictLgdCode() {
		return subDistrictLgdCode;
	}

	public void setSubDistrictLgdCode(Long subDistrictLgdCode) {
		this.subDistrictLgdCode = subDistrictLgdCode;
	}

	public Long getDistrictLgdCode() {
		return districtLgdCode;
	}

	public void setDistrictLgdCode(Long districtLgdCode) {
		this.districtLgdCode = districtLgdCode;
	}

	public Long getStateLgdCode() {
		return stateLgdCode;
	}

	public void setStateLgdCode(Long stateLgdCode) {
		this.stateLgdCode = stateLgdCode;
	}

	public Long getMode() {
		return mode;
	}

	public void setMode(Long mode) {
		this.mode = mode;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getDistrictName() {
		return districtName;
	}

	public void setDistrictName(String districtName) {
		this.districtName = districtName;
	}

	public String getSubDistrictName() {
		return subDistrictName;
	}

	public void setSubDistrictName(String subDistrictName) {
		this.subDistrictName = subDistrictName;
	}

	public String getVillageName() {
		return villageName;
	}

	public void setVillageName(String villageName) {
		this.villageName = villageName;
	}

	public String getModeName() {
		return modeName;
	}

	public void setModeName(String modeName) {
		this.modeName = modeName;
	}

	public Boolean getIsFlexible() {
		return isFlexible;
	}

	public void setIsFlexible(Boolean isFlexible) {
		this.isFlexible = isFlexible;
	}

}
