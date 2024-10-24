package com.amnex.agristack.dao;

import java.util.Date;

import lombok.Data;

@Data
public class ReportInputDAO {

	private Long districtId;
	private Long subDistricId;
	private Long villageId;
	private Long userId;
	private Date startDate;
	private Date endDate;
	private int page;
	private int limit;
	private String sortField;
	private String sortOrder;
	private Long offset;
}
