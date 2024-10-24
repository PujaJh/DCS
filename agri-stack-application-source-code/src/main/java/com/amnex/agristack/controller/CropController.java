package com.amnex.agristack.controller;

import com.amnex.agristack.dao.CropMasterInputDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.service.CropService;
import com.amnex.agristack.utils.CustomMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

/***
 * Controller class for managing Crops.
 * Handles HTTP requests related to Crops.
 *
 * Author: krupali.jogi
 **/
@RestController
@RequestMapping("/admin/crop")
public class CropController {

	@Autowired
	CropService cropService;

	/**
	 * Retrieves the Crop detail list.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @param cropInputDao the CropMasterInputDAO object containing the filters for retrieving the Crop detail list
	 * @return a ResponseEntity containing the Crop detail list
	 */
	@PostMapping(value = "/getCropDetailList")
	public ResponseEntity<?> getCropDetailList(HttpServletRequest request, @RequestBody CropMasterInputDAO cropInputDao) {
		try {
			return cropService.getCropDetailList(request, cropInputDao);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the Crop list.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseEntity containing the Crop list
	 */
	@GetMapping(value = "/getCropList")
	public ResponseEntity<?> getCropList(HttpServletRequest request) {
		try {
			return cropService.getCropList(request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the Crop list for mobile.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseEntity containing the Crop list for mobile
	 */
	@GetMapping(value = "/mobile/getCropList")
	public ResponseEntity<?> getCropMasterList(HttpServletRequest request) {
		try {
			return cropService.getCropListMobile(request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Adds a new Crop.
	 *
	 * @param cropMasterInputDAO the CropMasterInputDAO object containing the data for the new Crop
	 * @param request            the HttpServletRequest object representing the incoming request
	 * @return a ResponseEntity containing the result of the Crop addition
	 */
	@PostMapping(value = "/addCrop")
	public ResponseModel addCrop(@RequestBody CropMasterInputDAO cropMasterInputDAO, HttpServletRequest request) {
		try {
			Boolean isValidPayload = validateAddCropPayload(cropMasterInputDAO);
			if (Boolean.TRUE.equals(isValidPayload)){
				return cropService.addCrop(cropMasterInputDAO, request);
			}else{
				return CustomMessages.makeResponseModel("Invalid Data", CustomMessages.FAILURE,
						CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private Boolean validateAddCropPayload(CropMasterInputDAO cropMasterInputDAO) {
		Pattern REGEX = Pattern.compile("^[^ ][\\p{L}\\p{M}\\p{Zs}]{1,30}$");
		Boolean isValidCropTypes = true;
		if (cropMasterInputDAO.getCropTypes()!=null && !REGEX.matcher(cropMasterInputDAO.getCropTypes()).matches()){
			isValidCropTypes = false;
		}


		if (REGEX.matcher(cropMasterInputDAO.getCropName()).matches() && REGEX.matcher(cropMasterInputDAO.getDevelopedByInstitute()).matches() &&
				REGEX.matcher(cropMasterInputDAO.getVarietalCharacters()).matches() && REGEX.matcher(cropMasterInputDAO.getScientificName()).matches()
				&& isValidCropTypes){
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	/**
	 * Updates an existing Crop.
	 *
	 * @param cropMasterInputDAO the CropMasterInputDAO object containing the updated data for the Crop
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseEntity containing the result of the Crop update
	 */
	@PostMapping(value = "/updateCrop")
	public ResponseEntity<?> updateCrop(@RequestBody CropMasterInputDAO cropMasterInputDAO, HttpServletRequest request) {
		try {
			return cropService.updateCrop(cropMasterInputDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Updates the status of a Crop.
	 *
	 * @param cropMasterInputDAO the CropMasterInputDAO object containing the data for updating the Crop status
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseEntity containing the result of the Crop status update
	 */
	@PostMapping(value = "/updateCropStatus")
	public ResponseEntity<?> updateCropStatus(@RequestBody CropMasterInputDAO cropMasterInputDAO, HttpServletRequest request) {
		try {
			return cropService.updateCropStatus(cropMasterInputDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Deletes a Crop.
	 *
	 * @param cropId the ID of the Crop to be deleted
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseEntity containing the result of the Crop deletion
	 */
	@DeleteMapping(value = "/deleteCrop/{cropId}")
	public ResponseEntity<?> deleteCrop(@PathVariable("cropId") Long cropId, HttpServletRequest request) {
		try {
			return cropService.deleteCrop(cropId, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the detail of a Crop.
	 *
	 * @param cropId the ID of the Crop to retrieve
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseEntity containing the Crop detail
	 */
	@GetMapping(value = "/getCropById/{cropId}")
	public ResponseEntity<?> getCropById(@PathVariable("cropId") Long cropId, HttpServletRequest request) {
		try {
			return cropService.getCropById(cropId, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Adds a unique ID to a Crop.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseEntity containing the result of adding a unique ID to the Crop
	 */
	@GetMapping(value = "/addCropUniqueId")
	public ResponseEntity<?> addCropUniqueId(HttpServletRequest request) {
		try {
			return cropService.addCropUniqueId(request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves all Crops.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseEntity containing all the Crops
	 */
	@GetMapping(value = "/getAllCrops")
	public ResponseEntity<?> getAllCrops(HttpServletRequest request) {
		try {
			return cropService.getAllCrops(request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves all Crop Details.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseEntity containing all the Crop Details
	 */
	@GetMapping(value = "/getAllCropDetails")
	public ResponseEntity<?> getAllCropDetails(HttpServletRequest request) {
		try {
			return cropService.getAllCropDetails(request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
