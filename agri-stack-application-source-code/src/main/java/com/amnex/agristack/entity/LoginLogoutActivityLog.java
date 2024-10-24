package com.amnex.agristack.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@Table(schema = "public")
@EqualsAndHashCode(callSuper = true)
public class LoginLogoutActivityLog extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long userId;
	private Date logInDate;
	private Date logoutDate;

	@Column(name = "auth_token", columnDefinition = "TEXT")
	private String authToken;
	private boolean isLogin;

	private Long sessionDuration;

	private String userName;

}
