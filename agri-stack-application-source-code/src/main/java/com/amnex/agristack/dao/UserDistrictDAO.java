package com.amnex.agristack.dao;

import java.util.List;

public class UserDistrictDAO {
	private Long districtLgdCode;
	private Long stateLgdCode;
	private Boolean isAllSelected;
	private List<UserSubDistrictDAO> subDistrict;
	private Long menuId;
	private Long menuParentId;
	
	
	private List<UserSubDistrictDAO> children;
    private String label;
    private Boolean leaf;
	
	
	
	public List<UserSubDistrictDAO> getChildren() {
		return children;
	}

	public void setChildren(List<UserSubDistrictDAO> children) {
		this.children = children;
	}

	public Boolean getLeaf() {
		return leaf;
	}

	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
	}

    
	

	public String getLabel() {
		return label;
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
	
	public Long getDistrictLgdCode() {
		return districtLgdCode;
	}
	public Long getStateLgdCode() {
		return stateLgdCode;
	}
	public List<UserSubDistrictDAO> getSubDistrict() {
		return subDistrict;
	}
	public void setDistrictLgdCode(Long districtLgdCode) {
		this.districtLgdCode = districtLgdCode;
	}
	public void setStateLgdCode(Long stateLgdCode) {
		this.stateLgdCode = stateLgdCode;
	}
	public void setSubDistrict(List<UserSubDistrictDAO> subDistrict) {
		this.subDistrict = subDistrict;
	}

	


}
