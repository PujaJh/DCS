package com.amnex.agristack.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class UserDeviceMappingDAO {

	private Long userDeviceMappingId;

	private Long userId;

	private String userName;

	private String imeiNumber;

	private String userMobileNumber;

	private String userEmailAddress;

	private Integer statusCode;

	private String statusName;

	private String stateName;

	private String districtName;

	private String subDistrictName;

	private String villageName;

	private String remarks;

	private Boolean isActive;

	private List<Integer> villageLgdCodeList;

	private Integer page;

	private Integer limit;

	private String sortField;

	private String sortOrder;

	private String search;

	private Timestamp createdOn;

	private Long createdBy;
	
	private Date startDate;
	
	private Date endDate;

	public UserDeviceMappingDAO(Long userDeviceMappingId, Long userId, String userName, String imeiNumber,
			String userMobileNumber, String userEmailAddress, Integer statusCode, String statusName, String stateName,
			String districtName, String subDistrictName, List<Integer> villageLgdCodeList, String villageName,
			String remarks, Boolean isActive, Integer page, Integer limit, String sortField, String sortOrder,
			String search, Timestamp createdOn, Long createdBy) {
		super();
		this.userDeviceMappingId = userDeviceMappingId;
		this.userId = userId;
		this.userName = userName;
		this.imeiNumber = imeiNumber;
		this.userMobileNumber = userMobileNumber;
		this.userEmailAddress = userEmailAddress;
		this.statusCode = statusCode;
		this.statusName = statusName;
		this.stateName = stateName;
		this.districtName = districtName;
		this.subDistrictName = subDistrictName;
		this.villageLgdCodeList = villageLgdCodeList;
		this.villageName = villageName;
		this.remarks = remarks;
		this.isActive = isActive;
		this.page = page;
		this.limit = limit;
		this.sortField = sortField;
		this.sortOrder = sortOrder;
		this.search = search;
		this.createdOn = createdOn;
		this.createdBy = createdBy;
	}
}
