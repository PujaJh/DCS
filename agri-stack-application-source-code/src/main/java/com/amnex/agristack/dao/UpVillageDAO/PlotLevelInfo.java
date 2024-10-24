package com.amnex.agristack.dao.UpVillageDAO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Data
public class PlotLevelInfo {
    public PlotLevelInfo plotLevelInfo;
    public CropDetail cropDetail;
    public Long villageLgdCode;
    public String farmlandPlotId;
    public String surveyNumber;
    public String subSurveyNumber;
    public Double totalArea;
    public String areaUnit;
    public String uniqueCode;
    public String plotGeometry;
    public List<OwnerDetail> ownerDetails;
}
