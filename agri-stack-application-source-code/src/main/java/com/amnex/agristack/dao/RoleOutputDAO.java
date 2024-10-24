package com.amnex.agristack.dao;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * Role Output DAO
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
@Data
public class RoleOutputDAO {

	private Long roleId;

	private String roleName;

	private Timestamp createdOn;

	private String createdBy;

	private String createdIp;

	private Timestamp modifiedOn;

	private String modifiedBy;
	private String modifiedByName;

	private String modifiedIp;

	private Boolean isDeleted;

	private Boolean isActive;

	private String roleCode;

	private List<MenuOutputDAO> menuList;

	private String territoryLevel;

	private Integer noOfUsers;

	private Integer lgdCode;

	private String territoryLevelName;

	private String userName;

	private Integer totalUserCount;

	private List<PatternDAO> patterMappingList;

	private List<UserOutputDAO> userList;

	private String prefix;

	private Long userId;

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public String getCreatedIp() {
		return createdIp;
	}

	public Timestamp getModifiedOn() {
		return modifiedOn;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public String getModifiedIp() {
		return modifiedIp;
	}

	public void setCreatedOn(Timestamp createdOn) {
		this.createdOn = createdOn;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreatedIp(String createdIp) {
		this.createdIp = createdIp;
	}

	public void setModifiedOn(Timestamp modifiedOn) {
		this.modifiedOn = modifiedOn;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public void setModifiedIp(String modifiedIp) {
		this.modifiedIp = modifiedIp;
	}


	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public List<MenuOutputDAO> getMenuList() {
		return menuList;
	}

	public void setMenuList(List<MenuOutputDAO> menuList) {
		this.menuList = menuList;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public String getTerritoryLevel() {
		return territoryLevel;
	}

	public void setTerritoryLevel(String territoryLevel) {
		this.territoryLevel = territoryLevel;
	}

	public Integer getNoOfUsers() {
		return noOfUsers;
	}

	public void setNoOfUsers(Integer noOfUsers) {
		this.noOfUsers = noOfUsers;
	}
}
