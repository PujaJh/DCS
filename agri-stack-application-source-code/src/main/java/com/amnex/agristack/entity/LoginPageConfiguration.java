package com.amnex.agristack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;


import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class LoginPageConfiguration extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String logoImagePath;
	
	private String backgroundImagePath;
	
	private String landingPageTitleContent;
	
	@Column(columnDefinition = "TEXT")
	private String landingPageDescContent;
	
	private Long stateLgdCode;
	
	private Integer landingPageFor;

	@Transient
	private byte[] logoImageInByte;
	@Transient
	private byte[] backgroundImageInByte;
}
