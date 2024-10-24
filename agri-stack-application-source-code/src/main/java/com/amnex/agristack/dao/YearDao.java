package com.amnex.agristack.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Data
public class YearDao {
	
	private String startYear;
	
	private String endYear;

	private String year;

	public YearDao(String year) {
		this.year = year;
	}
}
