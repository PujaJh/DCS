package com.amnex.agristack.entity;

import com.amnex.agristack.Enum.VerifierReasonOfAssignmentEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
public class CropAggregationByDistrictLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cropAggregationByDistrictLogId;

	@CreationTimestamp
	private Timestamp createdOn;

	@UpdateTimestamp
	private Timestamp modifiedOn;

	private Timestamp startTime;

	private Long districtLgdCode;

	private Timestamp endTime;

	private String isDataProcessed;

	@OneToOne
	@JoinColumn(name = "status_id")
	private StatusMaster status;



}
