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
public class PRPaymentReleaseDAO {
	private Long prId;
	private List<Long> prIds;
	private Integer startYear;

	private Integer endYear;

	private Long seasonId;
	private String seasonName;

	private Long surveyorId;
	private String surveyorPrId;
	private String surveyorFullName;

	private Long totalAssignPlotCount;
	private Long surveyConductedPlotCount;
	private Long surveyApprovedPlotCount;
	private Integer releasePaymentPlotCount;
	private Long totalPlotForPayment;
	

	private String Remarks;

	private Long statusId;

	private String statusName;

	private Integer statusCode;
	private Long nAAmountPerSurvey;
	private Long fallowAmountPerSurvey;
	private Long cropAmountPerSurvey;
	


	private Long naCount;

	private Long fallowCount;

	private Long cropCount;
	
	private Long minimumAmountPerSurvey;
	private Long maximumAmountPerSurvey;
	private Long calculatedAmount;
	
	private String status;
	
	private String villageName;
	
}
