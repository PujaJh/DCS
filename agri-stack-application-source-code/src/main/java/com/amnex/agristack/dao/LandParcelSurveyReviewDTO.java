package com.amnex.agristack.dao;

import lombok.Data;

@Data
public class LandParcelSurveyReviewDTO {
    Long lpsdId;
    Boolean isAccepted;
    Long reasonId;
    String rejectedRemarks;
}
