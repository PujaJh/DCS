/**
 *
 */
package com.amnex.agristack.dao.common;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author kinnari.soni
 *
 */

@Data
@AllArgsConstructor
public class SubDistrictMasterDAO {

	private Long subDistrictLgdCode;

	private String subDistrictName;

	private Long stateLgdCode;

	private Long districtLgdCode;

}
