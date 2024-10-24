package com.amnex.agristack.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * @author majid.belim
 *
 * Table containing sowing season details
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
public class SowingSeasonHistory extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

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
