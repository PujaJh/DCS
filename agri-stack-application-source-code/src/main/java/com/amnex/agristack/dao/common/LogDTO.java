package com.amnex.agristack.dao.common;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class LogDTO {

	private String userId;
	private String userName;
	private String domainName;
	private String clientName;
	private String module;
	private String page;
	private String operation;
	private String type;
	private LocalDateTime datetime;
	private String attributes;
	private String errorStacktrace;
	private String errorMessage;

	private String ipAddress;
	private String browser;
	private String userAgent;
	private String os;

	@Override
	public String toString() {
		return "LogDTO [userId=" + userId + ", userName=" + userName + ", domainName=" + domainName + ", clientName="
				+ clientName + ", module=" + module + ", page=" + page + ", operation=" + operation + ", type=" + type
				+ ", datetime=" + LocalDateTime.now() + ", attributes=" + attributes + ", errorStacktrace="
				+ errorStacktrace + ", errorMessage=" + errorMessage + ", ipAddress=" + ipAddress + ", browser="
				+ browser + ", userAgent=" + userAgent + ", os=" + os + "]";
	}
}
