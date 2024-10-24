package com.amnex.agristack.dao;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlotLandDetailDAO {

    public Long recordId;
    public String villageLgdCode;
    public String surveyNo;
    public String farmId;
    public String projectionCode;
    public String plotGeometry;
    public String action;
    public String geometryType;
    public String ulpin;
}
