package com.amnex.agristack.dao;

public class UserVillageDAO {
	private Long villageLgdCode;
	private Long stateLgdCode;
	private Long subDistrictLgdCode;
	private Long districtLgdCode;
	private Boolean isAllSelected;
	private Long menuId;
	private Long menuParentId;
    private String label;
	
    
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

	public Long getVillageLgdCode() {
		return villageLgdCode;
	}

	public Long getStateLgdCode() {
		return stateLgdCode;
	}

	public Long getSubDistrictLgdCode() {
		return subDistrictLgdCode;
	}

	public Long getDistrictLgdCode() {
		return districtLgdCode;
	}

	public void setVillageLgdCode(Long villageLgdCode) {
		this.villageLgdCode = villageLgdCode;
	}

	public void setStateLgdCode(Long stateLgdCode) {
		this.stateLgdCode = stateLgdCode;
	}

	public void setSubDistrictLgdCode(Long subDistrictLgdCode) {
		this.subDistrictLgdCode = subDistrictLgdCode;
	}

	public void setDistrictLgdCode(Long districtLgdCode) {
		this.districtLgdCode = districtLgdCode;
	}

}
