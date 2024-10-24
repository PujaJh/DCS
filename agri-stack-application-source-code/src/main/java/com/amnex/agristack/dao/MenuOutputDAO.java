package com.amnex.agristack.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

import java.util.List;

/**
 * Menu Output DAO
 * @author krupali.jogi
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
@Data
public class MenuOutputDAO {
	private Long menuId;
	private String menuName;
	private String menuDescription;
	private Long menuParentId;
	private String menuUrl;
	private Integer displaySrNo;
	private String menuIcon;
	private Integer menuLevel;
	private String menuType;


	private Boolean isAdd;
	private Boolean isEdit;
	private Boolean isView;
	private Boolean isDelete;
	private List<String> projectType;
	private Integer menuCode;

	public Integer getMenuLevel() {
		return menuLevel;
	}

	public void setMenuLevel(Integer menuLevel) {
		this.menuLevel = menuLevel;
	}

	public String getMenuIcon() {
		return menuIcon;
	}

	public void setMenuIcon(String menuIcon) {
		this.menuIcon = menuIcon;
	}

	public String getMenuName() {
		return menuName;
	}

	public String getMenuDescription() {
		return menuDescription;
	}


	public String getMenuUrl() {
		return menuUrl;
	}

	public Integer getDisplaySrNo() {
		return displaySrNo;
	}


	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}

	public void setMenuDescription(String menuDescription) {
		this.menuDescription = menuDescription;
	}

	public Long getMenuParentId() {
		return menuParentId;
	}

	public void setMenuParentId(Long menuParentId) {
		this.menuParentId = menuParentId;
	}

	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}

	public void setDisplaySrNo(Integer displaySrNo) {
		this.displaySrNo = displaySrNo;
	}

	public Long getMenuId() {
		return menuId;
	}

	public void setMenuId(Long menuId) {
		this.menuId = menuId;
	}

	public Integer getMenuCode() {
		return menuCode;
	}

	public void setMenuCode(Integer menuCode) {
		this.menuCode = menuCode;
	}
	
}
