package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.Data;

import java.sql.Timestamp;

/**
 * Entity class representing details of land parcel survey master.
 * Each instance corresponds to a specific survey master with various attributes including
 * survey master ID, parcel ID, season start year, season end year, season ID,
 * survey one status, survey two status, verifier status, inspection officer status,
 * survey status, survey mode, flexible survey reason ID, village LGD code,
 * sub-district LGD code, district LGD code, state LGD code, app version, and token.
 */

@Entity
@Data
public class LandParcelSurveyMaster extends BaseEntity {
	
	private static final long serialVersionUID = 1L;
	
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lpsmId;

    @OneToOne
    @JoinColumn(name = "parcel_id", referencedColumnName = "fpr_farmland_plot_registry_id")
    private FarmlandPlotRegistry parcelId;

    private Integer seasonStartYear;

    private Integer seasonEndYear;

    private Long seasonId;

    private Integer surveyOneStatus;

    private Integer surveyTwoStatus;

    private Integer verifierStatus;
    private Integer inspectionOfficerStatus;

    private Integer surveyStatus;

    private Integer surveyMode;

    private Integer flexibleSurveyReasonId;
    
    private Long villageLgdCode;
    
    private Long subDistrictLgdCode;
    
    private Long districtLgdCode;
    
    private Long stateLgdCode;
    
    private String appVersion;
//    @Column(columnDefinition = "TEXT")
//    private String token;
    
}
