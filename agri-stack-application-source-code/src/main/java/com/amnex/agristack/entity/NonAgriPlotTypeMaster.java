package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

import lombok.Data;

/**
 * Entity class representing non-agricultural plot types.
 * Each instance corresponds to a specific plot type with its unique identifier,
 * type name, and active status.
 */

@Entity
@Data
public class NonAgriPlotTypeMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "type_name")
	@Type(type = "text")
	private String typeName;
	
	private Boolean isActive=true;
	
}
