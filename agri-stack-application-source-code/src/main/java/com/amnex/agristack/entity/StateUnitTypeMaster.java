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

import com.amnex.agristack.entity.BaseEntity;
import com.amnex.agristack.entity.StateLgdMaster;
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
public class StateUnitTypeMaster extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ut_unit_type_master_id")
	private Long unitTypeMasterId;

	@Column(name = "ut_unit_type_desc_eng", columnDefinition = "VARCHAR(100)")
	private String unitTypeDescEng;

	@Column(name = "ut_unit_type_desc_ll", columnDefinition = "VARCHAR(100)")
	private String unitTypeDescLl;

	@ManyToOne
	@JoinColumn(name = "state_lgd_code", referencedColumnName = "state_lgd_code")
	private StateLgdMaster stateLgdCode;

	@Column(name = "unit_value")
	private double unitValue;
	
	@Column(name = "is_default")
	private boolean isDefault = false;

}
