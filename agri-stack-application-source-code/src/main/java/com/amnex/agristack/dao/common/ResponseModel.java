package com.amnex.agristack.dao.common;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ResponseModel {
	private String status;
	private int code;
	private String message;
	private String method;
	private Object data;

	private String lastSyncDate;
	private int startYear;
	private int endYear;
	private int seasonId;
	private String territoryLevel;
	private int page;
	private int limit;

	public String getMessage() {
		return message;
	}
	public String getMethod() {
		return method;
	}
	public Object getData() {
		return data;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public String getStatus() {
		return status;
	}
	public int getCode() {
		return code;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public void setCode(int code) {
		this.code = code;
	}
	
	public ResponseModel(Object data, String message, int code, String status, String method){
		this.data = data;
		this.message = message;
		this.code = code;
		this.status = status;
		this.method = method;
	}
	
}
