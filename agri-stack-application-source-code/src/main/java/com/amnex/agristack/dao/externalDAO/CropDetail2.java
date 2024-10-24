package com.amnex.agristack.dao.externalDAO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Data
public class CropDetail2 {
    public double cropArea;
    public String cropRemarks;
    public String cropCode;
    public String cropName;
    public String areaType;
    public String sowingDate;
    public double cropClassId;
    public String irrigationSource;
    public String cropStage;
    public String cropClassName;
    public String cropVarietyName;
    public Object nonAgriTypeName;
    public ArrayList<String> media;
    public String landType;
    public String unitName;
    public String cropType;
    public String irrigationType;
    public double cropCount;
    public String farmlandId;
    public String surveyNumber;
    public String subSurveyNumber;
}
