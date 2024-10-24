package com.amnex.agristack.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Crop Category Output DAO
 * @author krupali.jogi
 * 
 * */

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class CropCategoryOutputDAO {
	private Long cropCategoryId;
	private String cropCategoryName;
	private String description;
	private Boolean isActive;
	private Boolean isDeleted;

	public CropCategoryOutputDAO(Long id, String name) {
		this.cropCategoryId = id;
		this.cropCategoryName = name;
	}
}
