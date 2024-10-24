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
public class VillageMasterDAO {

	private Long villageLgdCode;

	private String villageName;

	private Long stateLgdCode;

	private Long districtLgdCode;

	private Long subDistrictLgdCode;

}
