package com.amnex.agristack.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * @author krupali.jogi
 *
 * Table containing crop Variety details
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
public class CropVarietyMaster extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "crop_variety_id")
	private Long cropVarietyId;
	@Column(name = "crop_variety_name", columnDefinition = "VARCHAR(25)")
	private String cropVarietyName;
	@Column(name = "variety_description", columnDefinition = "VARCHAR(250)")
	private String varietyDescription;
	@OneToOne
	@JoinColumn(name ="crop_id", referencedColumnName = "crop_id")
	private CropRegistry cropMaster;
}
