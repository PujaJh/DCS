package com.amnex.agristack.centralcore.dao;

import lombok.Data;

import java.util.List;

@Data
public class O1005OutputDAO {
    public String farmer_id;
    public String state_lgd_code;
    public Object FarmerData;
    public List<CropDetailsOutputDAO> crop_sown_data;


}
