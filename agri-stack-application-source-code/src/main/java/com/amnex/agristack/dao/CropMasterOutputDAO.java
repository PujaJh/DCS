package com.amnex.agristack.dao;

import com.amnex.agristack.entity.CropClassMaster;
import com.amnex.agristack.entity.CropRegistryDetail;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Crop Output DAO
 * @author krupali.jogi
 * 
 * */

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class CropMasterOutputDAO {
	private Long cropId;
	private String cropUniqueID;
	private String botanicalNameOfCrop;
	private String developedByInstitute;
	private String yearOfRelease;
	private Double seedRateInRupeesPerKilo;
	private Integer durationToMaturityInMonths;
	private String varietalCharacters;
	private Double averageYieldKiloPerHectare;
	private String cropName;
	private SeasonMasterOutputDAO cropSeasonId;
	private CropCategoryOutputDAO cropCategoryId;
	private Boolean isActive;
	private Long seasonId;
	private String seasonName;
	private Long categoryId;
	private String categoryName;
	private Boolean isCountable;
	private String cropNameLocal;
	private String cropNameHi;
	private Boolean isDefault;
	private Long stateLgdCode;
	private String stateName;
	private String hsnCode;
	private Integer cropStatus;
	private String cropStatusName;
	private String scientificName;
	private Boolean isAdmin;
	private CropRegistryDetail cropDetail;
	private String vernacularLanguage;
	private String cropTypes;
	private Integer sequenceNumber;
	private  Long cropClassId;
	private String cropClassName;
	
	private CropClassMaster cropClassMaster;
	
	public CropMasterOutputDAO(Long cropId, String cropName, Long seasonId, String seasonName, Long categoryId , String categoryName,Boolean isCountable) {
		this.cropId = cropId;
		this.cropName = cropName;
		this.seasonId = seasonId;
		this.seasonName = seasonName;
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.isCountable = isCountable;
	}

	public CropMasterOutputDAO(Long cropId, String cropName, Long seasonId, String seasonName,
							   Long categoryId , String categoryName,Boolean isCountable, String cropNameLocal,
							   String cropNameHi, String hsnCode,  String vernacularLanguage,String cropTypes) {
		this.cropId = cropId;
		this.cropName = cropName;
		this.seasonId = seasonId;
		this.seasonName = seasonName;
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.isCountable = isCountable;
		this.cropNameLocal =cropNameLocal;
		this.cropNameHi =cropNameHi;
		this.hsnCode =hsnCode;
		this.vernacularLanguage = vernacularLanguage;
		this.cropTypes = cropTypes;
	}

	public CropMasterOutputDAO(Long cropId, String cropName) {
		this.cropId = cropId;
		this.cropName = cropName;
	}

	public CropMasterOutputDAO(Long cropId, String cropName, Long seasonId, String seasonName, Long categoryId,
			String categoryName, Boolean isCountable, Long cropClassId, String cropClassName) {
		this.cropId = cropId;
		this.cropName = cropName;
		this.seasonId = seasonId;
		this.seasonName = seasonName;
		this.categoryId = categoryId;
		this.categoryName = categoryName;
		this.isCountable = isCountable;
		this.cropClassId = cropClassId;
		this.cropClassName = cropClassName;
	}
}
