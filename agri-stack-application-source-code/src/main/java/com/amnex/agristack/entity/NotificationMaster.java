package com.amnex.agristack.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.amnex.agristack.Enum.MasterTableName;
import com.amnex.agristack.Enum.MenuTypeEnum;
import com.amnex.agristack.Enum.NotificationType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author majid.belim
 * 
 * Entity class representing notification details.
 * Each instance corresponds to a specific notification with its unique identifier,
 * title, message, notification type, activity type ID, activity status ID,
 * send status, read status, sender ID, receiver ID, CC list, master table ID,
 * master table name, and reminder escalation date.
 */
@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
public class NotificationMaster extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long notificationId;

	private String notificationTitle;
	private String message;

	@Enumerated(EnumType.STRING)
	private NotificationType notificationType;
	private Long activityTypeId; // ('crop Survey')
	private Long activityStatusId;// ('survey_completed','survey_rejected')

	private Boolean isSend = false;
	private Boolean isRead = false;
	private Long senderId;
	private Long receiverId;
	@Column(columnDefinition = "TEXT")
	private String ccList;

	private Long masterTableId;
	@Enumerated(EnumType.STRING)
	private MasterTableName masterTableName;

	private Date reminderEscalationDate;

	public Long getNotificationId() {
		return notificationId;
	}

	public String getNotificationTitle() {
		return notificationTitle;
	}

	public String getMessage() {
		return message;
	}

	public NotificationType getNotificationType() {
		return notificationType;
	}

	public Boolean getIsSend() {
		return isSend;
	}

	public Boolean getIsRead() {
		return isRead;
	}

	public Long getSenderId() {
		return senderId;
	}

	public Long getReceiverId() {
		return receiverId;
	}

	public String getCcList() {
		return ccList;
	}

	public Long getMasterTableId() {
		return masterTableId;
	}

	public Date getReminderEscalationDate() {
		return reminderEscalationDate;
	}

	public void setNotificationId(Long notificationId) {
		this.notificationId = notificationId;
	}

	public void setNotificationTitle(String notificationTitle) {
		this.notificationTitle = notificationTitle;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setNotificationType(NotificationType notificationType) {
		this.notificationType = notificationType;
	}

	public void setIsSend(Boolean isSend) {
		this.isSend = isSend;
	}

	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}

	public void setSenderId(Long senderId) {
		this.senderId = senderId;
	}

	public void setReceiverId(Long receiverId) {
		this.receiverId = receiverId;
	}

	public void setCcList(String ccList) {
		this.ccList = ccList;
	}

	public void setMasterTableId(Long masterTableId) {
		this.masterTableId = masterTableId;
	}

	public void setReminderEscalationDate(Date reminderEscalationDate) {
		this.reminderEscalationDate = reminderEscalationDate;
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

	public MasterTableName getMasterTableName() {
		return masterTableName;
	}

	public void setMasterTableName(MasterTableName masterTableName) {
		this.masterTableName = masterTableName;
	}

}
