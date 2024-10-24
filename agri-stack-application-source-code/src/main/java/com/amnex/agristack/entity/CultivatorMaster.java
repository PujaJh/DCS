package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Entity class representing cultivator details.
 * Each instance corresponds to a specific cultivator with its unique identifier and various attributes including
 * village LGD code, name, mobile number, Aadhar number, father's name.
 */

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class CultivatorMaster extends BaseEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cultivatorId;
	
	@Column
	private Long villageLgdCode;

	@Column
	@Type(type = "text")
	private String cultivatorName;
	
	@Column
	@Type(type = "text")
	private String cultivatorMobileNo;
	
	@Column
	@Type(type = "text")
	private String cultivatorAadharNo;
	
	@Column
	@Type(type = "text")
	private String cultivatorFatherName;
	
	@Column(name = "cultivator_ko_ref_no")
	@Type(type = "text")
	private String cultivatorKORefNo;
	 
}
