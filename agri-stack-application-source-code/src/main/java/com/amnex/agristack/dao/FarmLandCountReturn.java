/**
 * 
 */
package com.amnex.agristack.dao;

import lombok.Data;

/**
 * @author majid.belim
 *
 */
@Data
public class FarmLandCountReturn {
	Long villageLgdCode;

	Long totalCount;
	String villageName;
	String subDistrictName;
}
