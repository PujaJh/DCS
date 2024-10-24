package com.amnex.agristack.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Type;

/**
 * @author majid.belim
 *
 * Table containing crop registry details
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
public class CropRegistry extends BaseEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "crop_id")
	private Long cropId;

	@Column(name = "crop_unique_id")
	private String cropUniqueId;

	@Column
	@Type(type = "text")
	private String botanicalNameOfCrop;

	@Column(name = "crop_name",columnDefinition = "VARCHAR(100)")
	private String cropName;
	
	@Column(columnDefinition = "VARCHAR(250)")
	private String developedByInstitute;
	
	@Column(columnDefinition = "VARCHAR(4)")
	private String yearOfRelease;
	
	@Column
	private Double seedRateInRupeesPerKilo;
	
	
	@Column(columnDefinition = "Numeric")
	private Integer durationToMaturityInMonths;
	
	@Column(columnDefinition = "VARCHAR")
	private String varietalCharacters;
	
	@Column
	private Double averageYieldKiloPerHectare;
	
	@OneToOne
	@JoinColumn(name = "crop_season_id", referencedColumnName = "season_id")
	private SowingSeason cropSeasonId;

	@OneToOne
	@JoinColumn(name = "crop_category_id", referencedColumnName = "crop_category_id")
	private CropCategoryMaster cropCategoryId;
	
	@OneToOne
	@JoinColumn(name = "crop_class_id", referencedColumnName = "crop_class_id")	
	private CropClassMaster cropClassId;

	@Column(name = "is_countable")
	private Boolean isCountable;

	@Column(name = "is_default")
	private Boolean isDefault;

	@Column(name = "hsn_code")
	@Type(type = "text")
	private String hsnCode;

	@Column(name = "crop_status")
	private Integer cropStatus;

	@Column
	@Type(type = "text")
	private String scientificName;

	@Column
	@Type(type = "text")
	private String rejectionReason;

	@Column
	@Type(type = "text")
	private String cropTypes;

	@Column(name = "state_lgd_code")
	private Long stateLgdCode;
	
	@Column(name = "crop_name_hi", columnDefinition = "VARCHAR(255)")
	private String cropNameHi;
	
	@Transient
	private Integer sequenceNumber;
}
