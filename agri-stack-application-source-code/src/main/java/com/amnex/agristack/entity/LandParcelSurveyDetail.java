package com.amnex.agristack.entity;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.vividsolutions.jts.geom.Geometry;

import lombok.Data;

/**
 * Entity class representing details of land parcel survey.
 * Each instance corresponds to a specific survey detail with various attributes including
 * survey detail ID, survey map ID, review number, review by, review date, surveyor, status,
 * survey date, rejected reason, rejected remarks, owner ID, mobile number, area, unit,
 * total area, total area unit, balanced area, balanced area unit, geometry, surveyor latitude,
 * surveyor longitude, unique survey ID, and online status flag.
 */

@Entity
@Data
public class LandParcelSurveyDetail extends BaseEntity {

	private static final long serialVersionUID = 1L;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long lpsdId;

    // commented and converted to long instead of JPA for performance optimization by HARSHAL PATIL 
    // @OneToOne
    // @JoinColumn(name = "lpsm_id", referencedColumnName = "lpsmId")
    private Long lpsmId;
    private Integer reviewNo;
    private Long reviewBy;
    private Timestamp reviewDate;
    private Long surveyBy;
    private Integer status;
    private Timestamp surveyDate;

    //    commented and converted to long instead of JPA for performance optimization by HARSHAL PATIL 
    // @OneToOne
    // @JoinColumn(name = "reason", referencedColumnName = "id")
    private Long rejectedReason;
    private String rejectedRemarks;
    private Long ownerId;
    private String mobileNo;
    private Double area;
    private Long unit;
    private Double totalArea;
    private Long totalAreaUnit;
    private Double balancedArea;
    private Long balancedAreaUnit;


    @Column(columnDefinition = "GEOMETRY")
    private Geometry geom;
    private Double surveyorLat;
    private Double surveyorLong;  
    @Column(columnDefinition = "TEXT")
    private String uniqueSurveyId;
    
    private Boolean isOnline;
}
