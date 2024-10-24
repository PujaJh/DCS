package com.amnex.agristack.dao;

public class PushNotificationRequest {
	private String title;
	private String message;
	private String token;
	private String topic;
	private String deviceType;
	private String click_action;
	private String thumbnailURL;
	
	public String getTitle() {
		return title;
	}
	public String getMessage() {
		return message;
	}
	public String getToken() {
		return token;
	}
	public String getTopic() {
		return topic;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public String getClick_action() {
		return click_action;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public void setClick_action(String click_action) {
		this.click_action = click_action;
	}
	public String getThumbnailURL() {
		return thumbnailURL;
	}
	public void setThumbnailURL(String thumbnailURL) {
		this.thumbnailURL = thumbnailURL;
	}
	
	
}
