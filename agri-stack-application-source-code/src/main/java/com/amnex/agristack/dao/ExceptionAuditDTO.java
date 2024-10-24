package com.amnex.agristack.dao;

import java.util.Date;

import lombok.Data;

@Data
public class ExceptionAuditDTO {

	private String exceptionDescription;

	private String exceptionType;

	private String exceptionOriginDetails;

	private String controllerName;

	private String actionName;

	private Integer exceptionOriginId;

	private Date createdOn;

	private boolean isActive;
	
	private Long userId;
	
	private Integer exceptionCode;
}
