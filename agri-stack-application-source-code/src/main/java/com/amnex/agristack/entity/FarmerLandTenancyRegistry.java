//package com.amnex.agristack.entity;
//
//
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
//public class FarmerLandTenancyRegistry extends BaseEntity{
//    @Id
//    @GeneratedValue(generator = "system-uuid")
//    @GenericGenerator(name = "system-uuid", strategy = "uuid")
//    @Column(name = "farmer_land_tenancy_registry_id", columnDefinition="VARCHAR(15)")
//    private String FarmerLandTenancyRegistryId;
//    @ManyToOne
//    @JoinColumn(name = "fltr_farmer_id_key")
//    private FarmerRegistry fltrFarmerIdKey;
//    @OneToOne
//    @JoinColumn(name = "fltr_farmland_parcel_plot_id",referencedColumnName = "farm_land_parcel_plot_id", columnDefinition="VARCHAR(1000)")
//    private FarmlandPlotRegistry fltrFarmlandParcelPlotId;
//    @OneToOne
//    @JoinColumn(name = "farmland_plot_ulpin", referencedColumnName = "farm_land_parcel_ulpin", columnDefinition="VARCHAR(10000)")
//    private FarmlandPlotRegistry farmlandPlotULPin;
//    @OneToOne
//    @JoinColumn(name = "owner_farmer_id_key", columnDefinition="VARCHAR(15)")
//    private FarmerLandOwnershipRegistry ownerFarmerIdKey;
//    @Column(name = "farm_owner_no_as_per_ror", columnDefinition="VARCHAR(30)")
//    private String farmOwnerNoAsPerRor;
//    @Column(name = "farm_owner_name_ror", columnDefinition="VARCHAR(100)")
//    private String farmOwnerNameRor;
//    @Column(name = "fltr_extent_tenanted_in_hect", columnDefinition="numeric(8,5)")
//    private Double fltrExtentTenantedInHect;
//    @Column(name = "fltr_update_date", columnDefinition="TIMESTAMP")
//    private Calendar fltrUpdateDate;
//    @Column(name = "fltr_synchronisation_date", columnDefinition="TIMESTAMP")
//    private Calendar fltrSynchronisationDate;
//
//    @Column(name = "farm_owership_status", columnDefinition="CHAR(1)")
//    private String farmOwnershipStatus;
//
//    public String getFarmerLandTenancyRegistryId() {
//        return FarmerLandTenancyRegistryId;
//    }
//
//    public void setFarmerLandTenancyRegistryId(String farmerLandTenancyRegistryId) {
//        FarmerLandTenancyRegistryId = farmerLandTenancyRegistryId;
//    }
//
//    public FarmerRegistry getFltrFarmerIdKey() {
//        return fltrFarmerIdKey;
//    }
//
//    public void setFltrFarmerIdKey(FarmerRegistry fltrFarmerIdKey) {
//        this.fltrFarmerIdKey = fltrFarmerIdKey;
//    }
//
//    public FarmlandPlotRegistry getFltrFarmlandParcelPlotId() {
//        return fltrFarmlandParcelPlotId;
//    }
//
//    public void setFltrFarmlandParcelPlotId(FarmlandPlotRegistry fltrFarmlandParcelPlotId) {
//        this.fltrFarmlandParcelPlotId = fltrFarmlandParcelPlotId;
//    }
//
//    public FarmlandPlotRegistry getFarmlandPlotULPin() {
//        return farmlandPlotULPin;
//    }
//
//    public void setFarmlandPlotULPin(FarmlandPlotRegistry farmlandPlotULPin) {
//        this.farmlandPlotULPin = farmlandPlotULPin;
//    }
//
//    public FarmerLandOwnershipRegistry getOwnerFarmerIdKey() {
//        return ownerFarmerIdKey;
//    }
//
//    public void setOwnerFarmerIdKey(FarmerLandOwnershipRegistry ownerFarmerIdKey) {
//        this.ownerFarmerIdKey = ownerFarmerIdKey;
//    }
//
//    public String getFarmOwnerNoAsPerRor() {
//        return farmOwnerNoAsPerRor;
//    }
//
//    public void setFarmOwnerNoAsPerRor(String farmOwnerNoAsPerRor) {
//        this.farmOwnerNoAsPerRor = farmOwnerNoAsPerRor;
//    }
//
//    public String getFarmOwnerNameRor() {
//        return farmOwnerNameRor;
//    }
//
//    public void setFarmOwnerNameRor(String farmOwnerNameRor) {
//        this.farmOwnerNameRor = farmOwnerNameRor;
//    }
//
//    public Double getFltrExtentTenantedInHect() {
//        return fltrExtentTenantedInHect;
//    }
//
//    public void setFltrExtentTenantedInHect(Double fltrExtentTenantedInHect) {
//        this.fltrExtentTenantedInHect = fltrExtentTenantedInHect;
//    }
//
//    public Calendar getFltrUpdateDate() {
//        return fltrUpdateDate;
//    }
//
//    public void setFltrUpdateDate(Calendar fltrUpdateDate) {
//        this.fltrUpdateDate = fltrUpdateDate;
//    }
//
//    public Calendar getFltrSynchronisationDate() {
//        return fltrSynchronisationDate;
//    }
//
//    public void setFltrSynchronisationDate(Calendar fltrSynchronisationDate) {
//        this.fltrSynchronisationDate = fltrSynchronisationDate;
//    }
//
//    public String getFarmOwnershipStatus() {
//        return farmOwnershipStatus;
//    }
//
//    public void setFarmOwnershipStatus(String farmOwnershipStatus) {
//        this.farmOwnershipStatus = farmOwnershipStatus;
//    }
//
//
//}
