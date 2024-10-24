package com.amnex.agristack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

/**
 * Entity class representing irrigation details.
 * Each instance corresponds to a specific irrigation type with its unique identifier and type.
 */

@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Entity
@Data
@NoArgsConstructor
public class IrrigationMaster extends  BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private long irrigationId;

	private String irrigationType;

	public IrrigationMaster(long irrigationId, String irrigationType) {
		this.irrigationId = irrigationId;
		this.irrigationType = irrigationType;
	}
}
