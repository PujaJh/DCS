package com.amnex.agristack.Enum;

public enum LandTypeEnum {
    AGRICULTURAL(1),
    NONAGRICULTURAL(2);

    private Integer value;

    public Integer getValue() {
        return this.value;
    }

    private LandTypeEnum(Integer value) {
        this.value = value;
    }
}
