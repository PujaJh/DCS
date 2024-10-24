/**
 *
 */
package com.amnex.agristack.dao;

import java.util.List;

import lombok.Data;

/**
 * @author kinnari.soni
 *
 *         22 Feb 2023 3:32:29 pm
 */

@Data
public class SubDistrictLandUnitTypeDTO {

	private List<Long> subDistrictLgdCodeList;

	private Long unitTypeId;
}
