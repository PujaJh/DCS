package com.amnex.agristack.centralcore.dao;

import lombok.Data;

import java.util.List;

@Data
public class CropDetailsOutputDAO {
    public String farmer_id;
    public String farm_id;
    public String village_lgd_code;
    public String district_lgd_code;
    public String sub_district_lgd_code;
    public String survey_number;
    public String year;
    public String season;
    public String sown_area;
    public String sown_area_unit;
    public String crop_code;
    public List<Object> crop_photo;
    public Object sown_area_geometry;
    public String sowing_date;
    public String irrigation_type;


}
