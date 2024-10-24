//package com.amnex.agristack.entity;
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
//public class FarmlandCropSownRegistry extends BaseEntity {
//    @Id
//    @GeneratedValue(generator = "system-uuid")
//    @GenericGenerator(name = "system-uuid", strategy = "uuid")
//    @Column(name = "farmland_crop_sown_registry_id", columnDefinition="VARCHAR(15)")
//    private String FarmlandCropSownRegistryId;
//    @ManyToOne
//    @JoinColumn(name = "farmland_plot_id_key", columnDefinition="VARCHAR(10000)")
//    private FarmlandPlotRegistry farmlandPlotIdKey;
//    @ManyToOne
//    @JoinColumn(name = "farmer_id")
//    private FarmerRegistry farmerId;
//    @Column(name = "sowing_year", columnDefinition="Integer")
//    private Integer sowingYear;
//    @Column(name = "sowing_season", columnDefinition="VARCHAR(1)")
//    private String sowingSeason;
//    @Column(name = "sowing_or_planting_date", columnDefinition="Date")
//    private String sowingOrPlantingDate;
//    @Column(name = "crop_id", columnDefinition="Integer")
//    private Integer cropId;
//    @Column(name = "fcr_crop_category", columnDefinition="Integer")
//    private Integer fcrCropCategory;
//    @Column(name = "fcr_crop_type", columnDefinition="Integer")
//    private Integer fcrCropType;
//    @Column(name = "fcr_irrigation_type", columnDefinition="Integer")
//    private Integer fcrIrrigationType;
//    @Column(name = "fcr_irrigation_source_type", columnDefinition="Integer")
//    private Integer fcrIrrigationSourceType;
//    @Column(name = "fcr_area_sown_in_hectares", columnDefinition="numeric(8,5)")
//    private String fcrAreaSownInHectares;
//    @Column(name = "fcr_last_upd_date_time", columnDefinition="TIMESTAMP")
//    private Calendar fcrLastUPDDateTime;
//
//    @Column(name = "fcr_synchronisation_date", columnDefinition="TIMESTAMP")
//    private Calendar fcrSynchronisationDate;
//
//    public String getFarmlandCropSownRegistryId() {
//        return FarmlandCropSownRegistryId;
//    }
//
//    public void setFarmlandCropSownRegistryId(String farmlandCropSownRegistryId) {
//        FarmlandCropSownRegistryId = farmlandCropSownRegistryId;
//    }
//    public Integer getSowingYear() {
//        return sowingYear;
//    }
//
//    public void setSowingYear(Integer sowingYear) {
//        this.sowingYear = sowingYear;
//    }
//
//    public String getSowingSeason() {
//        return sowingSeason;
//    }
//
//    public void setSowingSeason(String sowingSeason) {
//        this.sowingSeason = sowingSeason;
//    }
//
//    public String getSowingOrPlantingDate() {
//        return sowingOrPlantingDate;
//    }
//
//    public void setSowingOrPlantingDate(String sowingOrPlantingDate) {
//        this.sowingOrPlantingDate = sowingOrPlantingDate;
//    }
//
//    public Integer getCropId() {
//        return cropId;
//    }
//
//    public void setCropId(Integer cropId) {
//        this.cropId = cropId;
//    }
//
//    public Integer getFcrCropCategory() {
//        return fcrCropCategory;
//    }
//
//    public void setFcrCropCategory(Integer fcrCropCategory) {
//        this.fcrCropCategory = fcrCropCategory;
//    }
//
//    public Integer getFcrCropType() {
//        return fcrCropType;
//    }
//
//    public void setFcrCropType(Integer fcrCropType) {
//        this.fcrCropType = fcrCropType;
//    }
//
//    public Integer getFcrIrrigationType() {
//        return fcrIrrigationType;
//    }
//
//    public void setFcrIrrigationType(Integer fcrIrrigationType) {
//        this.fcrIrrigationType = fcrIrrigationType;
//    }
//
//    public Integer getFcrIrrigationSourceType() {
//        return fcrIrrigationSourceType;
//    }
//
//    public void setFcrIrrigationSourceType(Integer fcrIrrigationSourceType) {
//        this.fcrIrrigationSourceType = fcrIrrigationSourceType;
//    }
//
//    public String getFcrAreaSownInHectares() {
//        return fcrAreaSownInHectares;
//    }
//
//    public void setFcrAreaSownInHectares(String fcrAreaSownInHectares) {
//        this.fcrAreaSownInHectares = fcrAreaSownInHectares;
//    }
//
//    public Calendar getFcrLastUPDDateTime() {
//        return fcrLastUPDDateTime;
//    }
//
//    public void setFcrLastUPDDateTime(Calendar fcrLastUPDDateTime) {
//        this.fcrLastUPDDateTime = fcrLastUPDDateTime;
//    }
//
//    public Calendar getFcrSynchronisationDate() {
//        return fcrSynchronisationDate;
//    }
//
//    public void setFcrSynchronisationDate(Calendar fcrSynchronisationDate) {
//        this.fcrSynchronisationDate = fcrSynchronisationDate;
//    }
//
//    public FarmlandPlotRegistry getFarmlandPlotIdKey() {
//        return farmlandPlotIdKey;
//    }
//
//    public void setFarmlandPlotIdKey(FarmlandPlotRegistry farmlandPlotIdKey) {
//        this.farmlandPlotIdKey = farmlandPlotIdKey;
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
//
//
//}
