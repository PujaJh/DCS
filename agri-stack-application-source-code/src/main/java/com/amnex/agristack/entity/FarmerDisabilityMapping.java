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

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author kinnari.soni
 *
 */

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
public class FarmerDisabilityMapping  extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "dm_disability_mapping_id")
	private Long disabilityMappingId;
	
	@JsonBackReference
	@JoinColumn(name = "dm_fr_farmer_registry_id", referencedColumnName = "fr_farmer_registry_id")
	private Long farmerRegistryId;

	@ManyToOne
	@JoinColumn(name = "dm_dt_disability_type_master_id", referencedColumnName = "dt_disability_type_master_id")
	private DisabilityTypeMaster disabilityTypeMaster;

	/*@Column(name = "dm_disability_extent",columnDefinition = "VARCHAR(100)")
	private Integer disabilityExtent;*/
	@Column(name = "dm_disability_extent")
	private Double disabilityExtent;

	
}
