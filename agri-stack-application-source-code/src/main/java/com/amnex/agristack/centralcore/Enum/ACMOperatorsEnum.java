package com.amnex.agristack.centralcore.Enum;

import java.util.HashMap;
import java.util.Map;

public enum ACMOperatorsEnum {
    GT("gt", ">"),
    LT("lt", "<"),
    EQ("eq", "="),
    GE("ge", ">="),
    LE("le", "<="),
    IN("in", "in");

    private String operator;
    private String value;

    private ACMOperatorsEnum(String operator, String value) {
        this.operator = operator;
        this.value = value;
    }

    private static final Map<String, String> ENUM_MAP = new HashMap<>();

    static {
        for (ACMOperatorsEnum value : values()) {
            ENUM_MAP.put(value.operator, value.value);
        }
    }

    public String getValue() {
        return this.value;
    }

    public String getOperator() {
        return this.operator;
    }

    public static String getByOperator(String stringValue) {
        return ENUM_MAP.get(stringValue);
    }
}
