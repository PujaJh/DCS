/**
 *
 */
package com.amnex.agristack.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author kinnari.soni
 *
 */

@Entity
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@NoArgsConstructor
public class GenderMaster extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "g_gender_master_id")
	private Long genderMasterId;

	@Column(name = "g_gender_desc_eng",columnDefinition = "VARCHAR(100)")
	private String genderDescEng;

	@Column(name = "g_gender_desc_ll",columnDefinition = "VARCHAR(100)")
	private String genderDescLl;
	
	/*@OneToMany
	@JsonManagedReference
	private List<StateMastersDefaultValueMapping> stateMastersDefaultValueMappings;*/
	
	@Transient
	private Boolean show;
	
	@Transient
	private Boolean isDefaultValue;

	
	

}
