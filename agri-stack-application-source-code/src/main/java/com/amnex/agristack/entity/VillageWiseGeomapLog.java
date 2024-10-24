/**
 * 
 */
package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.amnex.agristack.Enum.StatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author majid.belim
 *
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
public class VillageWiseGeomapLog extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long totalRecords;

	@Column
	private Long pendingRecords;

	@Column
	private Long page;
	private String action;
	@Column
	private Long startSerialNumber;

	@Column
	private Long endSerialNumber;

	@Column
	private Long sharedLimit;
	@Column
	private Integer isAcknowledged = StatusEnum.PENDING.getValue();
	@Column
	private String apiCallReferenceId;

	@Column
	private Long startingNumber;

	@Column
	private Long endingNumber;

}
