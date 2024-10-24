package com.amnex.agristack.service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

import com.amnex.agristack.entity.*;
import com.amnex.agristack.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amnex.agristack.Enum.ActivityStatusEnum;
import com.amnex.agristack.Enum.ActivityTypeEnum;
import com.amnex.agristack.Enum.CropSurveyModeEnum;
import com.amnex.agristack.Enum.MasterTableName;
import com.amnex.agristack.Enum.MessageType;
import com.amnex.agristack.Enum.NotificationType;
import com.amnex.agristack.Enum.StatusEnum;
import com.amnex.agristack.dao.SurveyDetailMasterRequestDTO;
import com.amnex.agristack.dao.SurveyDetailRequestDTO;
import com.amnex.agristack.dao.SurveyReviewFetchRequestMobileDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

@Service
public class KCropSurveyService {

	@Autowired
	private KafkaTemplate<String, SurveyDetailMasterRequestDTO> kafkaCropSurveyTemplate;

	@Autowired
	private KafkaTemplate<String, String> kafkaDataTemplate;

	@Autowired
	private KafkaTemplate<String, List<MultipartFile>> kafkaMediaTemplate;

	@Autowired
	LandParcelSurveyMasterRespository landParcelSurveyMasterRespository;

	@Autowired
	LandParcelSurveyDetailRepository landParcelSurveyDetailRepository;

	@Autowired
	LandParcelSurveyCropDetailRepository landParcelSurveyCropDetailRepository;

	@Autowired
	UserMasterRepository userMasterRepository;

	@Autowired
	FarmerRegistryRepository farmerRegistryRepository;

	@Autowired
	IrrigationMasterRepository irrigationMasterRepository;

	@Autowired
	IrrigationSourceRepository irrigationSourceRepository;

	@Autowired
	FarmlandPlotRegistryRepository farmlandPlotRegistryRepository;

	@Autowired
	private UserLandAssignmentRepository userLandAssignmentRepository;

	@Autowired
	SurveyActivityConfigService surveyActivityConfigService;

	@Autowired
	SurveyReviewService surveyReviewService;

	@Autowired
	LandParcelSurveyCropMediaMappingRepository landParcelSurveyCropMediaMappingRepository;

	@Autowired
	SurveyActivityLogRepository surveyActivityLogRepository;

	@Autowired
	CropMasterRepository cropRegistryRepository;

	@Autowired
	CropVarietyRepository cropVarietyRepository;

	@Autowired
	CropTypeMasterRepository cropTypeMasterRepository;

	@Autowired
	CropStatusRepository cropStatusRepository;

	@Autowired
	CropCategoryRepository cropCategoryRepository;

	@Autowired
	SurveyAreaTypeMasterRepository surveyAreaTypeMasterRepository;

	private Object template;

	/**
	 * Adds a survey based on the provided survey detail request and HTTP servlet
	 * request.
	 * 
	 * @param surveyDetailMasterRequestDTO The DTO containing the survey detail
	 *                                     master request.
	 * @param request                      The HTTP servlet request.
	 * @return An object representing the survey review fetch request for the mobile
	 *         application.
	 * @throws Exception if an error occurs during the survey addition process.
	 */
	@Transactional
	public Object validateCropSurveyRecordAndAddInKafka(SurveyDetailMasterRequestDTO surveyDetailMasterRequestDTO,
			HttpServletRequest request) throws Exception {

//		kafkaDataTemplate.send(KafkaTopicConfig.DATA_TOPIC_NAME, surveyDetailMasterRequestDTO.getData());

//		kafkaMediaTemplate.send(KafkaTopicConfig.MEDIA_TOPIC_NAME, surveyDetailMasterRequestDTO.getMedia());

		return null;
	}

