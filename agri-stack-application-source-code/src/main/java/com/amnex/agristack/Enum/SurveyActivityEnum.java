package com.amnex.agristack.Enum;

public enum SurveyActivityEnum {
    TASK_ALLOCATION(0),
    SURVEY_BY_SURVEYOR(1),
    CROP_SURVEY_BY_FARMER(2),
    REVIEW_BY_SUPERVISOR(3),
    VERIFICATION_BY_VERIFIER(4),
    INSPECTION_BY_INSPECTION_OFFICER(5),
    GRIEVANCE_REDRESSAL(6),
    PR_APPROVAL(7);
    private Integer value;
    public Integer getValue()
    {
        return this.value;
    }

    private SurveyActivityEnum(Integer value)
    {
        this.value = value;
    }
}
