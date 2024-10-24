package com.amnex.agristack.dao;

import java.util.List;

import com.amnex.agristack.entity.FarmerGrievanceMediaMapping;

import lombok.Data;

@Data
public class FarmerGrievanceDTO {
    Long id;
    Long lpsmId;
    Long grievanceReasonId;
    String grievanceReason;
    String description;
    Long userId;
    Long status;
    Long resolvedBy;
    List<FarmerGrievanceMediaMapping> farmerGrievanceMedias;
    String statusName;
    String refId;
    String rejectedRemark;
    String username;
    String objectionRaiseDate;
    String villageName;
    String districtName;
    String subDistrictName;
    String surveyNumber;
    String subSurveyNumber;
    String seasonName;
    Long startYear;
    Long endYear;
}
