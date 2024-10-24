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
public class SubDistrictLandUnitTypeMapping extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "state_land_unit_type_mapping_id")
	private Long stateLandUnitTypeMappingId;

	@ManyToOne
	@JoinColumn(name = "sub_district_lgd_code", referencedColumnName = "sub_district_lgd_code")
	private SubDistrictLgdMaster subDistrictLgdCode;

	@ManyToOne
	@JoinColumn(name = "unit_type_master_id", referencedColumnName = "ut_unit_type_master_id")
	private StateUnitTypeMaster unitTypeMasterId;
}
