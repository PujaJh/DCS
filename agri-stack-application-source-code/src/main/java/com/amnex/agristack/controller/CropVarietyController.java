package com.amnex.agristack.controller;

import com.amnex.agristack.dao.CropVarietyInputDAO;
import com.amnex.agristack.service.CropVarietyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Controller class for managing Crop Variety operations.
 * Handles HTTP requests related to Crop Varieties.
 */
@RestController
@RequestMapping("/admin/cropVariety")
public class CropVarietyController {

	@Autowired
	CropVarietyService cropVarietyService;

	/**
	 * Retrieves the list of Crop Variety details.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return the ResponseEntity containing the Crop Variety detail list
	 */
	@GetMapping(value = "/getCropVarietyDetailList")
	public ResponseEntity<?> getCropVarietyDetailList(HttpServletRequest request) {
		try {
			return cropVarietyService.getCropVarietyDetailList(request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the list of Crop Varieties.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return the ResponseEntity containing the Crop Variety list
	 */
	@GetMapping(value = "/getCropVarietyList")
	public ResponseEntity<?> getCropVarietyList(HttpServletRequest request) {
		try {
			return cropVarietyService.getCropVarietyList(request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the list of Crop Varieties by Crop List.
	 *
	 * @param cropVarietyInputDAO the CropVarietyInputDAO object containing the crop ID list
	 * @param request             the HttpServletRequest object representing the incoming request
	 * @return the ResponseEntity containing the Crop Variety list for the specified crops
	 */
	@PostMapping(value = "/getCropVarietyListByCropList")
	public ResponseEntity<?> getCropVarietyListByCropList(@RequestBody CropVarietyInputDAO cropVarietyInputDAO, HttpServletRequest request) {
		try {
			return cropVarietyService.getCropVarietyByCropList(cropVarietyInputDAO.getCropIdList(), request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the list of Crop Varieties for mobile.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return the ResponseEntity containing the Crop Variety list for mobile
	 */
	@GetMapping(value = "/mobile/getCropVarietyList")
	public ResponseEntity<?> getCropVarietyMasterList(HttpServletRequest request) {
		try {
			return cropVarietyService.getCropVarietyMasterList(request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Adds a new Crop Variety.
	 *
	 * @param cropVarietyInputDAO the CropVarietyInputDAO object containing the Crop Variety details
	 * @param request             the HttpServletRequest object representing the incoming request
	 * @return the ResponseEntity indicating the result of the Crop Variety addition
	 */
	@PostMapping(value = "/addCropVariety")
	public ResponseEntity<?> addCropVariety(@RequestBody CropVarietyInputDAO cropVarietyInputDAO, HttpServletRequest request) {
		try {
			return cropVarietyService.addCropVariety(cropVarietyInputDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Updates an existing Crop Variety.
	 *
	 * @param cropVarietyInputDAO the CropVarietyInputDAO object containing the updated Crop Variety details
	 * @param request             the HttpServletRequest object representing the incoming request
	 * @return the ResponseEntity indicating the result of the Crop Variety update
	 */
	@PostMapping(value = "/updateCropVariety")
	public ResponseEntity<?> updateCropVariety(@RequestBody CropVarietyInputDAO cropVarietyInputDAO, HttpServletRequest request) {
		try {
			return cropVarietyService.updateCropVariety(cropVarietyInputDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Updates the status of a Crop Variety.
	 *
	 * @param cropVarietyInputDAO the CropVarietyInputDAO object containing the Crop Variety ID and status
	 * @param request             the HttpServletRequest object representing the incoming request
	 * @return the ResponseEntity indicating the result of the Crop Variety status update
	 */
	@PostMapping(value = "/updateCropVarietyStatus")
	public ResponseEntity<?> updateCropVarietyStatus(@RequestBody CropVarietyInputDAO cropVarietyInputDAO, HttpServletRequest request) {
		try {
			return cropVarietyService.updateCropVarietyStatus(cropVarietyInputDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Deletes a Crop Variety.
	 *
	 * @param varietyId the ID of the Crop Variety to be deleted
	 * @param request   the HttpServletRequest object representing the incoming request
	 * @return the ResponseEntity indicating the result of the Crop Variety deletion
	 */
	@DeleteMapping(value = "/deleteCropVariety/{varietyId}")
	public ResponseEntity<?> deleteCropVariety(@PathVariable("varietyId") Long varietyId, HttpServletRequest request) {
		try {
			return cropVarietyService.deleteCropVariety(varietyId, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves a Crop Variety by ID.
	 *
	 * @param categoryId the ID of the Crop Variety to be retrieved
	 * @param request    the HttpServletRequest object representing the incoming request
	 * @return the ResponseEntity containing the Crop Variety
	 */
	@GetMapping(value = "/getCropVarietyById/{varietyId}")
	public ResponseEntity<?> getCropVarietyById(@PathVariable("varietyId") Long categoryId, HttpServletRequest request) {
		try {
			return cropVarietyService.getCropVariety(categoryId, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves a list of Crop Varieties by Crop ID.
	 *
	 * @param cropId  the ID of the Crop
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return the ResponseEntity containing the list of Crop Varieties for the specified Crop
	 */
	@GetMapping(value = "/getCropVarietyByCrop/{cropId}")
	public ResponseEntity<?> getCropVarietyByCrop(@PathVariable("cropId") Long cropId, HttpServletRequest request) {
		try {
			return cropVarietyService.getCropVarietyByCrop(cropId, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
