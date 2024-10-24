package com.amnex.agristack.dao;


import lombok.Data;

@Data
public class FarmerRegistryCustomFieldDto {
	
	private String customFieldType;
	//private Map<String,Object> customFieldValueMap;
	private String customFieldJsonValue;
	
	

}
