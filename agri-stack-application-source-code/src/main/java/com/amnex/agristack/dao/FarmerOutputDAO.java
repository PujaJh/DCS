package com.amnex.agristack.dao;

import lombok.Data;

import java.util.List;

@Data
public class FarmerOutputDAO {
    private Long villageLgdCode;
    private Long farmlandPlotRegistryId;
    private String surveyNumber;
    private String subSurveyNumber;
    private String farmlandId;
    private String sfdbFarmlandId;
    private Double plotArea;
    private String villageName;
    private String districtName;
    private String subDistrictName;
    private String stateName;
    private List<SeasonMasterOutputDAO> seasonList;
    private List<YearDao> yearList;

    public FarmerOutputDAO(Long farmlandPlotRegistryId, String surveyNumber, String subSurveyNumber, String farmlandId, Double farmArea, Long villageLgdCode) {
        this.farmlandPlotRegistryId = farmlandPlotRegistryId;
        this.surveyNumber = surveyNumber;
        this.subSurveyNumber = subSurveyNumber;
        this.farmlandId = farmlandId;
        this.plotArea = farmArea;
        this.villageLgdCode = villageLgdCode;
    }
}
