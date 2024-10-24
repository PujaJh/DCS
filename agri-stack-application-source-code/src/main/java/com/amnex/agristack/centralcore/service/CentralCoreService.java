package com.amnex.agristack.centralcore.service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.centralcore.Enum.MapperIdEnum;
import com.amnex.agristack.centralcore.dao.*;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.SowingSeason;
import com.amnex.agristack.repository.SeasonMasterRepository;
import com.amnex.agristack.service.CloudStorageService;
import com.amnex.agristack.service.MediaMasterService;
import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.amnex.agristack.centralcore.Enum.AckStatusEnum;
import com.amnex.agristack.centralcore.client.CentralServiceCall;
import com.amnex.agristack.centralcore.entity.OnSeekRequestData;
import com.amnex.agristack.centralcore.entity.TransactionMaster;
import com.amnex.agristack.centralcore.entity.TransactionRequestMapping;
import com.amnex.agristack.centralcore.exception.UnauthorizedException;
import com.amnex.agristack.centralcore.repository.OnSeekRequestRepository;
import com.amnex.agristack.centralcore.repository.TransactionMasterRepository;
import com.amnex.agristack.centralcore.repository.TransactionRequestMappingRepository;
import com.amnex.agristack.centralcore.util.AESUtil;
import com.amnex.agristack.centralcore.util.ECDSAVerfierFinal;
import com.amnex.agristack.utils.CommonUtil;
import com.amnex.agristack.utils.CustomMessages;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gson.Gson;
import org.springframework.core.io.Resource;

@Service
public class CentralCoreService {
	@Autowired
	private TransactionRequestMappingRepository transactionRequestMappingRepository;
	@Autowired
	private TransactionMasterRepository transactionMasterRepository;

	@Autowired
	private OnSeekRequestRepository onSeekRequestRepository;
	@Autowired
	RestTemplate restTemplate;
	@Autowired
	private CentralServiceCall centralServiceCall;
	@Value("${cc.header.user}")
	private String agristackUser;
	@Value("${cc.header.password}")
	private String agristackPassword;

	@Value("${nm.keycloak.endpoint}")
	private String nmEndpoint;

	@Value("${aiu.client.secret}")
	private String aiuClientSecret;

	@Value("${aiu.client.id}")
	private String aiuClientId;

	@Value("${aip.client.id}")
	private String aipClientId;

	@Value("${aip.nm.username}")
	private String aipUsername;

	@Value("${aip.nm.password}")
	private String aipPassword;

	@Autowired
	ECDSAVerfierFinal ecdsaVerfierFinal;

	@Value("${app.datastore.networktype}")
	private int datastoreType;

	@Autowired
	private MediaMasterService mediaMasterService;

	@Autowired
	private CloudStorageService cloudStorageService;

	@Autowired
	private SeasonMasterRepository seasonMasterRepository;

