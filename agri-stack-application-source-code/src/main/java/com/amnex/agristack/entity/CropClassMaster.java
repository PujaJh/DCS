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

/**
 * Entity class representing crop class details.
 * Each instance corresponds to a specific crop class with its unique identifier, name, and status.
 */

@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
public class CropClassMaster {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "crop_class_id")
	private Long cropClassId;
	
	@Column(name = "crop_class_name")
	private String cropClassName;
	
	@Column(name = "is_active", columnDefinition = "Boolean default true")
	private Boolean isActive;
	
	@Column(name = "is_delete", columnDefinition = "Boolean default false")
	private Boolean isDeleted;
}
