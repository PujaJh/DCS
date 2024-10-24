package com.amnex.agristack.dao;

import java.util.List;

public class UserSubDistrictDAO {
	private Long subDistrictLgdCode;
	private Long districtLgdCode;
	private Long stateLgdCode;
	private List<UserVillageDAO> village;
	private Boolean isAllSelected;
	private Long menuId;
	private Long menuParentId;
	
	private List<UserVillageDAO> children;
    private String label;
    private Boolean leaf;
	
	
	
	public Boolean getLeaf() {
		return leaf;
	}

	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
	}

    
	
	
	public String getLabel() {
		return label;
	}
	
	public List<UserVillageDAO> getChildren() {
		return children;
	}

	public void setChildren(List<UserVillageDAO> children) {
		this.children = children;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	public Long getMenuId() {
		return menuId;
	}
	public Long getMenuParentId() {
		return menuParentId;
	}
	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}
	public void setMenuParentId(Long menuParentId) {
		this.menuParentId = menuParentId;
	}
	public Boolean getIsAllSelected() {
		return isAllSelected;
	}
	public void setIsAllSelected(Boolean isAllSelected) {
		this.isAllSelected = isAllSelected;
	}
	public Long getSubDistrictLgdCode() {
		return subDistrictLgdCode;
	}
	public Long getDistrictLgdCode() {
		return districtLgdCode;
	}
	public Long getStateLgdCode() {
		return stateLgdCode;
	}
	public List<UserVillageDAO> getVillage() {
		return village;
	}
	public void setSubDistrictLgdCode(Long subDistrictLgdCode) {
		this.subDistrictLgdCode = subDistrictLgdCode;
	}
	public void setDistrictLgdCode(Long districtLgdCode) {
		this.districtLgdCode = districtLgdCode;
	}
	public void setStateLgdCode(Long stateLgdCode) {
		this.stateLgdCode = stateLgdCode;
	}
	public void setVillage(List<UserVillageDAO> village) {
		this.village = village;
	}

	
	
	

}
