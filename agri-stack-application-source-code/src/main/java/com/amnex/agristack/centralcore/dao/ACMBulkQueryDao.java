package com.amnex.agristack.centralcore.dao;

public class ACMBulkQueryDao {
    private String seq_number;
    ACMCriteria criteria;
    private String condition;

    // Getter Methods

    public String getSeq_number() {
        return seq_number;
    }

    public ACMCriteria getCriteria() {
        return criteria;
    }

    public String getCondition() {
        return condition;
    }

    // Setter Methods

    public void setSeq_number(String seq_number) {
        this.seq_number = seq_number;
    }

    public void setCriteria(ACMCriteria criteriaObject) {
        this.criteria = criteriaObject;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}