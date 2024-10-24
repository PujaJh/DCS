package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class DisasterTypeMaster {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "disaster_type_id")
	private Long disasterTypeId;
	
	@Column(name = "disaster_type_name", columnDefinition = "TEXT")
	private String disasterTypeName;
	
	@Column(name = "is_active", columnDefinition = "Boolean default true")
	private Boolean isActive;
	
	@Column(name = "is_delete", columnDefinition = "Boolean default false")
	private Boolean isDeleted;
}