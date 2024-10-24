package com.amnex.agristack.controller;

import java.util.regex.Pattern;

import com.amnex.agristack.dao.LoginPageConfigurationDto;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.service.LoginPageConfigurationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.utils.CustomMessages;

/**
 * @author janmaijaysingh.bisen
 *
 */
@RestController
@RequestMapping("/generalConfiguration")
public class LoginPageConfigurationController {

	private LoginPageConfigurationService loginPageConfigurationService;

	public LoginPageConfigurationController(LoginPageConfigurationService loginPageConfigurationService) {
		this.loginPageConfigurationService = loginPageConfigurationService;

	}
/**
 * add or update login page configuration 
 * @param loginPageConfigurationDto input dao of LoginPageConfigurationDao
 * @return  The ResponseModel object representing the response.
 */
	@PostMapping("/loginPageConfiguration")
	public ResponseModel updateLoginPageConfiguration(LoginPageConfigurationDto loginPageConfigurationDto) {

		try {
			Boolean isValidPayload = validatePayload(loginPageConfigurationDto);
			if (Boolean.TRUE.equals(isValidPayload)){
			return loginPageConfigurationService.saveOrUpdateLoginPageConfiguration(loginPageConfigurationDto);
			}else{
				return CustomMessages.makeResponseModel("Invalid Data", CustomMessages.FAILURE,
						CustomMessages.INTERNAL_SERVER_ERROR, "Invalid Data");
			}

		} catch (Exception e) {
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}

		// return null;
	}
	
	private Boolean validatePayload(LoginPageConfigurationDto loginPageConfigurationDto) {
		Pattern REGEX = Pattern.compile("^[^ ][\\p{L}\\p{M}\\p{Zs}]{1,30}$");
		Boolean isValidTitle = Boolean.TRUE;
		Boolean isValidDesc = Boolean.TRUE;
		if ((loginPageConfigurationDto.getLandingPageTitleContent()!=null && REGEX.matcher(loginPageConfigurationDto.getLandingPageTitleContent()).matches()) ||
				loginPageConfigurationDto.getLandingPageTitleContent()==null || loginPageConfigurationDto.getLandingPageTitleContent().isEmpty()){
			isValidTitle = Boolean.TRUE;
		} else {
			isValidTitle = Boolean.FALSE;
		}
		
		if ((loginPageConfigurationDto.getLandingPageDescContent()!=null && REGEX.matcher(loginPageConfigurationDto.getLandingPageDescContent()).matches()) ||
				loginPageConfigurationDto.getLandingPageDescContent()==null || loginPageConfigurationDto.getLandingPageDescContent().isEmpty()){
			isValidDesc = Boolean.TRUE;
		} else {
			isValidDesc = Boolean.FALSE;
		}
		
		if(isValidTitle.equals(Boolean.TRUE) && isValidDesc.equals(Boolean.TRUE)) {
			return Boolean.TRUE;
		}else {
			return Boolean.FALSE;
		}
	}
/**
 * fech login page configuration by state and project type
 * @param stateLgdCode state lgd code to get login page configuration
 * @param landPageFor userId to fetch the login page configuration
 * @return  The ResponseModel object representing the response.
 */
	@GetMapping("/getLoginPageConfiguration")
	public ResponseModel getLoginPageConfiguration(@RequestParam("stateLgdCode") Long stateLgdCode,@RequestParam("landingPageFor") Integer landPageFor) {
		try {
			return loginPageConfigurationService.getLoginPageConfiguration(stateLgdCode,landPageFor);

		} catch (Exception e) {
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}
}
