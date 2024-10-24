package com.amnex.agristack.Enum;

public enum SurveyAreaTypeEnum {
    UNUTILIZED(1),
    WASTEVACANT(2),
    CROP(3),
    HARVESTED(4);

    private int value;

    public int getValue() {
        return this.value;
    }

    private SurveyAreaTypeEnum(int value) {
        this.value = value;
    }
}
