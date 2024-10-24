package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class StatePrefixMaster {
	@Id
	@Column(name = "state_lgd_code")
	private Long stateLGDCode;
	@Column(name = "state_name", length = 255)
	private String stateName;
	@Column(name = "state_short_name")
	private String stateShortName;

	public Long getStateLGDCode() {
		return stateLGDCode;
	}

	public void setStateLGDCode(Long stateLGDCode) {
		this.stateLGDCode = stateLGDCode;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public String getStateShortName() {
		return stateShortName;
	}

	public void setStateShortName(String stateShortName) {
		this.stateShortName = stateShortName;
	}

}
