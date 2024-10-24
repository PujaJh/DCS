package com.amnex.agristack.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;


@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class VillageWiseCropSurveyDetailDAO {

    
    private Long id;

  
    private Timestamp created_on;
  
    private Boolean isActive;
  
    private Boolean isDeleted;
  
    private String action;
  
    private Timestamp modifiedOn;
  
    private Long stateLgdCode;
  
    private String stateName;
  
    private Long villageLgdCode;
  
    private String villageName;
  
    private String year;
  
    private Long seasonId;
  
    private String seasonName;
  
    private Long cropId;
  
    private String cropName;
  
    private String sownAreaUnit;

    private Integer noOfFarmers;
    private Integer noOfPlots;
    private Double sownArea;
    private Boolean isShared = false;
    private String status;
    private String irrigationType;
    private String referenceId;
    private String sownAreaStr;

}
