package com.amnex.agristack.centralcore.dao;

import lombok.Data;

import java.util.List;

@Data
public class O1003OutputDAO {
    public String state_lgd_code;
    public String village_lgd_code;
    public String district_lgd_code;
    public String sub_district_lgd_code;
    public String survey_number;
    public String year;
    public String season;
    public List<O1003CropDetailsDAO> crop_sown_data;
    public String farmer_id;

}
