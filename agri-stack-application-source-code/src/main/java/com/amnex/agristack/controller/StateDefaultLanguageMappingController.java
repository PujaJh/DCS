package com.amnex.agristack.controller;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.UserDefaultLanguageMappingDTO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.service.StateDefaultLanguageMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stateDefaultLanguage")
public class StateDefaultLanguageMappingController {

	@Autowired
	StateDefaultLanguageMappingService stateDefaultLanguageMappingService;

	/**
	 * Add default language by LGD code
	 * @param request The HttpServletRequest Object
	 * @param userDeLanMapDTO The UserDefaultLangMappingDto containing input params
	 * @return  The ResponseModel object representing the response.
	 */
	@PostMapping(value = "/addDefaultLanguage")
	public ResponseModel addDefaultLanguage(HttpServletRequest request,
											@RequestBody UserDefaultLanguageMappingDTO userDeLanMapDTO) {

		return stateDefaultLanguageMappingService.addDefaultLanguage(userDeLanMapDTO,request);

	}

	/**
	 * get default language by state LGD code
	 * @param request The HttpServletRequest Object
	 * @param userDeLanMapDTO The UserDefaultLangMappingDto containing input params
	 * @return  The ResponseModel object representing the response.
	 */
	@PostMapping(value = "/getDefaultLanguage")
	public ResponseModel getDefaultLanguage(HttpServletRequest request,
			@RequestBody UserDefaultLanguageMappingDTO userDeLanMapDTO) {

		return stateDefaultLanguageMappingService.getDefaultLanguageByStateLgdCode(userDeLanMapDTO);

	}

}
