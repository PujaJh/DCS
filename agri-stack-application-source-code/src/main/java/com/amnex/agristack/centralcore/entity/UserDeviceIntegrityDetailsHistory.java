package com.amnex.agristack.centralcore.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.amnex.agristack.entity.BaseEntity;
import com.amnex.agristack.entity.UserDeviceIntegrityDetails;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@NoArgsConstructor
public class UserDeviceIntegrityDetailsHistory extends BaseEntity{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(columnDefinition = "TEXT")
	private String  deviceId;
	@Column(columnDefinition = "TEXT")
	private String uniqueKey;
	
	@Column(columnDefinition = "TEXT")
	private String decodeIntegrityTokenResponse;
	@Column(columnDefinition = "TEXT")
	private String token;
	
}
