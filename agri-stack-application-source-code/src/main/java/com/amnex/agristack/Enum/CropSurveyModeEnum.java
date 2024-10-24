package com.amnex.agristack.Enum;

public enum CropSurveyModeEnum {
    MANDATORY(1),
    FLEXIBLE(2);

    private Integer value;

    CropSurveyModeEnum(Integer data) {
        this.value = data;
    }

    public Integer getValue() {
        return this.value;
    }
}
