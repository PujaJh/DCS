/**
 * 
 */
package com.amnex.agristack.entity;

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
 *
 * Entity class representing the mapping between survey payments and users, land parcel survey master, and payment release.
 * Each mapping includes an identifier, user ID, land parcel survey master ID, payment release ID,
 * minimum amount, maximum amount, amount for non-agricultural land, amount for fallow land, amount per crop,
 * count of non-agricultural land, count of fallow land, and count of crops.
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
public class SurveyPaymentMapping extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long surveyPaymentId;
	@OneToOne
	@JoinColumn(name = "user_Id")
	private UserMaster userId;
	@OneToOne
	@JoinColumn(name = "lpsm_id", referencedColumnName = "lpsmId")
	private LandParcelSurveyMaster lpsmId;

	@OneToOne
	@JoinColumn(name = "pr_id", referencedColumnName = "prId")
	private PRPaymentRelease prId;

	
	private Long minAmount;
	private Long maxAmount;
	private Long naAmount;
	private Long falllowAmount;
	private Long perCropAmount;
	
	private Long naCount;
	private Long falllowCount;
	private Long cropCount;
	

}
