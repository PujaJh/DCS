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
 * Table containing farmer ownership status details	
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
public class FarmerOwnershipStatus extends BaseEntity{

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(columnDefinition = "VARCHAR(15)")
	private String farmerOwnershipStatusId;
	@Column(columnDefinition = "VARCHAR(1)")
	private String statusCode;
	
	@Column(columnDefinition = "VARCHAR(100)")
	private String statusDescription;

	public String getStatusCode() {
		return statusCode;
	}

	public String getStatusDescription() {
		return statusDescription;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
	}
	
	
}
