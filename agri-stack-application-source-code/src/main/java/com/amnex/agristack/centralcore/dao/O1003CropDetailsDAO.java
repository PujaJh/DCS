package com.amnex.agristack.centralcore.dao;

import lombok.Data;

import java.util.List;

@Data
public class O1003CropDetailsDAO {
    public String farmer_id;
    public String farm_id;
    public String sown_area;
    public String sown_area_unit;
    public String crop_code;
    public List<Object> crop_photo;
    public Object sown_area_geometry;
    public String sowing_date;
    public String irrigation_type;


}
