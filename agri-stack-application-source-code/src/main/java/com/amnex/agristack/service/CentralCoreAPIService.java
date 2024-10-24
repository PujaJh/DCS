package com.amnex.agristack.service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.Enum.ConfigCode;
import com.amnex.agristack.Enum.SOPType;
import com.amnex.agristack.Enum.StatusEnum;
import com.amnex.agristack.dao.CentralCoreDAO;
import com.amnex.agristack.dao.PlotLandDetailDAO;
import com.amnex.agristack.dao.VillageWiseCropSurveyDetailDAO;
import com.amnex.agristack.dao.VillageWiseSurveyDetailDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.*;
import com.amnex.agristack.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.utils.CustomMessages;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CentralCoreAPIService {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private CentralCoreAPIRepository centralCoreAPIRepository;

	@Autowired
	private ConfigurationRepository configurationRepository;

	@Autowired
	private SeasonMasterRepository seasonRepository;

	@Autowired
	private YearRepository yearRepository;

	@Autowired
	private VillageWiseCropDataLogRepository villageWiseCropDataLogRepository;

	@Autowired
	private SOPAcknowledgementRepository sopAcknowledgementRepository;

	@Autowired
	private ApiCallLogRepository apiCallLogRepository;

	// @Autowired
	// private VillageWiseCropSurveyDetailRepository
	// villageWiseCropSurveyDetailRepository;
	@Autowired
	private VillageWiseGeomapLogRepository villageWiseGeomapLogRepository;
	@Autowired
	private FarmlandPlotRegistryRepository farmlandPlotRegistryRepository;

	@Autowired
	private VillageWiseCropSurveyDetailRepository villageWiseCropDetailRepository;

	public Map<String, Object> getCropSownDetails(HttpServletRequest request) {
		// output declaration
		Map<String, Object> responseData = new HashMap();
		try {
			String userId = CustomMessages.getUserId(request, jwtTokenUtil);
			Integer pendingToShareCount = villageWiseCropDetailRepository
					.findCountOfIsAcknowledgedAndIsShared(StatusEnum.PENDING.getValue());

			if (Objects.isNull(userId)) {
				responseData.put("message", "Unauthorized request");
				return responseData;
			} else if (pendingToShareCount == 0) {
				List<Object> list = new ArrayList<>();
				responseData.put("referenceId", "");
				responseData.put("surveyDetails", list);
				responseData.put("limit", 0);
				responseData.put("pending", 0);
				return responseData;
			} else {
				// declarations
				Integer countNumber = 5000;
				Integer totalRecords = 0;
				Integer startSerialNumber = 0;
				Integer endSerialNumber = 0;
				Integer pendingRecords = 0;
				Integer page = 0;
				VillageWiseCropDataLog villageWiseCropDataLog = new VillageWiseCropDataLog();

				String apiRefereceId = getTenDigitUniqueId();
				List<Object> list = new ArrayList<>();

				responseData.put("referenceId", apiRefereceId);
				responseData.put("surveyDetails", list);
				responseData.put("limit", 0);
				responseData.put("pending", 0);

				List<VillageWiseCropDataLog> previouslySharedList = villageWiseCropDataLogRepository
						.findAllByOrderByReferenceIdDesc();
				if (!previouslySharedList.isEmpty()) {
					if (previouslySharedList.get(0).getIsAcknowledged().equals(StatusEnum.SUCCESS.getValue())
							&& !previouslySharedList.get(0).getPendingRecords().equals(0)) {
						VillageWiseCropDataLog lastSharedRecord = villageWiseCropDataLogRepository
								.findLastSharedSuccessLog().orElse(null);
						page = lastSharedRecord.getPage() + 1;
						totalRecords = lastSharedRecord.getTotalRecords();
					} else if (previouslySharedList.get(0).getIsAcknowledged().equals(StatusEnum.PENDING.getValue())) {
						VillageWiseCropDataLog lastSharedPendingRecord = villageWiseCropDataLogRepository
								.findLastSharedPendingLog().orElse(null);
						if (Objects.nonNull(lastSharedPendingRecord)
								&& lastSharedPendingRecord.getIsAcknowledged().equals(StatusEnum.PENDING.getValue())) {
							lastSharedPendingRecord.setIsAcknowledged(StatusEnum.FAILED.getValue());
							villageWiseCropDataLogRepository.save(lastSharedPendingRecord);
							List<SOPAcknowledgement> acknowledgementList = sopAcknowledgementRepository
									.findByReferenceIdAndSopTypeOrderByIdDesc(
											lastSharedPendingRecord.getApiCallReferenceId(),
											SOPType.VILLAGE_WISE_CROP_DATA.toString());
							if (!acknowledgementList.isEmpty()
									&& acknowledgementList.get(0).getStatus().equals(StatusEnum.PENDING.getValue())) {
								apiRefereceId = lastSharedPendingRecord.getApiCallReferenceId();
								SOPAcknowledgement acknowledgement = acknowledgementList.get(0);
								acknowledgement.setStatus(StatusEnum.FAILED.getValue());
								sopAcknowledgementRepository.save(acknowledgement);
							}
							page = lastSharedPendingRecord.getPage();
							villageWiseCropDataLog.setReferenceId(lastSharedPendingRecord.getReferenceId());
						}
						String result = centralCoreAPIRepository.getCropSownDetailsCentralCount();
						ObjectMapper jacksonObjMapper = new ObjectMapper();
						JsonNode jsonNode = jacksonObjMapper.readTree(result);
						totalRecords = jsonNode.get("totalData").intValue();
					} else if (previouslySharedList.get(0).getPendingRecords().equals(0)) {
						String result = centralCoreAPIRepository.getCropSownDetailsCentralCount();

						ObjectMapper jacksonObjMapper = new ObjectMapper();
						JsonNode jsonNode = jacksonObjMapper.readTree(result);
						totalRecords = jsonNode.get("totalData").intValue();
						page = 0;
					}
				} else {
					String result = centralCoreAPIRepository.getCropSownDetailsCentralCount();

					ObjectMapper jacksonObjMapper = new ObjectMapper();
					JsonNode jsonNode = jacksonObjMapper.readTree(result);
					totalRecords = jsonNode.get("totalData").intValue();
				}

				// find limit of sharing data
				Optional<ConfigurationMaster> op = configurationRepository
						.findByIsActiveTrueAndIsDeletedFalseAndConfigCode(
								ConfigCode.CENTRAL_CORE_DATA_SHARING_LIMIT);
				if (op.isPresent()) {
					ConfigurationMaster configurationMaster = op.get();
					if (configurationMaster.getConfigValue() != null) {
						countNumber = Integer.parseInt(configurationMaster.getConfigValue());
					}
				}

				// get records to be shared
				// String response =
				// centralCoreAPIRepository.getCropSownDetailsCentralData(page,countNumber);
				String response = centralCoreAPIRepository.getCropSownDetailsCentralDataV2(page, countNumber,
						apiRefereceId);

				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

				TypeFactory typeFactory = mapper.getTypeFactory();
				List<VillageWiseCropSurveyDetail> resultObject = mapper.readValue(response,
						typeFactory.constructCollectionType(List.class, VillageWiseCropSurveyDetail.class));

				List<VillageWiseSurveyDetailDAO> outputList = new ArrayList<>();

				String finalApiRefereceId = apiRefereceId;
				resultObject.forEach(record -> {
					// record.setReferenceId(finalApiRefereceId);
					VillageWiseSurveyDetailDAO dao = new VillageWiseSurveyDetailDAO();
					BeanUtils.copyProperties(record, dao);
					BigDecimal area = new BigDecimal(record.getSownAreaStr());
					dao.setSownArea(area);
					dao.setNoOfFarmer(record.getNoOfFarmers());
					// dao.setReferenceId(record.getReferenceId());
					outputList.add(dao);
					// if(!Objects.isNull(record.getReferenceId())) {
					// if(Objects.nonNull(record.getPreviousReferenceId()) &&
					// !record.getPreviousReferenceId().contains(record.getReferenceId())) {
					// StringBuilder stringBuilder = new
					// StringBuilder().append(record.getPreviousReferenceId()).append(",").append(record.getReferenceId());
					// record.setPreviousReferenceId(stringBuilder.toString());
					// } else {
					// record.setPreviousReferenceId(record.getReferenceId());
					// }
					// }
					// villageWiseCropDetailRepository.save(record);
				});
				// villageWiseCropDetailRepository.saveAll(resultObject);

				if (page == 0) {
					startSerialNumber = 1;
					if (totalRecords <= resultObject.size()) {
						pendingRecords = 0;
						endSerialNumber = resultObject.size();
					} else {
						pendingRecords = totalRecords - countNumber;
						endSerialNumber = countNumber;
					}
				} else if (page != 0) {
					startSerialNumber = (countNumber * page) + 1;
					endSerialNumber = (startSerialNumber + resultObject.size() - 1);
					if (endSerialNumber <= totalRecords) {
						pendingRecords = totalRecords - endSerialNumber;
						// endSerialNumber = (startSerialNumber + resultObject.size());

					} else {
						// endSerialNumber = (startSerialNumber + countNumber -1);
						pendingRecords = 0;
					}
				}
				// resultObject.forEach(record -> record.set);
				villageWiseCropDataLog.setTotalRecords(totalRecords);
				villageWiseCropDataLog.setPendingRecords(pendingRecords.intValue());
				villageWiseCropDataLog.setPage(page);
				villageWiseCropDataLog.setApiCallReferenceId(apiRefereceId);
				villageWiseCropDataLog.setStartSerialNumber(startSerialNumber.longValue());
				villageWiseCropDataLog.setEndSerialNumber(endSerialNumber.longValue());
				villageWiseCropDataLog.setSharedLimit(countNumber);
				villageWiseCropDataLog.setTotalRecords(totalRecords);
				villageWiseCropDataLog.setIsAcknowledged(StatusEnum.PENDING.getValue());
				villageWiseCropDataLog.setStartingNumber(resultObject.get(0).getRecordId());
				villageWiseCropDataLog.setEndingNumber(resultObject.get(resultObject.size() - 1).getRecordId());
				villageWiseCropDataLogRepository.save(villageWiseCropDataLog);

				SOPAcknowledgement acknowledgement = new SOPAcknowledgement();
				acknowledgement.setReferenceId(apiRefereceId);
				acknowledgement.setStatus(StatusEnum.PENDING.getValue());
				acknowledgement.setSopType(SOPType.VILLAGE_WISE_CROP_DATA.toString());
				acknowledgement.setDateOfDataSharing(new Timestamp(new Date().getTime()));
				acknowledgement.setStartSerialNumber(startSerialNumber.longValue());
				acknowledgement.setEndSerialNumber(endSerialNumber.longValue());
				acknowledgement.setStartingNumber(resultObject.get(0).getRecordId());
				acknowledgement.setEndingNumber(resultObject.get(resultObject.size() - 1).getRecordId());
				acknowledgement.setTotalRecords(totalRecords.longValue());

				sopAcknowledgementRepository.save(acknowledgement);

				responseData = new HashMap();
				responseData.put("referenceId", apiRefereceId);
				responseData.put("surveyDetails", outputList);
				responseData.put("limit", countNumber);
				responseData.put("pending", pendingRecords);
				return responseData;
				// new ResponseModel(responseData, CustomMessages.GET_RECORD,
				// CustomMessages.GET_DATA_SUCCESS,
				// CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
			}
		} catch (Exception e) {
			e.printStackTrace();

			responseData.put("message", e.getMessage());
			return responseData;
			// CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
			// CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	public ResponseModel getCropSownGeometryDetails(HttpServletRequest request) {
		try {

			String userId = CustomMessages.getUserId(request, jwtTokenUtil);
			userId = (userId != null && !userId.isEmpty()) ? userId : "0";

			String response = centralCoreAPIRepository.getCropSownGeometryDetails();

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<Object> resultObject = mapper.readValue(response,
					typeFactory.constructCollectionType(List.class, Object.class));

			return new ResponseModel(resultObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	@Transactional
	public ResponseModel acknowledgeData(CentralCoreDAO centralRequest, HttpServletRequest request) {
		try {
			List<SOPAcknowledgement> acknowledgementList = sopAcknowledgementRepository
					.findByReferenceIdAndStartingNumberAndEndingNumberOrderByIdDesc(centralRequest.getReferenceId(),
							Long.valueOf(centralRequest.getRecordIdStart()),
							Long.valueOf(centralRequest.getRecordIdEnd()));

			if (acknowledgementList.isEmpty()) {
				ResponseModel responseModel = new ResponseModel();
				responseModel.setData(null);
				responseModel.setMessage("No record found for the given request");
				responseModel.setCode(404);
				responseModel.setStatus(CustomMessages.FAILED);
				responseModel.setMethod(CustomMessages.METHOD_POST);
				return responseModel;
			}
			SOPAcknowledgement acknowledgement = acknowledgementList.get(0);
			if (acknowledgement.getStatus().equals(StatusEnum.SUCCESS.getValue())
					|| acknowledgement.getStatus().equals(StatusEnum.FAILED.getValue())) {
				ResponseModel responseModel = new ResponseModel();
				responseModel.setData(null);
				responseModel.setMessage("Already Acknowledged.");
				responseModel.setCode(409);
				responseModel.setStatus(CustomMessages.FAILED);
				responseModel.setMethod(CustomMessages.METHOD_POST);
				return responseModel;
			}
			if (acknowledgement.getSopType().equals(SOPType.VILLAGE_WISE_CROP_DATA.toString())) {
				switch (centralRequest.getStatus().toLowerCase()) {
					case "success":
						acknowledgement.setStatus(StatusEnum.SUCCESS.getValue());
						VillageWiseCropDataLog previouslySharedData = villageWiseCropDataLogRepository
								.findByApiCallReferenceId(centralRequest.getReferenceId());
						if (Objects.nonNull(previouslySharedData)
								&& Objects.nonNull(previouslySharedData.getReferenceId())) {
							previouslySharedData.setIsAcknowledged(StatusEnum.SUCCESS.getValue());
							if (previouslySharedData.getPendingRecords().equals(0)) {
								acknowledgeSharedData(previouslySharedData.getApiCallReferenceId());
							}
							villageWiseCropDataLogRepository.save(previouslySharedData);
						}
						break;
					case "fail":
						acknowledgement.setStatus(StatusEnum.FAILED.getValue());
						VillageWiseCropDataLog data = villageWiseCropDataLogRepository
								.findByApiCallReferenceId(centralRequest.getReferenceId());
						if (Objects.nonNull(data) && Objects.nonNull(data.getReferenceId())) {
							data.setIsAcknowledged(StatusEnum.PENDING.getValue());
							villageWiseCropDataLogRepository.save(data);
						}
						break;
				}
			} else if (acknowledgement.getSopType().equals(SOPType.GEO_REFERENCE.toString())) {
				switch (centralRequest.getStatus().toLowerCase()) {
					case "success":
						acknowledgement.setStatus(StatusEnum.SUCCESS.getValue());
						VillageWiseGeomapLog previouslySharedData = villageWiseGeomapLogRepository
								.findByApiCallReferenceId(centralRequest.getReferenceId());
						if (Objects.nonNull(previouslySharedData) && Objects.nonNull(previouslySharedData.getId())) {
							previouslySharedData.setIsAcknowledged(StatusEnum.SUCCESS.getValue());
							villageWiseGeomapLogRepository.save(previouslySharedData);
						}
						break;
					case "fail":
						acknowledgement.setStatus(StatusEnum.FAILED.getValue());
						VillageWiseGeomapLog data = villageWiseGeomapLogRepository
								.findByApiCallReferenceId(centralRequest.getReferenceId());
						if (Objects.nonNull(data) && Objects.nonNull(data.getId())) {
							data.setIsAcknowledged(StatusEnum.PENDING.getValue());
							villageWiseGeomapLogRepository.save(data);
						}
						break;
				}
			} else if (acknowledgement.getSopType().equals(SOPType.VILLAGE_MAP.toString())) {

			}

			sopAcknowledgementRepository.save(acknowledgement);
			return CustomMessages.makeResponseModel("Acknowledged", "Acknowledgement received successfully",
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	@Transactional
	private void acknowledgeSharedData(String referenceId) throws JsonProcessingException {
		VillageWiseCropDataLog previouslySharedData = villageWiseCropDataLogRepository
				.findByApiCallReferenceId(referenceId);
		if (Objects.nonNull(previouslySharedData) && Objects.nonNull(previouslySharedData.getReferenceId())) {
			if (previouslySharedData.getPendingRecords().equals(0)) {
				String response = centralCoreAPIRepository.acknowledgeSharedData(previouslySharedData.getPage(),
						previouslySharedData.getSharedLimit());
			}

		}

	}

	public String getTenDigitUniqueId() {
		Long LIMIT = 10000000000L;
		Long last = 0L;
		// 10 digits.
		long id = System.currentTimeMillis() % LIMIT;
		if (id <= last) {
			id = (last + 1) % LIMIT;
		}
		return String.valueOf(id);
	}

	/**
	 * @param request
	 * @return
	 */
	public Map<String, Object> getGeomapsDetails(HttpServletRequest request) {
		Map<String, Object> responseData = new HashMap();
		try {

			String userId = CustomMessages.getUserId(request, jwtTokenUtil);
			VillageWiseGeomapLog villageWiseGeomapLog = new VillageWiseGeomapLog();
			if (Objects.isNull(userId)) {
				responseData.put("message", CustomMessages.UNAUTHORIZED_MESSAGE);
				return responseData;
				// return new ResponseModel("Unauthorized request",
				// CustomMessages.UNAUTHORIZED_MESSAGE, CustomMessages.GET_DATA_ERROR,
				// CustomMessages.FAILED, CustomMessages.METHOD_POST);
			} else {

				// List<VillageWiseGeomapLog>
				// villageWiseGeomapDetailList=villageWiseGeomapLogRepository.findByIsAcknowledgedOrderByIdDesc(StatusEnum.PENDING.getValue());
				Long totalRecords = farmlandPlotRegistryRepository.countBy();
				Long limit = 500l;
				Long startSerialNumber = 0L;
				Long endSerialNumber = 0L;
				Long pendingRecords = 0L;
				Long page = 0l;
				String response = null;
				String apiReferenceId = getTenDigitUniqueId();
				;

				List<VillageWiseGeomapLog> previouslySharedList = villageWiseGeomapLogRepository
						.findAllByOrderByIdDesc();
				if (!previouslySharedList.isEmpty()) {
					if (previouslySharedList.get(0).getIsAcknowledged().equals(StatusEnum.SUCCESS.getValue())
							&& !previouslySharedList.get(0).getPendingRecords().equals(0)) {
						VillageWiseGeomapLog lastSharedRecord = villageWiseGeomapLogRepository
								.findLastSharedSuccessLog().orElse(null);
						page = Long.valueOf(lastSharedRecord.getPage().toString()) + 1;
						totalRecords = Long.valueOf(lastSharedRecord.getTotalRecords().toString());
					} else if (previouslySharedList.get(0).getIsAcknowledged().equals(StatusEnum.PENDING.getValue())) {
						VillageWiseGeomapLog lastSharedPendingRecord = villageWiseGeomapLogRepository
								.findLastSharedPendingLog().orElse(null);
						if (Objects.nonNull(lastSharedPendingRecord)
								&& lastSharedPendingRecord.getIsAcknowledged().equals(StatusEnum.PENDING.getValue())) {
							lastSharedPendingRecord.setIsAcknowledged(StatusEnum.FAILED.getValue());
							villageWiseGeomapLogRepository.save(lastSharedPendingRecord);
							List<SOPAcknowledgement> acknowledgementList = sopAcknowledgementRepository
									.findByReferenceIdAndSopTypeOrderByIdDesc(
											lastSharedPendingRecord.getApiCallReferenceId(),
											SOPType.GEO_REFERENCE.toString());
							if (!acknowledgementList.isEmpty()
									&& acknowledgementList.get(0).getStatus().equals(StatusEnum.PENDING.getValue())) {
								SOPAcknowledgement acknowledgement = acknowledgementList.get(0);
								acknowledgement.setStatus(StatusEnum.FAILED.getValue());
								sopAcknowledgementRepository.save(acknowledgement);
							}
							page = lastSharedPendingRecord.getPage();
							apiReferenceId = lastSharedPendingRecord.getApiCallReferenceId();
							villageWiseGeomapLog.setId(lastSharedPendingRecord.getId());
						}
					} else if (previouslySharedList.get(0).getPendingRecords().equals(0)) {
						page = 0L;
					}
				}

				// find limit of sharing data
				Optional<ConfigurationMaster> op = configurationRepository
						.findByIsActiveTrueAndIsDeletedFalseAndConfigCode(
								ConfigCode.CENTRAL_CORE_DATA_SHARING_LIMIT);
				if (op.isPresent()) {
					ConfigurationMaster configurationMaster = op.get();
					if (configurationMaster.getConfigValue() != null) {
						limit = Long.parseLong(configurationMaster.getConfigValue());
					}
				}

				// get records to be shared

				response = centralCoreAPIRepository.getGeomapsDetails(page, limit);
				Long landCountsForState = farmlandPlotRegistryRepository.geTotalPLotCountForState();
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

				TypeFactory typeFactory = mapper.getTypeFactory();
				List<Object> resultObject = mapper.readValue(response,
						typeFactory.constructCollectionType(List.class, Object.class));

				if (page == 0) {
					startSerialNumber = 1L;
					if (totalRecords <= resultObject.size()) {
						pendingRecords = 0L;
						endSerialNumber = Long.valueOf(resultObject.size());
					} else {
						pendingRecords = totalRecords - limit;
						endSerialNumber = limit;
					}
				} else if (page != 0) {
					startSerialNumber = (limit * page) + 1;
					endSerialNumber = (startSerialNumber + resultObject.size() - 1);
					if (endSerialNumber <= totalRecords) {
						pendingRecords = totalRecords - endSerialNumber;
						// endSerialNumber = (startSerialNumber + resultObject.size());

					} else {
						// endSerialNumber = (startSerialNumber + countNumber -1);
						pendingRecords = 0L;
					}
				}

				villageWiseGeomapLog.setTotalRecords(totalRecords);
				villageWiseGeomapLog.setPendingRecords(pendingRecords);
				villageWiseGeomapLog.setPage(page);
				villageWiseGeomapLog.setApiCallReferenceId(apiReferenceId);
				villageWiseGeomapLog.setStartSerialNumber(startSerialNumber.longValue());
				villageWiseGeomapLog.setEndSerialNumber(endSerialNumber.longValue());
				villageWiseGeomapLog.setSharedLimit(limit);
				villageWiseGeomapLog.setTotalRecords(totalRecords);
				villageWiseGeomapLog.setIsAcknowledged(StatusEnum.PENDING.getValue());
				villageWiseGeomapLog.setStartingNumber(
						Long.valueOf((Integer) ((LinkedHashMap) resultObject.get(0)).get("recordId")));
				villageWiseGeomapLog.setEndingNumber(Long.valueOf(
						(Integer) ((LinkedHashMap) resultObject.get(resultObject.size() - 1)).get("recordId")));
				villageWiseGeomapLogRepository.save(villageWiseGeomapLog);

				SOPAcknowledgement acknowledgement = new SOPAcknowledgement();
				acknowledgement.setReferenceId(apiReferenceId);
				acknowledgement.setStatus(StatusEnum.PENDING.getValue());
				acknowledgement.setSopType(SOPType.GEO_REFERENCE.toString());
				acknowledgement.setDateOfDataSharing(new Timestamp(new Date().getTime()));
				acknowledgement.setStartSerialNumber(startSerialNumber.longValue());
				acknowledgement.setEndSerialNumber(endSerialNumber.longValue());
				acknowledgement.setTotalRecords(totalRecords.longValue());
				acknowledgement.setStartingNumber(
						Long.valueOf((Integer) ((LinkedHashMap) resultObject.get(0)).get("recordId")));
				acknowledgement.setEndingNumber(Long.valueOf(
						(Integer) ((LinkedHashMap) resultObject.get(resultObject.size() - 1)).get("recordId")));
				sopAcknowledgementRepository.save(acknowledgement);

				responseData.put("referenceId", apiReferenceId);
				responseData.put("numberOfSurveyNumbers", landCountsForState);
				responseData.put("geomaps", resultObject);
				responseData.put("limit", limit);
				responseData.put("pending", pendingRecords);
				return responseData;
			}
		} catch (Exception e) {
			e.printStackTrace();
			responseData.put("message", e.getMessage());
			return responseData;
			// return CustomMessages.makeResponseModel(e.getMessage(),
			// CustomMessages.FAILURE,
			// CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	public ResponseModel getCropSownLog(HttpServletRequest request) {
		try {
			// List<VillageWiseCropDataLog> logList = new ArrayList<>();
			String dataString = villageWiseCropDataLogRepository.findVillageWiseSharedSurveyLog();

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<Object> resultObject = mapper.readValue(dataString,
					typeFactory.constructCollectionType(List.class, Object.class));
			return CustomMessages.makeResponseModel(resultObject, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	public ResponseModel getGeoMapSharedLog(HttpServletRequest request) {
		try {
			String dataString = villageWiseGeomapLogRepository.findGeoMapSharedLog();

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<Object> resultObject = mapper.readValue(dataString,
					typeFactory.constructCollectionType(List.class, Object.class));
			return CustomMessages.makeResponseModel(resultObject, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	public ResponseModel getSownDataSharedDataByReferenceId(String referenceId, HttpServletRequest request) {
		try {
			List<VillageWiseCropSurveyDetail> dataSetList = new ArrayList<>();
			List<VillageWiseCropSurveyDetailDAO> outputList = new ArrayList<>();
			VillageWiseCropDataLog data = villageWiseCropDataLogRepository.findByApiCallReferenceId(referenceId);
			dataSetList = villageWiseCropDetailRepository.getNativeData(data.getStartingNumber(),
					data.getEndingNumber());
			dataSetList.forEach(ele -> {
				VillageWiseCropSurveyDetailDAO dao = new VillageWiseCropSurveyDetailDAO();
				BeanUtils.copyProperties(ele, dao);
				outputList.add(dao);
			});
			return CustomMessages.makeResponseModel(outputList, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	public ResponseModel getGeoMapSharedDataByReferenceId(String referenceId, HttpServletRequest request) {
		try {
			VillageWiseGeomapLog data = villageWiseGeomapLogRepository.findByApiCallReferenceId(referenceId);
			String dataStr = farmlandPlotRegistryRepository.findLandDetailInRange(data.getStartingNumber(),
					data.getEndingNumber());
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<PlotLandDetailDAO> outputList = mapper.readValue(dataStr,
					typeFactory.constructCollectionType(List.class, PlotLandDetailDAO.class));
			return CustomMessages.makeResponseModel(outputList, CustomMessages.GET_RECORD,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}
}
