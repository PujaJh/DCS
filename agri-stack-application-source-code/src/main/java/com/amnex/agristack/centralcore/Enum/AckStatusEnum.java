package com.amnex.agristack.centralcore.Enum;

public enum AckStatusEnum {
    ACK("ACK"),
    NACK("NACK"),
    ERR("ERR");
    private String value;
    private AckStatusEnum(String value) {
        this.value = value;
    }
    public String getValue() {
        return this.value;
    }
}