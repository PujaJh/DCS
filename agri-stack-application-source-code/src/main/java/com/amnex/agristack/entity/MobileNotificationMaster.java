package com.amnex.agristack.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.amnex.agristack.Enum.NotificationSourceType;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing mobile notification details.
 * Each instance corresponds to a specific mobile notification with its unique identifier,
 * title, message, serial number, and source type.
 */

@Entity
@Data
@NoArgsConstructor
public class MobileNotificationMaster extends BaseEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "mobile_notification_id")
	private Long mobileNotificationId;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "message")
	private String message;
	
	private Integer serialNo;
	
	private NotificationSourceType sourceType;
}
