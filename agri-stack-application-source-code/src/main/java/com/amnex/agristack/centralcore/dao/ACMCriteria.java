package com.amnex.agristack.centralcore.dao;

/**
 * ACMCriteria
 */
public class ACMCriteria {
    private String attribute_name;
    private String operator;
    private String attribute_value;

    // Getter Methods

    public String getAttribute_name() {
        return attribute_name;
    }

    public String getOperator() {
        return operator;
    }

    public String getAttribute_value() {
        return attribute_value;
    }

    // Setter Methods

    public void setAttribute_name(String attribute_name) {
        this.attribute_name = attribute_name;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public void setAttribute_value(String attribute_value) {
        this.attribute_value = attribute_value;
    }
}