package com.amnex.agristack.service;

import com.amnex.agristack.Enum.StatusEnum;
import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.dao.CropMasterInputDAO;
import com.amnex.agristack.dao.CropCategoryOutputDAO;
import com.amnex.agristack.dao.CropMasterOutputDAO;
import com.amnex.agristack.dao.SeasonMasterOutputDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.*;
import com.amnex.agristack.repository.*;
import com.amnex.agristack.utils.CommonUtil;
import com.amnex.agristack.utils.CustomMessages;
import com.amnex.agristack.utils.ResponseMessages;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author krupali.jogi
 */
@Service
public class CropService {

	@Autowired
	CropMasterRepository cropMasterRepository;

	@Autowired
	CropCategoryRepository cropCategoryRepository;

	@Autowired
	SeasonMasterRepository seasonMasterRepository;

	@Autowired
	UserMasterRepository userMasterRepository;
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	CropDetailRepository cropDetailRepository;

	@Autowired
	StatusMasterRepository statusRepository;

	@Autowired
	private JwtTokenUtil jwtTokenUtils;

	@Autowired
	private MessageConfigurationService messageConfigurationService;

	@Autowired
	private StateLgdMasterRepository stateLgdMasterRepository;

	@Autowired
	private UserVillageMappingRepository userVillageMappingRepository;

	@Autowired
	private UserService userService;
	@Autowired
	private CropSequenceMappingRepository cropSequenceMappingRepository;
	@Autowired
	private GeneralService generalService;
	
	@Autowired
	private CropClassNameRepository cropClassNameRepository;

