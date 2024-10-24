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
 * Entity class representing user device integrity details.
 * It stores information related to the integrity of user devices, including device ID, unique key, 
 * integrity token response, and token.
 */

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@NoArgsConstructor
public class UserDeviceIntegrityDetails extends BaseEntity{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userDeviceIntegrityDetailsId;
	
	@Column(columnDefinition = "TEXT")
	private String  deviceId;
	@Column(columnDefinition = "TEXT")
	private String uniqueKey;
	
	@Column(columnDefinition = "TEXT")
	private String decodeIntegrityTokenResponse;
	@Column(columnDefinition = "TEXT")
	private String token;
	
}
