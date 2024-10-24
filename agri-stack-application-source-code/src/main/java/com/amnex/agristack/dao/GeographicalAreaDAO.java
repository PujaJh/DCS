package com.amnex.agristack.dao;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
public class GeographicalAreaDAO {
	String boundary;
	String data;
	Long stateLgdCode;
	Long districtLgdCode;
	Long subDistrictLgdCode;
	Long villageLgdCode;
	String label;
	Boolean partialSelected;
	List<GeographicalAreaDAO> parent;
	List<GeographicalAreaDAO> children;
	
	
	
	public List<GeographicalAreaDAO> getChildren() {
		return children;
	}

	public void setChildren(List<GeographicalAreaDAO> children) {
		this.children = children;
	}

	public Boolean getPartialSelected() {
		return partialSelected;
	}

	public void setPartialSelected(Boolean partialSelected) {
		this.partialSelected = partialSelected;
	}

	public String getBoundary() {
		return boundary;
	}

	public String getData() {
		return data;
	}

	

	public List<GeographicalAreaDAO> getParent() {
		return parent;
	}

	public void setBoundary(String boundary) {
		this.boundary = boundary;
	}

	public void setData(String data) {
		this.data = data;
	}

	

	public void setParent(List<GeographicalAreaDAO> parent) {
		this.parent = parent;
	}

	public Long getStateLgdCode() {
		return stateLgdCode;
	}

	public Long getDistrictLgdCode() {
		return districtLgdCode;
	}

	public Long getSubDistrictLgdCode() {
		return subDistrictLgdCode;
	}

	public Long getVillageLgdCode() {
		return villageLgdCode;
	}


	public void setStateLgdCode(Long stateLgdCode) {
		this.stateLgdCode = stateLgdCode;
	}

	public void setDistrictLgdCode(Long districtLgdCode) {
		this.districtLgdCode = districtLgdCode;
	}

	public void setSubDistrictLgdCode(Long subDistrictLgdCode) {
		this.subDistrictLgdCode = subDistrictLgdCode;
	}

	public void setVillageLgdCode(Long villageLgdCode) {
		this.villageLgdCode = villageLgdCode;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}


	
	
}
