//package com.amnex.agristack.entity;
//import com.fasterxml.jackson.annotation.JsonInclude;
//import lombok.EqualsAndHashCode;
//import org.hibernate.annotations.GenericGenerator;
//
//import java.util.Set;
//
//import javax.persistence.*;
//
//@Entity
//@EqualsAndHashCode(callSuper = false)
//@JsonInclude(value = JsonInclude.Include.NON_NULL)
//public class FarmlandPlotLandParcelIdMapping extends BaseEntity {
//    @Id
//	@GeneratedValue(generator = "system-uuid")
//	@GenericGenerator(name = "system-uuid", strategy = "uuid")
//    @Column(name = "fplp_mapping", columnDefinition = "VARCHAR(15)")
//    private String fplpMapping;
//
//    @OneToOne
//    @JoinColumn(name = "farm_land_parcel_plot_id",referencedColumnName = "farm_land_parcel_plot_id", columnDefinition="VARCHAR(15)")
//    private FarmlandPlotRegistry farmLandParcelPlotId;
//    @Column(name = "farmland_location", columnDefinition="VARCHAR(2)")
//    private String farmlandLocation;
//    @Column(name = "farm_state_lgd_code", columnDefinition="VARCHAR(100)")
//    private Integer farmStateLGDCode;
//    @Column(name = "farm_district_lgd_code", columnDefinition="Integer")
//    private Integer farmDistrictLGDCode;
//    @Column(name = "farm_subdist_lgd_code", columnDefinition="Integer")
//    private Integer farmSubdistLGDCode;
//    @Column(name = "farm_village_lgd_code", columnDefinition="Integer")
//    private Integer farmVillageLGDCode;
//    @Column(name = "farm_ulb_code", columnDefinition="Integer")
//    private Integer farmULBCode;
//    @Column(name = "farm_ward_code", columnDefinition="Integer")
//    private Integer FarmWardCode;
//    @Column(name = "farm_unique_id", columnDefinition="VARCHAR(20)")
//    private String farmUniqueId;
//
//	@OneToOne
//	@JoinTable(name = "farmland_plot_land_parcel_registry_mapping")
//	private FarmlandPlotRegistry farmlandPlotRegistryId;
//
//	public String getFplpMapping() {
//		return fplpMapping;
//	}
//
//	public FarmlandPlotRegistry getFarmLandParcelPlotId() {
//		return farmLandParcelPlotId;
//	}
//
//	public String getFarmlandLocation() {
//		return farmlandLocation;
//	}
//
//	public Integer getFarmStateLGDCode() {
//		return farmStateLGDCode;
//	}
//
//	public Integer getFarmDistrictLGDCode() {
//		return farmDistrictLGDCode;
//	}
//
//	public Integer getFarmSubdistLGDCode() {
//		return farmSubdistLGDCode;
//	}
//
//	public Integer getFarmVillageLGDCode() {
//		return farmVillageLGDCode;
//	}
//
//	public Integer getFarmULBCode() {
//		return farmULBCode;
//	}
//
//	public Integer getFarmWardCode() {
//		return FarmWardCode;
//	}
//
//	public String getFarmUniqueId() {
//		return farmUniqueId;
//	}
//
//	public FarmlandPlotRegistry getFarmlandPlotRegistryId() {
//		return farmlandPlotRegistryId;
//	}
//
//	public void setFplpMapping(String fplpMapping) {
//		this.fplpMapping = fplpMapping;
//	}
//
//	public void setFarmLandParcelPlotId(FarmlandPlotRegistry farmLandParcelPlotId) {
//		this.farmLandParcelPlotId = farmLandParcelPlotId;
//	}
//
//	public void setFarmlandLocation(String farmlandLocation) {
//		this.farmlandLocation = farmlandLocation;
//	}
//
//	public void setFarmStateLGDCode(Integer farmStateLGDCode) {
//		this.farmStateLGDCode = farmStateLGDCode;
//	}
//
//	public void setFarmDistrictLGDCode(Integer farmDistrictLGDCode) {
//		this.farmDistrictLGDCode = farmDistrictLGDCode;
//	}
//
//	public void setFarmSubdistLGDCode(Integer farmSubdistLGDCode) {
//		this.farmSubdistLGDCode = farmSubdistLGDCode;
//	}
//
//	public void setFarmVillageLGDCode(Integer farmVillageLGDCode) {
//		this.farmVillageLGDCode = farmVillageLGDCode;
//	}
//
//	public void setFarmULBCode(Integer farmULBCode) {
//		this.farmULBCode = farmULBCode;
//	}
//
//	public void setFarmWardCode(Integer farmWardCode) {
//		FarmWardCode = farmWardCode;
//	}
//
//	public void setFarmUniqueId(String farmUniqueId) {
//		this.farmUniqueId = farmUniqueId;
//	}
//
//	public void setFarmlandPlotRegistryId(FarmlandPlotRegistry farmlandPlotRegistryId) {
//		this.farmlandPlotRegistryId = farmlandPlotRegistryId;
//	}
//
//
//
//
//}
