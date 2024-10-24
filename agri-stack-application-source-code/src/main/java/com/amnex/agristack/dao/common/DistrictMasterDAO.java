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
public class DistrictMasterDAO {

	private Long districtLgdCode;

	private String districtName;

	private Long stateLgdCode;

}