	public Map<String, Object> seek(HttpServletRequest request, TransactionRequestDAO transactionRequestDAO) {

		String requestTokenHeader = request.getHeader("Authorization");

		AIUTokenRequestDAO aiuTokenRequestDAO = new AIUTokenRequestDAO();
		aiuTokenRequestDAO.setOsId(transactionRequestDAO.getHeader().getSender_id());
		// aiuTokenRequestDAO.setToken(transactionRequestDAO.getRequestToken());
		aiuTokenRequestDAO.setToken(requestTokenHeader);

		// Boolean
		// isStatus=ccNetworkManagerService.validateTokenOfAIUFromSC(aiuTokenRequestDAO);
		
		Boolean isStatus = false;

		String resp = centralServiceCall.validateTokenOfAIUFromSC(agristackUser, agristackPassword, aiuTokenRequestDAO);
		JSONObject validateTokenResponse = new JSONObject(resp);
		if (validateTokenResponse != null && validateTokenResponse.has("data")) {
			JSONObject validateTokenResponseData = validateTokenResponse.getJSONObject("data");
			if (validateTokenResponseData != null && validateTokenResponseData.has("validationFlag")) {

				isStatus = validateTokenResponseData.getBoolean("validationFlag");
			}
		}

		List<TransactionRequestMapping> finalReferenceMappingList = new ArrayList<>();
		Map<String, Object> responseData = new HashMap();
		if (isStatus.equals(Boolean.FALSE)) {

//			transactionMaster.setAckStatus(AckStatusEnum.ERR.getValue());
//			transactionMasterRepository.save(transactionMaster);

			throw new UnauthorizedException("You are not authorized to access this resource.");
		} else {
		ResponseMessageDAO responseMessageDAO = new ResponseMessageDAO();
		
		TransactionMaster transactionMaster = new TransactionMaster();
		transactionMaster.setCreatedIp(CommonUtil.getRequestIp(request));
		transactionMaster.setMessageId(transactionRequestDAO.getHeader().getMessage_id());
		transactionMaster.setMessageTs(transactionRequestDAO.getHeader().getMessage_ts());

		transactionMaster.setSenderId(transactionRequestDAO.getHeader().getSender_id());

		transactionMaster.setSenderUri(transactionRequestDAO.getHeader().getSender_uri());
		transactionMaster.setReceiverId(transactionRequestDAO.getHeader().getReceiver_id());
		transactionMaster.setTotalCount(transactionRequestDAO.getHeader().getTotal_count());
		transactionMaster.setIsMsgEncrypted(transactionRequestDAO.getHeader().getIs_msg_encrypted());
		transactionMaster.setTransactionId(transactionRequestDAO.getMessage().getTransaction_id());

		transactionMaster.setAckStatus(AckStatusEnum.ERR.getValue());
		UUID uuid = UUID.randomUUID();
		transactionMaster.setCorrelationId(uuid.toString());
		transactionMaster.setVersion(transactionRequestDAO.getHeader().getVersion());
		transactionMaster.setAckTimestamp(transactionMaster.getCreatedOn());

		ErrorDAO error = new ErrorDAO();

		if (transactionRequestDAO.getMessage().getSearch_request() != null
				&& !transactionRequestDAO.getMessage().getSearch_request().isEmpty()) {

			List<TransactionRequestMapping> transactionRequestMappingList = new ArrayList<>();
			transactionRequestDAO.getMessage().getSearch_request().forEach(action -> {

				TransactionRequestMapping transactionRequestMapping = new TransactionRequestMapping();
				transactionRequestMapping.setCreatedIp(CommonUtil.getRequestIp(request));
				transactionRequestMapping.setTransactionId(transactionRequestDAO.getMessage().getTransaction_id());

				transactionRequestMapping.setReferenceId(action.getReference_id());

				transactionRequestMapping.setTimeStamp(action.getTimestamp());

				transactionRequestMapping.setLocale(action.getLocale());

				transactionRequestMapping.setQueryType(action.getSearch_criteria().getQuery_type());

				transactionRequestMapping.setRegType(action.getSearch_criteria().getReg_type());

				transactionRequestMapping.setQueryName(action.getSearch_criteria().getQuery().getQuery_name());

				transactionRequestMapping.setMapperId(action.getSearch_criteria().getQuery().getMapper_id());

				transactionRequestMapping.setPageNumber(action.getSearch_criteria().getPagination().getPage_number());

				transactionRequestMapping.setPageSize(action.getSearch_criteria().getPagination().getPage_size());

				ObjectMapper objectMapper = new ObjectMapper();
				try {
					if (action.getSearch_criteria().getQuery() != null
							&& action.getSearch_criteria().getQuery().getQuery_params() != null
					// && !action.getSearch_criteria().getQuery().getQuery_params().isEmpty()
					) {

						String queryParamsString = objectMapper
								.writeValueAsString(action.getSearch_criteria().getQuery().getQuery_params());
						transactionRequestMapping.setQueryParams(queryParamsString);

					}
					if (action.getSearch_criteria().getSort() != null
							&& !action.getSearch_criteria().getSort().isEmpty()) {
						String sortString = objectMapper.writeValueAsString(action.getSearch_criteria().getSort());
						transactionRequestMapping.setSort(sortString);
					}
					if (action.getSearch_criteria().getConsent() != null
					// && !action.getSearch_criteria().getConsent().isEmpty()
					) {

						String consentString = objectMapper
								.writeValueAsString(action.getSearch_criteria().getConsent());
						transactionRequestMapping.setConsentArtifect(consentString);
						ObjectMapper mapper = new ObjectMapper();
						mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

						TypeFactory typeFactory = mapper.getTypeFactory();
						ConsentAttributesDAO consentAttributesDAO = mapper.readValue(consentString,
								ConsentAttributesDAO.class);
						// consentAttributesDAO.getMain()
						if (consentAttributesDAO.getConsent_required() != null
								&& consentAttributesDAO.getConsent_required().equals(Boolean.TRUE)
								&& consentAttributesDAO.getMain() != null
								&& consentAttributesDAO.getMain().getAttributes() != null) {
							String attributesString = objectMapper
									.writeValueAsString(consentAttributesDAO.getMain().getAttributes());
							transactionRequestMapping.setAttributes(attributesString);
							JSONObject jsonRes = new JSONObject(attributesString);
							String assetArtifact = jsonRes.has("asset_artifact")
									? jsonRes.get("asset_artifact").toString()
									: null;

							transactionRequestMapping.setAssetArtifactDecoded(AESUtil.decrypt(assetArtifact));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					transactionMaster.setErrorCode("500");
					transactionMaster.setErrorMessage(e.getMessage());
					error.setCode("500");
					error.setMessage(e.getMessage());

				}

				transactionRequestMappingList.add(transactionRequestMapping);
			});
			finalReferenceMappingList = transactionRequestMappingRepository.saveAll(transactionRequestMappingList);

		}
		transactionMasterRepository.save(transactionMaster);
		Boolean isDataFound = Boolean.FALSE;
		if (finalReferenceMappingList != null) {
			List<String> referenceIdList = finalReferenceMappingList.stream().map(p -> p.getReferenceId())
					.collect(Collectors.toList());

			for(String referenceId: referenceIdList){

				String response = transactionRequestMappingRepository.fetchDataOnSeekForCount(referenceId);
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				OnSeekCommonResponseDAO resultObject = null;
                try {
                    resultObject = mapper.readValue(response, OnSeekCommonResponseDAO.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                Long totalCount = resultObject.getTotal_count();
				if (totalCount > 0){
					isDataFound = Boolean.TRUE;
					break;
				}
			}


			new Thread() {
				@Override
				public void run() {
					try {
						referenceIdList.forEach((refId) -> {
							generateRequestAndSendToSeeker(refId, transactionRequestDAO.getHeader().getSender_uri());
						});

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
		}

//		Boolean isStatus = false;
//
//		String resp = centralServiceCall.validateTokenOfAIUFromSC(agristackUser, agristackPassword, aiuTokenRequestDAO);
//		JSONObject validateTokenResponse = new JSONObject(resp);
//		if (validateTokenResponse != null && validateTokenResponse.has("data")) {
//			JSONObject validateTokenResponseData = validateTokenResponse.getJSONObject("data");
//			if (validateTokenResponseData != null && validateTokenResponseData.has("validationFlag")) {
//
//				isStatus = validateTokenResponseData.getBoolean("validationFlag");
//			}
//		}
//		if (isStatus.equals(Boolean.FALSE)) {
//
//			transactionMaster.setAckStatus(AckStatusEnum.ERR.getValue());
//			transactionMasterRepository.save(transactionMaster);
//
//			throw new UnauthorizedException("You are not authorized to access this resource.");
//		} else {
//			transactionMaster.setAckStatus(AckStatusEnum.ACK.getValue());
//			transactionMasterRepository.save(transactionMaster);
//			responseMessageDAO.setAckStatus(AckStatusEnum.ACK.getValue());
//			responseMessageDAO.setTimestamp(transactionMaster.getCreatedOn());
//			responseMessageDAO.setError(error);
//			responseMessageDAO.setCorrelationId(transactionMaster.getCorrelationId());
//			responseData.put("message", responseMessageDAO);
//		}


			if (Boolean.FALSE.equals(isDataFound)) {
				transactionMaster.setErrorCode("400");
				transactionMaster.setErrorMessage("data not found");
				error.setCode("400");
				transactionMaster.setAckStatus(AckStatusEnum.NACK.getValue());
				transactionMasterRepository.save(transactionMaster);
				responseMessageDAO.setAckStatus(AckStatusEnum.NACK.getValue());
				responseMessageDAO.setTimestamp(transactionMaster.getCreatedOn());
				error.setMessage("data not found");
            }else {
				transactionMaster.setAckStatus(AckStatusEnum.ACK.getValue());
				transactionMasterRepository.save(transactionMaster);
				responseMessageDAO.setAckStatus(AckStatusEnum.ACK.getValue());
				responseMessageDAO.setTimestamp(transactionMaster.getCreatedOn());
            }
            responseMessageDAO.setError(error);
            responseMessageDAO.setCorrelationId(transactionMaster.getCorrelationId());
            responseData.put("message", responseMessageDAO);
        }

		return responseData;

	}

	public Object onSeek(HttpServletRequest request, TransactionRequestDAO transactionRequestDAO) {
		try {
			Object onSeekRes = generateRequestForOnSeek(transactionRequestDAO.getReferenceId());
			return onSeekRes;
		} catch (Exception e) {
			Map<String, Object> responseData = new HashMap<>();
			ErrorDAO error = new ErrorDAO();
			error.setCode("500");
			error.setCode(e.getMessage());
			responseData.put("error", error);
			return responseData;
		}
	}

	private Object generateRequestAndSendToSeeker(String referenceId, String seekerURL) {
		TransactionResponseDAO requestOnSeek = generateRequestForOnSeek(referenceId);

		try {

			RestTemplate restTemplate = new RestTemplate();

			HttpHeaders headers = new HttpHeaders();
			// headers.add("Authorization", aipToken.getToken_type() + " " +
			// aipToken.getAccess_token());
			headers.add("user-agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");

			HttpEntity<TransactionResponseDAO> entity = new HttpEntity<TransactionResponseDAO>(requestOnSeek, headers);

			ResponseEntity<String> responseEntity = restTemplate.postForEntity(seekerURL, entity, String.class);

			return responseEntity.getBody();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private Object generateRequestAndSendToSeekerV2(String referenceId, String seekerURL,
			AIPTokenResponseDAO aipToken) {
		TransactionResponseDAO requestOnSeek = generateRequestForOnSeek(referenceId);

		try {

			RestTemplate restTemplate = new RestTemplate();

			HttpHeaders headers = new HttpHeaders();
			// headers.add("Authorization", aipToken.getToken_type() + " " +
			// aipToken.getAccess_token());
			HttpEntity<TransactionResponseDAO> entity = new HttpEntity<TransactionResponseDAO>(requestOnSeek, headers);

			ResponseEntity<String> responseEntity = restTemplate.postForEntity(seekerURL, entity, String.class);

			return responseEntity.getBody();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public TransactionResponseDAO generateRequestForOnSeek(String referenceId) {

		try {
			TransactionRequestMapping transactionRequestMapping = transactionRequestMappingRepository
					.findByReferenceId(referenceId);

			TransactionMaster transactionMaster = transactionMasterRepository
					.findByTransactionId(transactionRequestMapping.getTransactionId());

			String response = transactionRequestMappingRepository.fetchDataOnSeek(referenceId);

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			OnSeekCommonResponseDAO resultObject = mapper.readValue(response, OnSeekCommonResponseDAO.class);

			Long totalCount = resultObject.getTotal_count();

			Object resData = mapper.readValue(resultObject.getData(), Object.class);


			//get crop photo byte array by crop photo name
			List<String> mapperIds = Arrays.stream(MapperIdEnum.values()).map(MapperIdEnum::getValue).collect(Collectors.toList());
			if (mapperIds.contains(transactionRequestMapping.getMapperId())) {
				resData = getCropPhotoByteArray(transactionRequestMapping, mapper, resultObject, resData);
			}

			TransactionResponseDAO responseDAO = new TransactionResponseDAO();
			HeaderDAO headerDAO = new HeaderDAO();

			headerDAO.setVersion(transactionMaster.getVersion());
			headerDAO.setMessage_id(transactionMaster.getMessageId());
			headerDAO.setMessage_ts(transactionMaster.getMessageTs());
			headerDAO.setAction("on-seek");
			headerDAO.setStatus("succ");
			headerDAO.setStatus_reason_code("NA");
			headerDAO.setStatus_reason_message("null");
			headerDAO.setTotal_count(1);
			headerDAO.setCompleted_count(1);
			headerDAO.setSender_id("afa7fb88-a2cd-499e-8aef-cab87bd19015");
			headerDAO.setReceiver_id(transactionMaster.getSenderId());
			headerDAO.setIs_msg_encrypted(false);

			responseDAO.setHeader(headerDAO);

			SearchResponseDAO searchResDAO = new SearchResponseDAO();
			SearchResponseDataDAO searchResponseDataDAO = new SearchResponseDataDAO();

			searchResponseDataDAO.setReg_record_type(transactionRequestMapping.getQueryName());
			searchResponseDataDAO.setReg_records(resData);

			searchResDAO.setReference_id(transactionRequestMapping.getReferenceId());
			searchResDAO.setData(searchResponseDataDAO);

			List<SearchResponseDAO> searchResDAOList = new ArrayList<SearchResponseDAO>();
			searchResDAOList.add(searchResDAO);

			MessageDAO messageDAO = new MessageDAO();
			messageDAO.setTransaction_id(transactionMaster.getTransactionId());
			messageDAO.setCorrelation_id(transactionMaster.getCorrelationId());
			messageDAO.setSearch_response(searchResDAOList);
			messageDAO.setLocale("en");

			PaginationCentralDAO paginationCentralDAO = new PaginationCentralDAO();
			paginationCentralDAO.setPage_number(transactionRequestMapping.getPageNumber());
			paginationCentralDAO.setPage_size(transactionRequestMapping.getPageSize());
			paginationCentralDAO.setTotal_count(totalCount);

			messageDAO.setPagination(paginationCentralDAO);

			responseDAO.setMessage(messageDAO);
			return responseDAO;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public Object seekerOnSeek(HttpServletRequest request, TransactionResponseDAO seekerDAO) {
		try {
			Gson gson = new Gson();

			OnSeekRequestData onSeekRequestData = new OnSeekRequestData();
			onSeekRequestData.setReferenceId(seekerDAO.getMessage().getSearch_response().get(0).getReference_id());

			onSeekRequestData.setData(gson.toJson(seekerDAO));

			onSeekRequestRepository.save(onSeekRequestData);
			return new ResponseModel(null, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED, CustomMessages.METHOD_POST);
		}
	}

	public ResponseModel getResponseByRefId(HttpServletRequest request, String refId) {
		try {
			OnSeekRequestData reqData = onSeekRequestRepository.findByReferenceId(refId);
			if (reqData != null) {

				Gson gson = new Gson();
				TransactionResponseDAO transactionResponseDAO = gson.fromJson(reqData.getData(),
						TransactionResponseDAO.class);
				Object resData = transactionResponseDAO.getMessage().getSearch_response().get(0).getData()
						.getReg_records();
				ObjectMapper objectMapper = new ObjectMapper();
				// String json = objectMapper.writeValueAsString(resData);
				// List<RegRecordsResponseDAO> customList = objectMapper.readValue(json, new
				// TypeReference<List<RegRecordsResponseDAO>>() {});
				//
				//
				// customList=checkConsent(refId,customList);

				return new ResponseModel(resData, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
						CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
			} else {
				return new ResponseModel(null, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_ERROR,
						CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED, CustomMessages.METHOD_POST);
		}
	}

	List<RegRecordsResponseDAO> checkConsent(String referenceId, List<RegRecordsResponseDAO> customList) {
		List<RegRecordsResponseDAO> resultCustomList = new ArrayList<>();
		TransactionRequestMapping transactionRequestMapping = transactionRequestMappingRepository
				.findByReferenceId(referenceId);
		if (transactionRequestMapping != null && transactionRequestMapping.getConsentArtifect() != null) {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();

			try {
				// ConsentDAO resultObject =
				// mapper.readValue(transactionRequestMapping.getConsentArtifect(),
				// ConsentDAO.class);
				List<ConsentDAO> resultObject = mapper.readValue(transactionRequestMapping.getConsentArtifect(),
						new TypeReference<List<ConsentDAO>>() {
						});

				resultObject.forEach(result -> {
					if (result.getMain() != null && result.getMain().getAttributes() != null
							&& result.getMain().getAttributes().getCropRegAttributes() != null
							&& !result.getMain().getAttributes().getCropRegAttributes().isEmpty()) {

						List<String> cropRegAttributesList = result.getMain().getAttributes().getCropRegAttributes()
								.stream().map(action -> action.get(0)).collect(Collectors.toList());
						if (cropRegAttributesList != null && cropRegAttributesList.size() > 0) {
							customList.forEach(action -> {

								RegRecordsResponseDAO regRecordsResponseDAO = new RegRecordsResponseDAO();
								cropRegAttributesList.forEach(action2 -> {
									switch (action2) {
										case "farm_id":
											regRecordsResponseDAO.setFarm_id(action.getFarm_id());
											break;
										case "year":
											regRecordsResponseDAO.setYearString(action.getYearString());
											break;
										case "season":
											regRecordsResponseDAO.setSeason(action.getSeason());

											break;
										case "season_id":
											regRecordsResponseDAO.setSeason_id(action.getSeason_id());
											break;
										case "crop_name":
											regRecordsResponseDAO.setCrop_name(action.getCrop_name());
											break;
										case "sown_area":
											regRecordsResponseDAO.setSown_area(action.getSown_area());
											break;
										case "sown_area_unit":
											regRecordsResponseDAO.setSown_area_unit(action.getSown_area_unit());
											break;

									}
								});
								resultCustomList.add(regRecordsResponseDAO);
							});
						}

					}
					// else {
					// return customList;
					// }
				});
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
			return resultCustomList;
		} else {
			return customList;
		}
	}

	public Object seekv2(HttpServletRequest request, TransactionRequestDAO transactionRequestDAO) {
		String requestTokenHeader = request.getHeader("Authorization");
		AIPTokenResponseDAO aipToken = getTokenForAIP();
		AIUTokenRequestDAO aiuTokenRequestDAO = new AIUTokenRequestDAO();
		aiuTokenRequestDAO.setToken(requestTokenHeader);
		AIUIntroSpectResponseDAO aiuIntroSpectResponseDAO = getAIUDetailsFromToken(aipToken, aiuTokenRequestDAO);
		if (aiuIntroSpectResponseDAO != null && aiuIntroSpectResponseDAO.getActive() == false) {
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}

		List<TransactionRequestMapping> finalReferenceMappingList = new ArrayList<>();
		ResponseMessageDAO responseMessageDAO = new ResponseMessageDAO();
		Map<String, Object> responseData = new HashMap();
		TransactionMaster transactionMaster = new TransactionMaster();
		transactionMaster.setCreatedIp(CommonUtil.getRequestIp(request));
		transactionMaster.setMessageId(transactionRequestDAO.getHeader().getMessage_id());
		transactionMaster.setMessageTs(transactionRequestDAO.getHeader().getMessage_ts());

		transactionMaster.setSenderId(transactionRequestDAO.getHeader().getSender_id());

		transactionMaster.setSenderUri(transactionRequestDAO.getHeader().getSender_uri());
		transactionMaster.setReceiverId(transactionRequestDAO.getHeader().getReceiver_id());
		transactionMaster.setTotalCount(transactionRequestDAO.getHeader().getTotal_count());
		transactionMaster.setIsMsgEncrypted(transactionRequestDAO.getHeader().getIs_msg_encrypted());
		transactionMaster.setTransactionId(transactionRequestDAO.getMessage().getTransaction_id());

		transactionMaster.setAckStatus(AckStatusEnum.ERR.getValue());
		UUID uuid = UUID.randomUUID();
		transactionMaster.setCorrelationId(uuid.toString());
		transactionMaster.setVersion(transactionRequestDAO.getHeader().getVersion());
		transactionMaster.setAckTimestamp(transactionMaster.getCreatedOn());

		ErrorDAO error = new ErrorDAO();

		if (transactionRequestDAO.getMessage().getSearch_request() != null
				&& !transactionRequestDAO.getMessage().getSearch_request().isEmpty()) {

			List<TransactionRequestMapping> transactionRequestMappingList = new ArrayList<>();
			transactionRequestDAO.getMessage().getSearch_request().forEach(action -> {

				TransactionRequestMapping transactionRequestMapping = new TransactionRequestMapping();
				transactionRequestMapping.setCreatedIp(CommonUtil.getRequestIp(request));
				transactionRequestMapping.setTransactionId(transactionRequestDAO.getMessage().getTransaction_id());

				transactionRequestMapping.setReferenceId(action.getReference_id());

				transactionRequestMapping.setTimeStamp(action.getTimestamp());

				transactionRequestMapping.setLocale(action.getLocale());

				transactionRequestMapping.setQueryType(action.getSearch_criteria().getQuery_type());

				transactionRequestMapping.setRegType(action.getSearch_criteria().getReg_type());

				transactionRequestMapping.setQueryName(action.getSearch_criteria().getQuery().getQuery_name());

				transactionRequestMapping.setMapperId(action.getSearch_criteria().getQuery().getMapper_id());

				transactionRequestMapping.setPageNumber(action.getSearch_criteria().getPagination().getPage_number());

				transactionRequestMapping.setPageSize(action.getSearch_criteria().getPagination().getPage_size());

				ObjectMapper objectMapper = new ObjectMapper();
				try {
					if (action.getSearch_criteria().getQuery() != null
							&& action.getSearch_criteria().getQuery().getQuery_params() != null
					// && !action.getSearch_criteria().getQuery().getQuery_params().isEmpty()
					) {

						String queryParamsString = objectMapper
								.writeValueAsString(action.getSearch_criteria().getQuery().getQuery_params());
						transactionRequestMapping.setQueryParams(queryParamsString);

					}
					if (action.getSearch_criteria().getSort() != null
							&& !action.getSearch_criteria().getSort().isEmpty()) {
						String sortString = objectMapper.writeValueAsString(action.getSearch_criteria().getSort());
						transactionRequestMapping.setSort(sortString);
					}
					if (action.getSearch_criteria().getConsent() != null
					// && !action.getSearch_criteria().getConsent().isEmpty()
					) {

						String consentString = objectMapper
								.writeValueAsString(action.getSearch_criteria().getConsent());

						if (!validateACMSignature(consentString)) {
							return;
						}
						if (!validateEsignConsent(consentString)) {
							return;
						}

						transactionRequestMapping.setConsentArtifect(consentString);
						ObjectMapper mapper = new ObjectMapper();
						mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

						TypeFactory typeFactory = mapper.getTypeFactory();
						ConsentAttributesDAO consentAttributesDAO = mapper.readValue(consentString,
								ConsentAttributesDAO.class);
						// consentAttributesDAO.getMain()
						String attributesString = objectMapper
								.writeValueAsString(consentAttributesDAO.getMain().getAttributes());
						transactionRequestMapping.setAttributes(attributesString);
						JSONObject jsonRes = new JSONObject(attributesString);
						String assetArtifact = jsonRes.has("asset_artifact") ? jsonRes.get("asset_artifact").toString()
								: null;

						transactionRequestMapping.setAssetArtifactDecoded(AESUtil.decrypt(assetArtifact));

					}
				} catch (Exception e) {
					e.printStackTrace();
					transactionMaster.setErrorCode("500");
					transactionMaster.setErrorMessage(e.getMessage());
					error.setCode("500");
					error.setMessage(e.getMessage());

				}

				transactionRequestMappingList.add(transactionRequestMapping);
			});
			finalReferenceMappingList = transactionRequestMappingRepository.saveAll(transactionRequestMappingList);

		}
		transactionMasterRepository.save(transactionMaster);

		if (finalReferenceMappingList != null) {
			List<String> referenceIdList = finalReferenceMappingList.stream().map(p -> p.getReferenceId())
					.collect(Collectors.toList());

			new Thread() {
				@Override
				public void run() {
					try {
						referenceIdList.forEach((refId) -> {
							generateRequestAndSendToSeekerV2(refId, transactionRequestDAO.getHeader().getSender_uri(),
									aipToken);
						});

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
		}

		// Boolean isStatus = false;

		// String resp = centralServiceCall.validateTokenOfAIUFromSC(agristackUser,
		// agristackPassword, aiuTokenRequestDAO);
		// JSONObject validateTokenResponse = new JSONObject(resp);
		// if (validateTokenResponse != null && validateTokenResponse.has("data")) {
		// JSONObject validateTokenResponseData =
		// validateTokenResponse.getJSONObject("data");
		// if (validateTokenResponseData != null &&
		// validateTokenResponseData.has("validationFlag")) {

		// isStatus = validateTokenResponseData.getBoolean("validationFlag");
		// }
		// }
		// if (isStatus.equals(Boolean.FALSE)) {

		// transactionMaster.setAckStatus(AckStatusEnum.ERR.getValue());
		// transactionMasterRepository.save(transactionMaster);

		// throw new UnauthorizedException("You are not authorized to access this
		// resource.");
		// } else {
		// transactionMaster.setAckStatus(AckStatusEnum.ERR.getValue());
		// transactionMasterRepository.save(transactionMaster);
		responseMessageDAO.setAckStatus(AckStatusEnum.ACK.getValue());
		responseMessageDAO.setTimestamp(transactionMaster.getCreatedOn());
		// responseMessageDAO.setError(error);
		responseMessageDAO.setCorrelationId(transactionMaster.getCorrelationId());
		responseData.put("message", responseMessageDAO);
		// }
		return responseData;
	}

	public AIPTokenResponseDAO getTokenForAIP() {

		AIPTokenResponseDAO tokenResponse = new AIPTokenResponseDAO();
		try {
			RestTemplate restTemplate = new RestTemplate();
			String url = this.nmEndpoint + "/token";
			HttpHeaders headers = new HttpHeaders();
			MultiValueMap<String, String> map = new LinkedMultiValueMap();
			map.add("client_id", aipClientId);
			map.add("username", aipUsername);
			map.add("password", aipPassword);
			map.add("grant_type", "password");
			HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(map,
					headers);

			ResponseEntity<AIPTokenResponseDAO> responseEntity = restTemplate.postForEntity(url, entity,
					AIPTokenResponseDAO.class);
			tokenResponse = responseEntity.getBody();
			return tokenResponse;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tokenResponse;
	}

	private AIUIntroSpectResponseDAO getAIUDetailsFromToken(AIPTokenResponseDAO aipToken,
			AIUTokenRequestDAO aiuRequestDAO) {
		RestTemplate restTemplate = new RestTemplate();
		String url = nmEndpoint + "/token/introspect";
		HttpHeaders headers = new HttpHeaders();
		// headers.add("Authorization", aipToken.getToken_type() + " " +
		// aipToken.getAccess_token());
		MultiValueMap<String, String> map = new LinkedMultiValueMap();
		map.add("client_secret", aiuClientSecret);
		map.add("client_id", aiuClientId);
		map.add("token", aiuRequestDAO.getToken().replace("Bearer ", ""));

		HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(map, headers);

		ResponseEntity<AIUIntroSpectResponseDAO> responseEntity = restTemplate.postForEntity(url, entity,
				AIUIntroSpectResponseDAO.class);
		return responseEntity.getBody();

	}

	public Boolean validateACMToken(HttpServletRequest request) {
		String authToken = request.getHeader("authorization");
		String apiUrl = nmEndpoint + "/userinfo";
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", authToken);
		HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
		try {
			ResponseEntity<String> responseEntity = restTemplate.exchange(
					apiUrl, HttpMethod.GET, requestEntity, String.class);
			responseEntity.getHeaders().get("Authorization");
			return true;
		} catch (HttpClientErrorException e) {
			// 401 will throw error here
			return false;
		}
	}

	public Boolean validateACMSignature(String consent) {
		JsonNode jsonNode = convertStringToJsonNode(consent);
		ObjectNode newJson = JsonNodeFactory.instance.objectNode();
		newJson.set("meta", jsonNode.get("meta"));
		newJson.set("main", jsonNode.get("main"));
		newJson.set("userSignature", jsonNode.get("userSignature"));
		String signature = jsonNode.get("cmSignature").toString();
		String[] parts = signature.split("\\.");
		if (parts.length == 3) {
			String payload = decodeBase64(parts[1]);
			// Print the decoded information
			JsonNode decodeData = convertStringToJsonNode(payload);
			ObjectNode decodeDataJson = JsonNodeFactory.instance.objectNode();
			decodeDataJson.set("meta", decodeData.get("meta"));
			decodeDataJson.set("main", decodeData.get("main"));
			decodeDataJson.set("userSignature", decodeData.get("userSignature"));
			System.out.println(newJson.toString());
			System.out.println(decodeDataJson.toString());
			System.out.println(newJson.toString().equals(decodeDataJson.toString()));
			return newJson.toString().equals(decodeDataJson.toString());
		} else {
			System.out.println("Invalid JWT token format");
		}
		return false;
	}

	private static String decodeBase64(String base64String) {
		byte[] decodedBytes = Base64.getUrlDecoder().decode(base64String);
		return new String(decodedBytes);
	}

	public Boolean validateEsignConsent(String consent) throws JsonMappingException, JsonProcessingException {
		JsonNode jsonNode = convertStringToJsonNode(consent);
		ObjectNode newJson = JsonNodeFactory.instance.objectNode();
//		newJson.set("main", jsonNode.get("main"));
//		System.out.println("Raw Consent: " + consent);
//		System.out.println("Main Part: " + newJson.toString());
//		System.out.println("Hash: " +ECDSAVerfierFinal.generateHash(jsonNode.get("main").toString()));
		String userSignature = jsonNode.get("userSignature").toString()
				.substring(1, jsonNode.get("userSignature").toString().length()-1);
 		return ecdsaVerfierFinal.PKCS7Verifier(ECDSAVerfierFinal.generateHash(jsonNode.get("main").toString()),
				userSignature);
	}

//	public static void main(String[] args) throws JsonProcessingException {
////		validateEsignConsent("{\"meta\":{\"status\":\"ACTIVE\",\"timestamp\":\"2024-01-10T06:27:04.274Z\"},\\\"main\\\":{\\\"farmerId\\\":\\\"15130004480\\\",\\\"aipId\\\":\\\"437cee7f-40f4-4cb5-9ee4-0cd5421b3909\\\",\\\"aiuId\\\":\\\"35cf23fc-82c3-4870-b225-1782778ecf42\\\",\\\"purposeCode\\\":\\\"CREDIT_KCC\\\",\\\"validUntil\\\":\\\"2024-03-31\\\",\\\"attributes\\\":{\\\"dob\\\":\\\"GRANTED\\\",\\\"gender\\\":\\\"GRANTED\\\",\\\"farmer_name\\\":\\\"GRANTED\\\",\\\"asset_artifact\\\":\\\"sdYMyeTdksPRoR/jdlV5A6HPXA9/5ZcVUpLvy+BhfxBx8L7GOaGKOR95pcl+vwIairXjcozGd9LvUoAAI5Pj8041c7Q0WfT4qBPTGCUKhZzu5v71NvzDCy9a4dPaxxq4XUPZFhcNpeDGuxlb43OwIg==\\\",\\\"state_lgd_code\\\":\\\"GRANTED\\\"}},\"userSignature\":\"MIILDQYJKoZIhvcNAQcCoIIK/jCCCvoCAQExDzANBglghkgBZQMEAgEFADALBgkqhkiG9w0BBwGg\\r\\ngglyMIIDzzCCAregAwIBAgIJAMxt5h7OM6WEMA0GCSqGSIb3DQEBBQUAMH4xCzAJBgNVBAYTAklO\\r\\nMRQwEgYDVQQIDAtNYWhhcmFzaHRyYTENMAsGA1UEBwwEUFVORTEOMAwGA1UECgwFQy1EQUMxIjAg\\r\\nBgNVBAsMGVRlc3QgQ2VydGlmeWluZyBBdXRob3JpdHkxFjAUBgNVBAMMDVRlc3QgQy1EQUMgQ0Ew\\r\\nHhcNMTgwMTEwMTEzOTM1WhcNMjgwMTA4MTEzOTM1WjB+MQswCQYDVQQGEwJJTjEUMBIGA1UECAwL\\r\\nTWFoYXJhc2h0cmExDTALBgNVBAcMBFBVTkUxDjAMBgNVBAoMBUMtREFDMSIwIAYDVQQLDBlUZXN0\\r\\nIENlcnRpZnlpbmcgQXV0aG9yaXR5MRYwFAYDVQQDDA1UZXN0IEMtREFDIENBMIIBIjANBgkqhkiG\\r\\n9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzeJIAmzyhl49G+KfQPQmP5zg/Zoz6TDZImel43VklbKHRc4a\\r\\nWEAZR9Mp4pwsVXaWeDd+GWpBexzCv8KcBRat1Vv4ZyR7RgDwMJ8MSQkOkIo5oZ31sSnLlehbHC2d\\r\\nDUzOW66O1pzqFtvKyf6QIUxEpYRdn0bbLaZYOfHWKUW6LTCWRZ5S+HWilTaFI2aOIrG3Vg/Hf+3L\\r\\nQkJu4H7Urmr92Yjxd3Z7DKxVkjES4kexUe5PUMY5wmYfDC1PWOkv9GyKu1/sZEmQ+GUcUR/TNDnQ\\r\\noLbHvbmQaQ7TiyyiTCzY1kipAHOTs4YtSgdhLqqQK6jWe7WthGYPp0ejXCUg81bZeQIDAQABo1Aw\\r\\nTjAdBgNVHQ4EFgQUNdt2Xzx7JRnr58wo475nPP2MSQUwHwYDVR0jBBgwFoAUNdt2Xzx7JRnr58wo\\r\\n475nPP2MSQUwDAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQUFAAOCAQEATjibTJJGHMMNrU8u/D7D\\r\\nUe2VTBtRqprWvLTC9w825KKl/doPsP1Y1YTVS13U+do6SLGFEKd/rKwIvBwThuPUTXNOKocYfwrP\\r\\n8qC2RfcqVa7Xl1el3qg4bvVmZ+ST9GLBB5e5CAdY9yTDbYXOmMqv7DCN+1BRbq+AY520kZtpMWS4\\r\\nqoVdKMI9g3s3t59jgc89jRoXom5eszC8HfUiPZkHt4kaTZqKcUW2dFW7y3yVooB2CaCi5HHatN3E\\r\\nQn0tb3WQqvRL3ZkQ3mihVlXybvCCpSASTpW7MNqR05hM3d3+OybiHOih22+iceW69RGC12aU1yGN\\r\\nSavhQCQPky++Xx93MzCCBZswggSDoAMCAQICAwOe8TANBgkqhkiG9w0BAQsFADB+MQswCQYDVQQG\\r\\nEwJJTjEUMBIGA1UECAwLTWFoYXJhc2h0cmExDTALBgNVBAcMBFBVTkUxDjAMBgNVBAoMBUMtREFD\\r\\nMSIwIAYDVQQLDBlUZXN0IENlcnRpZnlpbmcgQXV0aG9yaXR5MRYwFAYDVQQDDA1UZXN0IEMtREFD\\r\\nIENBMB4XDTI0MDIxNjA4NTEzOFoXDTI0MDIxNjA5MjEzOFowggFEMQ4wDAYDVQQGEwVJbmRpYTEU\\r\\nMBIGA1UECBMLU3RhdGUgU2V2ZW4xETAPBgNVBAoTCFBlcnNvbmFsMRowGAYDVQQDExFQZXJzb24g\\r\\nU2V2ZW4gTmFtZTEPMA0GA1UEERMGOTAwMDA3MVIwUAYDVQQtA0kAOGg5ajBrMWEyYjNjNGQ1ZTZm\\r\\nN2c4aDlqMGsxYTJiM2M0ZDVlNmY3ZzhoOWowazEyMzQ1Njc4OTAxMjFhMmIzYzRkNWU2ZjdnMSkw\\r\\nJwYDVQRBEyBjNmQ0YTg1NGZlN2Q0YjUwYTQ5ODRmMjI3MDAxNjdmYTENMAsGA1UEDBMENjc4OTFO\\r\\nMEwGA1UELhNFMTk3N0ZiNTMzOTFiNGEzNGE1OTI2NGUxZGJkYTkxM2UyYjEzOThlYjdmNmU0ZDM2\\r\\nMWFlYTJkNTI2YTY0NTkxNzIwZjQyMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAESuNq3RmZ9zzn\\r\\n2yK95u+Cq1qmDZ5FuVeGzZSBX/Lz5zk8mMH4WXi9BjEp9xCFD/bHD50u4sARtzIoNGFwtDqQXaOC\\r\\nAiMwggIfMAkGA1UdEwQCMAAwHQYDVR0OBBYEFDu/fcDeRBiL0y3g97PQ0Ucq0LtQMB8GA1UdIwQY\\r\\nMBaAFA58oZXW2swg8yhPlL1306H0MIsWMA4GA1UdDwEB/wQEAwIGwDA5BgNVHR8EMjAwMC6gLKAq\\r\\nhihodHRwczovL2VzaWduLmNkYWMuaW4vY2EvZXNpZ25DQTIwMTQuY3JsMIIBPwYDVR0gBIIBNjCC\\r\\nATIwggEBBgdggmRkAQkCMIH1MDAGCCsGAQUFBwIBFiRodHRwczovL2VzaWduLmNkYWMuaW4vY2Ev\\r\\nQ1BTL0NQUy5wZGYwgcAGCCsGAQUFBwICMIGzMD4WOkNlbnRyZSBmb3IgRGV2ZWxvcG1lbnQgb2Yg\\r\\nQWR2YW5jZWQgQ29tcHV0aW5nIChDLURBQyksIFB1bmUwABpxVGhpcyBDUFMgaXMgb3duZWQgYnkg\\r\\nQy1EQUMgYW5kIHVzZXJzIGFyZSByZXF1ZXN0ZWQgdG8gcmVhZCBDUFMgYmVmb3JlIHVzaW5nIHRo\\r\\nZSBDLURBQyBDQSdzIGNlcnRpZmljYXRpb24gc2VydmljZXMwKwYHYIJkZAIEATAgMB4GCCsGAQUF\\r\\nBwICMBIaEEFhZGhhYXIgZUtZQy1PVFAwRAYIKwYBBQUHAQEEODA2MDQGCCsGAQUFBzAChihodHRw\\r\\nczovL2VzaWduLmNkYWMuaW4vY2EvQ0RBQy1DQTIwMTQuZGVyMA0GCSqGSIb3DQEBCwUAA4IBAQB5\\r\\nh7WiYJlOSSB4fa6leLXsUXIwk4a7brroWOxTHF50iq5br5/8eKCjbGrTKuzTchwdec6QkQVGKbUp\\r\\nP5ioX2aJ/ay6itQSgi9pjPnW2hB2YDzvq7oHJzMV1KJ2jR6FCTxzIjxHEUrowMTr+EjvDv2O0dPr\\r\\n77fNMwn7dCb7dqhljn5W/ZPYtknfob/unKz5cBmNAfEzUaSZKI6WukZvRQZVrS3esDRkC8yFYjdS\\r\\nQpB5V1vMdAZFRDTmzCc0sECX3lopHyYuVZSQQyjbO2mLLVYDyv4kWT2AdOtbzalqwaBJQh3dM9xz\\r\\nSJJOT4k+ZrZiDAi+uFT7rChgQybSsC3jkCmXMYIBXzCCAVsCAQEwgYUwfjELMAkGA1UEBhMCSU4x\\r\\nFDASBgNVBAgMC01haGFyYXNodHJhMQ0wCwYDVQQHDARQVU5FMQ4wDAYDVQQKDAVDLURBQzEiMCAG\\r\\nA1UECwwZVGVzdCBDZXJ0aWZ5aW5nIEF1dGhvcml0eTEWMBQGA1UEAwwNVGVzdCBDLURBQyBDQQID\\r\\nA57xMA0GCWCGSAFlAwQCAQUAoGkwGAYJKoZIhvcNAQkDMQsGCSqGSIb3DQEHATAcBgkqhkiG9w0B\\r\\nCQUxDxcNMjQwMjE2MDg1MTM4WjAvBgkqhkiG9w0BCQQxIgQgV4EWS0OLjfgCViE42+fON7mGbHRJ\\r\\n8ZRE42TfJFZzdAMwDAYIKoZIzj0EAwIFAARGMEQCIHh2ScPJPAf98Z4fsQpsA10ohAkPZ0+NTKIX\\r\\nuxZK2R3PAiBXa8dd1HG2APq8qhPHDraT8PVzFl7hcYZ9WvpK5FtT4g==\",\"cmSignature\":\"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImV4dHJhUGFyYW1zIjoiZXhhbXBsZSJ9.eyJtZXRhIjp7InN0YXR1cyI6IkFDVElWRSIsInRpbWVzdGFtcCI6IjIwMjQtMDEtMTBUMDY6Mjc6MDQuMjc0WiJ9LCJtYWluIjp7ImZhcm1lcklkIjoiMzc5NjQ0MTU0NDAiLCJhaXBJZCI6ImZhZTBjOTY4LTA1MGQtNDAzMy05YjE0LWJlZGIyNjM1NDM5MiIsImFpdUlkIjoiZjAxMzI1MWYtMTNkMS00NWExLTgzMTMtZTEzMzZjZWI3MGMyIiwicHVycG9zZUNvZGUiOiJDUkVESVRfS0NDIiwidmFsaWRVbnRpbCI6IjIwMjQtMDMtMzEiLCJhdHRyaWJ1dGVzIjp7ImRvYiI6Ik5PVC1HUkFOVEVEIiwiZ2VuZGVyIjoiTk9ULUdSQU5URUQiLCJmYXJtZXJfbmFtZSI6IkdSQU5URUQiLCJhc3NldF9hcnRpZmFjdCI6InNkWU15ZVRka3NQUm9SL2pkbFY1QXhhOU5wYm0vZmh5ZzFwZnU4Yi9aMlgvTnlqUXNYZEhxSVIydkV6MzBBYlA3c1o1amExTmlZaVNjbmQlu4z2LwW8BnR3Rqjyexa-GwKg60SnyTwh2SSbdW-gEz-xKqzqlsHgCMGZ_uWd-tX6j3WjBo6VuzmWqA8-eKwdSGQuP1wQKPUqDea_AFyY-PP7qrXLAAQr9LN-wm-FkZa-knymRoZUHiMXejhn2BmHuZYvpdQ6_T19j12aI5BbQgsiZQ--fu979aepkIhVb3YtmSIZzt5YVqxcLClnUc1w\"}");
////		System.out.println(validateEsignConsent("{\"meta\":{\"status\":\"ACTIVE\",\"timestamp\":\"2024-01-10T06:27:04.274Z\"},\"main\":{\"farmerId\":\"37964415440\",\"aipId\":\"fae0c968-050d-4033-9b14-bedb26354392\",\"aiuId\":\"f013251f-13d1-45a1-8313-e1336ceb70c2\",\"purposeCode\":\"CREDIT_KCC\",\"validUntil\":\"2024-03-31\",\"attributes\":{\"farmer_id\":\"GRANTED\",\"farmer_name\":\"GRANTED\",\"father_name\":\"NOT-GRANTED\",\"dob\":\"GRANTED\",\"gender\":\"GRANTED\",\"caste_category\":\"GRANTED\",\"pmkid\":\"NOT-GRANTED\",\"pan_number\":\"GRANTED\",\"pan_alt_id_proof\":\"NOT-GRANTED\",\"pan_alt_id_number\":\"NOT-GRANTED\",\"occupation\":\"GRANTED\",\"permanent_address\":\"GRANTED\",\"permanent_state_lgd_code\":\"GRANTED\",\"permanent_village_lgd_code\":\"GRANTED\",\"permanent_pincode\":\"GRANTED\",\"family_member_details\":\"GRANTED\",\"asset_artifact\":\"sdYMyeTdksPRoR/jdlV5Axa9Npbm/fhyg1pfu8b/Z2X/NyjQsXdHqIR2vEz30AbP7sZ5ja1NiYiScnmk1O9pC8GdQuK6/vR+J7crIO/fEp8=\",\"state_lgd_code\":\"NOT-GRANTED\"}},\"userSignature\":\"MIILCwYJKoZIhvcNAQcCoIIK/DCCCvgCAQExDzANBglghkgBZQMEAgEFADALBgkqhkiG9w0BBwGg\\r\\nggluMIIDzzRpZnlpbmcgQXV0aG9yaXQBgNVBC0DSQAzYzRkNWU2ZjdnOGg5ajBrMWEyYjNj\\r\\nNGQ1ZTZmN2c4aDlqMGsxYTJiM2M0ZDVlNmY3ZzhoOWowazEyMzQ1Njc4OTAxMjFhMmIxKTAnBgNV\\r\\nBEETIGM2ZDRhODU0ZmU3ZDRiNTBhNDk4NGYyMjcwMDE2N2ZhMQ0wCwY7yjZrrB4okyOs6DdwMsEPGuAjwAfPO\\r\\nr3ODLURcZDAMBggqhkjOPQQDAgUABEgwRgIhAM8BztlCc5aUKDaLsLJvR7zlddPZnaj6DgHgUQvE\\r\\nB7hEAiEA30mJF0g6yhAjE9ayHjQ3CC929GlN9VmRMVeI8WP8o3s=\",\"cmSignature\":\"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImV4dHJhUGFyYW1zIjoiZXhhbXBsZSJ9.eyJtZXRhIjp7InN0YXR1cyI6IkFDVElWRSIsInRpbWVzdGFtcCI6IjIwMjQtMDEtMTBUMDY6Mjc6MDQuMjc0WiJ9LCJtYWluIjp7ImZhcm1lcklkIjoiMzc5NjQ0MTU0NDAiLCJhaXBJZCI6ImZhZTBjOTY4LTA1MGQtNDAzMy05YjE0LWJlZGIyNjM1NDM5MiIsImFpdUlkIjoiZjAxMzI1MWYtMTNkMS00NWExLTgzMTMtZTEzMzZjZWI3MGMyIiwicHVycG9zZUNvZGUiOiJDUkVESVRfS0NDIiwidmFsaWRVbnRpbCI6IjIwMjQtMDMtMzEiLCJhdHRyaWJ1dGVzIjp7ImRvYiI6Ik5PVC1HUkFOVEVEIiwiZ2VuZGVyIjoiTk9ULUdSQU5URUQiLCJmYXJtZXJfbmFtZSI6IkdSQU5URUQiLCJhc3NldF9hcnRpZmFjdCI6InNkWU15ZVRka3NQUm9SL2pkbFY1QXhhOU5wYm0vZmh5ZzFwZnU4Yi9aMlgvTnlqUXNYZEhxSVIydkV6MzBBYlA3c1o1amExTmlZaVNjbmQlu4z2LwW8BnR3Rqjyexa-GwKg60SnyTwh2SSbdW-gEz-xKqzqlsHgCMGZ_uWd-tX6j3WjBo6VuzmWqA8-eKwdSGQuP1wQKPUqDea_AFyY-PP7qrXLAAQr9LN-wm-FkZa-knymRoZUHiMXejhn2BmHuZYvpdQ6_T19j12aI5BbQgsiZQ--fu979aepkIhVb3YtmSIZzt5YVqxcLClnUc1w\"}"));
//		validateEsignConsent("{\"meta\":{\"status\":\"ACTIVE\",\"timestamp\":\"2024-02-16T09:00:54.252Z\"},\"main\":{\"farmerId\":\"15130004480\",\"aipId\":\"437cee7f-40f4-4cb5-9ee4-0cd5421b3909\",\"aiuId\":\"35cf23fc-82c3-4870-b225-1782778ecf42\",\"purposeCode\":\"CREDIT_KCC\",\"validUntil\":\"2024-03-31\",\"attributes\":{\"dob\":\"GRANTED\",\"gender\":\"GRANTED\",\"farmer_name\":\"GRANTED\",\"asset_artifact\":\"sdYMyeTdksPRoR/jdlV5A6HPXA9/5ZcVUpLvy+BhfxBx8L7GOaGKOR95pcl+vwIairXjcozGd9LvUoAAI5Pj8041c7Q0WfT4qBPTGCUKhZzu5v71NvzDCy9a4dPaxxq4XUPZFhcNpeDGuxlb43OwIg==\",\"state_lgd_code\":\"GRANTED\"}},\"userSignature\":\"MIILDQYJKoZIhvcNAQcCoIIK/jCCCvoCAQExDzANBglghkgBZQMEAgEFADALBgkqhkiG9w0BBwGg\\r\\ngglyMIIDzzCCAregAwIBAgIJAMxt5h7OM6WEMA0GCSqGSIb3DQEBBQUAMH4xCzAJBgNVBAYTAklO\\r\\nMRQwEgYDVQQIDAtNYWhhcmFzaHRyYTENMAsGA1UEBwwEUFVORTEOMAwGA1UECgwFQy1EQUMxIjAg\\r\\nBgNVBAsMGVRlc3QgQ2VydGlmeWluZyBBdXRob3JpdHkxFjAUBgNVBAMMDVRlc3QgQy1EQUMgQ0Ew\\r\\nHhcNMTgwMTEwMTEzOTM1WhcNMjgwMTA4MTEzOTM1WjB+MQswCQYDVQQGEwJJTjEUMBIGA1UECAwL\\r\\nTWFoYXJhc2h0cmExDTALBgNVBAcMBFBVTkUxDjAMBgNVBAoMBUMtREFDMSIwIAYDVQQLDBlUZXN0\\r\\nIENlcnRpZnlpbmcgQXV0aG9yaXR5MRYwFAYDVQQDDA1UZXN0IEMtREFDIENBMIIBIjANBgkqhkiG\\r\\n9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzeJIAmzyhl49G+KfQPQmP5zg/Zoz6TDZImel43VklbKHRc4a\\r\\nWEAZR9Mp4pwsVXaWeDd+GWpBexzCv8KcBRat1Vv4ZyR7RgDwMJ8MSQkOkIo5oZ31sSnLlehbHC2d\\r\\nDUzOW66O1pzqFtvKyf6QIUxEpYRdn0bbLaZYOfHWKUW6LTCWRZ5S+HWilTaFI2aOIrG3Vg/Hf+3L\\r\\nQkJu4H7Urmr92Yjxd3Z7DKxVkjES4kexUe5PUMY5wmYfDC1PWOkv9GyKu1/sZEmQ+GUcUR/TNDnQ\\r\\noLbHvbmQaQ7TiyyiTCzY1kipAHOTs4YtSgdhLqqQK6jWe7WthGYPp0ejXCUg81bZeQIDAQABo1Aw\\r\\nTjAdBgNVHQ4EFgQUNdt2Xzx7JRnr58wo475nPP2MSQUwHwYDVR0jBBgwFoAUNdt2Xzx7JRnr58wo\\r\\n475nPP2MSQUwDAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQUFAAOCAQEATjibTJJGHMMNrU8u/D7D\\r\\nUe2VTBtRqprWvLTC9w825KKl/doPsP1Y1YTVS13U+do6SLGFEKd/rKwIvBwThuPUTXNOKocYfwrP\\r\\n8qC2RfcqVa7Xl1el3qg4bvVmZ+ST9GLBB5e5CAdY9yTDbYXOmMqv7DCN+1BRbq+AY520kZtpMWS4\\r\\nqoVdKMI9g3s3t59jgc89jRoXom5eszC8HfUiPZkHt4kaTZqKcUW2dFW7y3yVooB2CaCi5HHatN3E\\r\\nQn0tb3WQqvRL3ZkQ3mihVlXybvCCpSASTpW7MNqR05hM3d3+OybiHOih22+iceW69RGC12aU1yGN\\r\\nSavhQCQPky++Xx93MzCCBZswggSDoAMCAQICAwOe8TANBgkqhkiG9w0BAQsFADB+MQswCQYDVQQG\\r\\nEwJJTjEUMBIGA1UECAwLTWFoYXJhc2h0cmExDTALBgNVBAcMBFBVTkUxDjAMBgNVBAoMBUMtREFD\\r\\nMSIwIAYDVQQLDBlUZXN0IENlcnRpZnlpbmcgQXV0aG9yaXR5MRYwFAYDVQQDDA1UZXN0IEMtREFD\\r\\nIENBMB4XDTI0MDIxNjA4NTEzOFoXDTI0MDIxNjA5MjEzOFowggFEMQ4wDAYDVQQGEwVJbmRpYTEU\\r\\nMBIGA1UECBMLU3RhdGUgU2V2ZW4xETAPBgNVBAoTCFBlcnNvbmFsMRowGAYDVQQDExFQZXJzb24g\\r\\nU2V2ZW4gTmFtZTEPMA0GA1UEERMGOTAwMDA3MVIwUAYDVQQtA0kAOGg5ajBrMWEyYjNjNGQ1ZTZm\\r\\nN2c4aDlqMGsxYTJiM2M0ZDVlNmY3ZzhoOWowazEyMzQ1Njc4OTAxMjFhMmIzYzRkNWU2ZjdnMSkw\\r\\nJwYDVQRBEyBjNmQ0YTg1NGZlN2Q0YjUwYTQ5ODRmMjI3MDAxNjdmYTENMAsGA1UEDBMENjc4OTFO\\r\\nMEwGA1UELhNFMTk3N0ZiNTMzOTFiNGEzNGE1OTI2NGUxZGJkYTkxM2UyYjEzOThlYjdmNmU0ZDM2\\r\\nMWFlYTJkNTI2YTY0NTkxNzIwZjQyMFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAESuNq3RmZ9zzn\\r\\n2yK95u+Cq1qmDZ5FuVeGzZSBX/Lz5zk8mMH4WXi9BjEp9xCFD/bHD50u4sARtzIoNGFwtDqQXaOC\\r\\nAiMwggIfMAkGA1UdEwQCMAAwHQYDVR0OBBYEFDu/fcDeRBiL0y3g97PQ0Ucq0LtQMB8GA1UdIwQY\\r\\nMBaAFA58oZXW2swg8yhPlL1306H0MIsWMA4GA1UdDwEB/wQEAwIGwDA5BgNVHR8EMjAwMC6gLKAq\\r\\nhihodHRwczovL2VzaWduLmNkYWMuaW4vY2EvZXNpZ25DQTIwMTQuY3JsMIIBPwYDVR0gBIIBNjCC\\r\\nATIwggEBBgdggmRkAQkCMIH1MDAGCCsGAQUFBwIBFiRodHRwczovL2VzaWduLmNkYWMuaW4vY2Ev\\r\\nQ1BTL0NQUy5wZGYwgcAGCCsGAQUFBwICMIGzMD4WOkNlbnRyZSBmb3IgRGV2ZWxvcG1lbnQgb2Yg\\r\\nQWR2YW5jZWQgQ29tcHV0aW5nIChDLURBQyksIFB1bmUwABpxVGhpcyBDUFMgaXMgb3duZWQgYnkg\\r\\nQy1EQUMgYW5kIHVzZXJzIGFyZSByZXF1ZXN0ZWQgdG8gcmVhZCBDUFMgYmVmb3JlIHVzaW5nIHRo\\r\\nZSBDLURBQyBDQSdzIGNlcnRpZmljYXRpb24gc2VydmljZXMwKwYHYIJkZAIEATAgMB4GCCsGAQUF\\r\\nBwICMBIaEEFhZGhhYXIgZUtZQy1PVFAwRAYIKwYBBQUHAQEEODA2MDQGCCsGAQUFBzAChihodHRw\\r\\nczovL2VzaWduLmNkYWMuaW4vY2EvQ0RBQy1DQTIwMTQuZGVyMA0GCSqGSIb3DQEBCwUAA4IBAQB5\\r\\nh7WiYJlOSSB4fa6leLXsUXIwk4a7brroWOxTHF50iq5br5/8eKCjbGrTKuzTchwdec6QkQVGKbUp\\r\\nP5ioX2aJ/ay6itQSgi9pjPnW2hB2YDzvq7oHJzMV1KJ2jR6FCTxzIjxHEUrowMTr+EjvDv2O0dPr\\r\\n77fNMwn7dCb7dqhljn5W/ZPYtknfob/unKz5cBmNAfEzUaSZKI6WukZvRQZVrS3esDRkC8yFYjdS\\r\\nQpB5V1vMdAZFRDTmzCc0sECX3lopHyYuVZSQQyjbO2mLLVYDyv4kWT2AdOtbzalqwaBJQh3dM9xz\\r\\nSJJOT4k+ZrZiDAi+uFT7rChgQybSsC3jkCmXMYIBXzCCAVsCAQEwgYUwfjELMAkGA1UEBhMCSU4x\\r\\nFDASBgNVBAgMC01haGFyYXNodHJhMQ0wCwYDVQQHDARQVU5FMQ4wDAYDVQQKDAVDLURBQzEiMCAG\\r\\nA1UECwwZVGVzdCBDZXJ0aWZ5aW5nIEF1dGhvcml0eTEWMBQGA1UEAwwNVGVzdCBDLURBQyBDQQID\\r\\nA57xMA0GCWCGSAFlAwQCAQUAoGkwGAYJKoZIhvcNAQkDMQsGCSqGSIb3DQEHATAcBgkqhkiG9w0B\\r\\nCQUxDxcNMjQwMjE2MDg1MTM4WjAvBgkqhkiG9w0BCQQxIgQgV4EWS0OLjfgCViE42+fON7mGbHRJ\\r\\n8ZRE42TfJFZzdAMwDAYIKoZIzj0EAwIFAARGMEQCIHh2ScPJPAf98Z4fsQpsA10ohAkPZ0+NTKIX\\r\\nuxZK2R3PAiBXa8dd1HG2APq8qhPHDraT8PVzFl7hcYZ9WvpK5FtT4g==\",\"cmSignature\":\"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImV4dHJhUGFyYW1zIjoiZXhhbXBsZSJ9.eyJtZXRhIjp7InN0YXR1cyI6IkFDVElWRSIsInRpbWVzdGFtcCI6IjIwMjQtMDItMTZUMDk6MDA6NTQuMjUyWiJ9LCJtYWluIjp7ImZhcm1lcklkIjoiMTUxMzAwMDQ0ODAiLCJhaXBJZCI6IjQzN2NlZTdmLTQwZjQtNGNiNS05ZWU0LTBjZDU0MjFiMzkwOSIsImFpdUlkIjoiMzVjZjIzZmMtODJjMy00ODcwLWIyMjUtMTc4Mjc3OGVjZjQyIiwicHVycG9zZUNvZGUiOiJDUkVESVRfS0NDIiwidmFsaWRVbnRpbCI6IjIwMjQtMDMtMzEiLCJhdHRyaWJ1dGVzIjp7ImRvYiI6IkdSQU5URUQiLCJnZW5kZXIiOiJHUkFOVEVEIiwiZmFybWVyX25hbWUiOiJHUkFOVEVEIiwiYXNzZXRfYXJ0aWZhY3QiOiJzZFlNeWVUZGtzUFJvUi9qZGxWNUE2SFBYQTkvNVpjVlVwTHZ5K0JoZnhCeDhMN0dPYUdLT1I5NXBjbCt2d0lhaXJYamNvekdkOUx2VW9BQUk1UGo4MDQxYzdRMFdmVDRxQlBUR0NVS2haenU1djcxTnZ6REN5OWE0ZFBheHhxNFhVUFpGaGNOcGVER3V4bGI0M093SWc9PSIsInN0YXRlX2xnZF9jb2RlIjoiR1JBTlRFRCJ9fSwidXNlclNpZ25hdHVyZSI6Ik1JSUxEUVlKS29aSWh2Y05BUWNDb0lJSy9qQ0NDdm9DQVFFeER6QU5CZ2xnaGtnQlpRTUVBZ0VGQURBTEJna3Foa2lHOXcwQkJ3R2dcclxuZ2dseU1JSUR6ekNDQXJlZ0F3SUJBZ0lKQU14dDVoN09NNldFTUEwR0NTcUdTSWIzRFFFQkJRVUFNSDR4Q3pBSkJnTlZCQVlUQWtsT1xyXG5NUlF3RWdZRFZRUUlEQXROWVdoaGNtRnphSFJ5WVRFTk1Bc0dBMVVFQnd3RVVGVk9SVEVPTUF3R0ExVUVDZ3dGUXkxRVFVTXhJakFnXHJcbkJnTlZCQXNNR1ZSbGMzUWdRMlZ5ZEdsbWVXbHVaeUJCZFhSb2IzSnBkSGt4RmpBVUJnTlZCQU1NRFZSbGMzUWdReTFFUVVNZ1EwRXdcclxuSGhjTk1UZ3dNVEV3TVRFek9UTTFXaGNOTWpnd01UQTRNVEV6T1RNMVdqQitNUXN3Q1FZRFZRUUdFd0pKVGpFVU1CSUdBMVVFQ0F3TFxyXG5UV0ZvWVhKaGMyaDBjbUV4RFRBTEJnTlZCQWNNQkZCVlRrVXhEakFNQmdOVkJBb01CVU10UkVGRE1TSXdJQVlEVlFRTERCbFVaWE4wXHJcbklFTmxjblJwWm5scGJtY2dRWFYwYUc5eWFYUjVNUll3RkFZRFZRUUREQTFVWlhOMElFTXRSRUZESUVOQk1JSUJJakFOQmdrcWhraUdcclxuOXcwQkFRRUZBQU9DQVE4QU1JSUJDZ0tDQVFFQXplSklBbXp5aGw0OUcrS2ZRUFFtUDV6Zy9ab3o2VERaSW1lbDQzVmtsYktIUmM0YVxyXG5XRUFaUjlNcDRwd3NWWGFXZURkK0dXcEJleHpDdjhLY0JSYXQxVnY0WnlSN1JnRHdNSjhNU1FrT2tJbzVvWjMxc1NuTGxlaGJIQzJkXHJcbkRVek9XNjZPMXB6cUZ0dkt5ZjZRSVV4RXBZUmRuMGJiTGFaWU9mSFdLVVc2TFRDV1JaNVMrSFdpbFRhRkkyYU9JckczVmcvSGYrM0xcclxuUWtKdTRIN1VybXI5MllqeGQzWjdES3hWa2pFUzRrZXhVZTVQVU1ZNXdtWWZEQzFQV09rdjlHeUt1MS9zWkVtUStHVWNVUi9UTkRuUVxyXG5vTGJIdmJtUWFRN1RpeXlpVEN6WTFraXBBSE9UczRZdFNnZGhMcXFRSzZqV2U3V3RoR1lQcDBlalhDVWc4MWJaZVFJREFRQUJvMUF3XHJcblRqQWRCZ05WSFE0RUZnUVVOZHQyWHp4N0pSbnI1OHdvNDc1blBQMk1TUVV3SHdZRFZSMGpCQmd3Rm9BVU5kdDJYeng3SlJucjU4d29cclxuNDc1blBQMk1TUVV3REFZRFZSMFRCQVV3QXdFQi96QU5CZ2txaGtpRzl3MEJBUVVGQUFPQ0FRRUFUamliVEpKR0hNTU5yVTh1L0Q3RFxyXG5VZTJWVEJ0UnFwcld2TFRDOXc4MjVLS2wvZG9Qc1AxWTFZVFZTMTNVK2RvNlNMR0ZFS2Qvckt3SXZCd1RodVBVVFhOT0tvY1lmd3JQXHJcbjhxQzJSZmNxVmE3WGwxZWwzcWc0YnZWbVorU1Q5R0xCQjVlNUNBZFk5eVREYllYT21NcXY3RENOKzFCUmJxK0FZNTIwa1p0cE1XUzRcclxucW9WZEtNSTlnM3MzdDU5amdjODlqUm9Yb201ZXN6QzhIZlVpUFprSHQ0a2FUWnFLY1VXMmRGVzd5M3lWb29CMkNhQ2k1SEhhdE4zRVxyXG5RbjB0YjNXUXF2UkwzWmtRM21paFZsWHlidkNDcFNBU1RwVzdNTnFSMDVoTTNkMytPeWJpSE9paDIyK2ljZVc2OVJHQzEyYVUxeUdOXHJcblNhdmhRQ1FQa3krK1h4OTNNekNDQlpzd2dnU0RvQU1DQVFJQ0F3T2U4VEFOQmdrcWhraUc5dzBCQVFzRkFEQitNUXN3Q1FZRFZRUUdcclxuRXdKSlRqRVVNQklHQTFVRUNBd0xUV0ZvWVhKaGMyaDBjbUV4RFRBTEJnTlZCQWNNQkZCVlRrVXhEakFNQmdOVkJBb01CVU10UkVGRFxyXG5NU0l3SUFZRFZRUUxEQmxVWlhOMElFTmxjblJwWm5scGJtY2dRWFYwYUc5eWFYUjVNUll3RkFZRFZRUUREQTFVWlhOMElFTXRSRUZEXHJcbklFTkJNQjRYRFRJME1ESXhOakE0TlRFek9Gb1hEVEkwTURJeE5qQTVNakV6T0Zvd2dnRkVNUTR3REFZRFZRUUdFd1ZKYm1ScFlURVVcclxuTUJJR0ExVUVDQk1MVTNSaGRHVWdVMlYyWlc0eEVUQVBCZ05WQkFvVENGQmxjbk52Ym1Gc01Sb3dHQVlEVlFRREV4RlFaWEp6YjI0Z1xyXG5VMlYyWlc0Z1RtRnRaVEVQTUEwR0ExVUVFUk1HT1RBd01EQTNNVkl3VUFZRFZRUXRBMGtBT0dnNWFqQnJNV0V5WWpOak5HUTFaVFptXHJcbk4yYzRhRGxxTUdzeFlUSmlNMk0wWkRWbE5tWTNaemhvT1dvd2F6RXlNelExTmpjNE9UQXhNakZoTW1Jell6UmtOV1UyWmpkbk1Ta3dcclxuSndZRFZRUkJFeUJqTm1RMFlUZzFOR1psTjJRMFlqVXdZVFE1T0RSbU1qSTNNREF4TmpkbVlURU5NQXNHQTFVRURCTUVOamM0T1RGT1xyXG5NRXdHQTFVRUxoTkZNVGszTjBaaU5UTXpPVEZpTkdFek5HRTFPVEkyTkdVeFpHSmtZVGt4TTJVeVlqRXpPVGhsWWpkbU5tVTBaRE0yXHJcbk1XRmxZVEprTlRJMllUWTBOVGt4TnpJd1pqUXlNRmt3RXdZSEtvWkl6ajBDQVFZSUtvWkl6ajBEQVFjRFFnQUVTdU5xM1JtWjl6em5cclxuMnlLOTV1K0NxMXFtRFo1RnVWZUd6WlNCWC9MejV6azhtTUg0V1hpOUJqRXA5eENGRC9iSEQ1MHU0c0FSdHpJb05HRnd0RHFRWGFPQ1xyXG5BaU13Z2dJZk1Ba0dBMVVkRXdRQ01BQXdIUVlEVlIwT0JCWUVGRHUvZmNEZVJCaUwweTNnOTdQUTBVY3EwTHRRTUI4R0ExVWRJd1FZXHJcbk1CYUFGQTU4b1pYVzJzd2c4eWhQbEwxMzA2SDBNSXNXTUE0R0ExVWREd0VCL3dRRUF3SUd3REE1QmdOVkhSOEVNakF3TUM2Z0xLQXFcclxuaGlob2RIUndjem92TDJWemFXZHVMbU5rWVdNdWFXNHZZMkV2WlhOcFoyNURRVEl3TVRRdVkzSnNNSUlCUHdZRFZSMGdCSUlCTmpDQ1xyXG5BVEl3Z2dFQkJnZGdnbVJrQVFrQ01JSDFNREFHQ0NzR0FRVUZCd0lCRmlSb2RIUndjem92TDJWemFXZHVMbU5rWVdNdWFXNHZZMkV2XHJcblExQlRMME5RVXk1d1pHWXdnY0FHQ0NzR0FRVUZCd0lDTUlHek1ENFdPa05sYm5SeVpTQm1iM0lnUkdWMlpXeHZjRzFsYm5RZ2IyWWdcclxuUVdSMllXNWpaV1FnUTI5dGNIVjBhVzVuSUNoRExVUkJReWtzSUZCMWJtVXdBQnB4VkdocGN5QkRVRk1nYVhNZ2IzZHVaV1FnWW5rZ1xyXG5ReTFFUVVNZ1lXNWtJSFZ6WlhKeklHRnlaU0J5WlhGMVpYTjBaV1FnZEc4Z2NtVmhaQ0JEVUZNZ1ltVm1iM0psSUhWemFXNW5JSFJvXHJcblpTQkRMVVJCUXlCRFFTZHpJR05sY25ScFptbGpZWFJwYjI0Z2MyVnlkbWxqWlhNd0t3WUhZSUprWkFJRUFUQWdNQjRHQ0NzR0FRVUZcclxuQndJQ01CSWFFRUZoWkdoaFlYSWdaVXRaUXkxUFZGQXdSQVlJS3dZQkJRVUhBUUVFT0RBMk1EUUdDQ3NHQVFVRkJ6QUNoaWhvZEhSd1xyXG5jem92TDJWemFXZHVMbU5rWVdNdWFXNHZZMkV2UTBSQlF5MURRVEl3TVRRdVpHVnlNQTBHQ1NxR1NJYjNEUUVCQ3dVQUE0SUJBUUI1XHJcbmg3V2lZSmxPU1NCNGZhNmxlTFhzVVhJd2s0YTdicnJvV094VEhGNTBpcTVicjUvOGVLQ2piR3JUS3V6VGNod2RlYzZRa1FWR0tiVXBcclxuUDVpb1gyYUovYXk2aXRRU2dpOXBqUG5XMmhCMllEenZxN29ISnpNVjFLSjJqUjZGQ1R4eklqeEhFVXJvd01UcitFanZEdjJPMGRQclxyXG43N2ZOTXduN2RDYjdkcWhsam41Vy9aUFl0a25mb2IvdW5LejVjQm1OQWZFelVhU1pLSTZXdWtadlJRWlZyUzNlc0RSa0M4eUZZamRTXHJcblFwQjVWMXZNZEFaRlJEVG16Q2Mwc0VDWDNsb3BIeVl1VlpTUVF5amJPMm1MTFZZRHl2NGtXVDJBZE90YnphbHF3YUJKUWgzZE05eHpcclxuU0pKT1Q0aytaclppREFpK3VGVDdyQ2hnUXliU3NDM2prQ21YTVlJQlh6Q0NBVnNDQVFFd2dZVXdmakVMTUFrR0ExVUVCaE1DU1U0eFxyXG5GREFTQmdOVkJBZ01DMDFoYUdGeVlYTm9kSEpoTVEwd0N3WURWUVFIREFSUVZVNUZNUTR3REFZRFZRUUtEQVZETFVSQlF6RWlNQ0FHXHJcbkExVUVDd3daVkdWemRDQkRaWEowYVdaNWFXNW5JRUYxZEdodmNtbDBlVEVXTUJRR0ExVUVBd3dOVkdWemRDQkRMVVJCUXlCRFFRSURcclxuQTU3eE1BMEdDV0NHU0FGbEF3UUNBUVVBb0drd0dBWUpLb1pJaHZjTkFRa0RNUXNHQ1NxR1NJYjNEUUVIQVRBY0Jna3Foa2lHOXcwQlxyXG5DUVV4RHhjTk1qUXdNakUyTURnMU1UTTRXakF2QmdrcWhraUc5dzBCQ1FReElnUWdWNEVXUzBPTGpmZ0NWaUU0MitmT043bUdiSFJKXHJcbjhaUkU0MlRmSkZaemRBTXdEQVlJS29aSXpqMEVBd0lGQUFSR01FUUNJSGgyU2NQSlBBZjk4WjRmc1Fwc0ExMG9oQWtQWjArTlRLSVhcclxudXhaSzJSM1BBaUJYYThkZDFIRzJBUHE4cWhQSERyYVQ4UFZ6Rmw3aGNZWjlXdnBLNUZ0VDRnPT0iLCJpYXQiOjE3MDgwNzQwNTR9.KlaRWhjDFQtsMDcX1T2VUx1ynIlWJF9sgcX2a1Hb-awBzmP7dBAoPmLEvPtwDea6SN33spy9PK-T_9YgKIgDcZ0f3i3gqa11otB_MqNJif9SWhJZp06QEKpuNJty3tW0dLCCIDVICB5d8QV4sjMS4-ryS8WMpkz0FTZGllF6w3s3cnBm2fSZL38BNgl0z45g1HtD8p8woRGUEIUi-0ocpimeZHhcQBmmFlHOu1k-v46WL8Ztb-ewqAITshsWErww-GZ0BUAIqxR4lK3Fua5GajwKYFMJM3HasoEEgWdA_eyHwYUJ_89BH63_A4TSpL87D7oAhj4og0ZZAvMBC34Oog\"}");
//	}
	private static JsonNode convertStringToJsonNode(String jsonString) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			return objectMapper.readTree(jsonString);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	public ResponseModel onSync(HttpServletRequest request, TransactionRequestDAO transactionRequestDAO) {

		String requestTokenHeader = request.getHeader("Authorization");

		AIUTokenRequestDAO aiuTokenRequestDAO = new AIUTokenRequestDAO();
		aiuTokenRequestDAO.setOsId(transactionRequestDAO.getHeader().getSender_id());
		aiuTokenRequestDAO.setToken(requestTokenHeader);
		Boolean isStatus = false;

		String resp = centralServiceCall.validateTokenOfAIUFromSC(agristackUser, agristackPassword, aiuTokenRequestDAO);
		JSONObject validateTokenResponse = new JSONObject(resp);
		if (validateTokenResponse != null && validateTokenResponse.has("data")) {
			JSONObject validateTokenResponseData = validateTokenResponse.getJSONObject("data");
			if (validateTokenResponseData != null && validateTokenResponseData.has("validationFlag")) {

				isStatus = validateTokenResponseData.getBoolean("validationFlag");
			}
		}
		List<Object> finalResultObject = new ArrayList<>();
		if (isStatus.equals(Boolean.FALSE)) {
//
//			transactionMaster.setAckStatus(AckStatusEnum.ERR.getValue());
//			transactionMasterRepository.save(transactionMaster);

			throw new UnauthorizedException("You are not authorized to access this resource.");
		} else {
				List<TransactionRequestMapping> finalReferenceMappingList = new ArrayList<>();
				ResponseMessageDAO responseMessageDAO = new ResponseMessageDAO();
				Map<String, Object> responseData = new HashMap();
				TransactionMaster transactionMaster = new TransactionMaster();
				transactionMaster.setCreatedIp(CommonUtil.getRequestIp(request));
				transactionMaster.setMessageId(transactionRequestDAO.getHeader().getMessage_id());
				transactionMaster.setMessageTs(transactionRequestDAO.getHeader().getMessage_ts());
		
				transactionMaster.setSenderId(transactionRequestDAO.getHeader().getSender_id());
		
				transactionMaster.setSenderUri(transactionRequestDAO.getHeader().getSender_uri());
				transactionMaster.setReceiverId(transactionRequestDAO.getHeader().getReceiver_id());
				transactionMaster.setTotalCount(transactionRequestDAO.getHeader().getTotal_count());
				transactionMaster.setIsMsgEncrypted(transactionRequestDAO.getHeader().getIs_msg_encrypted());
				transactionMaster.setTransactionId(transactionRequestDAO.getMessage().getTransaction_id());
		
				transactionMaster.setAckStatus(AckStatusEnum.ERR.getValue());
				UUID uuid = UUID.randomUUID();
				transactionMaster.setCorrelationId(uuid.toString());
				transactionMaster.setVersion(transactionRequestDAO.getHeader().getVersion());
				transactionMaster.setAckTimestamp(transactionMaster.getCreatedOn());
		
				ErrorDAO error = new ErrorDAO();
		
				if (transactionRequestDAO.getMessage().getSearch_request() != null
						&& !transactionRequestDAO.getMessage().getSearch_request().isEmpty()) {
		
					List<TransactionRequestMapping> transactionRequestMappingList = new ArrayList<>();
					transactionRequestDAO.getMessage().getSearch_request().forEach(action -> {
		
						TransactionRequestMapping transactionRequestMapping = new TransactionRequestMapping();
						transactionRequestMapping.setCreatedIp(CommonUtil.getRequestIp(request));
						transactionRequestMapping.setTransactionId(transactionRequestDAO.getMessage().getTransaction_id());
		
						transactionRequestMapping.setReferenceId(action.getReference_id());
		
						transactionRequestMapping.setTimeStamp(action.getTimestamp());
		
						transactionRequestMapping.setLocale(action.getLocale());
		
						transactionRequestMapping.setQueryType(action.getSearch_criteria().getQuery_type());
		
						transactionRequestMapping.setRegType(action.getSearch_criteria().getReg_type());
		
						transactionRequestMapping.setQueryName(action.getSearch_criteria().getQuery().getQuery_name());
		
						transactionRequestMapping.setMapperId(action.getSearch_criteria().getQuery().getMapper_id());
		
						transactionRequestMapping.setPageNumber(action.getSearch_criteria().getPagination().getPage_number());
		
						transactionRequestMapping.setPageSize(action.getSearch_criteria().getPagination().getPage_size());
		
						ObjectMapper objectMapper = new ObjectMapper();
						try {
							if (action.getSearch_criteria().getQuery() != null
									&& action.getSearch_criteria().getQuery().getQuery_params() != null
								// && !action.getSearch_criteria().getQuery().getQuery_params().isEmpty()
							) {
		
								String queryParamsString = objectMapper
										.writeValueAsString(action.getSearch_criteria().getQuery().getQuery_params());
								transactionRequestMapping.setQueryParams(queryParamsString);
		
							}
							if (action.getSearch_criteria().getSort() != null
									&& !action.getSearch_criteria().getSort().isEmpty()) {
								String sortString = objectMapper.writeValueAsString(action.getSearch_criteria().getSort());
								transactionRequestMapping.setSort(sortString);
							}
							if (action.getSearch_criteria().getConsent() != null
								// && !action.getSearch_criteria().getConsent().isEmpty()
							) {
		
								String consentString = objectMapper
										.writeValueAsString(action.getSearch_criteria().getConsent());
								transactionRequestMapping.setConsentArtifect(consentString);
								ObjectMapper mapper = new ObjectMapper();
								mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
								TypeFactory typeFactory = mapper.getTypeFactory();
								ConsentAttributesDAO consentAttributesDAO = mapper.readValue(consentString,
										ConsentAttributesDAO.class);
								// consentAttributesDAO.getMain()
								if (consentAttributesDAO.getConsent_required() != null
										&& consentAttributesDAO.getConsent_required().equals(Boolean.TRUE)
										&& consentAttributesDAO.getMain() != null
										&& consentAttributesDAO.getMain().getAttributes() != null) {
									String attributesString = objectMapper
											.writeValueAsString(consentAttributesDAO.getMain().getAttributes());
									transactionRequestMapping.setAttributes(attributesString);
									JSONObject jsonRes = new JSONObject(attributesString);
									String assetArtifact = jsonRes.has("asset_artifact")
											? jsonRes.get("asset_artifact").toString()
											: null;
		
									transactionRequestMapping.setAssetArtifactDecoded(AESUtil.decrypt(assetArtifact));
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							transactionMaster.setErrorCode("500");
							transactionMaster.setErrorMessage(e.getMessage());
							error.setCode("500");
							error.setMessage(e.getMessage());
		
						}
		
						transactionRequestMappingList.add(transactionRequestMapping);
					});
					finalReferenceMappingList = transactionRequestMappingRepository.saveAll(transactionRequestMappingList);
		
				}
				transactionMasterRepository.save(transactionMaster);
		
//				List<Object> finalResultObject = new ArrayList<>();
				if (finalReferenceMappingList != null) {
					List<String> referenceIdList = finalReferenceMappingList.stream().map(p -> p.getReferenceId())
							.collect(Collectors.toList());
		
					referenceIdList.forEach((refId) -> {
						TransactionResponseDAO resultOnSeek = generateRequestForOnSeek(refId);
						seekerOnSeek(request, resultOnSeek);
						resultOnSeek.getMessage().getSearch_response().forEach((data) -> {
							if (data.getData().getReg_records() != null){
								finalResultObject.add(data.getData().getReg_records());
							}
						});
					});
				}
		
		//		Boolean isStatus = false;
		//
		//		String resp = centralServiceCall.validateTokenOfAIUFromSC(agristackUser, agristackPassword, aiuTokenRequestDAO);
		//		JSONObject validateTokenResponse = new JSONObject(resp);
		//		if (validateTokenResponse != null && validateTokenResponse.has("data")) {
		//			JSONObject validateTokenResponseData = validateTokenResponse.getJSONObject("data");
		//			if (validateTokenResponseData != null && validateTokenResponseData.has("validationFlag")) {
		//
		//				isStatus = validateTokenResponseData.getBoolean("validationFlag");
		//			}
		//		}
		//		
		
					transactionMaster.setAckStatus(AckStatusEnum.ACK.getValue());
					transactionMasterRepository.save(transactionMaster);
					responseMessageDAO.setAckStatus(AckStatusEnum.ACK.getValue());
					responseMessageDAO.setTimestamp(transactionMaster.getCreatedOn());
					responseMessageDAO.setError(error);
					responseMessageDAO.setCorrelationId(transactionMaster.getCorrelationId());
					responseData.put("message", responseMessageDAO);
				}

//		if (isStatus.equals(Boolean.FALSE)) {
//
//			transactionMaster.setAckStatus(AckStatusEnum.ERR.getValue());
//			transactionMasterRepository.save(transactionMaster);
//
//			throw new UnauthorizedException("You are not authorized to access this resource.");
//		} else {
//			transactionMaster.setAckStatus(AckStatusEnum.ACK.getValue());
//			transactionMasterRepository.save(transactionMaster);
//			responseMessageDAO.setAckStatus(AckStatusEnum.ACK.getValue());
//			responseMessageDAO.setTimestamp(transactionMaster.getCreatedOn());
//			responseMessageDAO.setError(error);
//			responseMessageDAO.setCorrelationId(transactionMaster.getCorrelationId());
//			responseData.put("message", responseMessageDAO);
//		}

		return new ResponseModel(finalResultObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
				CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
	}

	public ResponseModel getSyncSeek(HttpServletRequest request, SyncSeekRequestDAO requestDAO) {
		try{
			String response = transactionRequestMappingRepository.fetchFarmerData(requestDAO.getState_lgd_code(),
					requestDAO.getFarmer_id());

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


	// public static void main(String[] args) throws JsonMappingException, JsonProcessingException {
	// 	// CentralCoreService ccs = new CentralCoreService();
	// 	// ccs.validateACMSignature(
	// 	// "{\"meta\":{\"status\":\"ACTIVE\",\"timestamp\":\"2023-12-19T13:04:50.995Z\"},\"main\":{\"farmerId\":\"15130004480\",\"aipId\":\"AP3\",\"aiuId\":\"BB3\",\"purposeCode\":\"CREDIT_KCC\",\"validUntil\":\"2024-03-31\",\"attributes\":{\"dob\":\"REQUIRED\",\"farmer_name\":\"REQUIRED\",\"asset_artifact\":\"sdYMyeTdksPRoR/jdlV5A6HPXA9/5ZcVUpLvy+BhfxBx8L7GOaGKOR95pcl+vwIairXjcozGd9LvUoAAI5Pj8041c7Q0WfT4qBPTGCUKhZzu5v71NvzDCy9a4dPaxxq4XUPZFhcNpeDGuxlb43OwIg==\",\"state_lgd_code\":\"REQUIRED\",\"crop_asset_data\":\"REQUIRED\",\"land_asset_data\":\"REQUIRED\",\"asset_data_details\":\"REQUIRED\"}},\"signature\":{\"cm_signature\":{\"cmId\":\"7609ad\",\"signature\":\"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImV4dHJhUGFyYW1zIjoiZXhhbXBsZSJ9.eyJtZXRhIjp7InN0YXR1cyI6IkFDVElWRSIsInRpbWVzdGFtcCI6IjIwMjMtMTItMTlUMTI6NTk6MDguNzMxWiJ9LCJtYWluIjp7ImZhcm1lcklkIjoiMTUxMzAwMDQ0ODAiLCJhaXBJZCI6IkFQMyIsImFpdUlkIjoiQkIzIiwicHVycG9zZUNvZGUiOiJDUkVESVRfS0NDIiwidmFsaWRVbnRpbCI6IjIwMjQtMDMtMzEiLCJhdHRyaWJ1dGVzIjp7ImRvYiI6IkdSQU5URUQiLCJmYXJtZXJfbmFtZSI6IkdSQU5URUQiLCJhc3NldF9hcnRpZmFjdCI6InNkWU15ZVRka3NQUm9SL2pkbFY1QTZIUFhBOS81WmNWVXBMdnkrQmhmeEJ4OEw3R09hR0tPUjk1cGNsK3Z3SWFoOGdVN3N0Tm9mYkdzS21xTkpmdTNNdTJZdjNCZG1tdklGQkYwRFFScUt5Vk0xMzhjSkJvc29qSlNJekw0VGdiIiwic3RhdGVfbGdkX2NvZGUiOiJHUkFOVEVEIiwiY3JvcF9hc3NldF9kYXRhIjoiR1JBTlRFRCIsImxhbmRfYXNzZXRfZGF0YSI6IkdSQU5URUQiLCJhc3NldF9kYXRhX2RldGFpbHMiOiJHUkFOVEVEIn19LCJpYXQiOjE3MDI5OTA3NDh9.BW2SCKLBwcEmWWzOEkZEQcuqMi0nFFtSJY0brkeGc6cYd3QPTA7cO5zvquG5vnuizJZNs8bJcdFg97YdRvPDoZDo2WVfxw3qX4PyNUHNZs2WZWx9u3wvCOVAg7XimlKqNcKvfnCkx38JWTzCUNNOD_BBIuGOtiDQYN5WM95b8T7k2kzt_MrxRdz_DesZ0wkxFdr9c-o9q5jnHoUGTwWKKC24-V_DFVAgjLfJxSV2oN6JrpzPBMZvTGAilw0tKvIEG5VxCvPjtKGEntnCqRdX0fslPIG3otMk6hsa6hFzk8J6oRTjyXzBoFVqRdpmppMmEWkYlcYQ_jS-nfIrSeDZPA\"},\"user_signature\":\"MIILDAYJKoZIhvcNAQcCoIIK/TCCCvkCAQExDzANBglghkgBZQMEAgEFADALBgkqhkiG9w0BBwGg\\r\\ngglwMIIDzzCCAregAwIBAgIJAMxt5h7OM6WEMA0GCSqGSIb3DQEBBQUAMH4xCzAJBgNVBAYTAklO\\r\\nMRQwEgYDVQQIDAtNYWhhcmFzaHRyYTENMAsGA1UEBwwEUFVORTEOMAwGA1UECgwFQy1EQUMxIjAg\\r\\nBgNVBAsMGVRlc3QgQ2VydGlmeWluZyBBdXRob3JpdHkxFjAUBgNVBAMMDVRlc3QgQy1EQUMgQ0Ew\\r\\nHhcNMTgwMTEwMTEzOTM1WhcNMjgwMTA4MTEzOTM1WjB+MQswCQYDVQQGEwJJTjEUMBIGA1UECAwL\\r\\nTWFoYXJhc2h0cmExDTALBgNVBAcMBFBVTkUxDjAMBgNVBAoMBUMtREFDMSIwIAYDVQQLDBlUZXN0\\r\\nIENlcnRpZnlpbmcgQXV0aG9yaXR5MRYwFAYDVQQDDA1UZXN0IEMtREFDIENBMIIBIjANBgkqhkiG\\r\\n9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzeJIAmzyhl49G+KfQPQmP5zg/Zoz6TDZImel43VklbKHRc4a\\r\\nWEAZR9Mp4pwsVXaWeDd+GWpBexzCv8KcBRat1Vv4ZyR7RgDwMJ8MSQkOkIo5oZ31sSnLlehbHC2d\\r\\nDUzOW66O1pzqFtvKyf6QIUxEpYRdn0bbLaZYOfHWKUW6LTCWRZ5S+HWilTaFI2aOIrG3Vg/Hf+3L\\r\\nQkJu4H7Urmr92Yjxd3Z7DKxVkjES4kexUe5PUMY5wmYfDC1PWOkv9GyKu1/sZEmQ+GUcUR/TNDnQ\\r\\noLbHvbmQaQ7TiyyiTCzY1kipAHOTs4YtSgdhLqqQK6jWe7WthGYPp0ejXCUg81bZeQIDAQABo1Aw\\r\\nTjAdBgNVHQ4EFgQUNdt2Xzx7JRnr58wo475nPP2MSQUwHwYDVR0jBBgwFoAUNdt2Xzx7JRnr58wo\\r\\n475nPP2MSQUwDAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQUFAAOCAQEATjibTJJGHMMNrU8u/D7D\\r\\nUe2VTBtRqprWvLTC9w825KKl/doPsP1Y1YTVS13U+do6SLGFEKd/rKwIvBwThuPUTXNOKocYfwrP\\r\\n8qC2RfcqVa7Xl1el3qg4bvVmZ+ST9GLBB5e5CAdY9yTDbYXOmMqv7DCN+1BRbq+AY520kZtpMWS4\\r\\nqoVdKMI9g3s3t59jgc89jRoXom5eszC8HfUiPZkHt4kaTZqKcUW2dFW7y3yVooB2CaCi5HHatN3E\\r\\nQn0tb3WQqvRL3ZkQ3mihVlXybvCCpSASTpW7MNqR05hM3d3+OybiHOih22+iceW69RGC12aU1yGN\\r\\nSavhQCQPky++Xx93MzCCBZkwggSBoAMCAQICAwNzXTANBgkqhkiG9w0BAQsFADB+MQswCQYDVQQG\\r\\nEwJJTjEUMBIGA1UECAwLTWFoYXJhc2h0cmExDTALBgNVBAcMBFBVTkUxDjAMBgNVBAoMBUMtREFD\\r\\nMSIwIAYDVQQLDBlUZXN0IENlcnRpZnlpbmcgQXV0aG9yaXR5MRYwFAYDVQQDDA1UZXN0IEMtREFD\\r\\nIENBMB4XDTIzMTIxOTEyNTg1OFoXDTIzMTIxOTEzMjg1OFowggFCMQ4wDAYDVQQGEwVJbmRpYTET\\r\\nMBEGA1UECBMKU3RhdGUgRml2ZTERMA8GA1UEChMIUGVyc29uYWwxGTAXBgNVBAMTEFBlcnNvbiBG\\r\\naXZlIE5hbWUxDzANBgNVBBETBjkwMDAwNTFSMFAGA1UELQNJADZmN2c4aDlqMGsxYTJiM2M0ZDVl\\r\\nNmY3ZzhoOWowazFhMmIzYzRkNWU2ZjdnOGg5ajBrMTIzNDU2Nzg5MDEyMWEyYjNjNGQ1ZTEpMCcG\\r\\nA1UEQRMgYzZkNGE4NTRmZTdkNGI1MGE0OTg0ZjIyNzAwMTY3ZmExDTALBgNVBAwTBDY3ODkxTjBM\\r\\nBgNVBC4TRTE5NzVGYjUzMzkxYjRhMzRhNTkyNjRlMWRiZGE5MTNlMmIxMzk4ZWI3ZjZlNGQzNjFh\\r\\nZWEyZDUyNmE2NDU5MTcyMGY0MjBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABErjat0Zmfc859si\\r\\nvebvgqtapg2eRblXhs2UgV/y8+c5PJjB+Fl4vQYxKfcQhQ/2xw+dLuLAEbcyKDRhcLQ6kF2jggIj\\r\\nMIICHzAJBgNVHRMEAjAAMB0GA1UdDgQWBBQ7v33A3kQYi9Mt4Pez0NFHKtC7UDAfBgNVHSMEGDAW\\r\\ngBQOfKGV1trMIPMoT5S9d9Oh9DCLFjAOBgNVHQ8BAf8EBAMCBsAwOQYDVR0fBDIwMDAuoCygKoYo\\r\\naHR0cHM6Ly9lc2lnbi5jZGFjLmluL2NhL2VzaWduQ0EyMDE0LmNybDCCAT8GA1UdIASCATYwggEy\\r\\nMIIBAQYHYIJkZAEJAjCB9TAwBggrBgEFBQcCARYkaHR0cHM6Ly9lc2lnbi5jZGFjLmluL2NhL0NQ\\r\\nUy9DUFMucGRmMIHABggrBgEFBQcCAjCBszA+FjpDZW50cmUgZm9yIERldmVsb3BtZW50IG9mIEFk\\r\\ndmFuY2VkIENvbXB1dGluZyAoQy1EQUMpLCBQdW5lMAAacVRoaXMgQ1BTIGlzIG93bmVkIGJ5IEMt\\r\\nREFDIGFuZCB1c2VycyBhcmUgcmVxdWVzdGVkIHRvIHJlYWQgQ1BTIGJlZm9yZSB1c2luZyB0aGUg\\r\\nQy1EQUMgQ0EncyBjZXJ0aWZpY2F0aW9uIHNlcnZpY2VzMCsGB2CCZGQCBAEwIDAeBggrBgEFBQcC\\r\\nAjASGhBBYWRoYWFyIGVLWUMtT1RQMEQGCCsGAQUFBwEBBDgwNjA0BggrBgEFBQcwAoYoaHR0cHM6\\r\\nLy9lc2lnbi5jZGFjLmluL2NhL0NEQUMtQ0EyMDE0LmRlcjANBgkqhkiG9w0BAQsFAAOCAQEAksuX\\r\\nsQnwQU7mzKrvC8T0hrbHYGWhTsoL7QivLab+BSTrT53h9BcCHq0fxSB8rpDNcoXPKRGRSMYmm/+T\\r\\nxsoP6oyvDkBRYcvshX9ENyB80KLYhRjpYwlcdKmHs+cVm7smuSEU3g2o95ajwCg4ofWc/e0IC//H\\r\\naFl0RQKJxbR3mFTpiYARZ5jAygrz+Z+VefW7rhBSn9F4t0X9xpuntOisysH/r5RXhSvCS/SnsFuE\\r\\nVV6LcBWo7Iz0ATD+9o/I0mmDmMwS+7giXXAN5jQrgdsp+SFaqLeVvDZLRD3vilo9fJzx4DEYIXFo\\r\\n+mYQrnmmcSv0N17EnrAEyQBhhdsh5+eMUTGCAWAwggFcAgEBMIGFMH4xCzAJBgNVBAYTAklOMRQw\\r\\nEgYDVQQIDAtNYWhhcmFzaHRyYTENMAsGA1UEBwwEUFVORTEOMAwGA1UECgwFQy1EQUMxIjAgBgNV\\r\\nBAsMGVRlc3QgQ2VydGlmeWluZyBBdXRob3JpdHkxFjAUBgNVBAMMDVRlc3QgQy1EQUMgQ0ECAwNz\\r\\nXTANBglghkgBZQMEAgEFAKBpMBgGCSqGSIb3DQEJAzELBgkqhkiG9w0BBwEwHAYJKoZIhvcNAQkF\\r\\nMQ8XDTIzMTIxOTEyNTg1OFowLwYJKoZIhvcNAQkEMSIEINhIrw2WRMnp7gEQxvM2vDyJOkjf5EP5\\r\\nMiidMrn1PRGYMAwGCCqGSM49BAMCBQAERzBFAiEAo/wfRXd38+PMLRDTqNWkqtUItvS2CYK2CjeZ\\r\\nH0oKsrkCIEvfTur8IGymenayRuoBIDTsj9M7j9fbFQqtIZ1wyHdc\"}}");
	// 	String data = "{\"meta\":{\"status\":\"ACTIVE\",\"timestamp\":\"2023-12-19T13:04:50.995Z\"},\"main\":{\"farmerId\":\"15130004480\",\"aipId\":\"AP3\",\"aiuId\":\"BB3\",\"purposeCode\":\"CREDIT_KCC\",\"validUntil\":\"2024-03-31\",\"attributes\":{\"dob\":\"REQUIRED\",\"farmer_name\":\"REQUIRED\",\"asset_artifact\":\"sdYMyeTdksPRoR/jdlV5A6HPXA9/5ZcVUpLvy+BhfxBx8L7GOaGKOR95pcl+vwIairXjcozGd9LvUoAAI5Pj8041c7Q0WfT4qBPTGCUKhZzu5v71NvzDCy9a4dPaxxq4XUPZFhcNpeDGuxlb43OwIg==\",\"state_lgd_code\":\"REQUIRED\",\"crop_asset_data\":\"REQUIRED\",\"land_asset_data\":\"REQUIRED\",\"asset_data_details\":\"REQUIRED\"}},\"signature\":{\"cm_signature\":{\"cmId\":\"7609ad\",\"signature\":\"eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImV4dHJhUGFyYW1zIjoiZXhhbXBsZSJ9.eyJtZXRhIjp7InN0YXR1cyI6IkFDVElWRSIsInRpbWVzdGFtcCI6IjIwMjMtMTItMTlUMTI6NTk6MDguNzMxWiJ9LCJtYWluIjp7ImZhcm1lcklkIjoiMTUxMzAwMDQ0ODAiLCJhaXBJZCI6IkFQMyIsImFpdUlkIjoiQkIzIiwicHVycG9zZUNvZGUiOiJDUkVESVRfS0NDIiwidmFsaWRVbnRpbCI6IjIwMjQtMDMtMzEiLCJhdHRyaWJ1dGVzIjp7ImRvYiI6IkdSQU5URUQiLCJmYXJtZXJfbmFtZSI6IkdSQU5URUQiLCJhc3NldF9hcnRpZmFjdCI6InNkWU15ZVRka3NQUm9SL2pkbFY1QTZIUFhBOS81WmNWVXBMdnkrQmhmeEJ4OEw3R09hR0tPUjk1cGNsK3Z3SWFoOGdVN3N0Tm9mYkdzS21xTkpmdTNNdTJZdjNCZG1tdklGQkYwRFFScUt5Vk0xMzhjSkJvc29qSlNJekw0VGdiIiwic3RhdGVfbGdkX2NvZGUiOiJHUkFOVEVEIiwiY3JvcF9hc3NldF9kYXRhIjoiR1JBTlRFRCIsImxhbmRfYXNzZXRfZGF0YSI6IkdSQU5URUQiLCJhc3NldF9kYXRhX2RldGFpbHMiOiJHUkFOVEVEIn19LCJpYXQiOjE3MDI5OTA3NDh9.BW2SCKLBwcEmWWzOEkZEQcuqMi0nFFtSJY0brkeGc6cYd3QPTA7cO5zvquG5vnuizJZNs8bJcdFg97YdRvPDoZDo2WVfxw3qX4PyNUHNZs2WZWx9u3wvCOVAg7XimlKqNcKvfnCkx38JWTzCUNNOD_BBIuGOtiDQYN5WM95b8T7k2kzt_MrxRdz_DesZ0wkxFdr9c-o9q5jnHoUGTwWKKC24-V_DFVAgjLfJxSV2oN6JrpzPBMZvTGAilw0tKvIEG5VxCvPjtKGEntnCqRdX0fslPIG3otMk6hsa6hFzk8J6oRTjyXzBoFVqRdpmppMmEWkYlcYQ_jS-nfIrSeDZPA\"},\"user_signature\":\"MIILDAYJKoZIhvcNAQcCoIIK/TCCCvkCAQExDzANBglghkgBZQMEAgEFADALBgkqhkiG9w0BBwGg\\r\\ngglwMIIDzzCCAregAwIBAgIJAMxt5h7OM6WEMA0GCSqGSIb3DQEBBQUAMH4xCzAJBgNVBAYTAklO\\r\\nMRQwEgYDVQQIDAtNYWhhcmFzaHRyYTENMAsGA1UEBwwEUFVORTEOMAwGA1UECgwFQy1EQUMxIjAg\\r\\nBgNVBAsMGVRlc3QgQ2VydGlmeWluZyBBdXRob3JpdHkxFjAUBgNVBAMMDVRlc3QgQy1EQUMgQ0Ew\\r\\nHhcNMTgwMTEwMTEzOTM1WhcNMjgwMTA4MTEzOTM1WjB+MQswCQYDVQQGEwJJTjEUMBIGA1UECAwL\\r\\nTWFoYXJhc2h0cmExDTALBgNVBAcMBFBVTkUxDjAMBgNVBAoMBUMtREFDMSIwIAYDVQQLDBlUZXN0\\r\\nIENlcnRpZnlpbmcgQXV0aG9yaXR5MRYwFAYDVQQDDA1UZXN0IEMtREFDIENBMIIBIjANBgkqhkiG\\r\\n9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzeJIAmzyhl49G+KfQPQmP5zg/Zoz6TDZImel43VklbKHRc4a\\r\\nWEAZR9Mp4pwsVXaWeDd+GWpBexzCv8KcBRat1Vv4ZyR7RgDwMJ8MSQkOkIo5oZ31sSnLlehbHC2d\\r\\nDUzOW66O1pzqFtvKyf6QIUxEpYRdn0bbLaZYOfHWKUW6LTCWRZ5S+HWilTaFI2aOIrG3Vg/Hf+3L\\r\\nQkJu4H7Urmr92Yjxd3Z7DKxVkjES4kexUe5PUMY5wmYfDC1PWOkv9GyKu1/sZEmQ+GUcUR/TNDnQ\\r\\noLbHvbmQaQ7TiyyiTCzY1kipAHOTs4YtSgdhLqqQK6jWe7WthGYPp0ejXCUg81bZeQIDAQABo1Aw\\r\\nTjAdBgNVHQ4EFgQUNdt2Xzx7JRnr58wo475nPP2MSQUwHwYDVR0jBBgwFoAUNdt2Xzx7JRnr58wo\\r\\n475nPP2MSQUwDAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQUFAAOCAQEATjibTJJGHMMNrU8u/D7D\\r\\nUe2VTBtRqprWvLTC9w825KKl/doPsP1Y1YTVS13U+do6SLGFEKd/rKwIvBwThuPUTXNOKocYfwrP\\r\\n8qC2RfcqVa7Xl1el3qg4bvVmZ+ST9GLBB5e5CAdY9yTDbYXOmMqv7DCN+1BRbq+AY520kZtpMWS4\\r\\nqoVdKMI9g3s3t59jgc89jRoXom5eszC8HfUiPZkHt4kaTZqKcUW2dFW7y3yVooB2CaCi5HHatN3E\\r\\nQn0tb3WQqvRL3ZkQ3mihVlXybvCCpSASTpW7MNqR05hM3d3+OybiHOih22+iceW69RGC12aU1yGN\\r\\nSavhQCQPky++Xx93MzCCBZkwggSBoAMCAQICAwNzXTANBgkqhkiG9w0BAQsFADB+MQswCQYDVQQG\\r\\nEwJJTjEUMBIGA1UECAwLTWFoYXJhc2h0cmExDTALBgNVBAcMBFBVTkUxDjAMBgNVBAoMBUMtREFD\\r\\nMSIwIAYDVQQLDBlUZXN0IENlcnRpZnlpbmcgQXV0aG9yaXR5MRYwFAYDVQQDDA1UZXN0IEMtREFD\\r\\nIENBMB4XDTIzMTIxOTEyNTg1OFoXDTIzMTIxOTEzMjg1OFowggFCMQ4wDAYDVQQGEwVJbmRpYTET\\r\\nMBEGA1UECBMKU3RhdGUgRml2ZTERMA8GA1UEChMIUGVyc29uYWwxGTAXBgNVBAMTEFBlcnNvbiBG\\r\\naXZlIE5hbWUxDzANBgNVBBETBjkwMDAwNTFSMFAGA1UELQNJADZmN2c4aDlqMGsxYTJiM2M0ZDVl\\r\\nNmY3ZzhoOWowazFhMmIzYzRkNWU2ZjdnOGg5ajBrMTIzNDU2Nzg5MDEyMWEyYjNjNGQ1ZTEpMCcG\\r\\nA1UEQRMgYzZkNGE4NTRmZTdkNGI1MGE0OTg0ZjIyNzAwMTY3ZmExDTALBgNVBAwTBDY3ODkxTjBM\\r\\nBgNVBC4TRTE5NzVGYjUzMzkxYjRhMzRhNTkyNjRlMWRiZGE5MTNlMmIxMzk4ZWI3ZjZlNGQzNjFh\\r\\nZWEyZDUyNmE2NDU5MTcyMGY0MjBZMBMGByqGSM49AgEGCCqGSM49AwEHA0IABErjat0Zmfc859si\\r\\nvebvgqtapg2eRblXhs2UgV/y8+c5PJjB+Fl4vQYxKfcQhQ/2xw+dLuLAEbcyKDRhcLQ6kF2jggIj\\r\\nMIICHzAJBgNVHRMEAjAAMB0GA1UdDgQWBBQ7v33A3kQYi9Mt4Pez0NFHKtC7UDAfBgNVHSMEGDAW\\r\\ngBQOfKGV1trMIPMoT5S9d9Oh9DCLFjAOBgNVHQ8BAf8EBAMCBsAwOQYDVR0fBDIwMDAuoCygKoYo\\r\\naHR0cHM6Ly9lc2lnbi5jZGFjLmluL2NhL2VzaWduQ0EyMDE0LmNybDCCAT8GA1UdIASCATYwggEy\\r\\nMIIBAQYHYIJkZAEJAjCB9TAwBggrBgEFBQcCARYkaHR0cHM6Ly9lc2lnbi5jZGFjLmluL2NhL0NQ\\r\\nUy9DUFMucGRmMIHABggrBgEFBQcCAjCBszA+FjpDZW50cmUgZm9yIERldmVsb3BtZW50IG9mIEFk\\r\\ndmFuY2VkIENvbXB1dGluZyAoQy1EQUMpLCBQdW5lMAAacVRoaXMgQ1BTIGlzIG93bmVkIGJ5IEMt\\r\\nREFDIGFuZCB1c2VycyBhcmUgcmVxdWVzdGVkIHRvIHJlYWQgQ1BTIGJlZm9yZSB1c2luZyB0aGUg\\r\\nQy1EQUMgQ0EncyBjZXJ0aWZpY2F0aW9uIHNlcnZpY2VzMCsGB2CCZGQCBAEwIDAeBggrBgEFBQcC\\r\\nAjASGhBBYWRoYWFyIGVLWUMtT1RQMEQGCCsGAQUFBwEBBDgwNjA0BggrBgEFBQcwAoYoaHR0cHM6\\r\\nLy9lc2lnbi5jZGFjLmluL2NhL0NEQUMtQ0EyMDE0LmRlcjANBgkqhkiG9w0BAQsFAAOCAQEAksuX\\r\\nsQnwQU7mzKrvC8T0hrbHYGWhTsoL7QivLab+BSTrT53h9BcCHq0fxSB8rpDNcoXPKRGRSMYmm/+T\\r\\nxsoP6oyvDkBRYcvshX9ENyB80KLYhRjpYwlcdKmHs+cVm7smuSEU3g2o95ajwCg4ofWc/e0IC//H\\r\\naFl0RQKJxbR3mFTpiYARZ5jAygrz+Z+VefW7rhBSn9F4t0X9xpuntOisysH/r5RXhSvCS/SnsFuE\\r\\nVV6LcBWo7Iz0ATD+9o/I0mmDmMwS+7giXXAN5jQrgdsp+SFaqLeVvDZLRD3vilo9fJzx4DEYIXFo\\r\\n+mYQrnmmcSv0N17EnrAEyQBhhdsh5+eMUTGCAWAwggFcAgEBMIGFMH4xCzAJBgNVBAYTAklOMRQw\\r\\nEgYDVQQIDAtNYWhhcmFzaHRyYTENMAsGA1UEBwwEUFVORTEOMAwGA1UECgwFQy1EQUMxIjAgBgNV\\r\\nBAsMGVRlc3QgQ2VydGlmeWluZyBBdXRob3JpdHkxFjAUBgNVBAMMDVRlc3QgQy1EQUMgQ0ECAwNz\\r\\nXTANBglghkgBZQMEAgEFAKBpMBgGCSqGSIb3DQEJAzELBgkqhkiG9w0BBwEwHAYJKoZIhvcNAQkF\\r\\nMQ8XDTIzMTIxOTEyNTg1OFowLwYJKoZIhvcNAQkEMSIEINhIrw2WRMnp7gEQxvM2vDyJOkjf5EP5\\r\\nMiidMrn1PRGYMAwGCCqGSM49BAMCBQAERzBFAiEAo/wfRXd38+PMLRDTqNWkqtUItvS2CYK2CjeZ\\r\\nH0oKsrkCIEvfTur8IGymenayRuoBIDTsj9M7j9fbFQqtIZ1wyHdc\"}}";
	// 	System.out.println(data);
	// 	JSONObject json = new JSONObject(data);
	// 	JSONObject metaMain = new JSONObject();
	// 	metaMain.put("meta", json.get("meta").toString());
	// 	metaMain.put("main", json.get("main").toString());
	// 	System.out.println(metaMain);
	// }

	public ResponseModel getCropSownData(HttpServletRequest request, List<CropSownRequestDao> requestDao) {
		try {
			List<Object> responseObject = new ArrayList<>();
			for(CropSownRequestDao data : requestDao) {
				String response = transactionRequestMappingRepository.getCropSownData(data.getVillageLgdCode(),
						data.getSurveyNumber(), data.getSubSurveyNumber());

				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

				TypeFactory typeFactory = mapper.getTypeFactory();
				List<Object> resultObject = mapper.readValue(response,
						typeFactory.constructCollectionType(List.class, Object.class));
                responseObject.addAll(resultObject);
			}


			return new ResponseModel(responseObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
						CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED, CustomMessages.METHOD_POST);
		}
	}

	public ResponseModel getCropDetails(HttpServletRequest request, List<CropSownRequestDao> requestDao) {
		try {
			List<Object> responseObject = new ArrayList<>();
			for(CropSownRequestDao data : requestDao) {
				String response = transactionRequestMappingRepository.getCropDetails(data.getVillageLgdCode(),
						data.getSurveyNumber(), data.getSubSurveyNumber());

				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

				TypeFactory typeFactory = mapper.getTypeFactory();
				List<Object> resultObject = mapper.readValue(response,
						typeFactory.constructCollectionType(List.class, Object.class));
				responseObject.addAll(resultObject);
			}


			return new ResponseModel(responseObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED, CustomMessages.METHOD_POST);
		}
	}

	private Object getCropPhotoByteArray(TransactionRequestMapping transactionRequestMapping, ObjectMapper mapper,
										 OnSeekCommonResponseDAO resultObject, Object resData) throws IOException {
		String oMapper = Arrays.asList(transactionRequestMapping.getMapperId().split(":")).get(transactionRequestMapping.getMapperId().split(":").length - 1);
		if ("o16".equals(oMapper)) {
			TypeFactory typeFactory = mapper.getTypeFactory();
			List<O16OutputDAO> r = mapper.readValue(resultObject.getData(),
					typeFactory.constructCollectionType(List.class, O16OutputDAO.class));
			for (O16CropDetailsDAO o16CropDetailsDAO : r.get(0).getCropsowndetails()){
				List<Object> resourceList = new ArrayList<>();
				for (Object cropPhotoName: o16CropDetailsDAO.getCropPhotos()) {
					System.out.println(cropPhotoName);
					Resource resource = null;  //agristack_tMvFyqC4_166524228140260
//						resource=cloudStorageService.loadFileAsResource(String.valueOf(cropPhotoName), "agristack", "image");
					//agristack_N1Qqg3jf_15402682353496871
					switch (datastoreType) {
						case 1:
							resource = mediaMasterService.loadFileAsResource(String.valueOf(cropPhotoName), "agristack", "image");
							break;
						case 2:
							break;
						case 3:
							resource = cloudStorageService.loadFileAsResource(String.valueOf(cropPhotoName), "agristack", "image");

							break;
						default:
							break;
					}
					if (resource != null) {
						HashMap<String, Object> cropPhoto = new HashMap<>();
						byte[] media = IOUtils.toByteArray(resource.getInputStream());
						cropPhoto.put("file", media);
						cropPhoto.put("fileName",cropPhotoName);
//								resourceList.add(resource);
						resourceList.add(cropPhoto);
					}
				}
				o16CropDetailsDAO.setCropPhotos(resourceList);
			}
			resData = r;
		}

		if ("o1003".equals(oMapper)) {
			TypeFactory typeFactory = mapper.getTypeFactory();
			List<O1003OutputDAO> r = mapper.readValue(resultObject.getData(),
					typeFactory.constructCollectionType(List.class, O1003OutputDAO.class));
			for (O1003CropDetailsDAO o1003CropDetailsDAO : r.get(0).getCrop_sown_data()){
				List<Object> resourceList = new ArrayList<>();
				for (Object cropPhotoName: o1003CropDetailsDAO.getCrop_photo()) {
					System.out.println(cropPhotoName);
					Resource resource = null;  //agristack_tMvFyqC4_166524228140260
//						resource=cloudStorageService.loadFileAsResource(String.valueOf(cropPhotoName), "agristack", "image");
					//agristack_N1Qqg3jf_15402682353496871
					switch (datastoreType) {
						case 1:
							resource = mediaMasterService.loadFileAsResource(String.valueOf(cropPhotoName), "agristack", "image");
							break;
						case 2:
							break;
						case 3:
							resource = cloudStorageService.loadFileAsResource(String.valueOf(cropPhotoName), "agristack", "image");

							break;
						default:
							break;
					}
					if (resource != null) {
						HashMap<String, Object> cropPhoto = new HashMap<>();
						byte[] media = IOUtils.toByteArray(resource.getInputStream());
						cropPhoto.put("file", media);
						cropPhoto.put("fileName",cropPhotoName);
//								resourceList.add(resource);
						resourceList.add(cropPhoto);
					}
				}
				o1003CropDetailsDAO.setCrop_photo(resourceList);
			}
			resData = r;
		}

		if ("o1005".equals(oMapper)) {
			TypeFactory typeFactory = mapper.getTypeFactory();
			List<O1005OutputDAO> r = mapper.readValue(resultObject.getData(),
					typeFactory.constructCollectionType(List.class, O1005OutputDAO.class));
			for (CropDetailsOutputDAO cropDetailsOutputDAO : r.get(0).getCrop_sown_data()){
				List<Object> resourceList = new ArrayList<>();
				for (Object cropPhotoName: cropDetailsOutputDAO.getCrop_photo()) {
					System.out.println(cropPhotoName);
					Resource resource = null;  //agristack_tMvFyqC4_166524228140260
//						resource=cloudStorageService.loadFileAsResource(String.valueOf(cropPhotoName), "agristack", "image");
					//agristack_N1Qqg3jf_15402682353496871
					switch (datastoreType) {
						case 1:
							resource = mediaMasterService.loadFileAsResource(String.valueOf(cropPhotoName), "agristack", "image");
							break;
						case 2:
							break;
						case 3:
							resource = cloudStorageService.loadFileAsResource(String.valueOf(cropPhotoName), "agristack", "image");

							break;
						default:
							break;
					}
					if (resource != null) {
						HashMap<String, Object> cropPhoto = new HashMap<>();
						byte[] media = IOUtils.toByteArray(resource.getInputStream());
						cropPhoto.put("file", media);
						cropPhoto.put("fileName",cropPhotoName);
//								resourceList.add(resource);
						resourceList.add(cropPhoto);
					}
				}
				cropDetailsOutputDAO.setCrop_photo(resourceList);
			}
			resData = r;
		}

		if ("o1006".equals(oMapper)) {
			TypeFactory typeFactory = mapper.getTypeFactory();
			List<O1006OutputDAO> r = mapper.readValue(resultObject.getData(),
					typeFactory.constructCollectionType(List.class, O1006OutputDAO.class));
			for (CropDetailsOutputDAO cropDetailsOutputDAO : r.get(0).getCrop_sown_data()){
				List<Object> resourceList = new ArrayList<>();
				for (Object cropPhotoName: cropDetailsOutputDAO.getCrop_photo()) {
					System.out.println(cropPhotoName);
					Resource resource = null;  //agristack_tMvFyqC4_166524228140260
//						resource=cloudStorageService.loadFileAsResource(String.valueOf(cropPhotoName), "agristack", "image");
					//agristack_N1Qqg3jf_15402682353496871
					switch (datastoreType) {
						case 1:
							resource = mediaMasterService.loadFileAsResource(String.valueOf(cropPhotoName), "agristack", "image");
							break;
						case 2:
							break;
						case 3:
							resource = cloudStorageService.loadFileAsResource(String.valueOf(cropPhotoName), "agristack", "image");

							break;
						default:
							break;
					}
					if (resource != null) {
						HashMap<String, Object> cropPhoto = new HashMap<>();
						byte[] media = IOUtils.toByteArray(resource.getInputStream());
						cropPhoto.put("file", media);
						cropPhoto.put("fileName",cropPhotoName);
//								resourceList.add(resource);
						resourceList.add(cropPhoto);
					}
				}
				cropDetailsOutputDAO.setCrop_photo(resourceList);
			}
			resData = r;
		}

		if ("o1007".equals(oMapper)) {
			TypeFactory typeFactory = mapper.getTypeFactory();
			List<O1007OutputDAO> r = mapper.readValue(resultObject.getData(),
					typeFactory.constructCollectionType(List.class, O1007OutputDAO.class));
			for (CropDetailsOutputDAO cropDetailsOutputDAO : r.get(0).getCrop_sown_data()){
				List<Object> resourceList = new ArrayList<>();
				for (Object cropPhotoName: cropDetailsOutputDAO.getCrop_photo()) {
					System.out.println(cropPhotoName);
					Resource resource = null;  //agristack_tMvFyqC4_166524228140260
//						resource=cloudStorageService.loadFileAsResource(String.valueOf(cropPhotoName), "agristack", "image");
					//agristack_N1Qqg3jf_15402682353496871
					switch (datastoreType) {
						case 1:
							resource = mediaMasterService.loadFileAsResource(String.valueOf(cropPhotoName), "agristack", "image");
							break;
						case 2:
							break;
						case 3:
							resource = cloudStorageService.loadFileAsResource(String.valueOf(cropPhotoName), "agristack", "image");

							break;
						default:
							break;
					}
					if (resource != null) {
						HashMap<String, Object> cropPhoto = new HashMap<>();
						byte[] media = IOUtils.toByteArray(resource.getInputStream());
						cropPhoto.put("file", media);
						cropPhoto.put("fileName",cropPhotoName);
//								resourceList.add(resource);
						resourceList.add(cropPhoto);
					}
				}
				cropDetailsOutputDAO.setCrop_photo(resourceList);
			}
			resData = r;
		}
		return resData;
	}


	public List<Object> getOwnerAndCropAndAreaData(List<CropSownRequestDao> requestDAO) {
		List<Object> responseObject = new ArrayList<>();
		for(CropSownRequestDao data : requestDAO) {
			List<SowingSeason> season = seasonMasterRepository.findByIsDeletedFalseAndSeason(data.getSeason());

			String response = transactionRequestMappingRepository.getOwnerAndCropAndAreaData(data.getVillageLgdCode(),
					data.getSurveyNumber(), data.getSubSurveyNumber()!=null ? data.getSubSurveyNumber() : "", season.isEmpty()?2:season.get(0).getSeasonId(), data.getYear());

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
            List<Object> resultObject = null;
            try {
                resultObject = mapper.readValue(response,
                        typeFactory.constructCollectionType(List.class, Object.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            responseObject.addAll(resultObject);
		}


		return responseObject;
	}

	public ResponseModel getCropSownDataForFr(HttpServletRequest request, List<CropSownRequestDao> requestDAO) {
		try {
			List<Object> responseObject = new ArrayList<>();

			if (!requestDAO.isEmpty()) {
				String mapperId = requestDAO.get(0).getMapper();

				if ("o9".equalsIgnoreCase(mapperId)){
					responseObject = getOwnerAndCropAndAreaData(requestDAO);
				} else if ("o1003".equalsIgnoreCase(mapperId)){
					responseObject = getO3CropSownData(requestDAO);
				} else{
					responseObject = getCommonCropSownData(requestDAO);
				}
			}

			return new ResponseModel(responseObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED, CustomMessages.METHOD_POST);
		}

	}

	private List<Object> getO3CropSownData(List<CropSownRequestDao> requestDAO) {
		List<Object> responseObject = new ArrayList<>();
		for(CropSownRequestDao data : requestDAO) {

			String response = transactionRequestMappingRepository.getO3CropSownData(data.getSurveyNumber(),data.getStateLgdCode(),
					data.getVillageLgdCode(), data.getYear(), data.getSeason());

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<Object> resultObject = null;
			try {
				resultObject = mapper.readValue(response,
						typeFactory.constructCollectionType(List.class, Object.class));
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
			responseObject.addAll(resultObject);
		}


		return responseObject;
	}

	private List<Object> getCommonCropSownData(List<CropSownRequestDao> requestDAO) {
		List<Object> responseObject = new ArrayList<>();
		for(CropSownRequestDao data : requestDAO) {

			String response = transactionRequestMappingRepository.getCommonCropSownData(data.getSurveyNumber(),data.getStateLgdCode(),
					data.getVillageLgdCode(), data.getYear(), data.getSeason());

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<Object> resultObject = null;
			try {
				resultObject = mapper.readValue(response,
						typeFactory.constructCollectionType(List.class, Object.class));
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
			responseObject.addAll(resultObject);
		}


		return responseObject;
	}


}