package com.amnex.agristack.controller;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.dao.CommonRequestDAO;
import com.amnex.agristack.service.CultivatorService;
import com.amnex.agristack.utils.CustomMessages;

/**
 * @author darshankumar.gajjar
 */

@RestController
@RequestMapping("/cultivator")
public class CultivatorController {

	@Autowired
	CultivatorService cultivatorService;

	/**
	 * Retrieves a list of Cultivator types.
	 *
	 * @return the ResponseEntity containing the list of Cultivator types
	 */
	@GetMapping(value = "/getCultivatorTypes")
	public ResponseModel getCultivatorTypes() {
		try {
			return cultivatorService.getCultivatorTypes();
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_GET);
		}
	}

	/**
	 * Retrieves a list of Cultivators by village lgd codes.
	 *
	 * @param dao  input dao of CommonRequest
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return the ResponseEntity containing the list of Cultivators by village lgd codes
	 */
	@PostMapping(value = "/getCultivatorListByVillageCodes")
	public Object getCultivatorListByVillageCodes(HttpServletRequest request,
			@RequestBody CommonRequestDAO dao) {
		try {
			return cultivatorService.getCultivatorListByVillageCodes(request, dao);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
					CustomMessages.METHOD_POST);
		}
	}

}
