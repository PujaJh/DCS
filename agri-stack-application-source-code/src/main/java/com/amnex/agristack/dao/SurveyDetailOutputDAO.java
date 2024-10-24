package com.amnex.agristack.dao;

import java.util.List;

import lombok.Data;

@Data
public class SurveyDetailOutputDAO {

	public Integer lpsdId;
	public Integer lpsmId;
	public String ownerName;
	public Integer ownerId;
	public String villageName;
	public Integer villageLgdCode;
	public Integer subDistrictLgdCode;
	public String subDistrictName;
	public Integer districtLgdCode;
	public String districtName;
	public Integer stateLgdCode;
	public String stateName;
	public String farmlandId;
	public String farmlandParcelId;
	public String surveyNumber;
	public String subSurveyNumber;
	public Double totalArea;
	public Double balancedArea;
	public String totalAreaUnit;
	public String balancedAreaUnit;
	public Long totalAreaUnitId;
	public Long balancedAreaUnitId;
	public String surveyorUniqueId;
	public String surveyorName;
	public Integer reasonId;
	public String reasonName;
	public String rejectedReason;
	public Integer reviewNo;
	public String surveyedGeom;
	public String actualGeom;
	public String surveyAssignementDate;
	public String surveyDate;
	public String surveySubmissionDate;
	public Double surveyorLat;
	public Double surveyorLong;

	public List<SurveyCropDetailDAO> cropDetails;
}
