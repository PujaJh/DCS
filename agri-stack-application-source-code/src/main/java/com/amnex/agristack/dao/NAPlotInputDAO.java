package com.amnex.agristack.dao;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class NAPlotInputDAO {

	private Long stateLgdCode;
	private Long districtLgdCode;
	private Long subDistrictLgdCode;
	private Long villageLgdCode;
	
	private List<Long> stateLgdCodes;
	private List<Long> districtLgdCodes;
	private List<Long> subDistrictLgdCodes;
	private List<Long> villageLgdCodes;
	
	private Long naPlotId;
	private List<Long> naPlotIds;
	private Long userId;
	private Integer startYear;
	private Integer endYear;
	private Long seasonId;
	private List<String> landParcelIds;
	private Boolean isActive;
	private Boolean isDeleted;
	
	private SeasonMasterOutputDAO season;
	
	private FarmlandPlotRegistryDAO landParcelId;
	
	private String farmlandId;
	private Integer page;

	private Integer limit;

	private String sortField;
	private String sortOrder;
	private String search;

}
