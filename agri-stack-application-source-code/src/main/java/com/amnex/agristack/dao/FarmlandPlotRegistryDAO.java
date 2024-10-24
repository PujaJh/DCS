package com.amnex.agristack.dao;

import java.util.List;

import javax.persistence.Transient;

import com.amnex.agristack.entity.SowingSeason;

import lombok.Data;

@Data
public class FarmlandPlotRegistryDAO {
	Long villageLgdCode;
	Long subDistrictLgdCode;
	List<Long> villageLgdCodeList;
	Long totalCount;
	
	String stateName;
	String villageName;
	String districtName;
	String subDistrictName;
	SowingSeason season;

	Long totalAssignedPlotsCount;
	Long totalUnAssignedPlotsCount;

	Long totalPlotsSurveyDataCollectedCount;
	Long taskAllocationComplete;
	Double taskAllocationCompleted;
	Long surveyComplete;
	Double surveyCompleted;
	Long noOfSurveyorsAssociated;

	Long totalReviewCompletedCount;
	Long totalRejectedCount;
	private String farmlandId;
	private String landParcelId;
	private String surveyRejectionReason;
	private String surveyRejectionRemarks;
	private String assignedSupervisor;
//	List< surveyComplete;

	String verifierName;
	String userFullName;
	Long totalPlotVerificationCompleteCount;
	Long verifierId;
	Long farmlandPlotRegistryId;
	
	@Transient
	private Boolean isNAPlot;
	
	
	String parcelGeoms;
	
	String surveyNumber;
	String subSurveyNumber;
}
