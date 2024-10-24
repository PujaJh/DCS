/**
 * 
 */
package com.amnex.agristack.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author majid.belim
 *
 */
@Data
@NoArgsConstructor
public class FarmlandPlotDAO {

	private Long farmlandPlotRegistryId;

	private  String farmlandId;

	private String landParcelId;

	private Long villageLgdCode;

	private String surveyNumber;

	private String subSurveyNumber;

	public FarmlandPlotDAO(Long farmlandPlotRegistryId, String farmlandId, String landParcelId,  String surveyNo, String subSurveyNumber) {
		this.farmlandPlotRegistryId = farmlandPlotRegistryId;
		this.farmlandId = farmlandId;
		this.landParcelId = landParcelId;
		this.surveyNumber = surveyNo;
		this.subSurveyNumber = subSurveyNumber;
	}
}
