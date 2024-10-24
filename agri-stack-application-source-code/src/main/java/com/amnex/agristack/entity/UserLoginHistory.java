package com.amnex.agristack.entity;



import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity class representing the assignment of land to users.
 * It stores information such as the assignment ID, user details, year of assignment,
 * starting and ending years, associated season, farmland ID, land parcel ID, village LGD code,
 * boundary survey status, assignment count for farms, status details, survey status codes and names,
 * and parcel ID.
 */

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@NoArgsConstructor
public class UserLoginHistory extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userLoginHistory;
	
	private Long userId;
	
	@Column(name = "app_version", columnDefinition = "VARCHAR(10)")
	private String appVersion;
	
	@Column(name = "user_device_name", columnDefinition = "VARCHAR(50)")
	private String userDeviceName;
	
	@Column(name = "user_device_type", columnDefinition = "VARCHAR(30)")
	private String userDeviceType;
	
	
	@Column(name = "user_os", columnDefinition = "VARCHAR(20)")
	private String userOs;
	
	@Column(name = "imei_number",columnDefinition = "TEXT")
	private String imeiNumber;


}
