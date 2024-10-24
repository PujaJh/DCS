package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

import lombok.Data;

/**
 * Entity class representing reasons for being unable to survey a land parcel.
 * Each reason has an identifier, reason name, and an indicator of whether it is active or not.
 */

@Entity
@Data
public class UnableToSurveyReasonMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "reason_id")
	private Long reasonId;

	@Type(type = "text")
	@Column(name = "reason_name")
	private String reasonName; 
	
	@Column(name = "is_active", columnDefinition = "Boolean default true")
	private Boolean isActive = true;
	
}
