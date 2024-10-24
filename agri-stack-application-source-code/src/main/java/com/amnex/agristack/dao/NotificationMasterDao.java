/**
 * 
 */
package com.amnex.agristack.dao;

import java.util.Date;
import java.util.List;

import com.amnex.agristack.Enum.MasterTableName;
import com.amnex.agristack.Enum.NotificationType;

/**
 * @author majid.belim
 *
 */
public class NotificationMasterDao {
	private Long notificationId;
	private Long userId;
	private NotificationType notificationType;
	private String message;
	private Long activityTypeId; // ('crop Survey')
	private Long activityStatusId;// ('survey_completed','survey_rejected')

	private String messageWithOutHTML;
	private Long senderId;
	private Long receiverId;
	private String senderType;
	private String notificationTitle;
	private String thumbnailURL;

	private String ccList;
	private Long masterTableId;
	private MasterTableName masterTableName;

	private List<ReceiverDAO> receiverList;

	public void setMasterTableName(MasterTableName masterTableName) {
		this.masterTableName = masterTableName;
	}

	public List<ReceiverDAO> getReceiverList() {
		return receiverList;
	}

	public void setReceiverList(List<ReceiverDAO> receiverList) {
		this.receiverList = receiverList;
	}

	public String getCcList() {
		return ccList;
	}

	public Long getMasterTableId() {
		return masterTableId;
	}

	public void setCcList(String ccList) {
		this.ccList = ccList;
	}

	public void setMasterTableId(Long masterTableId) {
		this.masterTableId = masterTableId;
	}

	public MasterTableName getMasterTableName() {
		return masterTableName;
	}

	public Long getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(Long receiverId) {
		this.receiverId = receiverId;
	}

	public Long getActivityTypeId() {
		return activityTypeId;
	}

	public Long getActivityStatusId() {
		return activityStatusId;
	}

	public void setActivityTypeId(Long activityTypeId) {
		this.activityTypeId = activityTypeId;
	}

	public void setActivityStatusId(Long activityStatusId) {
		this.activityStatusId = activityStatusId;
	}

	public Long getNotificationId() {
		return notificationId;
	}

	public Long getUserId() {
		return userId;
	}

	public String getMessage() {
		return message;
	}

	public String getMessageWithOutHTML() {
		return messageWithOutHTML;
	}

	public Long getSenderId() {
		return senderId;
	}

	public String getSenderType() {
		return senderType;
	}

	public String getNotificationTitle() {
		return notificationTitle;
	}

	public String getThumbnailURL() {
		return thumbnailURL;
	}

	public void setNotificationId(Long notificationId) {
		this.notificationId = notificationId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setMessageWithOutHTML(String messageWithOutHTML) {
		this.messageWithOutHTML = messageWithOutHTML;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public void setSenderType(String senderType) {
		this.senderType = senderType;
	}

	public void setNotificationTitle(String notificationTitle) {
		this.notificationTitle = notificationTitle;
	}

	public void setThumbnailURL(String thumbnailURL) {
		this.thumbnailURL = thumbnailURL;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

}
