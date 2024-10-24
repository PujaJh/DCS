/**
 *
 */
package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

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
public class FarmerStatusMaster extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fs_farmer_status_master_id")
	private Long farmerStatusMasterId;

	@Column(name = "fs_farmer_status_id_desc_eng",columnDefinition = "VARCHAR(100)")
	private String farmerStatusDescEng;

	@Column(name = "fs_farmer_status_id_desc_ll",columnDefinition = "VARCHAR(100)")
	private String farmerStatusDescLl;

}
