package com.amnex.agristack.centralcore.dao;

import lombok.Data;

import java.util.List;

@Data
public class PlotLevelInfo {
    public Long village_lgd_code;
    public String village_plot_ref;
    public String farmland_plot_id;
    public String survey_number;
    public String sub_survey_number;
    public String total_area;
    public String area_unit;

    public String na_area;
    public String fallow_area;
    public String harvested_area;
    public List<OwnerDAO> owner;
    public List<CropDAO> crop;
}
