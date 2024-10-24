package com.amnex.agristack.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.*;

import javax.persistence.*;

/**
 * Entity class representing details about irrigation sources.
 * Each instance corresponds to a specific irrigation source with its type and localized type.
 */

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class IrrigationSource extends BaseEntity {



	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Integer irrigationId;

	@Column
	private String irrigationType;
	@Column
	private String irrigationTypeLocal;

	public IrrigationSource(Integer irrigationID, String irrigationType) {
		this.irrigationId = irrigationID;
		this.irrigationType = irrigationType;
	}

}
