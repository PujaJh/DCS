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
import javax.persistence.OneToOne;

import com.amnex.agristack.entity.BaseEntity;
import com.amnex.agristack.entity.FarmerRegistry;
import com.fasterxml.jackson.annotation.JsonBackReference;
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
public class FarmerBankDetails extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fbd_farmer_bank_details_id")
	private Long fbd_farmer_bank_details_id;

	@OneToOne
	@JsonBackReference
	@JoinColumn(name = "fbd_fr_farmer_registry_id", referencedColumnName = "fr_farmer_registry_id")
	private FarmerRegistry farmerRegistryId;

	@Column(name = "fbd_bank_name",columnDefinition = "VARCHAR(150)")
	private String fbd_bank_name;

	@Column(name = "fbd_branch_code",columnDefinition = "VARCHAR(10)")
	private String fbd_branch_code;

	@Column(name = "fbd_ifsc_code",columnDefinition = "VARCHAR(15)")
	private String fbd_ifsc_code	;

	@Column(name = "fbd_account_number",columnDefinition = "VARCHAR(16)")
	private String fbd_account_number;

}
