package com.amnex.agristack.dao;

import java.util.List;

import lombok.Data;


/**
 * Crop Variety Input DAO
 * @author krupali.jogi
 * 
 * */

@Data
public class CropVarietyInputDAO {
	private Long cropVarietyId;
	private String cropVarietyName;
	private String varietyDescription;
	private Boolean isActive;
	private Long cropId;
	private List<Long> cropIdList;

}
