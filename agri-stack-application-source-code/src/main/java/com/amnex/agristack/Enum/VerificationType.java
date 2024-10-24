package com.amnex.agristack.Enum;

public enum VerificationType {
    EMAIL(0),
    MOBILE(1),

    AADHAAR(2);
    private Integer value;

    public Integer getValue()
    {
        return this.value;
    }

    private VerificationType(Integer value)
    {
        this.value = value;
    }
}
