package com.amnex.agristack.dao;

import lombok.Data;

@Data
public class ReportDAO {
	private String originalData;
	private String newData;
	private String loginTimestamp;
	private String logoutTimestamp;
	private String sessionDuration;
	private String moduleName;
	private String userId;
	private String ipAddress;

}
