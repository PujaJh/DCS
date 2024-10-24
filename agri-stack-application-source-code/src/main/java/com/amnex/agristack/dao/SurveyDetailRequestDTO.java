package com.amnex.agristack.dao;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

@Data
public class SurveyDetailRequestDTO {
    private Long landParcelSurveyMasterId;
    private Long farmlandPlotRegisterId;
    private Long ownerId;
    private String ownerMobile;
    private Boolean isOnline;
    private Double area;
    private Double totalArea;
    private Long totalAreaUnit;
    private Double balancedArea;
    private Long balancedAreaUnit;
    private Long unit;
    private Integer startYear;
    private Integer endYear;
    private Long seasonId;
    private Integer reviewNo;
    // private SurveyDetailUnutilizedAreaDTO unutilizedArea;
    // private SurveyDetailWasteVacantAreaDTO wasteVacantArea;
    private List<SurveyDetailCropDTO> crops;
    private String geom;
    private Long surveyBy;
    private String surveyDate;
    private Double surveyorLat;
    private Double surveyorLong;
    private Boolean isFelixible;
    private Integer surveyMode;
    private Integer flexibleSurveyReasonId;
    private String uniqueSurveyId;
    
    private String appVersion;
    private String isSigned;
    private String token;
    
    
    private List<CultivatorsRequestDAO> cultivators;

    private Integer pageSize;
    private Integer pageNo;
    private String sortField;
    private String sortOrder;
    private String search;
    private Long roleId;
    private List<Long> stateLgdCodeList;
    private List<Long> districtLgdCodeList;
    private List<Long> subDistrictLgdCodeList;
    private List<Long> villageLgdCodeList;
    private List<Long> departmentIds;
    private Integer status;
}
