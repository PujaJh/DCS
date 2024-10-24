package com.amnex.agristack.dao;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class LoginResponseDAO {
	private Long userId;
	private String userName;
	private String verificationSource;
	private String token;
	private Boolean isVerified;
	private String deviceToken;
	private String  currentLanguage;
	
}
