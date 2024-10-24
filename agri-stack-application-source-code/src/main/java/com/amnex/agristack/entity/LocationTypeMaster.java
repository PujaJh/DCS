/**
 *
 */
package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.amnex.agristack.entity.BaseEntity;
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
public class LocationTypeMaster extends BaseEntity  {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lt_location_type_master_id")
	private Long locationTypeMasterId;

	@Column(name = "lt_location_type_desc_eng",columnDefinition = "VARCHAR(100)")
	private String locationTypeDescEng;

	@Column(name = "lt_location_type_desc_ll",columnDefinition = "VARCHAR(100)")
	private String locationTypeDescLl;

}
