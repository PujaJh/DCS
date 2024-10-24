package com.amnex.agristack.dao;

import lombok.Data;

import java.util.List;


/**
 * Crop Input DAO
 * @author krupali.jogi
 * 
 * */

@Data
public class CropMasterInputDAO {
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
	private String cropSeasonId;
	private String cropCategoryId;
	private Boolean isActive;
	private String cropNameLocal;
	private String cropNameHi;
	private Boolean isDefault;
	private Long stateLgdCode;
	private String hsnCode;
	private String scientificName;
	private String cropStatus;
	private Boolean isAdmin;
	private List<Long> cropIds;
	private String rejectionReason;
	private List<Long> stateLgdCodeList;
	private String vernacularLanguage;
	private String cropTypes;
	private Long cropClassId;
	private String cropClassName;
}
