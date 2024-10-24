package com.amnex.agristack.dao;

import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.Column;

@Data
public class CadastralMapDetailInput {

    private Integer farmlandPlotId;

    private Integer villageLgdCode;

    private String surveyNo;

    private String subSurveyNo;

    private Double surveyorLat;

    private Double surveyorLong;
    
    private Double nearestDistance;

    private String nearestPolylineWkt;

    private String nearestPointWkt;

    private String surveyorMobileDate;

    private Long userId;
}
