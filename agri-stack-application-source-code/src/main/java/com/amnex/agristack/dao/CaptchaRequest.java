package com.amnex.agristack.dao;

import lombok.Data;

@Data
public class CaptchaRequest {
	private String captcha = "";
	private String hiddenCaptcha = "";
	private String realCaptcha = "";
	private Boolean checkCaptcha = false;
}
