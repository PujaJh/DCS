/**
 * 
 */
package com.amnex.agristack.dao;

import java.util.List;

import lombok.Data;

/**
 * @author majid.belim
 *
 */
@Data
public class VerifierLandConfigurationDAO {

	private Long seasonId;
	private String seasonName;
	private String talukaName;
	private String villageName;
	private Long villageLgdCode;
	private Long stateLgdCode;
	private Long subDistrictLgdCode;
	private Long surveyVerificationConfigurationMasterId;
	private Long verifierLandConfigurationId;
	private Long randomPlotsPickedCount;
	private Long rejectedBySupervisorCount;
	private Long objectionsMarkedForVerificationSurveyBySupervisorCount;
	private String verifierName;
	private Long totalAssignedPlotsCount;
	private Long totalPlotVerificationCompleteCount;
	private Long verifierId;
}
