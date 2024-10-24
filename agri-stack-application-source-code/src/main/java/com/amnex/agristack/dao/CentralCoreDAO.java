package com.amnex.agristack.dao;

import lombok.Data;

import java.util.List;

@Data
public class CentralCoreDAO {
	private Integer page;

	private Integer limit;

	private String sortField;
	private String sortOrder;
	private String year;
	private Long villageLgdCode;
	Integer startingYear;
	Integer endingYear;
	private String startDate;
	private String endDate;
	private String season;
	private String referenceId;
	private String recordIdStart;
	private String recordIdEnd;
	private String status;

}
