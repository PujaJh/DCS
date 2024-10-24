/**
 * 
 */
package com.amnex.agristack.entity;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.hibernate.annotations.CreationTimestamp;

import com.amnex.agristack.dao.GeographicalAreaOutputDAO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author majid.belim
 * Entity class representing crop sequence mapping details.
 * Each instance corresponds to a specific crop sequence mapping with various attributes including
 * identifiers for geographical areas (state, district, sub-district), season, crop, sequence number,
 * creation timestamp, creator details.
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
public class CropSequenceMapping {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cropSequenceMappingId;

	private Long stateLgdCode;
	private Long districtLgdCode;
	private Long subDistrictLgdCode;

	private Long seasonId;

	private Long cropId;

	private Integer sequenceNumber;
	@CreationTimestamp
	private Timestamp createdOn;

	private String createdBy;

	private String createdIp;
	private Boolean isStateLevel;

}
