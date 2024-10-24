package com.amnex.agristack.ror.dao;

import java.util.List;

import com.google.gson.JsonObject;

import lombok.Data;

@Data
public class AssamRoRDataMainDAO {

	String surveyNo;
	String plotArea;
	String plotAreaUnit;
	List<AssamRoRDataOwnersDAO> owners;
	JsonObject plot_geometry;
 	
	String geometry;
	String projection_code;
 
}
