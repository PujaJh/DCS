package com.amnex.agristack.centralcore.Enum;

import lombok.Getter;

@Getter
public enum QueryTypeEnum {
    NAMED("namedQuery"),
    PREDICATE("predicateQuery");
    private String value;
    private QueryTypeEnum(String value) {
        this.value = value;
    }

    public static Boolean isValidQueryType(String str) {
        for (QueryTypeEnum queryType : QueryTypeEnum.values()) {
            if (queryType.getValue().equalsIgnoreCase(str))
                return true;
        }
        return false;
    }

}