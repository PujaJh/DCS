package com.amnex.agristack.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.*;

import javax.persistence.*;

/**
 * Entity class representing crop type details.
 * Each instance corresponds to a specific crop type with its unique identifier and name.
 * The 'cropTypeLocal' field represents the localized version of the crop type name.
 */

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class CropTypeMaster extends BaseEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cropTypeId;

	@Column
	private String cropType;

	@Column
	private String cropTypeLocal;

	public CropTypeMaster(Long cropTypeId, String cropType) {
		this.cropTypeId = cropTypeId;
		this.cropType = cropType;
	}
}
