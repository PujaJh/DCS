/**
 * 
 */
package com.amnex.agristack.dao;

import com.amnex.agristack.entity.SowingSeason;

import lombok.Data;

/**
 * @author majid.belim
 *
 */
@Data
public class FarmlandPlotRegistryReturnDAO {

	Long totalPlotsSurveyDataCollectedCount;

	Long taskAllocationComplete;
	Double taskAllocationCompleted;

	Long surveyComplete;
	Double surveyCompleted;

	Long noOfSurveyorsAssociated;
	Long totalAssignedPlotsCount;
	Long totalUnAssignedPlotsCount;
	Long totalPlotsUnableToSurveyCount;

	Long villageLgdCode;
	String villageName;

	Long subDistrictLgdCode;
	String subDistrictName;

	Long totalCount;

	SowingSeason season;

}
