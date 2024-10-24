package com.amnex.agristack.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
@EqualsAndHashCode(callSuper = false)
@Data
public class LogMaster {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String userId;
	private String userName;

	@Column(columnDefinition = "TEXT")
	private String originalDataIds;

	@Column(columnDefinition = "TEXT")
	private String originalDataNames;

	@Column(columnDefinition = "TEXT")
	private String newDataIds;

	@Column(columnDefinition = "TEXT")
	private String newDataNames;

	private String action;

	private Date actionTime;

	private String moduleName;

}
