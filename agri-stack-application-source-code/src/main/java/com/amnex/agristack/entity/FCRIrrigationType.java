package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.EqualsAndHashCode;


/**
 * @author majid.belim
 *
 * Table containing fcr irrigation type details	
 */
@Entity
@Table(name = "fcr_irrigation_type")
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
public class FCRIrrigationType extends BaseEntity{
	
	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid")
	@Column(name = "id", nullable = false, columnDefinition="Integer")
	private String id;
	
	@Column(columnDefinition = "VARCHAR(2)")
	private String irrigationTypeCode;
	
	@Column(columnDefinition = "VARCHAR(10)")
	private String irrigationType;

	public String getIrrigationTypeCode() {
		return irrigationTypeCode;
	}

	public String getIrrigationType() {
		return irrigationType;
	}

	public void setIrrigationTypeCode(String irrigationTypeCode) {
		this.irrigationTypeCode = irrigationTypeCode;
	}

	public void setIrrigationType(String irrigationType) {
		this.irrigationType = irrigationType;
	}

	
}
