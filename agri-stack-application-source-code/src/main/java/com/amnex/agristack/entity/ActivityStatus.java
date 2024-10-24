package com.amnex.agristack.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * @author majid.belim
 *
 * Entity class representing the status of an activity.
 * It stores information such as the activity status ID and name.
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
public class ActivityStatus extends BaseEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long activityStatusId;
	private String activityStatusName;
	public Long getActivityStatusId() {
		return activityStatusId;
	}
	public String getActivityStatusName() {
		return activityStatusName;
	}
	public void setActivityStatusId(Long activityStatusId) {
		this.activityStatusId = activityStatusId;
	}
	public void setActivityStatusName(String activityStatusName) {
		this.activityStatusName = activityStatusName;
	}
	
	
}
