package com.amnex.agristack.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Season Output DAO
 * @author darshankumar.gajjar
 * 
 * */

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class SeasonMasterOutputDAO {
	private Long seasonId;
	private String seasonCode;
	private String seasonType;
	private String startingMonth;
	private String endingMonth;
	private Boolean isActive;
	private Boolean isDeleted;
	private String seasonName;
	private Integer startingYear;

	private Integer endingYear;

	public SeasonMasterOutputDAO(Long seasonId, String seasonName, String seasonCode) {
		this.seasonId = seasonId;
		this.seasonName= seasonName;
		this.seasonCode = seasonCode;

	}

	public SeasonMasterOutputDAO(Long seasonId, String seasonName, String seasonCod, String startingMonth, String endingMonth) {
		this.seasonId = seasonId;
		this.seasonName= seasonName;
		this.seasonCode = seasonCode;
		this.startingMonth = startingMonth;
		this.endingMonth =endingMonth;
	}

	public SeasonMasterOutputDAO(Long seasonId, String seasonName) {
		this.seasonId = seasonId;
		this.seasonName = seasonName;
	}
}
