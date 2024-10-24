package com.amnex.agristack.centralcore.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
public class CropSownRequestDao {
	public Long stateLgdCode;
	public Long villageLgdCode;
	public String surveyNumber;
	public String subSurveyNumber;
	public String year;
	public String season;
	public String mapper;
}
