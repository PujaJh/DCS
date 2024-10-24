package com.amnex.agristack.dao;

import java.sql.Timestamp;

public interface UserMasterModel {
	Long getUserId();

	String getUserName();
	String getUserEmailAddress();
	String getUserMobileNumber();
	Timestamp getCreatedOn();
	
	Boolean getIsActive();
}
