//package com.amnex.agristack.entity;
//import com.fasterxml.jackson.annotation.JsonInclude;
//import lombok.EqualsAndHashCode;
//import org.hibernate.annotations.GenericGenerator;
//
//import javax.persistence.*;
//import java.util.Calendar;
//import java.util.List;
//
//@Entity
//@EqualsAndHashCode(callSuper = false)
//@JsonInclude(value = JsonInclude.Include.NON_NULL)
//public class FarmlandFarmerSchemeMapping extends BaseEntity {
//    @Id
//    @GeneratedValue(generator = "system-uuid")
//    @GenericGenerator(name = "system-uuid", strategy = "uuid")
//    @Column(name = "ffs_mapping", columnDefinition = "VARCHAR(100)")
//    private String ffsMapping;
//
//
//    @ManyToOne
//    @JoinColumn(name = "farmer_id")
//    private FarmerRegistry farmerId;
//    @ManyToOne
//    @JoinColumn(name = "farmland_plot_id", columnDefinition="VARCHAR(12)")
//    private FarmlandPlotRegistry farmlandPlotId;
//    @ManyToOne
//    @JoinColumn(name = "scheme_id", columnDefinition="VARCHAR(100)")
//    private SchemeMaster schemeId;
//    @Column(name = "subscheme_id", columnDefinition="VARCHAR(100)")
//    private String subschemeId;
//    @Column(name = "scheme_component_id", columnDefinition="VARCHAR(100)")
//    private String schemeComponentId;
//    @Column(name = "scheme_subcomponent_id", columnDefinition="VARCHAR(100)")
//    private String schemesubcomponentId;
//    @Column(name = "financial_year", columnDefinition="VARCHAR(12)")
//    private String financialYear;
//    @Column(name = "benefit_type", columnDefinition="VARCHAR(2)")
//    private String benefitType;
//    @Column(name = "benefit_value", columnDefinition="numeric(10,2)")
//    private Double benefitValue;
//    @Column(name = "date_of_disbursal", columnDefinition="TIMESTAMP")
//    private Calendar dateOfDisbursal;
//    @Column(name = "utr_details", columnDefinition="VARCHAR(30)")
//    private String utrDeatils;
//    @Column(name = "br_last_upd_date_time", columnDefinition="TIMESTAMP")
//    private Calendar brLastUpdDateTime;
//
//    @Column(name = "br_synchronisation_date", columnDefinition="TIMESTAMP")
//    private Calendar brSynchronisationDate;
//
//    public String getffsMapping() {
//        return ffsMapping;
//    }
//
//    public void setFffsMapping(String ffsMapping) {
//        this.ffsMapping = ffsMapping;
//    }
//
//
//    public FarmlandPlotRegistry getFarmlandPlotId() {
//        return farmlandPlotId;
//    }
//
//    public void setFarmlandPlotId(FarmlandPlotRegistry farmlandPlotId) {
//        this.farmlandPlotId = farmlandPlotId;
//    }
//
//    public String getSubschemeId() {
//        return subschemeId;
//    }
//
//    public void setSubschemeId(String subschemeId) {
//        this.subschemeId = subschemeId;
//    }
//
//    public String getSchemeComponentId() {
//        return schemeComponentId;
//    }
//
//    public void setSchemeComponentId(String schemeComponentId) {
//        this.schemeComponentId = schemeComponentId;
//    }
//
//    public String getSchemesubcomponentId() {
//        return schemesubcomponentId;
//    }
//
//    public void setSchemesubcomponentId(String schemesubcomponentId) {
//        this.schemesubcomponentId = schemesubcomponentId;
//    }
//
//    public String getFinancialYear() {
//        return financialYear;
//    }
//
//    public void setFinancialYear(String financialYear) {
//        this.financialYear = financialYear;
//    }
//
//    public String getBenefitType() {
//        return benefitType;
//    }
//
//    public void setBenefitType(String benefitType) {
//        this.benefitType = benefitType;
//    }
//
//    public Double getBenefitValue() {
//        return benefitValue;
//    }
//
//    public void setBenefitValue(Double benefitValue) {
//        this.benefitValue = benefitValue;
//    }
//
//    public Calendar getDateOfDisbursal() {
//        return dateOfDisbursal;
//    }
//
//    public void setDateOfDisbursal(Calendar dateOfDisbursal) {
//        this.dateOfDisbursal = dateOfDisbursal;
//    }
//
//    public String getUtrDeatils() {
//        return utrDeatils;
//    }
//
//    public void setUtrDeatils(String utrDeatils) {
//        this.utrDeatils = utrDeatils;
//    }
//
//    public Calendar getBrLastUpdDateTime() {
//        return brLastUpdDateTime;
//    }
//
//    public void setBrLastUpdDateTime(Calendar brLastUpdDateTime) {
//        this.brLastUpdDateTime = brLastUpdDateTime;
//    }
//
//    public Calendar getBrSynchronisationDate() {
//        return brSynchronisationDate;
//    }
//
//    public void setBrSynchronisationDate(Calendar brSynchronisationDate) {
//        this.brSynchronisationDate = brSynchronisationDate;
//    }
//
//    public String getFfsMapping() {
//        return ffsMapping;
//    }
//
//    public void setFfsMapping(String ffsMapping) {
//        this.ffsMapping = ffsMapping;
//    }
//
//    public FarmerRegistry getFarmerId() {
//        return farmerId;
//    }
//
//    public void setFarmerId(FarmerRegistry farmerId) {
//        this.farmerId = farmerId;
//    }
//
//    public SchemeMaster getSchemeId() {
//        return schemeId;
//    }
//
//    public void setSchemeId(SchemeMaster schemeId) {
//        this.schemeId = schemeId;
//    }
//
//
//
//}
