package com.amnex.agristack.entity;

import com.amnex.agristack.Enum.StatusEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * @author krupali.jogi
 *
 * Table containing stastus detail
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
public class StatusMaster extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "status_id")
	private Long statusId;
	@Column(name = "status_name", columnDefinition = "VARCHAR(25)")
	private String statusName;
	@Column(name = "status_code")
	private Integer statusCode;
}
