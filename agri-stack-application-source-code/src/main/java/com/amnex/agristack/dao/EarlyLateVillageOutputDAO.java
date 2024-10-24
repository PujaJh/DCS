package com.amnex.agristack.dao;

import com.amnex.agristack.entity.VillageLgdMaster;
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
public class EarlyLateVillageOutputDAO {
	private Long id;
	private Long year;
	private Boolean isActive;
	private Boolean isDeleted;
	private Long seasonId;
	private Integer startYear;
	private Integer endYear;
	private Integer stateLGDCode;
	private Integer subDistrictLGDCode;
	private Integer districtLGDCode;
	private List<Integer> subDistrictLGDCodes;
	private List<Long> villageLGDCodeList;
	private String stateName;
	private String districtName;
	private String subDistrictName;
	private String seasonName;
	private List<VillageLgdMaster> villageList;
}
