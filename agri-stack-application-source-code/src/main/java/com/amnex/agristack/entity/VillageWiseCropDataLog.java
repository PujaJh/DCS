package com.amnex.agristack.entity;

import com.amnex.agristack.Enum.StatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.checkerframework.checker.units.qual.C;
import org.hibernate.annotations.GenericGenerator;

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
public class VillageWiseCropDataLog extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long referenceId;

	@Column
	private String startYear;

	@Column
	private String endYear;

	@Column
	private String season;

	@Column
	private Timestamp startDate;

	@Column
	private Timestamp endDate;

	@Column
	private Integer totalRecords;

	@Column
	private Integer pendingRecords;

	@Column Integer page;

	@Column
	private String apiCallReferenceId;

	@Column
	private Long startSerialNumber;

	@Column
	private Long endSerialNumber;

	@Column
	private Integer sharedLimit;

	@Column
	private Integer isAcknowledged = StatusEnum.PENDING.getValue();

	@Column
	private Long startingNumber;

	@Column
	private Long endingNumber;


}
