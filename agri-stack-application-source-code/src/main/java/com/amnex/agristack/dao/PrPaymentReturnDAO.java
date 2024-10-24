package com.amnex.agristack.dao;

import lombok.Data;

@Data
public class PrPaymentReturnDAO {

	Long userId;

	Long totalAssignPlotCount;

	String userName;

	String surveyorFullName;

	String surveyorPrId;

	String userBankName;

	String userBankAccountNumber;

	String userIfscCode;

	Long moreThanOneCrop;

	Long payableAmount;

	Long minimumAmountPerSurvey;

	Long maximumAmountPerSurvey;

	Long surveyApprovedPlotCount;
	Long surveyConductedPlotCount;

	String villageName;

	Long villageLgdCode;

	String subdistrictName;

	Long subdistrictLgdCode;

}
