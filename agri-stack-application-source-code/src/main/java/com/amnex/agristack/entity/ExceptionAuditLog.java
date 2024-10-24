package com.amnex.agristack.entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Data;

@Data
@Entity
@Table(name = "exception_audit_log", schema = "agri_stack")
public class ExceptionAuditLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long exceptionId;
 
	@Lob
	@Type(type = "text")
	private String exceptionDescription;

	@Lob
	@Type(type = "text")
	private String exceptionType;

	@Lob
	@Type(type = "text")
	private String exceptionOriginDetails;
	
	@Lob
	@Type(type = "text")
	private String controllerName;
	
	@Lob
	@Type(type = "text")
	private String actionName;

	private Integer exceptionOriginId;

	private Date createdOn;

	private boolean isActive;
	
	private Long userId;
	
	private Integer exceptionCode;
}
