package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.amnex.agristack.Enum.MessageType;
import com.amnex.agristack.Enum.NotificationType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author kinnari.soni
 *
 * Entity class representing message configuration details.
 * Each instance corresponds to a specific message configuration with its unique identifier, 
 * email subject, receiver list, CC list, template, template type, template ID, 
 * notification type, and message type.
 */

@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
public class MessageConfigurationMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "email_subject", columnDefinition = "TEXT")
	private String emailSubject;

	@Column(name = "receiver_list")
	private String receiverList;

	@Column(name = "cc_list")
	private String ccList;

	@Column(name = "template", columnDefinition = "TEXT")
	private String template;

	@Column(name = "template_type")
	private String templateType;

	@Column(name = "template_id")
	private String templateId;

	@Enumerated(EnumType.STRING)
	private NotificationType notificationType;
	@Enumerated(EnumType.STRING)
	private MessageType messageType;

}
