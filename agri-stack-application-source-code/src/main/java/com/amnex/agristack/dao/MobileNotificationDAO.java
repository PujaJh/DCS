package com.amnex.agristack.dao;

import lombok.Data;

@Data
public class MobileNotificationDAO {
	
	private String title;
	
	private String message;
	
	private Integer serialNo;
	
	public MobileNotificationDAO(String title, String message, Integer serialNo) {
        this.title = title;
        this.message = message;
        this.serialNo = serialNo;
    }

}
