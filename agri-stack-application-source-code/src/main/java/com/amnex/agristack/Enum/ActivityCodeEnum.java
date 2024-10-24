package com.amnex.agristack.Enum;

public enum ActivityCodeEnum {
    CROP_SURVEY(100),
    USER_PROFILE_IMAGE(101),
    FARMER_GRIEVANCE(102);

    private int value;

    public int getValue() {
        return this.value;
    }

    private ActivityCodeEnum(int value) {
        this.value = value;
    }
}
