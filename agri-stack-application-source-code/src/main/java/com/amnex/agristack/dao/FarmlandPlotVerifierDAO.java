/**
 * 
 */
package com.amnex.agristack.dao;

import lombok.Data;

/**
 * @author majid.belim
 *
 */
//@Data
public interface FarmlandPlotVerifierDAO {

	Long getFarmlandPlotRegistryId();

	String getFarmlandId();

	String getLandParcelId();

}
