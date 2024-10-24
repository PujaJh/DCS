package com.amnex.agristack.dao;

import java.util.List;

import lombok.Data;

@Data
public class PaginationDao {
	private Long userId;

	private Integer page;

	private Integer limit;

	private String sortField;
	private String sortOrder;
	private String search;

	private Long seasonId;
	private List<Long> stateLgdCodeList;
	private List<Long> subDistrictLgdCodeList;
	private String year;
	private Long villageLgdCode;
	private String boundaryType;

	private List<Long> userList;
	private List<String> landIds;

	private Long roleId;
	private String roleName;
	private Integer id;

	Integer startingYear;
	Integer endingYear;

	private String startDate;
	private String endDate;
	private String season;
	private String referenceId;
	private List<Long> villageLgdCodeList;

	private Long userType;
}
