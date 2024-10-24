package com.amnex.agristack.entity;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "validation_log")
public class ValidationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long logId;  // Maps to SERIAL PRIMARY KEY

    @Column(name = "state_lgd_code", nullable = false)
    private Long stateLgdCode;  // Maps to BIGINT NOT NULL

    @Column(name = "record_id", nullable = false)
    private String recordId;  // Maps to UUID NOT NULL

    @Column(name = "issue_code", nullable = false)
    private String issueCode;  // Maps to TEXT NOT NULL

    @Column(name = "pulled_timestamp", nullable = false)
    private Timestamp pulledTimestamp;  // Maps to TIMESTAMP NOT NULL

    @Column(name = "error_description", nullable = false)
    private String errorDescription;  // Maps to TEXT NOT NULL

    @Column(name = "resource_name")
    private String resourceName;  // Maps to TEXT, can be NULL

    @Column(name = "status")
    private String status;  // Maps to TEXT, can be NULL

    @Column(name = "created_at", nullable = false, updatable = false)
    @Generated(GenerationTime.INSERT)
    private Timestamp createdAt;  // Maps to TIMESTAMP with default CURRENT_TIMESTAMP

    // Getters and Setters
    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public Long getStateLgdCode() {
        return stateLgdCode;
    }

    public void setStateLgdCode(Long stateLgdCode) {
        this.stateLgdCode = stateLgdCode;
    }

    public String  getRecordId() {
        return recordId;
    }

    public void setRecordId(String  recordId) {
        this.recordId = recordId;
    }

    public String getIssueCode() {
        return issueCode;
    }

    public void setIssueCode(String issueCode) {
        this.issueCode = issueCode;
    }

    public Timestamp getPulledTimestamp() {
        return pulledTimestamp;
    }

    public void setPulledTimestamp(Timestamp pulledTimestamp) {
        this.pulledTimestamp = pulledTimestamp;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    // createdAt is automatically set by the database, so no setter
}
