package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class FarmerOccuptationMaster extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fom_farmer_occuptationType_id")
	private Long farmerOccupationTypeId;

	@Column(name = "fom_farmeroccuptationType_Type")
	private String farmerOccuptationType;
	
	//@OneToOne
//	@JoinColumn(name = "fom_deparment_id" ,referencedColumnName = "department_type")
	@Column(name = "fom_deparment_id")
	private Integer departmentType;
}
