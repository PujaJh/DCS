package com.amnex.agristack.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Calendar;

@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class ServiceProviderDataServiceHistory extends BaseEntity {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "service_provider_history_id", columnDefinition = "VARCHAR(100)")
    private Integer serviceProviderHistoryId;
    @ManyToOne
    @JoinColumn(name = "service_provider_id", columnDefinition="VARCHAR(100)")
    private ServiceProviderRegistry serviceProviderId;
    @ManyToOne
    @JoinColumn(name = "farmer_id")
    private FarmerRegistry farmerId;
    @Column(name = "personal_data_consented", columnDefinition="VARCHAR(2)")
    private String personalDataConsented;
    @Column(name = "personal_data_accessed", columnDefinition="VARCHAR(2)")
    private String personalDataAccessed;
    @Column(name = "service_provided", columnDefinition="VARCHAR(2)")
    private String serviceProvided;
    @Column(name = "feedback_by_farmer", columnDefinition="VARCHAR(3)")
    private String feedbackByFarmer;
    @Column(name = "consent_date_time", columnDefinition="TIMESTAMP")
    private Calendar consentDateTime;
    @Column(name = "data_access_date_time", columnDefinition="TIMESTAMP")
    private Calendar dataAccessDateTime;

    @Column(name = "consent_request_id", columnDefinition="VARCHAR(100)")
    private String consentRequestId;
    @ManyToOne
    @JoinColumn(name = "farmer_registry_id")
    private FarmerRegistry farmerRegistryId;
    
    
    public FarmerRegistry getFarmerRegistryId() {
		return farmerRegistryId;
	}

	public void setFarmerRegistryId(FarmerRegistry farmerRegistryId) {
		this.farmerRegistryId = farmerRegistryId;
	}

	public Integer getServiceProviderHistoryId() {
        return serviceProviderHistoryId;
    }

    public void setServiceProviderHistoryId(Integer serviceProviderHistoryId) {
        this.serviceProviderHistoryId = serviceProviderHistoryId;
    }

    public ServiceProviderRegistry getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(ServiceProviderRegistry serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    public FarmerRegistry getFarmerId() {
        return farmerId;
    }

    public void setFarmerId(FarmerRegistry farmerId) {
        this.farmerId = farmerId;
    }

    public String getPersonalDataConsented() {
        return personalDataConsented;
    }

    public void setPersonalDataConsented(String personalDataConsented) {
        this.personalDataConsented = personalDataConsented;
    }

    public String getPersonalDataAccessed() {
        return personalDataAccessed;
    }

    public void setPersonalDataAccessed(String personalDataAccessed) {
        this.personalDataAccessed = personalDataAccessed;
    }

    public String getServiceProvided() {
        return serviceProvided;
    }

    public void setServiceProvided(String serviceProvided) {
        this.serviceProvided = serviceProvided;
    }

    public String getFeedbackByFarmer() {
        return feedbackByFarmer;
    }

    public void setFeedbackByFarmer(String feedbackByFarmer) {
        this.feedbackByFarmer = feedbackByFarmer;
    }

    public Calendar getConsentDateTime() {
        return consentDateTime;
    }

    public void setConsentDateTime(Calendar consentDateTime) {
        this.consentDateTime = consentDateTime;
    }

    public Calendar getDataAccessDateTime() {
        return dataAccessDateTime;
    }

    public void setDataAccessDateTime(Calendar dataAccessDateTime) {
        this.dataAccessDateTime = dataAccessDateTime;
    }

    public String getConsentRequestId() {
        return consentRequestId;
    }

    public void setConsentRequestId(String consentRequestId) {
        this.consentRequestId = consentRequestId;
    }


}
