package com.amnex.agristack.centralcore.Enum;

import java.util.ArrayList;
import java.util.List;

public enum MapperIdEnum {
    I2O16("i2:o16"),
    I1004O1003("i1004:o1003"),
    I1005O1003("i1005:o1003"),
    I1004O1005("i1004:o1005"),
    I1004O1006("i1004:o1006"),
    I1005O1006("i1005:o1006"),
    I1004O1007("i1004:o1007");
    private String value;
    private MapperIdEnum(String value) {
        this.value = value;
    }
    public String getValue() {
        return this.value;
    }

    public Boolean isExistMapperId(String value){
        MapperIdEnum[] valuesOfMapperId = MapperIdEnum.values();

        return false;
    }

}