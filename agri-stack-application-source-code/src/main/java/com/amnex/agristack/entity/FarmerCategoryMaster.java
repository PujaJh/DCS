/**
 *
 */
package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

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
public class FarmerCategoryMaster extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fc_farmer_category_master_id")
	private Long farmerCategoryMasterId;

	@Column(name = "fc_farmer_category_desc_eng",columnDefinition = "VARCHAR(100)")
	private String farmerCategoryDescEng;

	@Column(name = "fc_farmer_category_desc_ll",columnDefinition = "VARCHAR(100)")
	private String farmerCategoryDescLl;

	@Column(name = "fc_farmer_category_condition",columnDefinition = "VARCHAR(20)")
	private String farmerCategoryCondition;

	@Column(name = "fc_range_from")
	private Double rangeFrom;

	@Column(name = "fc_range_to")
	private Double rangeTo;

	@ManyToOne
	@JoinColumn(name = "state_lgd_code", referencedColumnName = "state_lgd_code")
	private StateLgdMaster stateLgdCode;
}
