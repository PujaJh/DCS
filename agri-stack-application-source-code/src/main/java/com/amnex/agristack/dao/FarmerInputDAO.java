package com.amnex.agristack.dao;

import lombok.Data;

import java.util.List;

@Data
public class FarmerInputDAO {
    private Long seasonId;
    private Integer startYear;
    private Integer endYear;
    private String sfdbFarmlandId;
    private List<Long> villageLgdCodeList;
    private List<FarmerDetailInputDAO> farmerDetailInputDAO;
}
