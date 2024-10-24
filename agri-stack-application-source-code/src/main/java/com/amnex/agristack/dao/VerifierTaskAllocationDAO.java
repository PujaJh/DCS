package com.amnex.agristack.dao;

import java.util.List;

import lombok.Data;

@Data
public class VerifierTaskAllocationDAO {

	private Long seasonId;
	private List<Long> subDistrictLgdCodeList;
	private List<Long> villageLdgCodeList;
	private String year;
	private Long villageLgdCode;
	private String boundaryType;

	private List<Long> userList;
	private List<String> landIds;

	private Integer startingYear;
	private Integer endingYear;

	private Long verifierId;
	private Long stateLgdCode;
	private Long userId;
	
	private Integer page;

	private Integer limit;

	private String sortField;
	private String sortOrder;
	private String search;
	private Long userType;

}
