package com.amnex.agristack.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

/**
 * class: KCC Linkage
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class KCCLinkage extends BaseEntity {
    /**
     * field: id
     * type: integer
     */
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "id", nullable = false, columnDefinition="Integer")
    private String id;

    /**
     * field: farmerIDKey
     * type: string
     */
    @ManyToOne
    @JoinColumn(name = "farmer_id_key")
    private FarmerRegistry farmerIDKey;
    /**
     * field: KCCBankCode
     * type: integer
     */
    @Column(name = "kcc_bank_code", columnDefinition="Integer")
    private Integer KCCBankCode;
    /**
     * field: KCCBranchCode
     * type: integer
     */
    @Column(name = "kcc_branch_code", columnDefinition="Integer")
    private Integer KCCBranchCode;
    /**
     * field: KCCBankDistrict
     * type: string
     */
    @Column(name = "kcc_bank_district", columnDefinition="VARCHAR(100)")
    private String KCCBankDistrict;
    /**
     * field: KCCAcNumber
     * type: string
     */
    @Column(name = "kcc_ac_number", columnDefinition="VARCHAR(20)")
    private String KCCAcNumber;
    /**
     * field: KCCCardValue
     * type: Double
     */
    @Column (name= "KCC_card_value", columnDefinition="NUMERIC(10, 0)")
    private Double KCCCardValue;
    /**
     * field: KCCCardIssuanceDate
     * type: date
     */
    @Column(name = "kcc_card_issue_date", columnDefinition="Date")
    private Date KCCCardIssueDate;
    /**
     * field: KCCCardLatestRenewalDate
     * type: date
     */
    @Column(name = "kcc_card_latest_renewal_date", columnDefinition="Date")
    private Date KCCCardLatestRenewalDate;
    /**
     * field: KCCCardValidityExpiryMonthYear
     * type: string
     */
    @Column(name = "KCC_card_validity_expiry_month_year", columnDefinition="VARCHAR(7)")
    private String KCCCardValidityExpiryMonthYear;

    /**
     * field: KCCLastUpdateDateTime
     * type: Calendar
     */
    @Column(name = "kcc_last_update_date_time", columnDefinition="TIMESTAMP")
    private Calendar KCCLastUpdateDateTime;

    /**
     * field: KCCSynchronisationDate
     * type: Calendar
     */
    @Column(name = "KCC_Synchronisation_Date", columnDefinition="TIMESTAMP")
    private Calendar KCCSynchronisationDate;

    /**
     * get id
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * set id
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * get farmerIDKey
     * @return farmerIDKey
     */
    public FarmerRegistry getFarmerIDKey() {
        return farmerIDKey;
    }

    /**
     * set farmerIDKey
     * @param farmerIDKey
     */
    public void setFarmerIDKey(FarmerRegistry farmerIDKey) {
        this.farmerIDKey = farmerIDKey;
    }

    /**
     * get KCCBankCode
     * @return
     */
    public Integer getKCCBankCode() {
        return KCCBankCode;
    }

    /**
     * set KCCBankCode
     * @param KCCBankCode
     */
    public void setKCCBankCode(Integer KCCBankCode) {
        this.KCCBankCode = KCCBankCode;
    }

    /**
     * get KCCBranchCode
     * @return
     */
    public Integer getKCCBranchCode() {
        return KCCBranchCode;
    }

    /**
     * set KCCBranchCode
     * @param KCCBranchCode
     */
    public void setKCCBranchCode(Integer KCCBranchCode) {
        this.KCCBranchCode = KCCBranchCode;
    }

    /**
     * get KCC Bank District
     * @return  KCCBankDistrict
     */
    public String getKCCBankDistrict() {
        return KCCBankDistrict;
    }

    /**
     * set KCCBankDistrict
     * @param KCCBankDistrict
     */
    public void setKCCBankDistrict(String KCCBankDistrict) {
        this.KCCBankDistrict = KCCBankDistrict;
    }

    /**
     * get KCCAcNumber
     * @return KCCAcNumber
     */
    public String getKCCAcNumber() {
        return KCCAcNumber;
    }

    /**
     * set KCCAcNumber
     * @param KCCAcNumber
     */
    public void setKCCAcNumber(String KCCAcNumber) {
        this.KCCAcNumber = KCCAcNumber;
    }

    /**
     * get KCCCardValue
     * @return
     */
    public Double getKCCCardValue() {
        return KCCCardValue;
    }

    /**
     * set KCCCardValue
     * @param KCCCardValue
     */
    public void setKCCCardValue(Double KCCCardValue) {
        this.KCCCardValue = KCCCardValue;
    }

    /**
     * get KCCCardIssueDate
     * @return KCCCardIssueDate
     */
    public Date getKCCCardIssueDate() {
        return KCCCardIssueDate;
    }

    /**
     * set KCCCardIssueDate
     * @param KCCCardIssueDate
     */
    public void setKCCCardIssueDate(Date KCCCardIssueDate) {
        this.KCCCardIssueDate = KCCCardIssueDate;
    }

    /**
     * get KCCCardLatestRenewalDate
     * @return
     */
    public Date getKCCCardLatestRenewalDate() {
        return KCCCardLatestRenewalDate;
    }

    /**
     * set KCCCardLatestRenewalDate
     * @param KCCCardLatestRenewalDate
     */
    public void setKCCCardLatestRenewalDate(Date KCCCardLatestRenewalDate) {
        this.KCCCardLatestRenewalDate = KCCCardLatestRenewalDate;
    }

    /**
     * get KCCCardValidityExpiryMonthYear
     * @return KCCCardValidityExpiryMonthYear
     */
    public String getKCCCardValidityExpiryMonthYear() {
        return KCCCardValidityExpiryMonthYear;
    }

    /**
     * set KCCCardValidityExpiryMonthYear
     * @param KCCCardValidityExpiryMonthYear
     */
    public void setKCCCardValidityExpiryMonthYear(String KCCCardValidityExpiryMonthYear) {
        this.KCCCardValidityExpiryMonthYear = KCCCardValidityExpiryMonthYear;
    }

    /**
     * get KCCLastUpdateDateTime
     * @return
     */
    public Calendar getKCCLastUpdateDateTime() {
        return KCCLastUpdateDateTime;
    }

    /**
     * set KCCLastUpdateDateTime
     * @param KCCLastUpdateDateTime
     */
    public void setKCCLastUpdateDateTime(Calendar KCCLastUpdateDateTime) {
        this.KCCLastUpdateDateTime = KCCLastUpdateDateTime;
    }

    public Calendar getKCCSynchronisationDate() {
        return KCCSynchronisationDate;
    }

    public void setKCCSynchronisationDate(Calendar KCCSynchronisationDate) {
        this.KCCSynchronisationDate = KCCSynchronisationDate;
    }
}
