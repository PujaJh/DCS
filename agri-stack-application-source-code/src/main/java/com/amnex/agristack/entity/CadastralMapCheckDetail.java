package com.amnex.agristack.entity;


import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
public class CadastralMapCheckDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @Column
    private Integer farmlandPlotId;

    @Column
    private Integer villageLgdCode;

    @Column
    private String surveyNo;

    @Column
    private String subSurveyNo;

    @Column
    private Double surveyorLat;

    @Column
    private Double surveyorLong;
    
    @Column
    private Double nearestDistance;

    @Column(columnDefinition = "TEXT")

    private String nearestPolylineWkt;

    @Column(columnDefinition = "TEXT")
    private String nearestPointWkt;

    @Column
    private Timestamp surveyorMobileDate;

    @Column
    private Boolean isActive;

    @Column
    private Boolean isDeleted;

    @Column
    private Long createdBy;

    @Column
    private Timestamp createdOn;

    @Column
    private Long modifiedBy;

    @Column
    private Timestamp modifiedOn;
}