	public void addCropSurveyDataFromKafkaToDB(String cropData) {

		try {
			ObjectMapper mapper = new ObjectMapper();

			SurveyDetailRequestDTO surveyDetailRequestDTO = mapper.readValue(cropData, SurveyDetailRequestDTO.class);

			Optional<FarmlandPlotRegistry> flpr = Optional.empty();
			if (surveyDetailRequestDTO.getFarmlandPlotRegisterId() != null) {
				flpr = farmlandPlotRegistryRepository.findById(surveyDetailRequestDTO.getFarmlandPlotRegisterId());
			}

			// Create a new instance of LandParcelSurveyMaster and check if there is an
			// existing record
			LandParcelSurveyMaster landParcelSurveyMaster = new LandParcelSurveyMaster();
			Optional<LandParcelSurveyMaster> landParcelSurveyOptionalMaster = Optional.empty();
			if (surveyDetailRequestDTO.getLandParcelSurveyMasterId() != null
					&& surveyDetailRequestDTO.getLandParcelSurveyMasterId() != 0) {
				landParcelSurveyOptionalMaster = landParcelSurveyMasterRespository
						.findById(surveyDetailRequestDTO.getLandParcelSurveyMasterId());
			} else {
				landParcelSurveyOptionalMaster = landParcelSurveyMasterRespository.findByParcelSeasonAndYear(
						surveyDetailRequestDTO.getFarmlandPlotRegisterId(), surveyDetailRequestDTO.getStartYear(),
						surveyDetailRequestDTO.getEndYear(), surveyDetailRequestDTO.getSeasonId());
			}

			// Check if the optional contains a value and update the landParcelSurveyMaster
			// accordingly
			if (landParcelSurveyOptionalMaster.isPresent()) {
				landParcelSurveyMaster = landParcelSurveyOptionalMaster.get();
			}

			// Update the landParcelSurveyMaster with farmlandPlotRegisterId parcelId
			if (flpr.isPresent()) {
				landParcelSurveyMaster.setParcelId(flpr.get());
			}
			landParcelSurveyMaster.setSeasonId(surveyDetailRequestDTO.getSeasonId());
			landParcelSurveyMaster.setSeasonStartYear(surveyDetailRequestDTO.getStartYear());
			landParcelSurveyMaster.setSeasonEndYear(surveyDetailRequestDTO.getEndYear());

			// Update the survey status based on the review number
			if (surveyDetailRequestDTO.getReviewNo() == 1) {
				landParcelSurveyMaster.setSurveyOneStatus(StatusEnum.PENDING.getValue());
				landParcelSurveyMaster.setSurveyStatus(StatusEnum.PENDING.getValue());
			}

			if (surveyDetailRequestDTO.getReviewNo() == 2) {
				landParcelSurveyMaster.setSurveyTwoStatus(StatusEnum.PENDING.getValue());
				landParcelSurveyMaster.setSurveyStatus(StatusEnum.PENDING.getValue());
			}

			if (surveyDetailRequestDTO.getReviewNo() == 3) {
				landParcelSurveyMaster.setVerifierStatus(StatusEnum.APPROVED.getValue());
				if (landParcelSurveyMaster.getSurveyOneStatus() == null
						&& landParcelSurveyMaster.getSurveyTwoStatus() == null) {
					landParcelSurveyMaster.setSurveyStatus(StatusEnum.SURVEY_PENDING.getValue());
				} else {
					landParcelSurveyMaster.setSurveyStatus(landParcelSurveyMaster.getSurveyStatus());

				}

			}

			// Update the survey mode and flexible survey reason
			if ((surveyDetailRequestDTO.getSurveyMode() != null) && (Integer
					.valueOf(CropSurveyModeEnum.FLEXIBLE.getValue()).equals(surveyDetailRequestDTO.getSurveyMode())

			)) {
				landParcelSurveyMaster.setSurveyMode(CropSurveyModeEnum.FLEXIBLE.getValue());
				landParcelSurveyMaster.setFlexibleSurveyReasonId(surveyDetailRequestDTO.getFlexibleSurveyReasonId());
			} else {
				landParcelSurveyMaster.setSurveyMode(CropSurveyModeEnum.MANDATORY.getValue());
			}

			// Save the updated landParcelSurveyMaster
			landParcelSurveyMaster = landParcelSurveyMasterRespository.save(landParcelSurveyMaster);
			surveyDetailRequestDTO.setLandParcelSurveyMasterId(landParcelSurveyMaster.getLpsmId());

			// Add LandParcelSurveyDetail
			LandParcelSurveyDetail landParcelSurveyDetail = addLandParcelSurveyDetails(landParcelSurveyMaster,
					surveyDetailRequestDTO);

			// add Crops
			// if (surveyDetailRequestDTO.getCrops() != null) {
			// 	addCropDetails(landParcelSurveyDetail, surveyDetailRequestDTO);
			// }

			// Start Send notification to admin
			try {
				List<String> receiverIds = userLandAssignmentRepository.getUserListByFilter(
						surveyDetailRequestDTO.getStartYear(), surveyDetailRequestDTO.getEndYear(),
						surveyDetailRequestDTO.getSeasonId(),
						landParcelSurveyMaster.getParcelId().getFarmlandPlotRegistryId());
				if (receiverIds != null && !receiverIds.isEmpty()) {
					surveyReviewService.sendNotification(null, MessageType.SURVEY_COMPLETED, NotificationType.WEB,
							ActivityTypeEnum.CROP_SURVEY, ActivityStatusEnum.SURVEY_COMPLETED,
							surveyDetailRequestDTO.getSurveyBy(), receiverIds, landParcelSurveyMaster.getLpsmId(),
							MasterTableName.LAND_PARCEL_SURVEY_MASTER);
				}

			} catch (Exception e) {
				System.err.print(e.getMessage());
			}
			// End Send notification to admin
			// Return response
			SurveyReviewFetchRequestMobileDTO surveyReviewFetchRequestMobileDTO = new SurveyReviewFetchRequestMobileDTO();
			surveyReviewFetchRequestMobileDTO.setUserId(surveyDetailRequestDTO.getSurveyBy().toString());
			surveyReviewFetchRequestMobileDTO.setSeasonId(surveyDetailRequestDTO.getSeasonId().toString());
			surveyReviewFetchRequestMobileDTO.setStartYear(surveyDetailRequestDTO.getStartYear().toString());
			surveyReviewFetchRequestMobileDTO.setEndYear(surveyDetailRequestDTO.getEndYear().toString());
			surveyReviewFetchRequestMobileDTO.setPlotIds("0");
			surveyReviewFetchRequestMobileDTO.setSurveyMasterId(landParcelSurveyMaster.getLpsmId().toString());
//			return surveyReviewFetchRequestMobileDTO;
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/**
	 * Adds a land parcel survey detail based on the provided parameters.
	 * 
	 * @param landParcelSurveyMaster       The land parcel survey master.
	 * @param surveyDetailRequestDTO       The survey detail request DTO.
	 * @return The added land parcel survey detail.
	 */
	public LandParcelSurveyDetail addLandParcelSurveyDetails(LandParcelSurveyMaster landParcelSurveyMaster,
			SurveyDetailRequestDTO surveyDetailRequestDTO) {
		Optional<LandParcelSurveyDetail> landParcelSurveyDetailOptioanl = landParcelSurveyDetailRepository
				.findByLpsmIdAndReviewNo(landParcelSurveyMaster.getLpsmId(), surveyDetailRequestDTO.getReviewNo());
		LandParcelSurveyDetail landParcelSurveyDetail = new LandParcelSurveyDetail();
		if (landParcelSurveyDetailOptioanl.isPresent()) {
			landParcelSurveyDetail = landParcelSurveyDetailOptioanl.get();
		}
		if (surveyDetailRequestDTO.getOwnerId() != null && surveyDetailRequestDTO.getOwnerId() != 0) {
			landParcelSurveyDetail.setOwnerId(surveyDetailRequestDTO.getOwnerId());
		}
		// landParcelSurveyDetail.setLpsmId(landParcelSurveyMaster);
		landParcelSurveyDetail.setMobileNo(surveyDetailRequestDTO.getOwnerMobile());
		landParcelSurveyDetail.setArea(surveyDetailRequestDTO.getArea());
		landParcelSurveyDetail.setUnit(surveyDetailRequestDTO.getUnit());

		landParcelSurveyDetail.setBalancedArea(surveyDetailRequestDTO.getBalancedArea());
		landParcelSurveyDetail.setBalancedAreaUnit(surveyDetailRequestDTO.getBalancedAreaUnit());
		landParcelSurveyDetail.setSurveyorLat(surveyDetailRequestDTO.getSurveyorLat());
		landParcelSurveyDetail.setSurveyorLong(surveyDetailRequestDTO.getSurveyorLong());
		// wkt to geom
		if (surveyDetailRequestDTO.getGeom() != null) {
			WKTReader wktReader = new WKTReader();
			try {
				landParcelSurveyDetail.setGeom(wktReader.read(surveyDetailRequestDTO.getGeom()));
			} catch (ParseException e) {
				// e.printStackTrace();
				System.out.println("Error while parsing wkt to geom");
			}
		}
		landParcelSurveyDetail.setTotalArea(surveyDetailRequestDTO.getArea());
		landParcelSurveyDetail.setTotalAreaUnit(surveyDetailRequestDTO.getUnit());
		landParcelSurveyDetail.setBalancedArea(surveyDetailRequestDTO.getBalancedArea());
		landParcelSurveyDetail.setBalancedAreaUnit(surveyDetailRequestDTO.getBalancedAreaUnit());
		landParcelSurveyDetail.setReviewNo(surveyDetailRequestDTO.getReviewNo());
		landParcelSurveyDetail.setSurveyBy(surveyDetailRequestDTO.getSurveyBy());
		landParcelSurveyDetail.setStatus(StatusEnum.PENDING.getValue());

		if (surveyDetailRequestDTO.getSurveyDate() != null) {
			try {
				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
				Date parsedDate = formatter.parse(surveyDetailRequestDTO.getSurveyDate());
				Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
				landParcelSurveyDetail.setSurveyDate(timestamp);
			} catch (java.text.ParseException e) {
				// e.printStackTrace();
			}
		}
		landParcelSurveyDetail = landParcelSurveyDetailRepository.save(landParcelSurveyDetail);

		addSurveyActivityLog(landParcelSurveyMaster.getLpsmId(), landParcelSurveyDetail.getLpsdId(),
				landParcelSurveyDetail.getStatus().longValue(), landParcelSurveyDetail.getSurveyBy());
		return landParcelSurveyDetail;
	}

	/**
	 * Adds a survey activity log with the specified parameters.
	 * 
	 * @param lpsmId   The ID of the land parcel survey master.
	 * @param lpsdId   The ID of the land parcel survey detail.
	 * @param statusId The ID of the status.
	 * @param userId   The ID of the user.
	 */
	public void addSurveyActivityLog(Long lpsmId, Long lpsdId, Long statusId, Long userId) {
		try {
			SurveyActivityLog surveyActivityLog = new SurveyActivityLog();
			surveyActivityLog.setLpsmId(lpsmId);
			surveyActivityLog.setLpsdId(lpsdId);
			surveyActivityLog.setStatusId(statusId);
			surveyActivityLog.setUserId(userId);
			surveyActivityLogRepository.save(surveyActivityLog);
		} catch (Exception e) {
			System.out.println("Error while adding activity load");
		}
	}

	/**
	 * Adds crop details for a land parcel survey.
	 * 
	 * @param landParcelSurveyDetail       The land parcel survey detail.
	 * @param surveyDetailMasterRequestDTO The survey detail master request DTO.
	 * @param surveyDetailRequestDTO       The survey detail request DTO.
	 */
	// public void addCropDetails(LandParcelSurveyDetail landParcelSurveyDetail,
	// 		SurveyDetailRequestDTO surveyDetailRequestDTO) {
	// 	try {
	// 		landParcelSurveyCropDetailRepository.deleteCropMediaByLpsdId(landParcelSurveyDetail.getLpsdId());
	// 	} catch (Exception e) {
	// 		System.out.println(e.getMessage());
	// 	}

	// 	try {
	// 		landParcelSurveyCropDetailRepository.deleteCropMediaMappingByLpsdId(landParcelSurveyDetail.getLpsdId());
	// 	} catch (Exception e) {
	// 		System.out.println(e.getMessage());
	// 	}

	// 	try {
	// 		landParcelSurveyCropDetailRepository.deleteByLpsdId(landParcelSurveyDetail.getLpsdId());
	// 	} catch (Exception e) {
	// 		System.out.println(e.getMessage());
	// 	}

	// 	for (SurveyDetailCropDTO surveyCrop : surveyDetailRequestDTO.getCrops()) {
	// 		LandParcelSurveyCropDetail landParcelSurveyCropDetail = new LandParcelSurveyCropDetail();
	// 		landParcelSurveyCropDetail.setLpsdId(landParcelSurveyDetail);
	// 		// area;
	// 		landParcelSurveyCropDetail.setArea(surveyCrop.getArea());
	// 		// unit;
	// 		landParcelSurveyCropDetail.setUnit(surveyCrop.getUnit());
	// 		// remark
	// 		landParcelSurveyCropDetail.setRemarks(surveyCrop.getRemark());
	// 		if (surveyCrop.getSowingDate() != null) {
	// 			try {
	// 				SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
	// 				Date parsedDate = formatter.parse(surveyCrop.getSowingDate());
	// 				Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
	// 				landParcelSurveyCropDetail.setSowingDate(timestamp);
	// 			} catch (java.text.ParseException e) {
	// 				// e.printStackTrace();
	// 			}
	// 		}

	// 		Optional<SurveyAreaTypeMaster> surveyAreaTypeMaster = surveyAreaTypeMasterRepository
	// 				.findById(surveyCrop.getAreaTypeId());
	// 		if (surveyAreaTypeMaster.isPresent()) {
	// 			landParcelSurveyCropDetail.setAreaTypeId(surveyAreaTypeMaster.get());
	// 		}
	// 		Integer areaTypeId = surveyCrop.getAreaTypeId().intValue();
	// 		if (areaTypeId.equals(SurveyAreaTypeEnum.WASTEVACANT.getValue())) {
	// 			landParcelSurveyCropDetail.setIsWasteArea(surveyCrop.getIsWasteArea());
	// 			landParcelSurveyCropDetail.setLandtypeId(LandTypeEnum.AGRICULTURAL.getValue());
	// 		} else if (areaTypeId.equals(SurveyAreaTypeEnum.UNUTILIZED.getValue())) {
	// 			landParcelSurveyCropDetail.setIsUnutilizedArea(surveyCrop.getIsUnutilizedArea());
	// 			landParcelSurveyCropDetail.setLandtypeId(LandTypeEnum.NONAGRICULTURAL.getValue());
	// 		} else {
	// 			// cropId;
	// 			CropRegistry crop = cropRegistryRepository.findByCropId(surveyCrop.getCropId());
	// 			landParcelSurveyCropDetail.setCropId(crop);
	// 			// cropVarietyId;
	// 			Optional<CropVarietyMaster> cropVariety = cropVarietyRepository.findById(surveyCrop.getCropVarietyId());
	// 			landParcelSurveyCropDetail.setCropVarietyId(cropVariety.get());
	// 			// cropTypeId;
	// 			Optional<CropTypeMaster> cropType = cropTypeMasterRepository.findById(surveyCrop.getCropTypeId());
	// 			landParcelSurveyCropDetail.setCropTypeId(cropType.get());
	// 			// cropStatusId
	// 			landParcelSurveyCropDetail.setCropStage(surveyCrop.getCropStatusId());
	// 			// irrigationTypeId
	// 			Optional<IrrigationMaster> irrigationMaster = irrigationMasterRepository
	// 					.findById(surveyCrop.getIrrigationTypeId());
	// 			if (irrigationMaster.isPresent()) {
	// 				landParcelSurveyCropDetail.setIrrigationTypeId(irrigationMaster.get());
	// 			}
	// 			// irrigationSourceId
	// 			Optional<IrrigationSource> irrigationSource = irrigationSourceRepository
	// 					.findById(surveyCrop.getIrrigationSourceId());
	// 			if (irrigationSource.isPresent()) {
	// 				landParcelSurveyCropDetail.setIrrigationSourceId(irrigationSource.get());
	// 			}
	// 			// irrigationSourceId
	// 			Optional<CropCategoryMaster> cropCategory = cropCategoryRepository
	// 					.findById(surveyCrop.getCropCategory());
	// 			if (irrigationSource.isPresent()) {
	// 				landParcelSurveyCropDetail.setCropCategoryId(cropCategory.get());
	// 			}

	// 			if (surveyCrop.getNumberOfTree() != null) {
	// 				landParcelSurveyCropDetail.setNoOfTree(surveyCrop.getNumberOfTree());
	// 			}

	// 			landParcelSurveyCropDetail.setLandtypeId(LandTypeEnum.AGRICULTURAL.getValue());

	// 		}
	// 		landParcelSurveyCropDetail = landParcelSurveyCropDetailRepository.save(landParcelSurveyCropDetail);
	// 		// media
	// 		try {
	// 			addCropSurveyMediaMappingFromKafka(surveyCrop.getUploadedMedia(),
	// 					landParcelSurveyCropDetail.getCropSdId());

	// 		} catch (Exception e) {
	// 			// e.printStackTrace();
	// 			System.out.println("Error while adding media surveyCrop");
	// 		}
	// 	}
	// }

	/**
	 * Adds media mappings for crop survey details.
	 * 
	 * @param mediaIds                    The list of multipart files representing
	 *                                     the media.
	 * @param landParcelSurveyCropDetailId The ID of the land parcel survey crop
	 *                                     detail.
	 */
	public void addCropSurveyMediaMappingFromKafka(List<String> mediaIds, Long landParcelSurveyCropDetailId) {
		for (String mediaId : mediaIds) {
			LandParcelSurveyCropMediaMapping landParcelSurveyCropMediaMapping = new LandParcelSurveyCropMediaMapping();
			landParcelSurveyCropMediaMapping.setMediaId(mediaId);
			landParcelSurveyCropMediaMapping.setSurveyCropId(landParcelSurveyCropDetailId);
			landParcelSurveyCropMediaMappingRepository.save(landParcelSurveyCropMediaMapping);
		}
	}

}
