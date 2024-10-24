package com.amnex.agristack.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class VillageWiseCropSurveyDetail{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private Timestamp createdOn;
    @Column
    private Boolean isActive;
    @Column
    private Boolean isDeleted;
    @Column
    private String action;
    @Column
    private Timestamp modifiedOn;
    @Column
    private Long stateLgdCode;
    @Column
    private String stateName;
    @Column
    private Long villageLgdCode;
    @Column
    private String villageName;
    @Column
    private String year;
    @Column
    private Long seasonId;
    @Column
    private String seasonName;
    @Column
    private Long cropId;
    @Column
    private String cropCode;
    @Column
    private String cropName;
    @Column
    private String sownAreaUnit;

    private Integer noOfFarmers;
    private Integer noOfPlots;
    private Double sownArea;
    private Boolean isShared;

    @Column
    private Integer isAcknowledged = 103;

    @Column
    private String status;

    @Column
    private String irrigationType;

    @Transient
    private Long recordId;

    @Transient
    private String season;

    @Transient
    private String surveyStatus;

    @Column
    private String referenceId;

    @Column
    private String previousReferenceId;

    @Column
    private String irrigationSource;

    @Transient
    private String sownAreaStr;

}
