package com.amnex.agristack.dao;

import lombok.Data;

@Data
public class FarmerDetailInputDAO {
       private Long villageLgdCode;
       private String surveyNumber;
       private String subSurveyNumber;
       private String farmlandId;
       private String sfdbFarmlandId;
       private Long seasonId;
       private Integer startYear;
       private Integer endYear;

}
