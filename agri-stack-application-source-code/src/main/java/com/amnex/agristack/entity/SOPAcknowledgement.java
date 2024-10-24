package com.amnex.agristack.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author majid.belim
 *
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
public class SOPAcknowledgement extends BaseEntity {


	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String masterCallAPI;

	@Column
	private String referenceId;

	@Column
	private Timestamp dateOfDataSharing;

	@Column
	private Long totalRecords;

	@Column
	private Long startSerialNumber;

	@Column
	private Long endSerialNumber;

	@Column
	private Integer status;

	@Column
	private String sopType;

	@Column
	private Long startingNumber;

	@Column
	private Long endingNumber;
}
