package com.amnex.agristack.dao;

import java.util.List;

public class UserStateDAO {
	private Long stateLgdCode;
	private Boolean isAllSelected;
	private List<UserDistrictDAO> district;
	private Long menuId;
	private Long menuParentId;
	
	private List<UserDistrictDAO> children;
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

	

	public List<UserDistrictDAO> getChildren() {
		return children;
	}

	public void setChildren(List<UserDistrictDAO> children) {
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

	public List<UserDistrictDAO> getDistrict() {
		return district;
	}

	public void setDistrict(List<UserDistrictDAO> district) {
		this.district = district;
	}

	public Long getStateLgdCode() {
		return stateLgdCode;
	}

	public void setStateLgdCode(Long stateLgdCode) {
		this.stateLgdCode = stateLgdCode;
	}

	
}
