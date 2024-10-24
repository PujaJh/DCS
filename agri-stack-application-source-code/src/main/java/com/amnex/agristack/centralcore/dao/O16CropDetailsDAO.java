package com.amnex.agristack.centralcore.dao;

import lombok.Data;

import java.util.List;

@Data
public class O16CropDetailsDAO {
    public String farmId;
    public String year;
    public String season;
    public String seasonId;
    public String cropId;
    public String cropName;
    public String sownArea;
    public String sownAreaUnit;
    public List<Object> cropPhotos;
    public Object plotGeometry;
    public String sowingDate;
    public String irrigationType;

}
