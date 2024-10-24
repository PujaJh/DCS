package com.amnex.agristack.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * Early Late Village Input DAO
 * @author krupali.jogi
 *
 * */

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class EarlyLateVillageInputDAO {
	private Long id;
	private Long year;
	private Boolean isActive;
	private Long seasonId;
	private Integer startYear;
	private Integer endYear;
	private Integer stateLGDCode;
	private Integer subDistrictLGDCode;
	private Integer districtLGDCode;
	private List<Integer> subDistrictLgdCodeList;
	private List<Long> villageLGDCodeList;
	private List<Integer> villageLGDCodes;
}
