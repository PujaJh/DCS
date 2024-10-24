package com.amnex.agristack.Enum;

public enum NotificationSourceType {
	
	WEB(0),
    MOBILE(1);
    
    private Integer value;

    public Integer getValue() {
        return this.value;
    }
    
    NotificationSourceType(int value) {
    	this.value = value;
    }
	
}
