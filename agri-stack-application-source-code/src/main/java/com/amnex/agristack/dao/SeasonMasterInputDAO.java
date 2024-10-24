package com.amnex.agristack.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


/**
 * Season Input DAO
 * @author krupali.jogi
 * 
 * */

@Data
public class SeasonMasterInputDAO {
	private Long seasonId;
	private String seasonCode;
	private String seasonName;
	private String seasonType;
	private String startingMonth;
	private String endingMonth;
	private Boolean isActive;
	private Boolean isCentralProvided;
}
