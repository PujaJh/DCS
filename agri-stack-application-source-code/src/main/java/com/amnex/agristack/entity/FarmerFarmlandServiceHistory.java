package com.amnex.agristack.entity;

import java.util.Calendar;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.EqualsAndHashCode;

/**
 * @author majid.belim
 *
 * Table containing farmer farmland service history details	
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
public class FarmerFarmlandServiceHistory extends BaseEntity{


	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(columnDefinition = "VARCHAR(15)")
	private String farmerFarmlandServiceHistoryId;

	@ManyToOne
	@JoinColumn(name = "farmer_id")
	private FarmerRegistry farmerId;
	
	@Column(columnDefinition = "VARCHAR(12)")
	private String farmlandPlotId;
	

	@ManyToOne
	@JoinColumn(name = "service_provider_id",columnDefinition = "VARCHAR(100)")
	private ServiceProviderRegistry serviceProviderId;
	
	@Column(columnDefinition="TIMESTAMP")
	private Calendar dateOfServiceAvailment;
	
	@Column(columnDefinition="TIMESTAMP")
	private Calendar typeOfServiceAvailment;
	
	@Column(columnDefinition = "Numeric")
	private Integer amountPaidByFarmer;

	public FarmerRegistry getFarmerId() {
		return farmerId;
	}

	public String getFarmlandPlotId() {
		return farmlandPlotId;
	}

	public ServiceProviderRegistry getServiceProviderId() {
		return serviceProviderId;
	}

	public Calendar getDateOfServiceAvailment() {
		return dateOfServiceAvailment;
	}

	public Calendar getTypeOfServiceAvailment() {
		return typeOfServiceAvailment;
	}

	public Integer getAmountPaidByFarmer() {
		return amountPaidByFarmer;
	}

	public void setFarmerId(FarmerRegistry farmerId) {
		this.farmerId = farmerId;
	}

	public void setFarmlandPlotId(String farmlandPlotId) {
		this.farmlandPlotId = farmlandPlotId;
	}

	public void setServiceProviderId(ServiceProviderRegistry serviceProviderId) {
		this.serviceProviderId = serviceProviderId;
	}

	public void setDateOfServiceAvailment(Calendar dateOfServiceAvailment) {
		this.dateOfServiceAvailment = dateOfServiceAvailment;
	}

	public void setTypeOfServiceAvailment(Calendar typeOfServiceAvailment) {
		this.typeOfServiceAvailment = typeOfServiceAvailment;
	}

	public void setAmountPaidByFarmer(Integer amountPaidByFarmer) {
		this.amountPaidByFarmer = amountPaidByFarmer;
	}

	public String getFarmerFarmlandServiceHistoryId() {
		return farmerFarmlandServiceHistoryId;
	}

	public void setFarmerFarmlandServiceHistoryId(String farmerFarmlandServiceHistoryId) {
		this.farmerFarmlandServiceHistoryId = farmerFarmlandServiceHistoryId;
	}
	
}
