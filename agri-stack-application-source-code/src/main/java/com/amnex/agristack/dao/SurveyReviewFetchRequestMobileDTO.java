package com.amnex.agristack.dao;

import lombok.Data;

@Data
public class SurveyReviewFetchRequestMobileDTO {
    private String userId;
    private String seasonId;
    private String startYear;
    private String endYear;
    private String plotIds;
    private String surveyMasterId;
    private Double lat = 0d;
    private Double lon = 0d;
    private String year;
}
