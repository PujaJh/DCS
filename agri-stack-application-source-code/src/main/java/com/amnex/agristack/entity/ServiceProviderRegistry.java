package com.amnex.agristack.entity;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class ServiceProviderRegistry extends BaseEntity {
    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "service_provider_id", columnDefinition="VARCHAR(100)")
    private String serviceProviderId;
    @Column(name = "service_provider_registered_name", columnDefinition="VARCHAR(100)")
    private String serviceProviderRegisteredName;
    @Column(name = "service_provider_entity_type", columnDefinition="VARCHAR(2)")
    private String serviceProviderEntityType;
    @Column(name = "service_provided_type", columnDefinition="VARCHAR(10)")
    private String serviceProvidedType;
    @Column(name = "sp_farmer_data_needed", columnDefinition="VARCHAR(3)")
    private String spFarmerDataNeeded;
    @Column(name = "sp_registration_date", columnDefinition="Date")
    private Date spRegistrationDate;
    @Column(name = "sp_validity_expiry_date", columnDefinition="Date")
    private Date spValidityExpiryDate;
    @Column(name = "sp_mca_id", columnDefinition="Integer")
    private Integer spMCAId;
    @Column(name = "sp_states_of_operation", columnDefinition="Integer")
    private Integer spStatesOfOperation;

    @Column(name = "sp_gstn_no", columnDefinition="VARCHAR(12)")
    private String spGstnNO;

    public String getServiceProviderId() {
        return serviceProviderId;
    }

    public void setServiceProviderId(String serviceProviderId) {
        this.serviceProviderId = serviceProviderId;
    }

    public String getServiceProviderRegisteredName() {
        return serviceProviderRegisteredName;
    }

    public void setServiceProviderRegisteredName(String serviceProviderRegisteredName) {
        this.serviceProviderRegisteredName = serviceProviderRegisteredName;
    }

    public String getServiceProviderEntityType() {
        return serviceProviderEntityType;
    }

    public void setServiceProviderEntityType(String serviceProviderEntityType) {
        this.serviceProviderEntityType = serviceProviderEntityType;
    }

    public String getServiceProvidedType() {
        return serviceProvidedType;
    }

    public void setServiceProvidedType(String serviceProvidedType) {
        this.serviceProvidedType = serviceProvidedType;
    }

    public String getSpFarmerDataNeeded() {
        return spFarmerDataNeeded;
    }

    public void setSpFarmerDataNeeded(String spFarmerDataNeeded) {
        this.spFarmerDataNeeded = spFarmerDataNeeded;
    }

    public Date getSpRegistrationDate() {
        return spRegistrationDate;
    }

    public void setSpRegistrationDate(Date spRegistrationDate) {
        this.spRegistrationDate = spRegistrationDate;
    }

    public Date getSpValidityExpiryDate() {
        return spValidityExpiryDate;
    }

    public void setSpValidityExpiryDate(Date spValidityExpiryDate) {
        this.spValidityExpiryDate = spValidityExpiryDate;
    }

    public Integer getSpMCAId() {
        return spMCAId;
    }

    public void setSpMCAId(Integer spMCAId) {
        this.spMCAId = spMCAId;
    }

    public Integer getSpStatesOfOperation() {
        return spStatesOfOperation;
    }

    public void setSpStatesOfOperation(Integer spStatesOfOperation) {
        this.spStatesOfOperation = spStatesOfOperation;
    }

    public String getSpGstnNO() {
        return spGstnNO;
    }

    public void setSpGstnNO(String spGstnNO) {
        this.spGstnNO = spGstnNO;
    }


}
