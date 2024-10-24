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
import javax.persistence.OneToOne;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author majid.belim
 * Entity class representing payment release details for plot reviews.
 * Each instance corresponds to a specific payment release with its unique identifier,
 * start year, end year, associated season, surveyor ID, reviewer ID, total plots for payment,
 * remarks, payment status, and calculated amount.
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
public class PRPaymentRelease extends BaseEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long prId;
	
	@Column
	private Integer startYear;

	@Column
	private Integer endYear;
	
	@OneToOne
	@JoinColumn(name = "season_id")
	private SowingSeason season;
	
	@OneToOne
	@JoinColumn(name = "surveyor_id")
	private UserMaster surveyorId;
	
	@OneToOne
	@JoinColumn(name = "review_by")
	private UserMaster reviewBy;
	
	private Integer totalPlotForPayment;
	
	
	@Column(columnDefinition = "TEXT")
	private String Remarks;
	
	@OneToOne
	@JoinColumn(name="status_id")
	private StatusMaster paymentStatus;
	
	private Long calculatedAmount;

}