	/**
	 * Get the list of crop details.
	 *
	 * @param request     HTTP servlet request.
	 * @param cropInputDao CropMasterInputDAO object containing the input data.
	 * @return ResponseEntity containing the crop detail list.
	 */
	public ResponseEntity<?> getCropDetailList(HttpServletRequest request, CropMasterInputDAO cropInputDao) {
		List<CropMasterOutputDAO> cropList = new ArrayList<>();
		try {
			String userId = CustomMessages.getUserId(request, jwtTokenUtils);
			UserMaster user = userMasterRepository.findByUserId(Long.valueOf(userId)).orElse(null);
			if (Objects.nonNull(user)) {
//				if(!Objects.isNull(cropInputDao.getStateLgdCodeList())) {
				if (!cropInputDao.getIsAdmin() || (cropInputDao.getStateLgdCodeList().size() > 0)) {

					if ((cropInputDao.getStateLgdCodeList().size() == 0)) {
						List<Long> stateLgdCodes = userVillageMappingRepository.getStateCodesById(user.getUserId());
						cropInputDao.setStateLgdCodeList(stateLgdCodes);
					}

					cropInputDao.getStateLgdCodeList().stream().forEach(ele -> {
						List<Long> stateCodes = new ArrayList<>();
						stateCodes.add(ele);
						List<CropRegistry> crops = cropMasterRepository.findStateCrops(stateCodes);
						crops.stream().forEach(crop -> {
							List<CropRegistryDetail> cropDetail = cropDetailRepository
									.findByCropIdAndState(crop.getCropId(), ele);
							CropMasterOutputDAO cropOutput = new CropMasterOutputDAO();
							BeanUtils.copyProperties(crop, cropOutput);
							cropOutput.setCropClassMaster(crop.getCropClassId());
							StateLgdMaster state = stateLgdMasterRepository.findByStateLgdCode(ele);
							cropOutput.setStateName(state.getStateName());
							StatusMaster status = statusRepository
									.findByIsDeletedFalseAndStatusCode(crop.getCropStatus());
							cropOutput.setCropStatusName(status.getStatusName());
							if (cropDetail.size()>0) {
								CropRegistryDetail cropDetailObj = cropDetail.get(0);
								cropOutput.setCropNameLocal(
										Objects.nonNull(cropDetailObj) ? cropDetailObj.getCropNameLocal() : "");
//								cropOutput.setCropNameHi(Objects.nonNull(cropDetail) ? cropDetail.getCropNameHi() : "");
								cropOutput.setVernacularLanguage(cropDetailObj.getVernacularLanguage());
							}
							cropOutput.setCropNameHi(crop.getCropNameHi());
							if (Objects.nonNull(crop.getCropSeasonId())) {
								cropOutput.setCropSeasonId(new SeasonMasterOutputDAO(
										crop.getCropSeasonId().getSeasonId(), crop.getCropSeasonId().getSeasonName(),
										crop.getCropSeasonId().getSeasonCode()));
							}
							cropOutput.setCropCategoryId(
									new CropCategoryOutputDAO(crop.getCropCategoryId().getCropCategoryId(),
											crop.getCropCategoryId().getCropCategoryName()));
							cropList.add(cropOutput);
						});
					});

					return new ResponseEntity<String>(
							CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, cropList),
							HttpStatus.OK);
				} else {
					List<CropRegistry> crops = cropMasterRepository
							.findByIsDefaultTrueAndIsDeletedFalse();
					crops.stream().forEach(crop -> {
						CropMasterOutputDAO cropOutput = new CropMasterOutputDAO();
						BeanUtils.copyProperties(crop, cropOutput);
						StatusMaster status = statusRepository.findByIsDeletedFalseAndStatusCode(crop.getCropStatus());
						if (!Objects.isNull(status) && !Objects.isNull(status.getStatusId())) {
							cropOutput.setCropStatusName(status.getStatusName());
						}

						cropOutput.setCropNameLocal("");
//						cropOutput.setCropNameHi("");
						if (Objects.nonNull(cropOutput.getCropSeasonId())) {
							cropOutput.setCropSeasonId(new SeasonMasterOutputDAO(crop.getCropSeasonId().getSeasonId(),
									crop.getCropSeasonId().getSeasonName(), crop.getCropSeasonId().getSeasonCode()));
						}
						cropOutput.setCropCategoryId(
								new CropCategoryOutputDAO(crop.getCropCategoryId().getCropCategoryId(),
										crop.getCropCategoryId().getCropCategoryName()));
						cropList.add(cropOutput);
					});
					return new ResponseEntity<String>(
							CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, cropList),
							HttpStatus.OK);
				}

			} else {
				return new ResponseEntity<String>(
						CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, cropList), HttpStatus.OK);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Get the list of crops.
	 *
	 * @param request HTTP servlet request.
	 * @return ResponseEntity containing the crop list.
	 */
	public ResponseEntity<?> getCropList(HttpServletRequest request) {
		try {
			List<CropMasterOutputDAO> cropList = new ArrayList<>();
			List<CropRegistry> crops = cropMasterRepository.findByIsDeletedFalseAndIsActiveTrueOrderByCropNameAsc();
			crops.forEach(ele -> {
				cropList.add(new CropMasterOutputDAO(ele.getCropId(), ele.getCropName(),
						(Objects.nonNull(ele.getCropSeasonId()) ? ele.getCropSeasonId().getSeasonId() : 0),
						(Objects.nonNull(ele.getCropSeasonId()) ? ele.getCropSeasonId().getSeasonName() : ""),
						ele.getCropCategoryId().getCropCategoryId(), ele.getCropCategoryId().getCropCategoryName(),
						ele.getIsCountable()));
			});
			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, cropList), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Get the list of crops for mobile.
	 *
	 * @param request HTTP servlet request.
	 * @return ResponseEntity containing the crop list.
	 */
	public ResponseEntity<?> getCropListMobile(HttpServletRequest request) {
		try {
			List<CropMasterOutputDAO> cropList = new ArrayList<>();
			List<CropRegistry> crops = new ArrayList<>();
			Long userId = Long.valueOf(userService.getUserId(request));
			StateLgdMaster stateMaster = getUserAccessibleState(userId);
			Optional<UserMaster> usOp=userMasterRepository.findByUserIdAndIsDeletedAndIsActive(userId, false, true);
			SowingSeason currentSowingSeason=generalService.getCurrentSeason();
			if(usOp.isPresent() && currentSowingSeason!=null && usOp.get().getUserTalukaLGDCode()!=null) {	
				UserMaster userMaster=usOp.get();
				
				List<CropSequenceMapping> cropSequenceMappingList =cropSequenceMappingRepository.findOneBySubDistrictLgdCodeAndSeasonIdOrderBySequenceNumberAsc(Long.valueOf(userMaster.getUserTalukaLGDCode()),currentSowingSeason.getSeasonId());
				crops=cropMasterRepository.findStateCrops(Long.valueOf(userMaster.getUserStateLGDCode()));	
				if(cropSequenceMappingList!=null && cropSequenceMappingList.size()>0) {
					List<Long> cropIds=cropSequenceMappingList.stream().map(x->x.getCropId()).collect(Collectors.toList());
					List<CropRegistry> cropNameWithList = new ArrayList<>();

					crops.forEach(action2->{
						cropSequenceMappingList.forEach(action->{
							if(action.getCropId().equals(action2.getCropId())) {
								action2.setSequenceNumber(action.getSequenceNumber());
								cropNameWithList.add(action2);
							}
							
						});						
					});
					Collections.sort(cropNameWithList, 
						    Comparator.comparingLong(CropRegistry::getSequenceNumber));

					List<CropRegistry> cropNameWithOutList = crops.stream()
							.filter(plot -> !cropIds.contains(plot.getCropId()))
							.collect(Collectors.toList());
					
					Collections.sort(cropNameWithOutList, 
						    Comparator.comparing(CropRegistry::getCropName));
					
					cropNameWithList.addAll(cropNameWithOutList);
					crops=cropNameWithList;
					
				}else {
					Collections.sort(crops, 
						    Comparator.comparing(CropRegistry::getCropName));
				}
			
			}else {
				crops=cropMasterRepository.findByIsDeletedFalseAndIsActiveTrueOrderByCropNameAsc();
			}

			crops.forEach(ele -> {
				List<CropRegistryDetail> details = cropDetailRepository
						.findByCropIdAndState(ele.getCropId(), stateMaster.getStateLgdCode());
				cropList.add(new CropMasterOutputDAO(ele.getCropId(), ele.getCropName(),
						(Objects.nonNull(ele.getCropSeasonId()) ? ele.getCropSeasonId().getSeasonId() : 0),
						(Objects.nonNull(ele.getCropSeasonId()) ? ele.getCropSeasonId().getSeasonName() : ""),
						ele.getCropCategoryId().getCropCategoryId(), ele.getCropCategoryId().getCropCategoryName(),
						ele.getIsCountable(),
						(Objects.nonNull(ele.getCropClassId()) ? ele.getCropClassId().getCropClassId() : 0),
						(Objects.nonNull(ele.getCropClassId()) ? ele.getCropClassId().getCropClassName() : "")));
			});
			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, cropList), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Add a new crop.
	 *
	 * @param cropInputDAO Crop input data.
	 * @param request      HTTP servlet request.
	 * @return ResponseEntity containing the result message.
	 */
	public ResponseModel addCrop(CropMasterInputDAO cropInputDAO, HttpServletRequest request) {
		try {
			CropRegistry cropRegistry = new CropRegistry();
			BeanUtils.copyProperties(cropInputDAO, cropRegistry);
			cropRegistry.setIsActive(true);
			cropRegistry.setIsDeleted(false);
			cropRegistry.setCropStatus(StatusEnum.APPROVED.getValue());
			cropRegistry.setCropNameHi(cropInputDAO.getCropNameHi());
			CropClassMaster cropClassName = cropClassNameRepository.findById(cropInputDAO.getCropClassId()).orElse(null);
			cropRegistry.setCropClassId(cropClassName);
			// set uniqueID
			/*List<Integer> count = cropMasterRepository.findByCropUniqueIdIsNotNullCropUniqueIdDesc();
			{
				Integer newCount  = count.get(0) + 1;
				Long cropUniqueId = Long.parseLong(newCount.toString());
				cropRegistry.setCropUniqueId(cropUniqueId);
			}*/

			if (cropInputDAO.getIsAdmin()) {
				cropRegistry.setIsDefault(true);
			} else {
				cropRegistry.setIsDefault(false);
				if (cropInputDAO.getStateLgdCode() != null) {
					cropRegistry.setStateLgdCode(cropInputDAO.getStateLgdCode());
				} else {
					String userId = CustomMessages.getUserId(request, jwtTokenUtils);
					UserMaster user = userMasterRepository.findByUserId(Long.valueOf(userId)).orElse(null);
					List<Long> stateLgdCodes = userVillageMappingRepository.getStateCodesById(user.getUserId());
					if (stateLgdCodes.size() > 0) {
						cropRegistry.setStateLgdCode(stateLgdCodes.get(0));
					}
				}

//				cropRegistry.setCropStatus(StatusEnum.PENDING.getValue());
			}

			/* set crop category and crop season */
			CropCategoryMaster cropCategory = new CropCategoryMaster();
			Optional<CropCategoryMaster> category = cropCategoryRepository
					.findByCropCategoryId(Long.parseLong(cropInputDAO.getCropCategoryId()));
			if (category.isPresent()) {
				cropCategory = category.get();
			}
			SowingSeason sowingSeason = seasonMasterRepository
					.findBySeasonId(Long.parseLong(cropInputDAO.getCropSeasonId()));
			cropRegistry.setCropCategoryId(Objects.nonNull(cropCategory) ? cropCategory : null);
			cropRegistry.setCropSeasonId(Objects.nonNull(sowingSeason) ? sowingSeason : null);
			/* set crop category and crop season */
			cropRegistry.setCreatedOn(new Timestamp(new Date().getTime()));
			cropRegistry.setCreatedIp(CommonUtil.getRequestIp(request));
			cropRegistry.setCreatedBy(CustomMessages.getUserId(request, jwtTokenUtil));
			cropRegistry.setModifiedOn(new Timestamp(new Date().getTime()));
			cropRegistry.setModifiedIp(CommonUtil.getRequestIp(request));
			cropRegistry.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
			cropRegistry = cropMasterRepository.save(cropRegistry);
			saveCropDetails(cropRegistry, cropInputDAO, request);

			// uncomment when approval flow in process

//			if(!cropInputDAO.getIsAdmin()) {
//				StateLgdMaster state = stateLgdMasterRepository.findByStateLgdCode(cropInputDAO.getStateLgdCode());
//				messageConfigurationService.sendEmailForCropUpdate(state.getStateName(), "added", cropRegistry.getCropName());
//			}
			return new ResponseModel(cropRegistry.getCropId(), "Crop" + CustomMessages.RECORD_ADD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}

	}

	/**
	 * Delete a crop.
	 *
	 * @param cropId  Crop ID to be deleted.
	 * @param request HTTP servlet request.
	 * @return ResponseEntity containing the result message.
	 */
	public ResponseEntity<?> deleteCrop(Long cropId, HttpServletRequest request) {
		try {
			CropRegistry cropRegistry = cropMasterRepository.findByCropId(cropId);
			cropRegistry.setIsActive(false);
			cropRegistry.setIsDeleted(true);
			cropRegistry.setModifiedOn(new Timestamp(new Date().getTime()));
			cropRegistry.setModifiedIp(CommonUtil.getRequestIp(request));
			cropRegistry.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
			cropRegistry = cropMasterRepository.save(cropRegistry);

			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.DELETE_SUCCESSFULLY, cropRegistry), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * @param cropInputDAO
	 * @param request
	 * @return
	 */
	public ResponseEntity<?> updateCropStatus(CropMasterInputDAO cropInputDAO, HttpServletRequest request) {
		try {
			CropRegistry cropRegistry = cropMasterRepository.findByCropId(cropInputDAO.getCropId());
			cropRegistry.setIsActive(cropInputDAO.getIsActive());
			cropRegistry.setModifiedOn(new Timestamp(new Date().getTime()));
			cropRegistry.setModifiedIp(CommonUtil.getRequestIp(request));
			cropRegistry.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
			cropRegistry = cropMasterRepository.save(cropRegistry);

			return new ResponseEntity<String>(CustomMessages.getMessageWithData(
					CustomMessages.STATUS_UPDATED_SUCCESSFULLY, cropRegistry.getCropId()), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Update a crop.
	 *
	 * @param cropInputDAO Crop input data.
	 * @param request      HTTP servlet request.
	 * @return ResponseEntity containing the result message.
	 */
	public ResponseEntity<?> updateCrop(CropMasterInputDAO cropInputDAO, HttpServletRequest request) {
		try {
			CropRegistry cropRegistry = cropMasterRepository.findByCropId(cropInputDAO.getCropId());
			BeanUtils.copyProperties(cropInputDAO, cropRegistry, "createdOn", "createdBy", "createdIp",
					"isDeleted", "isActive", "isDefault", "stateLgdCode", "cropUniqueId");
			
			/* set crop class name */
			CropClassMaster cropClassName = cropClassNameRepository.findById(cropInputDAO.getCropClassId()).orElse(null);
			cropRegistry.setCropClassId(cropClassName);
			
			/* set crop category and crop season */
			CropCategoryMaster cropCategory = new CropCategoryMaster();
			Optional<CropCategoryMaster> category = cropCategoryRepository
					.findByCropCategoryId(Long.parseLong(cropInputDAO.getCropCategoryId()));
			if (category.isPresent()) {
				cropCategory = category.get();
			}
			cropRegistry.setCropNameHi(cropInputDAO.getCropNameHi());
			cropRegistry.setCropStatus(StatusEnum.APPROVED.getValue());
			// uncomment when approval flow in process
			/*
			 * if(cropInputDAO.getIsAdmin()) {
			 * cropRegistry.setCropStatus(StatusEnum.APPROVED.getValue()); } else {
			 * cropRegistry.setCropStatus(StatusEnum.PENDING.getValue()); }
			 */
			SowingSeason sowingSeason = seasonMasterRepository
					.findBySeasonId(Long.parseLong(cropInputDAO.getCropSeasonId()));
			cropRegistry.setCropCategoryId(Objects.nonNull(cropCategory) ? cropCategory : null);
			cropRegistry.setCropSeasonId(Objects.nonNull(sowingSeason) ? sowingSeason : null);
			/* set crop category and crop season */
			cropRegistry.setModifiedOn(new Timestamp(new Date().getTime()));
			cropRegistry.setModifiedIp(CommonUtil.getRequestIp(request));
			cropRegistry.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
			cropRegistry = cropMasterRepository.save(cropRegistry);
			updateCropDetail(cropRegistry, cropInputDAO, request);

			// uncomment when approval flow in process
			/*
			 * if(!cropInputDAO.getIsAdmin()) { StateLgdMaster state =
			 * stateLgdMasterRepository.findByStateLgdCode(cropInputDAO.getStateLgdCode());
			 * messageConfigurationService.sendEmailForCropUpdate(state.getStateName(),
			 * "updated", cropRegistry.getCropName()); }
			 */
			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.CROP_UPDATE_SUCCESSFULLY, cropRegistry),
					HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * get season
	 * 
	 * @param cropId
	 * @param request
	 * @return Crop Category Detail
	 */
	public ResponseEntity<?> getCropById(Long cropId, HttpServletRequest request) {
		try {
			Long userId = Long.valueOf(CustomMessages.getUserId(request, jwtTokenUtil));
			CropMasterOutputDAO cropOutputDAO = new CropMasterOutputDAO();
			CropRegistry cropRegistry = cropMasterRepository.findByCropId(cropId);
			BeanUtils.copyProperties(cropRegistry, cropOutputDAO);
			if (Objects.nonNull(cropOutputDAO.getCropSeasonId())) {
				cropOutputDAO.setCropSeasonId(new SeasonMasterOutputDAO(cropRegistry.getCropSeasonId().getSeasonId(),
						cropRegistry.getCropSeasonId().getSeasonName(),
						cropRegistry.getCropSeasonId().getSeasonCode()));
			}

			cropOutputDAO.setCropClassMaster(cropRegistry.getCropClassId());
			// get cropDetail
			Long stateLgdCode = getUserAccessibleState(userId).getStateLgdCode();
			List<CropRegistryDetail> cropDetails = cropDetailRepository
					.findByCropIdAndState(cropOutputDAO.getCropId(), stateLgdCode);
			if (cropDetails.size()>0) {
				CropRegistryDetail detail = cropDetails.get(0);
				cropOutputDAO.setCropNameLocal(detail.getCropNameLocal());
				cropOutputDAO.setCropNameHi(cropRegistry.getCropNameHi());
				cropOutputDAO.setVernacularLanguage(detail.getVernacularLanguage());
			}
			cropOutputDAO.setSeasonId(cropRegistry.getCropSeasonId().getSeasonId());
			cropOutputDAO.setCategoryId(cropRegistry.getCropCategoryId().getCropCategoryId());
			cropOutputDAO
					.setCropCategoryId(new CropCategoryOutputDAO(cropRegistry.getCropCategoryId().getCropCategoryId(),
							cropRegistry.getCropCategoryId().getCropCategoryName()));
			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, cropOutputDAO), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Saves crop details.
	 *
	 * @param cropRegistry The crop registry.
	 * @param cropInputDAO The crop input DAO.
	 * @param request The HTTP servlet request.
	 * @return True if the crop details are successfully saved, false otherwise.
	 */
	@Transactional
	public Boolean saveCropDetails(CropRegistry cropRegistry, CropMasterInputDAO cropInputDAO,
			HttpServletRequest request) {
		try {

			String userId = CustomMessages.getUserId(request, jwtTokenUtils);
			UserMaster user = userMasterRepository.findByUserId(Long.valueOf(userId)).orElse(null);
			List<Long> stateLgdCodes = userVillageMappingRepository.getStateCodesById(user.getUserId());
			CropRegistryDetail cropDetail = new CropRegistryDetail();
			cropDetail.setCropId(cropRegistry);
//			cropDetail.setCropNameHi(cropInputDAO.getCropNameHi());
			cropDetail.setCropNameLocal(cropInputDAO.getCropNameLocal());
			cropDetail.setStateLgdCode(stateLgdCodes.size()> 0 ? stateLgdCodes.get(0) : null);
			cropDetail.setVernacularLanguage(cropInputDAO.getVernacularLanguage());
			cropDetail.setCreatedOn(new Timestamp(new Date().getTime()));
			cropDetail.setCreatedIp(CommonUtil.getRequestIp(request));
			cropDetail.setCreatedBy(CustomMessages.getUserId(request, jwtTokenUtil));
			cropDetail.setModifiedOn(new Timestamp(new Date().getTime()));
			cropDetail.setModifiedIp(CommonUtil.getRequestIp(request));
			cropDetail.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
			cropDetailRepository.save(cropDetail);
			if (Objects.nonNull(cropDetail.getId())) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Updates crop detail.
	 *
	 * @param cropRegistry The crop registry.
	 * @param cropInputDAO The crop input DAO.
	 * @param request The HTTP servlet request.
	 * @return True if the crop detail is successfully updated, false otherwise.
	 */
	@Transactional
	public Boolean updateCropDetail(CropRegistry cropRegistry, CropMasterInputDAO cropInputDAO,
			HttpServletRequest request) {
		String userId = CustomMessages.getUserId(request, jwtTokenUtils);
		UserMaster user = userMasterRepository.findByUserId(Long.valueOf(userId)).orElse(null);
		List<Long> stateLgdCodes = userVillageMappingRepository.getStateCodesById(user.getUserId());
		if(!CollectionUtils.isEmpty(stateLgdCodes)) {
			List<CropRegistryDetail> cropDetails = cropDetailRepository.findByCropIdAndState(cropRegistry.getCropId(),
					stateLgdCodes.get(0));
			if (cropDetails.size() >0) {
				try {
					CropRegistryDetail cropRegistryDetail = cropDetails.get(0);
					cropRegistryDetail.setCropId(cropRegistry);
					cropRegistryDetail.setCropNameLocal(cropInputDAO.getCropNameLocal());
					cropRegistryDetail.setVernacularLanguage(cropInputDAO.getVernacularLanguage());
					cropRegistryDetail.setModifiedOn(new Timestamp(new Date().getTime()));
					cropRegistryDetail.setModifiedIp(CommonUtil.getRequestIp(request));
					cropRegistryDetail.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
					cropDetailRepository.save(cropRegistryDetail);
				} catch (Exception e) {
					e.printStackTrace();
				}

				return true;
			} else {
				// to do
				saveCropDetails(cropRegistry, cropInputDAO, request);
				return true;
			}
		} else {
			return false;
		}
	}

	/**
	 * Approves or rejects crops.
	 *
	 * @param cropMasterInputDAO The crop master input DAO.
	 * @param request The HTTP servlet request.
	 * @return ResponseEntity with the result.
	 */
	public ResponseEntity<?> approveOrRejectCrops(CropMasterInputDAO cropMasterInputDAO, HttpServletRequest request) {
		try {
			CropMasterOutputDAO cropOutputDAO = new CropMasterOutputDAO();
			List<CropRegistry> crops = cropMasterRepository.findByCropIds(cropMasterInputDAO.getCropIds());
			if (crops.size() > 0) {
				for (CropRegistry crop : crops) {
					crop.setCropStatus(StatusEnum.valueOf(cropMasterInputDAO.getCropStatus()).getValue());
					if (cropMasterInputDAO.getCropStatus().equals(StatusEnum.REJECTED.toString())) {
						crop.setRejectionReason(crop.getRejectionReason());
					} else {
						crop.setRejectionReason(null);
					}
					crop.setModifiedOn(new Timestamp(new Date().getTime()));
					crop.setModifiedIp(CommonUtil.getRequestIp(request));
					crop.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
					cropMasterRepository.save(crop);
				}
			}
			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, cropOutputDAO), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Gets the user-accessible state.
	 *
	 * @param userId The user ID.
	 * @return The user-accessible state.
	 */
	protected StateLgdMaster getUserAccessibleState(Long userId) {
		List<Long> stateCodes = userVillageMappingRepository.getStateCodesById(userId);
		List<StateLgdMaster> stateList = new ArrayList<>();
		StateLgdMaster state = new StateLgdMaster();
		if (stateCodes != null && stateCodes.size() > 0) {
			stateList = stateLgdMasterRepository.findAllByStateLgdCodeIn(stateCodes);
			if (stateList.size() > 0) {
				state = stateList.get(0);
			}
		}
		return state;
	}

	/**
	 * Adds crop unique ID.
	 *
	 * @param request The HTTP servlet request.
	 * @return ResponseEntity with the result.
	 */
    public ResponseEntity<?> addCropUniqueId(HttpServletRequest request) {
        try {
//            List<CropRegistry> crops = cropMasterRepository.findByCropUniqueIdIsNullOrderByCropIdAsc();
//            crops.stream().forEach(ele -> {
//				Long cropUniqueId = 0L;
//				List<Integer> count = cropMasterRepository.findByCropUniqueIdIsNotNullCropUniqueIdDesc();
//				if(count.size() == 0) {
//					ele.setCropUniqueId(1L);
//				} else {
//					Integer newCount  = count.get(0) + 1;
//					cropUniqueId = Long.parseLong(newCount.toString());
//					ele.setCropUniqueId(cropUniqueId);
//				}
//				System.out.println(ele.getCropUniqueId());
//				cropMasterRepository.save(ele);
//			});

            return new ResponseEntity<String>(
                    CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, null), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<Mono<?>>(
                    Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

	/**
	 * Gets all crops.
	 *
	 * @param request The HTTP servlet request.
	 * @return ResponseEntity with the result.
	 */
	public ResponseEntity<?> getAllCrops(HttpServletRequest request) {
		try {
			List<CropRegistry> crops = cropMasterRepository.findAll();
			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, crops), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Gets all crop details.
	 *
	 * @param request The HTTP servlet request.
	 * @return ResponseEntity with the result.
	 */
	public ResponseEntity<?> getAllCropDetails(HttpServletRequest request) {
		try {
			List<CropRegistryDetail> cropdetails = cropDetailRepository.findAll();
			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.GET_DATA_SUCCESS, cropdetails), HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
