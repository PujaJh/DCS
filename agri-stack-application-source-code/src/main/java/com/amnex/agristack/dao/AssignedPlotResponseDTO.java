package com.amnex.agristack.dao;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AssignedPlotResponseDTO {

    ArrayList<AssignedPlotOwnerRepsonseDTO> owners;
    Long farmlandPlotRegistryId;
    String farmlandId;
    String farmlandParcelId;
    String surveyNumber;
    String subSurveyNumber;
    Double plotArea;
    Long plotAreaUnitId;
    String plotAreaUnit;
    Object plotGeom;
    Integer villageLgdCode;
    String villageName;
    Integer subDistrictLgdCode;
    String subDistrictName;
    Integer districtLgdCode;
    String districtName;
    Integer stateLgdCode;
    String stateName;
    Integer userLandAssignmentId;
    boolean isBoundarySurvey;
    String year;
    Integer season;
    String seasonName;
    Integer surveyorId;
    Integer endYear;
    Integer startYear;
    Integer surveyOneStatusCode;
    String surveyOneStatus;
    Integer surveyTwoStatusCode;
    String surveyTwoStatus;
    Integer verifierStatusCode;
    String verifierStatus;
    Integer surveyStatusCode;
    String surveyStatus;
    Long landParcelSurveyId;
    String rejectedRemark;
    String reason;
    ArrayList<SurveyDetailOutputDAO> surveyDetails;
    Double distanceMtr;
    Long surveyModeId;
    String surveyMode;
    Long flexibleSurveyReasonId;
    String flexibleSurveyReason;
    Integer inspectionOfficerStatusCode;
    String inspectionOfficerStatus;
    @JsonProperty("owners")
    public ArrayList<AssignedPlotOwnerRepsonseDTO> getOwners() {
        return this.owners;
    }

    public void setOwners(ArrayList<AssignedPlotOwnerRepsonseDTO> owners) {
        this.owners = owners;
    }

    @JsonProperty("farmlandPlotRegistryId")
    public Long getFarmlandPlotRegistryId() {
        return this.farmlandPlotRegistryId;
    }

    public void setFarmlandPlotRegistryId(Long farmlandPlotRegistryId) {
        this.farmlandPlotRegistryId = farmlandPlotRegistryId;
    }

    @JsonProperty("farmlandId")
    public String getFarmlandId() {
        return this.farmlandId;
    }

    public void setFarmlandId(String farmlandId) {
        this.farmlandId = farmlandId;
    }

    @JsonProperty("farmlandParcelId")
    public String getFarmlandParcelId() {
        return this.farmlandParcelId;
    }

    public void setFarmlandParcelId(String farmlandParcelId) {
        this.farmlandParcelId = farmlandParcelId;
    }

    @JsonProperty("plotArea")
    public Double getPlotArea() {
        return this.plotArea;
    }

    public void setPlotArea(Double plotArea) {
        this.plotArea = plotArea;
    }

    @JsonProperty("plotGeom")
    public Object getPlotGeom() {
        return this.plotGeom;
    }

    public void setPlotGeom(Object plotGeom) {
        this.plotGeom = plotGeom;
    }

    @JsonProperty("villageLgdCode")
    public Integer getVillageLgdCode() {
        return this.villageLgdCode;
    }

    public void setVillageLgdCode(Integer villageLgdCode) {
        this.villageLgdCode = villageLgdCode;
    }

    @JsonProperty("villageName")
    public String getVillageName() {
        return this.villageName;
    }

    public void setVillageName(String villageName) {
        this.villageName = villageName;
    }

    @JsonProperty("userLandAssignmentId")
    public Integer getUserLandAssignmentId() {
        return this.userLandAssignmentId;
    }

    public void setUserLandAssignmentId(Integer userLandAssignmentId) {
        this.userLandAssignmentId = userLandAssignmentId;
    }

    @JsonProperty("isBoundarySurvey")
    public boolean getIsBoundarySurvey() {
        return this.isBoundarySurvey;
    }

    public void setIsBoundarySurvey(boolean isBoundarySurvey) {
        this.isBoundarySurvey = isBoundarySurvey;
    }

    @JsonProperty("year")
    public String getYear() {
        return this.year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @JsonProperty("season")
    public Integer getSeason() {
        return this.season;
    }

    public void setSeason(Integer season) {
        this.season = season;
    }

    @JsonProperty("seasonName")
    public String getSeasonName() {
        return this.seasonName;
    }

    public void setSeasonName(String seasonName) {
        this.seasonName = seasonName;
    }

    @JsonProperty("surveyorId")
    public Integer getSurveyorId() {
        return this.surveyorId;
    }

    public void setSurveyorId(Integer surveyorId) {
        this.surveyorId = surveyorId;
    }

    @JsonProperty("endYear")
    public Integer getEndYear() {
        return this.endYear;
    }

    public void setEndYear(Integer endYear) {
        this.endYear = endYear;
    }

    @JsonProperty("startYear")
    public Integer getStartYear() {
        return this.startYear;
    }

    public void setStartYear(Integer startYear) {
        this.startYear = startYear;
    }

    @JsonProperty("surveyOneStatusCode")
    public Integer getSurveyOneStatusCode() {
        return this.surveyOneStatusCode;
    }

    public void setSurveyOneStatusCode(Integer surveyOneStatusCode) {
        this.surveyOneStatusCode = surveyOneStatusCode;
    }

    @JsonProperty("surveyOneStatus")
    public String getSurveyOneStatus() {
        return this.surveyOneStatus;
    }

    public void setSurveyOneStatus(String surveyOneStatus) {
        this.surveyOneStatus = surveyOneStatus;
    }

    @JsonProperty("surveyTwoStatusCode")
    public Integer getSurveyTwoStatusCode() {
        return this.surveyTwoStatusCode;
    }

    public void setSurveyTwoStatusCode(Integer surveyTwoStatusCode) {
        this.surveyTwoStatusCode = surveyTwoStatusCode;
    }

    @JsonProperty("surveyTwoStatus")
    public String getSurveyTwoStatus() {
        return this.surveyTwoStatus;
    }

    public void setSurveyTwoStatus(String surveyTwoStatus) {
        this.surveyTwoStatus = surveyTwoStatus;
    }

    @JsonProperty("verifierStatusCode")
    public Integer getVerifierStatusCode() {
        return this.verifierStatusCode;
    }

    public void setVerifierStatusCode(Integer verifierStatusCode) {
        this.verifierStatusCode = verifierStatusCode;
    }

    @JsonProperty("verifierStatus")
    public String getVerifierStatus() {
        return this.verifierStatus;
    }

    public void setVerifierStatus(String verifierStatus) {
        this.verifierStatus = verifierStatus;
    }

    @JsonProperty("surveyStatusCode")
    public Integer getSurveyStatusCode() {
        return this.surveyStatusCode;
    }

    public void setSurveyStatusCode(Integer surveyStatusCode) {
        this.surveyStatusCode = surveyStatusCode;
    }

    @JsonProperty("surveyStatus")
    public String getSurveyStatus() {
        return this.surveyStatus;
    }

    public void setSurveyStatus(String surveyStatus) {
        this.surveyStatus = surveyStatus;
    }

    @JsonProperty("landParcelSurveyId")
    public Long getLandParcelSurveyId() {
        return this.landParcelSurveyId;
    }

    public void setLandParcelSurveyId(Long landParcelSurveyId) {
        this.landParcelSurveyId = landParcelSurveyId;
    }

    @JsonProperty("rejectedRemark")
    public String getRejectedRemark() {
        return this.rejectedRemark;
    }

    public void setRejectedRemark(String rejectedRemark) {
        this.rejectedRemark = rejectedRemark;
    }

    @JsonProperty("reason")
    public String getReason() {
        return this.reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @JsonProperty("surveyDetails")
    public ArrayList<SurveyDetailOutputDAO> getSurveyDetails() {
        return surveyDetails;
    }

    public void setSurveyDetails(ArrayList<SurveyDetailOutputDAO> surveyDetails) {
        this.surveyDetails = surveyDetails;
    }

    @JsonProperty("plotAreaUnitId")
    public Long getPlotAreaUnitId() {
        return plotAreaUnitId;
    }

    public void setPlotAreaUnitId(Long plotAreaUnitId) {
        this.plotAreaUnitId = plotAreaUnitId;
    }

    @JsonProperty("plotAreaUnit")
    public String getPlotAreaUnit() {
        return plotAreaUnit;
    }

    public void setPlotAreaUnit(String plotAreaUnit) {
        this.plotAreaUnit = plotAreaUnit;
    }

    public Integer getSubDistrictLgdCode() {
        return subDistrictLgdCode;
    }

    public void setSubDistrictLgdCode(Integer subDistrictLgdCode) {
        this.subDistrictLgdCode = subDistrictLgdCode;
    }

    public String getSubDistrictName() {
        return subDistrictName;
    }

    public void setSubDistrictName(String subDistrictName) {
        this.subDistrictName = subDistrictName;
    }

    public Integer getDistrictLgdCode() {
        return districtLgdCode;
    }

    public void setDistrictLgdCode(Integer districtLgdCode) {
        this.districtLgdCode = districtLgdCode;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public Integer getStateLgdCode() {
        return stateLgdCode;
    }

    public void setStateLgdCode(Integer stateLgdCode) {
        this.stateLgdCode = stateLgdCode;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public void setBoundarySurvey(boolean isBoundarySurvey) {
        this.isBoundarySurvey = isBoundarySurvey;
    }

    public String getSurveyNumber() {
        return surveyNumber;
    }

    public void setSurveyNumber(String surveyNumber) {
        this.surveyNumber = surveyNumber;
    }

    public String getSubSurveyNumber() {
        return subSurveyNumber;
    }

    public void setSubSurveyNumber(String subSurveyNumber) {
        this.subSurveyNumber = subSurveyNumber;
    }

    public Double getDistanceMtr() {
        return distanceMtr;
    }

    public void setDistanceMtr(Double distanceMtr) {
        this.distanceMtr = distanceMtr;
    }

    public Long getSurveyModeId() {
        return surveyModeId;
    }

    public void setSurveyModeId(Long surveyModeId) {
        this.surveyModeId = surveyModeId;
    }

    public String getSurveyMode() {
        return surveyMode;
    }

    public void setSurveyMode(String surveyMode) {
        this.surveyMode = surveyMode;
    }

    public Long getFlexibleSurveyReasonId() {
        return flexibleSurveyReasonId;
    }

    public void setFlexibleSurveyReasonId(Long flexibleSurveyReasonId) {
        this.flexibleSurveyReasonId = flexibleSurveyReasonId;
    }

    public String getFlexibleSurveyReason() {
        return flexibleSurveyReason;
    }

    public void setFlexibleSurveyReason(String flexibleSurveyReason) {
        this.flexibleSurveyReason = flexibleSurveyReason;
    }

	/**
	 * @return the inspectionOfficerStatusCode
	 */
	public Integer getInspectionOfficerStatusCode() {
		return inspectionOfficerStatusCode;
	}

	/**
	 * @param inspectionOfficerStatusCode the inspectionOfficerStatusCode to set
	 */
	public void setInspectionOfficerStatusCode(Integer inspectionOfficerStatusCode) {
		this.inspectionOfficerStatusCode = inspectionOfficerStatusCode;
	}

	/**
	 * @return the inspectionOfficerStatus
	 */
	public String getInspectionOfficerStatus() {
		return inspectionOfficerStatus;
	}

	/**
	 * @param inspectionOfficerStatus the inspectionOfficerStatus to set
	 */
	public void setInspectionOfficerStatus(String inspectionOfficerStatus) {
		this.inspectionOfficerStatus = inspectionOfficerStatus;
	}
    
    
    
}