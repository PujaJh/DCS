package com.amnex.agristack.controller;

import com.amnex.agristack.dao.CropCategoryInputDAO;
import com.amnex.agristack.service.CropCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/***
 * Controller class for managing Crop Categories.
 * Handles HTTP requests related to Crop Categories.
 *
 * Author: krupali.jogi
 **/
@RestController
@RequestMapping("/admin/cropCategory")
public class CropCategoryController {

	@Autowired
	CropCategoryService cropCategoryService;

	/**
	 * Retrieves the Crop Category detail list.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseEntity containing the Crop Category detail list
	 */
	@GetMapping(value = "/getCropCategoryDetailList")
	public ResponseEntity<?> getCropCategoryDetailList(HttpServletRequest request) {
		try {
			return cropCategoryService.getCropCategoryDetailList(request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the Crop Category list.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseEntity containing the Crop Category list
	 */
	@GetMapping(value = "/getCropCategoryList")
	public ResponseEntity<?> getCropCategoryList(HttpServletRequest request) {
		try {
			return cropCategoryService.getCropCategoryList(request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the Crop Category list for mobile.
	 *
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseEntity containing the Crop Category list for mobile
	 */
	@GetMapping(value = "/mobile/getCropCategoryList")
	public ResponseEntity<?> getCategoryList(HttpServletRequest request) {
		try {
			return cropCategoryService.getCropCategoryList(request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Adds a new Crop Category.
	 *
	 * @param cropCategoryInputDAO the CropCategoryInputDAO object containing the data for the new Crop Category
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseEntity containing the result of the Crop Category addition
	 */
	@PostMapping(value = "/addCropCategory")
	public ResponseEntity<?> addCropCategory(@RequestBody CropCategoryInputDAO cropCategoryInputDAO, HttpServletRequest request) {
		try {
			return cropCategoryService.addCropCategory(cropCategoryInputDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Updates an existing Crop Category.
	 *
	 * @param cropCategoryInputDAO the CropCategoryInputDAO object containing the updated data for the Crop Category
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseEntity containing the result of the Crop Category update
	 */
	@PostMapping(value = "/updateCropCategory")
	public ResponseEntity<?> updateCropCategory(@RequestBody CropCategoryInputDAO cropCategoryInputDAO, HttpServletRequest request) {
		try {
			return cropCategoryService.updateCropCategory(cropCategoryInputDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Updates the status of a Crop Category.
	 *
	 * @param cropCategoryInputDAO the CropCategoryInputDAO object containing the data for updating the Crop Category status
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseEntity containing the result of the Crop Category status update
	 */
	@PostMapping(value = "/updateCropCategoryStatus")
	public ResponseEntity<?> updateCropCategoryStatus(@RequestBody CropCategoryInputDAO cropCategoryInputDAO, HttpServletRequest request) {
		try {
			return cropCategoryService.updateCropCategoryStatus(cropCategoryInputDAO, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Deletes a Crop Category.
	 *
	 * @param categoryId the ID of the Crop Category to be deleted
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseEntity containing the result of the Crop Category deletion
	 */
	@DeleteMapping(value = "/deleteCropCategory/{categoryId}")
	public ResponseEntity<?> deleteCropCategory(@PathVariable("categoryId") Long categoryId, HttpServletRequest request) {
		try {
			return cropCategoryService.deleteCropCategory(categoryId, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Retrieves the detail of a Crop Category.
	 *
	 * @param categoryId the ID of the Crop Category to retrieve
	 * @param request the HttpServletRequest object representing the incoming request
	 * @return a ResponseEntity containing the Crop Category detail
	 */
	@GetMapping(value = "/getCropCategoryById/{categoryId}")
	public ResponseEntity<?> getCropCategoryById(@PathVariable("categoryId") Long categoryId, HttpServletRequest request) {
		try {
			return cropCategoryService.getCropCategory(categoryId, request);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
