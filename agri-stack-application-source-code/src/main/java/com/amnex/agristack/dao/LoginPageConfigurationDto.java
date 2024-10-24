package com.amnex.agristack.dao;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class LoginPageConfigurationDto {

	

	private Long id;

	private  MultipartFile logoImage;
	
	private MultipartFile backgroundImage;
	
	private String landingPageTitleContent;
	
	private String landingPageDescContent;
	
	private Long stateLgdCode;
	
	private Integer landingPageFor;
	
	private byte[] logoImageInByte;
	private byte[] backgroundImageInByte;

}
