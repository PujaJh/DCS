package com.amnex.agristack.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * @author majid.belim
 *
 * Table containing crop registry details
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
public class CropRegistryDetail extends BaseEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@OneToOne
	@JoinColumn(name = "crop_id", referencedColumnName = "crop_id")
	private CropRegistry cropId;

	@Column(name = "crop_name_local", columnDefinition = "VARCHAR(255)")
	private String cropNameLocal;

//	@Column(name = "crop_name_hi", columnDefinition = "VARCHAR(255)")
//	private String cropNameHi;

	@Column(name = "vernacular_language", columnDefinition = "VARCHAR(255)")
	private String vernacularLanguage;

	@Column(name = "state_lgd_code")
	private Long stateLgdCode;

}
