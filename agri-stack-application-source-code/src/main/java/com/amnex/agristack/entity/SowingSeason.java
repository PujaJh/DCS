package com.amnex.agristack.entity;

import javax.persistence.*;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.EqualsAndHashCode;

/**
 * @author majid.belim
 *
 * Entity class representing the details of sowing seasons.
 * Each season has a unique identifier, season code, type, starting month,
 * ending month, name, a flag indicating if it's provided by the central authority,
 * starting year, ending year, and a flag indicating if it's the current season.
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
public class SowingSeason extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "season_id")
	private Long seasonId;
	@Column(columnDefinition = "VARCHAR(6)")
	private String seasonCode;
	@Column(columnDefinition = "VARCHAR(10)")
	private String seasonType;
	
	@Column(columnDefinition = "VARCHAR(10)")
	private String startingMonth;
	
	@Column(columnDefinition = "VARCHAR(10)")
	private String endingMonth;

	@Column(columnDefinition = "VARCHAR(15)")
	private String seasonName;

	@Column(name = "is_central_provided")
	private Boolean isCentralProvided;

	@Transient
	private Integer startingYear;

	@Transient
	private Integer endingYear;

	@Column(name = "is_current_season")
	private Boolean isCurrentSeason;

}
