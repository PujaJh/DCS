package com.amnex.agristack.ror.dao;

import java.util.List;

import lombok.Data;

@Data
public class GJRoRDataMainDAO {

	String villageLgdCode;
	String surveyNo;
	String subSurveyNo;
	String plotGeometry;
	String projectionCode;
	String plotArea;
	String plotAreaUnit;
	List<GJRoRDataOwnersDAO> owners;

}
