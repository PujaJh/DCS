package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.amnex.agristack.Enum.VerificationType;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author krupali.jogi class OPT Registration
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(name = "otp_registration")
public class OTPRegistration extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long otpId;

	@Column
	private String verificationSource;

	@Column
	private String otp;

	@Column
	@Enumerated(EnumType.STRING)
	private VerificationType verificationType;

	private Integer otpResendCount;

}
