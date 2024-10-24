package com.amnex.agristack.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;

@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Data
public class CentralCropSownDAO {
    public Long stateLgdCode;
    public Long villageLgdCode;
    public String year;
    public String season;
    public String cropCode;
    public Long noOfFarmers;
    public Long noOfPlots;
    public Double sownArea;
    public String sownAreaUnit;
    public String surveyStatus;
    public String action;
    public String irrigationType;
    public Long recordId;
}
