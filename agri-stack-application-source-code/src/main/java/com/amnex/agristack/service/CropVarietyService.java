package com.amnex.agristack.service;

import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.dao.*;
import com.amnex.agristack.entity.CropRegistry;
import com.amnex.agristack.entity.CropVarietyMaster;
import com.amnex.agristack.repository.CropCategoryRepository;
import com.amnex.agristack.repository.CropMasterRepository;
import com.amnex.agristack.repository.CropVarietyRepository;
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
import java.util.*;

/**
 * @author krupali.jogi
 */
@Service
public class CropVarietyService {

	@Autowired
	CropVarietyRepository cropVarietyRepository;

	@Autowired
	CropMasterRepository cropRepository;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	/**
	 * Retrieves the list of crop varieties.
	 *
	 * @param request HTTP servlet request
	 * @return ResponseEntity containing the crop variety list
	 */
	public ResponseEntity<?> getCropVarietyDetailList(HttpServletRequest request) {
		try {
			List<CropVarietyMaster> categoryList = cropVarietyRepository.findByIsDeletedFalseOrderByCropVarietyIdAsc();
			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.MENU_ADD_SUCCESSFULLY, categoryList), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Retrieves the list of crop varieties.
	 *
	 * @param request HTTP servlet request
	 * @return ResponseEntity containing the crop variety list
	 */
	public ResponseEntity<?> getCropVarietyList(HttpServletRequest request) {
		try {
			List<CropVarietyOutputDAO> cropVarietyOutputList = new ArrayList<>();
			List<CropVarietyMaster> varietyList = cropVarietyRepository.findByIsDeletedFalseOrderByModifiedOnDesc();
			varietyList.forEach(ele -> {
				cropVarietyOutputList.add(new CropVarietyOutputDAO(ele.getCropVarietyId(),ele.getCropVarietyName(), ele.getCropMaster().getCropId()));
			});
			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, cropVarietyOutputList), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Retrieves the list of active crop varieties.
	 *
	 * @param request HTTP servlet request
	 * @return ResponseEntity containing the active crop variety list
	 */
	public ResponseEntity<?> getCropVarietyMasterList(HttpServletRequest request) {
		try {
			List<CropVarietyOutputDAO> cropVarietyOutputList = new ArrayList<>();
			List<CropVarietyMaster> varietyList = cropVarietyRepository.findByIsActiveTrueAndIsDeletedFalseOrderByCropVarietyNameAsc();
			varietyList.forEach(ele -> {
				cropVarietyOutputList.add(new CropVarietyOutputDAO(ele.getCropVarietyId(),ele.getCropVarietyName(), ele.getCropMaster().getCropId()));
			});
			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, cropVarietyOutputList), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Adds a crop variety.
	 *
	 * @param cropVarietyInputDAO The input data for the crop variety.
	 * @param request             The HTTP request.
	 * @return ResponseEntity containing the result of the operation.
	 */
	public ResponseEntity<?> addCropVariety(CropVarietyInputDAO cropVarietyInputDAO, HttpServletRequest request) {
		try {
			CropVarietyMaster cropVariety= new CropVarietyMaster();
			BeanUtils.copyProperties(cropVarietyInputDAO, cropVariety);
			/* set crop master */
			CropRegistry crop = cropRepository.findByCropId(cropVarietyInputDAO.getCropId());
			cropVariety.setCropMaster(Objects.nonNull(crop) ? crop : null);
			/* set crop master */
			cropVariety.setIsActive(true);
			cropVariety.setIsDeleted(false);
			cropVariety.setCreatedOn(new Timestamp(new Date().getTime()));
			cropVariety.setCreatedIp(CommonUtil.getRequestIp(request));
			cropVariety.setCreatedBy(CustomMessages.getUserId(request,jwtTokenUtil));
			cropVariety.setModifiedOn(new Timestamp(new Date().getTime()));
			cropVariety.setModifiedIp(CommonUtil.getRequestIp(request));
			cropVariety.setModifiedBy(CustomMessages.getUserId(request,jwtTokenUtil));
			cropVariety= cropVarietyRepository.save(cropVariety);

			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.ADD_SUCCESSFULLY, cropVariety), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	/**
	 * Delete a crop variety.
	 *
	 * @param cropVarietyId The ID of the crop variety to delete.
	 * @param request       The HTTP request.
	 * @return ResponseEntity containing the result of the operation.
	 */
	public ResponseEntity<?> deleteCropVariety(Long cropVarietyId, HttpServletRequest request) {
		try {
			Optional<CropVarietyMaster> variety= cropVarietyRepository.findByCropVarietyId(cropVarietyId);
			if (variety.isPresent()){
				CropVarietyMaster cropVariety = variety.get();
				cropVariety.setIsActive(false);
				cropVariety.setIsDeleted(true);
				cropVariety.setModifiedOn(new Timestamp(new Date().getTime()));
				cropVariety.setModifiedIp(CommonUtil.getRequestIp(request));
				cropVariety.setModifiedBy(CustomMessages.getUserId(request,jwtTokenUtil));
				cropVariety = cropVarietyRepository.save(cropVariety);
				return new ResponseEntity<String>(
						CustomMessages.getMessageWithData(CustomMessages.DELETE_SUCCESSFULLY, cropVariety), HttpStatus.OK);
			} else {
				return new ResponseEntity<String>(
						CustomMessages.getMessageWithData(CustomMessages.NO_RECORD_FOUND, cropVarietyId), HttpStatus.OK);
			}



		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Updates the status of a crop variety.
	 *
	 * @param cropVarietyInputDAO The input data for the crop variety.
	 * @param request             The HTTP request.
	 * @return ResponseEntity containing the result of the operation.
	 */
	public ResponseEntity<?> updateCropVarietyStatus(CropVarietyInputDAO cropVarietyInputDAO, HttpServletRequest request) {
		try {
			Optional<CropVarietyMaster> variety= cropVarietyRepository.findByCropVarietyId(cropVarietyInputDAO.getCropVarietyId());
			if (variety.isPresent()){
			CropVarietyMaster cropVariety = variety.get();
			cropVariety.setIsActive(cropVarietyInputDAO.getIsActive());
			cropVariety.setModifiedOn(new Timestamp(new Date().getTime()));
			cropVariety.setModifiedIp(CommonUtil.getRequestIp(request));
			cropVariety.setModifiedBy(CustomMessages.getUserId(request,jwtTokenUtil));
			cropVariety = cropVarietyRepository.save(cropVariety);

			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.STATUS_UPDATED_SUCCESSFULLY, cropVariety),
					HttpStatus.OK);
			} else {
				return new ResponseEntity<String>(
						CustomMessages.getMessageWithData(CustomMessages.NO_RECORD_FOUND, cropVarietyInputDAO.getCropVarietyId()), HttpStatus.OK);
			}
		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Updates a crop variety.
	 *
	 * @param cropVarietyInputDAO The input data for the crop variety.
	 * @param request             The HTTP request.
	 * @return ResponseEntity containing the result of the operation.
	 */
	public ResponseEntity<?> updateCropVariety(CropVarietyInputDAO cropVarietyInputDAO, HttpServletRequest request) {
		try {
			Optional<CropVarietyMaster> variety= cropVarietyRepository.findByCropVarietyId(cropVarietyInputDAO.getCropVarietyId());
			if (variety.isPresent()){
				CropVarietyMaster cropVariety = variety.get();
				BeanUtils.copyProperties(cropVarietyInputDAO, cropVariety, "createdOn", "createdBy","createdIp","isDeleted", "isActive");
				/* set crop master */
				CropRegistry crop = cropRepository.findByCropId(cropVarietyInputDAO.getCropId());
				cropVariety.setCropMaster(Objects.nonNull(crop) ? crop : null);
				/* set crop master */
				cropVariety.setModifiedOn(new Timestamp(new Date().getTime()));
				cropVariety.setModifiedIp(CommonUtil.getRequestIp(request));
				cropVariety.setModifiedBy(CustomMessages.getUserId(request,jwtTokenUtil));
				cropVariety= cropVarietyRepository.save(cropVariety);

				return new ResponseEntity<String>(
						CustomMessages.getMessageWithData(CustomMessages.CROP_VARIETY_UPDATE_SUCCESSFULLY, cropVariety), HttpStatus.OK);
			} else {
				return new ResponseEntity<String>(
						CustomMessages.getMessageWithData(CustomMessages.NO_RECORD_FOUND, cropVarietyInputDAO.getCropVarietyId()), HttpStatus.OK);
			}

		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Retrieves a crop variety by its ID.
	 *
	 * @param varietyId The ID of the crop variety.
	 * @param request   The HTTP request.
	 * @return ResponseEntity containing the crop variety information.
	 */
    public ResponseEntity<?> getCropVariety(Long varietyId, HttpServletRequest request) {
		try {
			Optional<CropVarietyMaster> variety= cropVarietyRepository.findByCropVarietyId(varietyId);
			if (variety.isPresent()){
				CropVarietyMaster cropVariety = variety.get();
				CropVarietyOutputDAO cropVarietyOutput = new CropVarietyOutputDAO(cropVariety.getCropVarietyId(), cropVariety.getCropVarietyName(), new CropMasterOutputDAO(cropVariety.getCropMaster().getCropId(), cropVariety.getCropMaster().getCropName()),cropVariety.getVarietyDescription());
				return new ResponseEntity<String>(
						CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, cropVarietyOutput), HttpStatus.OK);
			} else {
				return new ResponseEntity<String>(
						CustomMessages.getMessageWithData(CustomMessages.NO_RECORD_FOUND, varietyId), HttpStatus.OK);
			}



		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
    }

	/**
	 * Retrieves crop varieties for a specific crop.
	 *
	 * @param cropId  The ID of the crop.
	 * @param request The HTTP request.
	 * @return ResponseEntity containing the crop variety information.
	 */
	public ResponseEntity<?> getCropVarietyByCrop(Long cropId, HttpServletRequest request) {
		try {
			List<CropVarietyOutputDAO> cropVarietyOutputDAOList = new ArrayList<>();
			List<CropVarietyMaster> varietyList= cropVarietyRepository.findByCropMaster_CropIdAndIsActiveTrueAndIsDeletedFalse(cropId);
			if (varietyList.size()>0){
				varietyList.forEach(cropVariety ->{
					cropVarietyOutputDAOList.add(new CropVarietyOutputDAO(cropVariety.getCropVarietyId(), cropVariety.getCropVarietyName()));
				});

				return new ResponseEntity<String>(
						CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, cropVarietyOutputDAOList), HttpStatus.OK);
			} else {
				return new ResponseEntity<String>(
						CustomMessages.getMessageWithData(CustomMessages.NO_RECORD_FOUND, cropId), HttpStatus.OK);
			}



		} catch (Exception e) {

			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Retrieves crop varieties for a list of crops.
	 *
	 * @param cropId  The list of crop IDs.
	 * @param request The HTTP request.
	 * @return ResponseEntity containing the crop variety information.
	 */
	public ResponseEntity<?> getCropVarietyByCropList(List<Long> cropId, HttpServletRequest request) {
		try {
			List<CropVarietyOutputDAO> cropVarietyOutputDAOList = new ArrayList<>();
			List<CropVarietyMaster> varietyList= cropVarietyRepository.findByIsActiveTrueAndIsDeletedFalseAndCropMaster_CropIdIn(cropId);
			if (varietyList.size()>0){
				varietyList.forEach(cropVariety ->{
					cropVarietyOutputDAOList.add(new CropVarietyOutputDAO(cropVariety.getCropVarietyId(), cropVariety.getCropVarietyName()));
				});

				return new ResponseEntity<String>(
						CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, cropVarietyOutputDAOList), HttpStatus.OK);
			} else {
				return new ResponseEntity<String>(
						CustomMessages.getMessageWithData(CustomMessages.NO_RECORD_FOUND, cropId), HttpStatus.OK);
			}
		} catch (Exception e) {

			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
