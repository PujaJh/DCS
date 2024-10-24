package com.amnex.agristack.ror.dao;

import java.util.List;

import lombok.Data;

@Data
public class ODRoRDataMainDAO {

	String villageCode;
	String villageLgdCode;
	String landCodeId;
	String surveyNo;
	String subSurveyNo;
	String plotGeometry;
	String projectionCode;
	String plotArea;
	String plotAreaUnit;
	String districtName;
	String subDistrictName;
	List<ODRoRDataOwnersDAO> owners;
	String kissamStatus;
}
