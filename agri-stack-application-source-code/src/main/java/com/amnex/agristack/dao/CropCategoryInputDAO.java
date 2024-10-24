package com.amnex.agristack.dao;

import lombok.Data;


/**
 * Crop Category Input DAO
 * @author krupali.jogi
 * 
 * */

@Data
public class CropCategoryInputDAO {
	private Long cropCategoryId;
	private String cropCategoryName;
	private String description;
	private Boolean isActive;
}
