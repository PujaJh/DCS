package com.amnex.agristack.controller;

import com.amnex.agristack.dao.RoleInputDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.kyc.dao.EKycDAO;
import com.amnex.agristack.service.PmKisanService;
import com.amnex.agristack.utils.PmKisanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/***
 * @author darshankumar.gajjar
 **/
@RestController
@RequestMapping("/pmKisan")
public class PMKisanController {

	@Autowired
	PmKisanService ekycService;

	@Autowired
	PmKisanUtil pmKisanUtil;


	/**
	 * Retrieves the PM Kisan data
	 * @param inputDAO The EKycDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping("/getPmKisanData")
	public ResponseModel getPmKisanData(@RequestBody EKycDAO inputDAO, HttpServletRequest request) {
		return ekycService.getPmKisanData(inputDAO,request);
	}

	/**
	 * Decrypt the text
	 * @param inputDAO The EKycDao containing the input params
	 * @param request the HTTP servlet request
	 * @return The ResponseModel object representing the response
	 */
	@PostMapping("/decrypt")
	public String decrypt(@RequestBody EKycDAO inputDAO, HttpServletRequest request) {
		try {
			return pmKisanUtil.decrypt(inputDAO.getText(), inputDAO.getKey());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
