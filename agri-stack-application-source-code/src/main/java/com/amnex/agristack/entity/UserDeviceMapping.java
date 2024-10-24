package com.amnex.agristack.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Entity class representing the mapping between users and their devices.
 * It stores information such as the user ID, IMEI number of the device, status code,
 * creation and modification timestamps, creators' and modifiers' details,
 * remarks, and active status.
 */

@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
@Entity
public class UserDeviceMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_device_mapping_id")
	private Long userDeviceMappingId;

	@OneToOne
	@JoinColumn(name = "user_id", referencedColumnName = "userId")
	private UserMaster userId;

	@Lob
	@Type(type = "text")
	@Column(name = "imei_number")
	private String imeiNumber;

	@Column(name = "status_code")
	private Integer statusCode;

	@CreationTimestamp
	private Timestamp createdOn;

	private Long createdBy;

	private String createdIp;

	@UpdateTimestamp
	private Timestamp modifiedOn;

	private Long modifiedBy;

	private String modifiedIp;

	@Column(name = "remarks")
	private String remarks;

	@Column(name = "is_active", columnDefinition = "Boolean default true")
	private Boolean isActive;

}
