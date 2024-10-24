package com.amnex.agristack.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@MappedSuperclass
@Data
public class BaseEntity {

	@CreationTimestamp
	private Timestamp createdOn;

	private String createdBy;

	private String createdIp;

	@UpdateTimestamp
	private Timestamp modifiedOn;

	private String modifiedBy;

	private String modifiedIp;

	// @Column(columnDefinition="tinyint(1) default 0")
	private Boolean isDeleted = false;

	// @Column(columnDefinition="tinyint(1) default 1")
	private Boolean isActive = true;
}
