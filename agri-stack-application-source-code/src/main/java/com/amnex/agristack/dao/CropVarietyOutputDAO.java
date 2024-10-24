package com.amnex.agristack.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Crop Variety Output DAO
 * @author krupali.jogi
 * 
 * */

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class CropVarietyOutputDAO {
	private Long cropVarietyId;
	private String cropVarietyName;
	private String varietyDescription;
	private Boolean isActive;
	private Boolean isDeleted;
	private Long cropId;
	private CropMasterOutputDAO cropMaster;

	public CropVarietyOutputDAO(Long id, String name, CropMasterOutputDAO cropMaster, String description) {
		this.cropVarietyId = id;
		this.cropVarietyName = name;
		this.cropMaster = cropMaster;
		this.varietyDescription = description;
	}

	public CropVarietyOutputDAO(Long id, String name) {
		this.cropVarietyId = id;
		this.cropVarietyName = name;
	}

	public CropVarietyOutputDAO(Long id, String name, Long cropId ) {
		this.cropVarietyId = id;
		this.cropVarietyName = name;
		this.cropId =cropId;
	}
}
