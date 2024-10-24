//package com.amnex.agristack.entity;
//
//import com.fasterxml.jackson.annotation.JsonInclude;
//import lombok.EqualsAndHashCode;
//import org.hibernate.annotations.GenericGenerator;
//
//import javax.persistence.*;
//
//import java.util.Calendar;
//
//@Entity
//@EqualsAndHashCode(callSuper = false)
//@JsonInclude(value = JsonInclude.Include.NON_NULL)
//public class FarmlandSoilRegistry extends BaseEntity {
//
//    @Id
//	@GeneratedValue(generator = "system-uuid")
//	@GenericGenerator(name = "system-uuid", strategy = "uuid")
//    @Column(name = "farmland_soil_registry_id", columnDefinition = "VARCHAR(15)")
//    private String farmlandSoilRegistryId;
//
//	@Column(name = "farmland_plot_id", columnDefinition="VARCHAR(1000)")
//    private String farmlandPlotId;
//    @Column(name = "lgd_state_code", columnDefinition="Integer")
//    private Integer lgdStateCode;
//    @Column(name = "grid_id", columnDefinition="Integer")
//    private Integer grid_Id;
//    @Column(name = "soil_health_measurement_date", columnDefinition="TIMESTAMP")
//    private Calendar soilHealthMeasurementDate;
//    @Column(name = "parameter_id", columnDefinition="Integer")
//    private Integer parameterId;
//    @Column(name = "parameter_value", columnDefinition="numeric(8,5)")
//    private Double parameterValue;
//    @Column(name = "fct_synchronisation_date", columnDefinition="TIMESTAMP")
//    private Calendar fctSynchronisationDate;
//    @Column(name = "fct_last_upd_date_time", columnDefinition="TIMESTAMP")
//    private Calendar fctLastUpdDateTime;
//
//    @ManyToOne
//    @JoinColumn(name = "farmland_plot_id_key", columnDefinition="VARCHAR(10000)")
//    private FarmlandPlotRegistry farmlandPlotIdKey;
//
//	public String getFarmlandSoilRegistryId() {
//		return farmlandSoilRegistryId;
//	}
//
//	public String getFarmlandPlotId() {
//		return farmlandPlotId;
//	}
//
//	public Integer getLgdStateCode() {
//		return lgdStateCode;
//	}
//
//	public Integer getGrid_Id() {
//		return grid_Id;
//	}
//
//	public Calendar getSoilHealthMeasurementDate() {
//		return soilHealthMeasurementDate;
//	}
//
//	public Integer getParameterId() {
//		return parameterId;
//	}
//
//	public Double getParameterValue() {
//		return parameterValue;
//	}
//
//	public Calendar getFctSynchronisationDate() {
//		return fctSynchronisationDate;
//	}
//
//	public Calendar getFctLastUpdDateTime() {
//		return fctLastUpdDateTime;
//	}
//
//	public FarmlandPlotRegistry getFarmlandPlotIdKey() {
//		return farmlandPlotIdKey;
//	}
//
//	public void setFarmlandSoilRegistryId(String farmlandSoilRegistryId) {
//		this.farmlandSoilRegistryId = farmlandSoilRegistryId;
//	}
//
//	public void setFarmlandPlotId(String farmlandPlotId) {
//		this.farmlandPlotId = farmlandPlotId;
//	}
//
//	public void setLgdStateCode(Integer lgdStateCode) {
//		this.lgdStateCode = lgdStateCode;
//	}
//
//	public void setGrid_Id(Integer grid_Id) {
//		this.grid_Id = grid_Id;
//	}
//
//	public void setSoilHealthMeasurementDate(Calendar soilHealthMeasurementDate) {
//		this.soilHealthMeasurementDate = soilHealthMeasurementDate;
//	}
//
//	public void setParameterId(Integer parameterId) {
//		this.parameterId = parameterId;
//	}
//
//	public void setParameterValue(Double parameterValue) {
//		this.parameterValue = parameterValue;
//	}
//
//	public void setFctSynchronisationDate(Calendar fctSynchronisationDate) {
//		this.fctSynchronisationDate = fctSynchronisationDate;
//	}
//
//	public void setFctLastUpdDateTime(Calendar fctLastUpdDateTime) {
//		this.fctLastUpdDateTime = fctLastUpdDateTime;
//	}
//
//	public void setFarmlandPlotIdKey(FarmlandPlotRegistry farmlandPlotIdKey) {
//		this.farmlandPlotIdKey = farmlandPlotIdKey;
//	}
//
//
//}
