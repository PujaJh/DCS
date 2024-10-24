/**
 * 
 */
package com.amnex.agristack.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.dao.PRPaymentReleaseDAO;
import com.amnex.agristack.service.PRPaymentReleaseService;

/**
 * @author majid.belim
 *
 */

@RestController
@RequestMapping("/prPaymentRelease")
public class PRPaymentReleaseController {

	@Autowired
	private PRPaymentReleaseService prPaymentReleaseService;

	/**
	 * Endpoint for adding or updating a payment.
	 * 
	 * @param request              The HttpServletRequest object.
	 * @param prPaymentReleaseDAO  The {@code PRPaymentReleaseDAO} object containing the payment details.
	 * @return                     The ResponseModel object representing the response.
	 */
	@PostMapping(value = "/addOrUpdatePayment")
	public ResponseModel addOrUpdatePayment(HttpServletRequest request,
											@RequestBody PRPaymentReleaseDAO prPaymentReleaseDAO) {

		return prPaymentReleaseService.addOrUpdatePayment(request, prPaymentReleaseDAO);

	}
	
	/**
	 * Endpoint for updating the payment status.
	 * 
	 * @param request              The HttpServletRequest object.
	 * @param prPaymentReleaseDAO  The {@code PRPaymentReleaseDAO} object containing the payment details.
	 * @return                     The ResponseModel object representing the response.
	 */
	@PutMapping(value = "/updatePaymentStatus")
	public ResponseModel updatePaymentStatus(HttpServletRequest request,
			@RequestBody PRPaymentReleaseDAO prPaymentReleaseDAO) {

		return prPaymentReleaseService.updatePaymentStatus(request, prPaymentReleaseDAO);

	}

}
