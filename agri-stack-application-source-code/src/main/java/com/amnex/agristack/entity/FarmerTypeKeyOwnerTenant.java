package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.EqualsAndHashCode;

/**
 * @author majid.belim
 *
 * Table containing farmer type owner tenant details	
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
public class FarmerTypeKeyOwnerTenant extends BaseEntity{
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
    @Column(name = "id", nullable = false, columnDefinition="Integer")
    private String id;
	
	
	@Column(columnDefinition = "VARCHAR(1)")
	private String ownerTenantTypeCode;
	
	@Column(columnDefinition = "VARCHAR(100)")
	private String ownerTenantTypeDescription;

	public String getOwnerTenantTypeCode() {
		return ownerTenantTypeCode;
	}

	public String getOwnerTenantTypeDescription() {
		return ownerTenantTypeDescription;
	}

	public void setOwnerTenantTypeCode(String ownerTenantTypeCode) {
		this.ownerTenantTypeCode = ownerTenantTypeCode;
	}

	public void setOwnerTenantTypeDescription(String ownerTenantTypeDescription) {
		this.ownerTenantTypeDescription = ownerTenantTypeDescription;
	}
	
	
}
