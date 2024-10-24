package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author kinnari.soni
 *
 *         21 Feb 2023 4:16:07 pm
 */

@Entity
@EqualsAndHashCode(callSuper = false)
@JsonInclude(value = Include.NON_NULL)
@Data
public class MessageCredentialMaster {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "host")
	private String host;

	@Column(name = "port")
	private Integer port;

	@Column(name = "user_name")
	private String userName;

	@Column(name = "password")
	private String password;

	@Column(name = "route")
	private String route;

	@Column(name = "unicode")
	private String uniCode;

	@Column(name = "message_credential_type")
	private String messageCredentialType;

	@Column(name = "auth")
	private Boolean auth;

	@Column(name = "start_tLS")
	private Boolean startTLS;
}
