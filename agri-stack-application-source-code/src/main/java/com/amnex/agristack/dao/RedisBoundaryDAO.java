/**
 * 
 */
package com.amnex.agristack.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

/**
 * @author majid.belim
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
public class RedisBoundaryDAO {

	private Long stateId;
	private Long stateLgdCode;
	private String stateName;

	private Long districtId;
	private String districtName;
	private Long districtLgdCode;

	private Long subDistrictId;
	private String subDistrictName;
	private Long subDistrictLgdCode;

	private Long villageId;
	private String villageName;
	private Long villageLgdCode;

}
