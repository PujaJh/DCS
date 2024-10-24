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
public class ApprovalStatusMaster  extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "as_approval_status_master_id")
	private Long approvalStatusMasterId;

	@Column(name = "as_approval_status_desc_eng",columnDefinition = "VARCHAR(100)")
	private String approvalStatusDescEng;

	@Column(name = "as_approval_status_desc_ll",columnDefinition = "VARCHAR(100)")
	private String approvalStatusDescLl;

	@Column(name = "approval_status_color",columnDefinition = "VARCHAR(100)")
	private String approvalStatusColor;

}
