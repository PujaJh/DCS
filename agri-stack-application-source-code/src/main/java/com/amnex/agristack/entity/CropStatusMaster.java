package com.amnex.agristack.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;

/**
 * Entity class representing crop status master details.
 * Each instance corresponds to a specific crop status with its unique identifier and type.
 * The 'cropStatusLocal' field represents the localized version of the crop status type.
 */

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class CropStatusMaster extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long cropStatusId;

	@Column
	private String cropStatusType;
	@Column
	private String cropStatusLocal;

	public CropStatusMaster(Long cropStatusId, String cropStatusType) {
		this.cropStatusId = cropStatusId;
		this.cropStatusType = cropStatusType;
	}
}
