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
 * @author hardik.siroya
 *
 */

@Entity
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@NoArgsConstructor
public class OwnerType  extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ot_owner_type_id")
	private Long ownerTypeId;

	@Column(name = "ot_owner_type_desc_eng",columnDefinition = "VARCHAR(100)")
	private String ownerTypeDescEng;

	@Column(name = "ot_owner_type_desc_ll",columnDefinition = "VARCHAR(100)")
	private String ownerTypeDescLl;

}
