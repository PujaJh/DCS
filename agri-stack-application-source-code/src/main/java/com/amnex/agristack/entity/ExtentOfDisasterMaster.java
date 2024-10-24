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
public class ExtentOfDisasterMaster {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "extent_of_disaster_id")
	private Long extentOfDisasterId;
	
	@Column(name = "extent_of_disaster_name", columnDefinition = "TEXT")
	private String extentOfDisasterName;
	
	@Column(name = "is_active", columnDefinition = "Boolean default true")
	private Boolean isActive;
	
	@Column(name = "is_delete", columnDefinition = "Boolean default false")
	private Boolean isDeleted;
}
