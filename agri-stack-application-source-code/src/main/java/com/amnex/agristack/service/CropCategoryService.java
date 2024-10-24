package com.amnex.agristack.service;

import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.dao.SeasonMasterOutputDAO;
import com.amnex.agristack.dao.CropCategoryOutputDAO;
import com.amnex.agristack.dao.CropCategoryInputDAO;
import com.amnex.agristack.entity.CropCategoryMaster;
import com.amnex.agristack.repository.CropCategoryRepository;
import com.amnex.agristack.utils.CommonUtil;
import com.amnex.agristack.utils.CustomMessages;
import com.amnex.agristack.utils.ResponseMessages;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Crop Category operations
 */
@Service
public class CropCategoryService {

	@Autowired
	CropCategoryRepository CropCategoryRepository;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	/**
	 * Get the list of Crop Category details
	 *
	 * @param request the HTTP servlet request
	 * @return ResponseEntity containing the list of Crop Category details
	 */
	public ResponseEntity<?> getCropCategoryDetailList(HttpServletRequest request) {
		try {
			List<CropCategoryMaster> categoryList = CropCategoryRepository.findByIsDeletedFalseOrderByCropCategoryIdAsc();
			categoryList.forEach(action->{
				action.setCreatedIp(null);
				action.setModifiedIp(null);
			});
			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.MENU_ADD_SUCCESSFULLY, categoryList), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Get the list of active Crop Categories
	 *
	 * @param request the HTTP servlet request
	 * @return ResponseEntity containing the list of active Crop Categories
	 */
	public ResponseEntity<?> getCropCategoryList(HttpServletRequest request) {
		try {
			List<CropCategoryOutputDAO> categoryOutputList = new ArrayList<>();
			List<CropCategoryMaster> categoryList = CropCategoryRepository.findByIsActiveTrueAndIsDeletedFalseOrderByCropCategoryNameAsc();
			categoryList.forEach(ele -> {
				categoryOutputList.add(new CropCategoryOutputDAO(ele.getCropCategoryId(),ele.getCropCategoryName()));
			});
			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.MENU_ADD_SUCCESSFULLY, categoryOutputList), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Add a new Crop Category
	 *
	 * @param cropCategoryDAO the input data for the Crop Category
	 * @param request         the HTTP servlet request
	 * @return ResponseEntity containing the success message and the added Crop Category
	 */
	public ResponseEntity<?> addCropCategory(CropCategoryInputDAO cropCategoryDAO, HttpServletRequest request) {
		try {
			CropCategoryMaster cropCategory = new CropCategoryMaster();
			BeanUtils.copyProperties(cropCategoryDAO, cropCategory);
			cropCategory.setIsActive(true);
			cropCategory.setIsDeleted(false);
			cropCategory.setCreatedOn(new Timestamp(new Date().getTime()));
			cropCategory.setCreatedIp(CommonUtil.getRequestIp(request));
			cropCategory.setCreatedBy(CustomMessages.getUserId(request,jwtTokenUtil));
			cropCategory.setModifiedOn(new Timestamp(new Date().getTime()));
			cropCategory.setModifiedIp(CommonUtil.getRequestIp(request));
			cropCategory.setModifiedBy(CustomMessages.getUserId(request,jwtTokenUtil));
			cropCategory = CropCategoryRepository.save(cropCategory);

			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.ADD_SUCCESSFULLY, cropCategory), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * Delete a Crop Category
	 *
	 * @param CropCategoryId the ID of the Crop Category to delete
	 * @param request        the HTTP servlet request
	 * @return ResponseEntity containing the success message and the deleted Crop Category
	 */
	public ResponseEntity<?> deleteCropCategory(Long CropCategoryId, HttpServletRequest request) {
		try {
			Optional<CropCategoryMaster> category = CropCategoryRepository.findByCropCategoryId(CropCategoryId);
			if (category.isPresent()){
				CropCategoryMaster cropCategory = category.get();
				cropCategory.setIsActive(false);
				cropCategory.setIsDeleted(true);
				cropCategory.setModifiedOn(new Timestamp(new Date().getTime()));
				cropCategory.setModifiedIp(CommonUtil.getRequestIp(request));
				cropCategory.setModifiedBy(CustomMessages.getUserId(request,jwtTokenUtil));
				cropCategory = CropCategoryRepository.save(cropCategory);

				return new ResponseEntity<String>(
						CustomMessages.getMessageWithData(CustomMessages.DELETE_SUCCESSFULLY, cropCategory), HttpStatus.OK);
			} else {
				return new ResponseEntity<String>(
						CustomMessages.getMessageWithData(CustomMessages.NO_RECORD_FOUND,CropCategoryId), HttpStatus.OK);
			}

		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Update the status of a Crop Category
	 *
	 * @param cropCategoryDAO the updated Crop Category data
	 * @param request         the HTTP servlet request
	 * @return ResponseEntity containing the success message and the updated Crop Category
	 */
	public ResponseEntity<?> updateCropCategoryStatus(CropCategoryInputDAO cropCategoryDAO, HttpServletRequest request) {
		try {
			Optional<CropCategoryMaster> category = CropCategoryRepository.findByCropCategoryId(cropCategoryDAO.getCropCategoryId());
			if (category.isPresent()){
				CropCategoryMaster cropCategory = category.get();
				cropCategory.setIsActive(cropCategoryDAO.getIsActive());
				cropCategory.setModifiedOn(new Timestamp(new Date().getTime()));
				cropCategory.setModifiedIp(CommonUtil.getRequestIp(request));
				cropCategory.setModifiedBy(CustomMessages.getUserId(request,jwtTokenUtil));
				cropCategory = CropCategoryRepository.save(cropCategory);

				return new ResponseEntity<String>(
						CustomMessages.getMessageWithData(CustomMessages.STATUS_UPDATED_SUCCESSFULLY, cropCategory),
						HttpStatus.OK);
			} else {
				return new ResponseEntity<String>(
						CustomMessages.getMessageWithData(CustomMessages.NO_RECORD_FOUND, cropCategoryDAO.getCropCategoryId()), HttpStatus.OK);
			}

		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Update a Crop Category
	 *
	 * @param cropCategoryDAO the updated Crop Category data
	 * @param request         the HTTP servlet request
	 * @return ResponseEntity containing the success message and the updated Crop Category
	 */
	public ResponseEntity<?> updateCropCategory(CropCategoryInputDAO cropCategoryDAO, HttpServletRequest request) {
		try {
			Optional<CropCategoryMaster> category = CropCategoryRepository.findByCropCategoryId(cropCategoryDAO.getCropCategoryId());
			if (category.isPresent()){
				CropCategoryMaster cropCategory = category.get();
				BeanUtils.copyProperties(cropCategoryDAO, cropCategory, "createdOn", "createdBy","createdIp","isDeleted", "isActive");
				cropCategory.setModifiedOn(new Timestamp(new Date().getTime()));
				cropCategory.setModifiedIp(CommonUtil.getRequestIp(request));
				cropCategory.setModifiedBy(CustomMessages.getUserId(request,jwtTokenUtil));
				cropCategory = CropCategoryRepository.save(cropCategory);

				return new ResponseEntity<String>(
						CustomMessages.getMessageWithData(CustomMessages.CROP_CATEGORY_UPDATE_SUCCESSFULLY, cropCategory), HttpStatus.OK);
			} else {
				return new ResponseEntity<String>(
						CustomMessages.getMessageWithData(CustomMessages.NO_RECORD_FOUND, cropCategoryDAO.getCropCategoryId()), HttpStatus.OK);
			}

		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Get a Crop Category by ID
	 *
	 * @param CropCategoryId the ID of the Crop Category
	 * @param request        the HTTP servlet request
	 * @return ResponseEntity containing the success message and the retrieved Crop Category
	 */
    public ResponseEntity<?> getCropCategory(Long CropCategoryId, HttpServletRequest request) {
		try {
			CropCategoryOutputDAO cropCategoryOutput = new CropCategoryOutputDAO();
			Optional<CropCategoryMaster> category = CropCategoryRepository.findByCropCategoryId(CropCategoryId);
			if (category.isPresent()){
				CropCategoryMaster cropCategory = category.get();
				BeanUtils.copyProperties(cropCategory, cropCategoryOutput);
				return new ResponseEntity<String>(
						CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, cropCategoryOutput), HttpStatus.OK);
			}else {
				return new ResponseEntity<String>(
						CustomMessages.getMessageWithData(CustomMessages.NO_RECORD_FOUND, CropCategoryId), HttpStatus.OK);
			}

		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }
}
