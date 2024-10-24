package com.amnex.agristack.Enum;

import java.util.HashMap;
import java.util.Map;

public enum StatusEnum {
    APPROVED(101),
    REJECTED(102),
    PENDING(103),
    SURVEY_PENDING(105),
    REASSIGNED(106),
    RESOLVED(107),
    COMPLETED(104),
    SUCCESS(108),
    FAILED(109);

    private Integer value;

    public Integer getValue() {
        return this.value;
    }

    private StatusEnum(Integer value) {
        this.value = value;
    }

    
    // Reverse-lookup map for getting a StatusEnum from a value
    private static final Map<Integer, StatusEnum> lookup = new HashMap<Integer, StatusEnum>();
    static {
        for (StatusEnum d : StatusEnum.values()) {
            lookup.put(d.getValue(), d);
        }
    }

    public static StatusEnum get(Integer value) {
        return lookup.get(value);
    }
}
