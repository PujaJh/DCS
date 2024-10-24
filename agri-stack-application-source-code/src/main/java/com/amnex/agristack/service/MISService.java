package com.amnex.agristack.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.FarmlandPlotRegistryDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.dao.common.ResponseWrapper;
import com.amnex.agristack.entity.SowingSeason;
import com.amnex.agristack.entity.UserMaster;
import com.amnex.agristack.entity.ValidationLog;
import com.amnex.agristack.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.dao.CommonRequestDAO;
import com.amnex.agristack.dao.CropDistributionDTO;
import com.amnex.agristack.dao.SqlData;
import com.amnex.agristack.utils.CustomMessages;
import com.amnex.agristack.utils.DBUtils;
import com.amnex.agristack.utils.ResponseMessages;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import reactor.core.publisher.Mono;

@Service
public class MISService {

	@Autowired
	DBUtils dbUtils;

	@Autowired
	FarmlandPlotRegistryRepository farmlandPlotRegistryRepository;

	@Autowired
	MISDashboardRepository misDashboardRepository;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	SeasonMasterRepository seasonMasterRepository;

	@Autowired
	ValidationLogRepository validationLogRepository;

	@Autowired
	StateLgdMasterRepository stateLgdMasterRepository;

	// Retrieves crop distribution data based on crop
	public Object getCropDistributionByCrop(CropDistributionDTO cropDistributionDTO) {
		// Stored procedure name
		String fnName = "agri_stack.fn_mis_get_crop_distribution_by_crop";
		// Parameters for the stored procedure
		SqlData[] params = { new SqlData(1, cropDistributionDTO.getUserId().toString(), "int"),
				new SqlData(2, cropDistributionDTO.getCropId(), "string"),
				new SqlData(3, cropDistributionDTO.getStartYear().toString(), "int"),
				new SqlData(4, cropDistributionDTO.getEndYear().toString(), "int"),
				new SqlData(5, cropDistributionDTO.getSeasonId().toString(), "int"), };

		return dbUtils.executeStoredProcedure(fnName, "{}", params);

	}

	// Retrieves crop distribution data based on crop variety
	public Object getCropDistributionByCropVariety(CropDistributionDTO cropDistributionDTO) {
		// Stored procedure name
		String fnName = "agri_stack.fn_mis_get_crop_distribution_by_crop_variety";
		// Parameters for the stored procedure
		SqlData[] params = { new SqlData(1, cropDistributionDTO.getUserId().toString(), "int"),
				new SqlData(2, cropDistributionDTO.getCropId(), "string"),
//        		new SqlData(3, cropDistributionDTO.getCropVarietyId(), "string"),
				new SqlData(3, cropDistributionDTO.getStartYear().toString(), "int"),
				new SqlData(4, cropDistributionDTO.getEndYear().toString(), "int"),
				new SqlData(5, cropDistributionDTO.getSeasonId().toString(), "int"),};

		return dbUtils.executeStoredProcedure(fnName, "{}", params);

	}

	// Retrieves crop distribution data based on territory
	public Object getCropDistributionByTerritory(CropDistributionDTO cropDistributionDTO) {
		// Stored procedure name
		String fnName = "agri_stack.fn_mis_get_crop_distribution_by_territory";
		// Parameters for the stored procedure
		SqlData[] params = { new SqlData(1, cropDistributionDTO.getUserId().toString(), "int"),
				new SqlData(2, cropDistributionDTO.getCropId(), "string"),
				new SqlData(3, cropDistributionDTO.getTerritoryLevel(), "string"),
				new SqlData(4, cropDistributionDTO.getStartYear().toString(), "int"),
				new SqlData(5, cropDistributionDTO.getEndYear().toString(), "int"),
				new SqlData(6, cropDistributionDTO.getSeasonId().toString(), "int"), };

		return dbUtils.executeStoredProcedure(fnName, "{}", params);

	}

	// Retrieves surveyor accuracy data based on territory
	public Object getSurveyorAccuracyByTerritory(CropDistributionDTO cropDistributionDTO) {
		// Stored procedure name
		String fnName = "agri_stack.fn_mis_get_surveyor_accuracy_by_territory";
		// Parameters for the stored procedure
		SqlData[] params = { new SqlData(1, cropDistributionDTO.getUserId().toString(), "int"),
				new SqlData(2, cropDistributionDTO.getTerritoryLevel(), "string"),
				new SqlData(3, cropDistributionDTO.getStartYear().toString(), "int"),
				new SqlData(4, cropDistributionDTO.getEndYear().toString(), "int"),
				new SqlData(5, cropDistributionDTO.getSeasonId().toString(), "int"), };

		return dbUtils.executeStoredProcedure(fnName, "{}", params);

	}

	// Retrieves farmer data based on territory
	public Object getFarmerByTerritory(CropDistributionDTO cropDistributionDTO) {
		// Stored procedure name
		String fnName = "agri_stack.fn_mis_get_farmer_grievance_by_territory";
		// Parameters for the stored procedure
		SqlData[] params = { new SqlData(1, cropDistributionDTO.getUserId().toString(), "int"),
				new SqlData(2, cropDistributionDTO.getTerritoryLevel(), "string"),
				new SqlData(3, cropDistributionDTO.getStartYear().toString(), "int"),
				new SqlData(4, cropDistributionDTO.getEndYear().toString(), "int"),
				new SqlData(5, cropDistributionDTO.getSeasonId().toString(), "int"),};

		return dbUtils.executeStoredProcedure(fnName, "{}", params);

	}

	// Retrieves village geometry data by village LGD code list
	public ResponseEntity<?> getVillageGeom(FarmlandPlotRegistryDAO farmlandPlotRegistryDAO,
											HttpServletRequest request) {
		try {
			// Get village geometry by village LGD code list
			List<Object> villageGeom = farmlandPlotRegistryRepository
					.getVillageGeometryByVillageLgdCodeList(farmlandPlotRegistryDAO.getVillageLgdCodeList());

			// Return the village geometry as a response with a success message
			return new ResponseEntity<String>(
					CustomMessages.getMessageWithData(CustomMessages.ADD_SUCCESSFULLY, villageGeom), HttpStatus.OK);
		} catch (Exception e) {
			// Return an error response with the exception message
			return new ResponseEntity<Mono<?>>(
					Mono.just(ResponseMessages.Toast(ResponseMessages.INTERNAL_SERVER_ERROR, e.getMessage(), null)),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	public ResponseModel getVillageLevelCropDetails(CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {

			String userId = CustomMessages.getUserId(request, jwtTokenUtil);

			userId = (userId != null && !userId.isEmpty()) ? userId : "0";

			String stateList = getJoinedValuefromList(requestDAO.getStateLgdCodeList());
			String districtList = getJoinedValuefromList(requestDAO.getDistrictLgdCodeList());
			String subDistrictList = getJoinedValuefromList(requestDAO.getSubDistrictLgdCodeList());
			String villageList = getJoinedValuefromList(requestDAO.getVillageLgdCodeList());
			String statusList = getJoinedValuefromList(requestDAO.getStatusCodeList());

			String response = misDashboardRepository.getVillageLevelCropDetails(Integer.valueOf(userId), stateList,
					districtList, subDistrictList, villageList, requestDAO.getSeasonId(), requestDAO.getStartYear(),
					requestDAO.getEndYear(), statusList, requestDAO.getStartDate(), requestDAO.getEndDate());

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

	private String getJoinedValuefromList(List<Long> list) {
		try {
			return list.stream().map(i -> i.toString()).collect(Collectors.joining(", "));
		} catch (Exception e) {
			return "";
		}
	}

	public ResponseModel getSurveySummaryDetails(CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {

			String userId = CustomMessages.getUserId(request, jwtTokenUtil);

			userId = (userId != null && !userId.isEmpty()) ? userId : "0";

			String stateList = getJoinedValuefromList(requestDAO.getStateLgdCodeList());
			String districtList = getJoinedValuefromList(requestDAO.getDistrictLgdCodeList());
			String subDistrictList = getJoinedValuefromList(requestDAO.getSubDistrictLgdCodeList());
			String villageList = getJoinedValuefromList(requestDAO.getVillageLgdCodeList());
			String statusList = getJoinedValuefromList(requestDAO.getStatusCodeList());

//			String response = misDashboardRepository.getSurveySummaryDetails(Integer.valueOf(userId), stateList,
//					districtList, subDistrictList, villageList, requestDAO.getSeasonId(), requestDAO.getStartYear(),
//					requestDAO.getEndYear(), statusList);
			
			String response = misDashboardRepository.getSurveySummaryDetailsDateWise(Integer.valueOf(userId), stateList,
					districtList, subDistrictList, villageList, requestDAO.getSeasonId(), requestDAO.getStartYear(),
					requestDAO.getEndYear(), statusList,requestDAO.getStartDate(),requestDAO.getEndDate());

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

	public ResponseModel getSurveyorSurveyDetails(CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {

			String userId = CustomMessages.getUserId(request, jwtTokenUtil);

			userId = (userId != null && !userId.isEmpty()) ? userId : "0";

			String stateList = getJoinedValuefromList(requestDAO.getStateLgdCodeList());
			String districtList = getJoinedValuefromList(requestDAO.getDistrictLgdCodeList());
			String subDistrictList = getJoinedValuefromList(requestDAO.getSubDistrictLgdCodeList());
			String villageList = getJoinedValuefromList(requestDAO.getVillageLgdCodeList());
			String statusList = getJoinedValuefromList(requestDAO.getStatusCodeList());

			String response = misDashboardRepository.getSurveyorSurveyDetails(Integer.valueOf(userId), stateList,
					districtList, subDistrictList, villageList, requestDAO.getSeasonId(), requestDAO.getStartYear(),
					requestDAO.getEndYear(), statusList, requestDAO.getStartDate(), requestDAO.getEndDate());

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

	public ResponseModel getSurveyorActivityDetails(CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {

			String userId = CustomMessages.getUserId(request, jwtTokenUtil);

			userId = (userId != null && !userId.isEmpty()) ? userId : "0";

			String stateList = getJoinedValuefromList(requestDAO.getStateLgdCodeList());
			String districtList = getJoinedValuefromList(requestDAO.getDistrictLgdCodeList());
			String subDistrictList = getJoinedValuefromList(requestDAO.getSubDistrictLgdCodeList());
			String villageList = getJoinedValuefromList(requestDAO.getVillageLgdCodeList());
			String statusList = getJoinedValuefromList(requestDAO.getStatusCodeList());

			String response = misDashboardRepository.getSurveyorActivityDetails(Integer.valueOf(userId), stateList,
					districtList, subDistrictList, villageList, requestDAO.getSeasonId(), requestDAO.getStartYear(),
					requestDAO.getEndYear(), statusList, requestDAO.getStartDate(), requestDAO.getEndDate());

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

	public ResponseModel getCadastralMapSurveyDetail(CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {

			String userId = CustomMessages.getUserId(request, jwtTokenUtil);

			userId = (userId != null && !userId.isEmpty()) ? userId : "0";

			String stateList = getJoinedValuefromList(requestDAO.getStateLgdCodeList());
			String districtList = getJoinedValuefromList(requestDAO.getDistrictLgdCodeList());
			String subDistrictList = getJoinedValuefromList(requestDAO.getSubDistrictLgdCodeList());
			String villageList = getJoinedValuefromList(requestDAO.getVillageLgdCodeList());
			
			String response = misDashboardRepository.getCadastralMapSurveyDetails(Integer.valueOf(userId), stateList,
					districtList, subDistrictList, villageList, requestDAO.getStartDate(),
					requestDAO.getEndDate());

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
	public ResponseModel surveyDataDetailsOfCropMedia(CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {
			String districtList = getJoinedValuefromList(requestDAO.getDistrictLgdCodeList());
			String subDistrictList = getJoinedValuefromList(requestDAO.getSubDistrictLgdCodeList());
			String villageList = getJoinedValuefromList(requestDAO.getVillageLgdCodeList());
			String  response = misDashboardRepository.getsureyDataDetailsOfCropMedia(districtList, subDistrictList,villageList);
			
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

	public ResponseModel getCultivatedAggregatedDetails(CommonRequestDAO requestDAO, HttpServletRequest request) {
		
		try {

			String userId = CustomMessages.getUserId(request, jwtTokenUtil);

			userId = (userId != null && !userId.isEmpty()) ? userId : "0";
			System.out.println("userId "+userId);
			
//			if(requestDAO.getSearch()==null) {
//				requestDAO.setSearch("");
//			}
//
//			String stateList = getJoinedValuefromList(requestDAO.getStateLgdCodeList());
//			String districtList = getJoinedValuefromList(requestDAO.getDistrictLgdCodeList());
//			String subDistrictList = getJoinedValuefromList(requestDAO.getSubDistrictLgdCodeList());
//			String villageList = getJoinedValuefromList(requestDAO.getVillageLgdCodeList());

//			String response = misDashboardRepository.getCultivatedAggregatedDetails(userId, stateList,
//					districtList, subDistrictList, villageList, requestDAO.getSeasonId(), requestDAO.getStartYear(),
//					requestDAO.getEndYear(),Long.valueOf(requestDAO.getPage()),Long.valueOf(requestDAO.getLimit()),requestDAO.getSortField(),
//					requestDAO.getSortOrder(),requestDAO.getSearch());
			
//			misDashboardRepository.getCultivatedAggregatedDetails(userId, "state", requestDAO.getSeasonId(), requestDAO.getStartYear(),requestDAO.getEndYear(), 1l);
			Integer startYear = requestDAO.getStartYear();
			Integer endYear = requestDAO.getEndYear();
			if(requestDAO.getStartYear() == null)
				startYear =2024;
			if(requestDAO.getEndYear() == null)
				endYear = 2025;

			String response = misDashboardRepository.getCultivatedAggregatedDetails(userId,requestDAO.getTerritoryLevel(), requestDAO.getSeasonId(), startYear,endYear, requestDAO.getCode());

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


	public ResponseModel getCultivatedAggregatedSummary(Map<String,Object> requestDAO, HttpServletRequest request) {

		try {

			String userId = "1171175";//CustomMessages.getUserId(request, jwtTokenUtil);

			String requestTokenHeader = request.getHeader("Authorization");
			System.out.println("requestTokenHeader "+requestTokenHeader);

			if ( requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {

				String jwtToken = requestTokenHeader.substring(7);
				if(!jwtToken.equals(getActiveTokenByServiceName("central_api_access")))
					return new ResponseModel(null, CustomMessages.UNAUTHORIZED_MESSAGE, CustomMessages.UNAUTHORIZED_ERROR,
							CustomMessages.UNAUTHORIZED, CustomMessages.METHOD_POST);

			}
			if ( requestTokenHeader != null && !requestTokenHeader.startsWith("Bearer ")) {

				String jwtToken = requestTokenHeader.substring(7);
				if(!jwtToken.equals(getActiveTokenByServiceName("central_api_access")))
					return new ResponseModel(null, CustomMessages.UNAUTHORIZED_MESSAGE, CustomMessages.UNAUTHORIZED_ERROR,
							CustomMessages.UNAUTHORIZED, CustomMessages.METHOD_POST);

			}

			if ( requestTokenHeader == null) {
				return new ResponseModel(null, CustomMessages.UNAUTHORIZED_MESSAGE, CustomMessages.UNAUTHORIZED_ERROR,
						CustomMessages.UNAUTHORIZED, CustomMessages.METHOD_POST);

			}

			userId = (userId != null && !userId.isEmpty()) ? userId : "0";

//			if(requestDAO.getSearch()==null) {
//				requestDAO.setSearch("");
//			}
//
//			String stateList = getJoinedValuefromList(requestDAO.getStateLgdCodeList());
//			String districtList = getJoinedValuefromList(requestDAO.getDistrictLgdCodeList());
//			String subDistrictList = getJoinedValuefromList(requestDAO.getSubDistrictLgdCodeList());
//			String villageList = getJoinedValuefromList(requestDAO.getVillageLgdCodeList());

//			String response = misDashboardRepository.getCultivatedAggregatedDetails(userId, stateList,
//					districtList, subDistrictList, villageList, requestDAO.getSeasonId(), requestDAO.getStartYear(),
//					requestDAO.getEndYear(),Long.valueOf(requestDAO.getPage()),Long.valueOf(requestDAO.getLimit()),requestDAO.getSortField(),
//					requestDAO.getSortOrder(),requestDAO.getSearch());

//			misDashboardRepository.getCultivatedAggregatedDetails(userId, "state", requestDAO.getSeasonId(), requestDAO.getStartYear(),requestDAO.getEndYear(), 1l);
//			Integer startYear = requestDAO.getStartYear();
//			Integer endYear = requestDAO.getEndYear();
//			if(requestDAO.getStartYear() == null)
//				startYear =2024;
//			if(requestDAO.getEndYear() == null)
//				endYear = 2025;
			System.out.println(requestDAO);


			Integer stateLgdCode = (Integer) requestDAO.get("state_lgd_code");
			String seasonName = (String) requestDAO.get("season");
			String year = (String) requestDAO.get("year");
			String lastSyncDate = (String) requestDAO.get("last_sync_date");
			// Assuming requestDAO is your Map<String, Object>
			Map<String, Object> pagination = (Map<String, Object>) requestDAO.get("pagination");

// Extracting page_number and page_size
			int pageNumber = (int) pagination.get("page_number");
			int pageSize = (int) pagination.get("page_size");

// Now you can use pageNumber and pageSize variables
			System.out.println("Page Number: " + pageNumber);
			System.out.println("Page Size: " + pageSize);

//			Integer pageSize = (Integer) requestDAO.get("page_size");
//			Integer pageNumber = (Integer) requestDAO.get("page_number");

			int seasonStartYear = Integer.parseInt(year.split("-")[0]);
			int seasonEndYear = Integer.parseInt(year.split("-")[1]);



			List<SowingSeason> seasons = seasonMasterRepository.findByIsDeletedFalseAndSeasonName(seasonName);

			userId = (userId != null && !userId.isEmpty()) ? userId : "2";
			//Optional<UserMaster> op = userRepository.findByUserIdAndIsDeletedAndIsActive(Long.valueOf(userId), false, true);
			UserMaster userMaster= new UserMaster();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

			// Parse the string to LocalDateTime
//			LocalDateTime localDateTime = LocalDateTime.parse(lastSyncDate, formatter);
//			Timestamp timestamp = Timestamp.valueOf(localDateTime);
//			System.out.println("timestamp "+timestamp);


			// Convert LocalDateTime to Timestamp
			String response = misDashboardRepository.getCultivatedAggregatedDetails_v2("village", 0L,pageNumber,pageSize);//timestamp);

			String total_count=misDashboardRepository.getCultivatedAggregatedTotalCount("village", 0L);
			pagination.put("total_count",total_count);
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<Object> resultObject = mapper.readValue(response,
					typeFactory.constructCollectionType(List.class, Object.class));

			ResponseWrapper responseWrapper = new ResponseWrapper();
			responseWrapper.setResultData(resultObject);
			//responseWrapper.setParameters(requestDAO);

			Map<String,Object> resultMap = new HashMap<>();
			resultMap.put("year",year);
			resultMap.put("season",seasonName);
			resultMap.put("state_lgd_code",stateLgdCode);
			resultMap.put("survey_details",resultObject);
//			resultMap.put("page_size",pageSize);
//			resultMap.put("page_limit",pageNumber);
			resultMap.put("pagination",pagination);
			//resultMap.put("total_count",total_record);
			Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
			resultMap.put("timestamp",currentTimestamp);




			return new ResponseModel(resultMap, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	public ResponseModel getCultivatedSummaryDetails(CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {

			String userId = CustomMessages.getUserId(request, jwtTokenUtil);

			userId = (userId != null && !userId.isEmpty()) ? userId : "0";
			
			String response = misDashboardRepository.getCultivatedSummaryDetails(userId,requestDAO.getTerritoryLevel(), requestDAO.getSeasonId(), requestDAO.getStartYear(),requestDAO.getEndYear(), requestDAO.getCode());

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


	public ResponseModel getCultivatedSummaryReport(Map<String ,Object> requestDAO, HttpServletRequest request) {
		try {

			String userId ="1171175";// CustomMessages.getUserId(request, jwtTokenUtil);
			System.out.println("request "+request);
			String requestTokenHeader = request.getHeader("Authorization");
			System.out.println("requestTokenHeader "+requestTokenHeader);

			if ( requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {

				String jwtToken = requestTokenHeader.substring(7);
				if(!jwtToken.equals(getActiveTokenByServiceName("central_api_access")))
					return new ResponseModel(null, CustomMessages.UNAUTHORIZED_MESSAGE, CustomMessages.UNAUTHORIZED_ERROR,
							CustomMessages.UNAUTHORIZED, CustomMessages.METHOD_POST);

			}
			if ( requestTokenHeader != null && !requestTokenHeader.startsWith("Bearer ")) {

				String jwtToken = requestTokenHeader.substring(7);
				if(!jwtToken.equals(getActiveTokenByServiceName("central_api_access")))
					return new ResponseModel(null, CustomMessages.UNAUTHORIZED_MESSAGE, CustomMessages.UNAUTHORIZED_ERROR,
							CustomMessages.UNAUTHORIZED, CustomMessages.METHOD_POST);

			}

			if ( requestTokenHeader == null) {
				return new ResponseModel(null, CustomMessages.UNAUTHORIZED_MESSAGE, CustomMessages.UNAUTHORIZED_ERROR,
						CustomMessages.UNAUTHORIZED, CustomMessages.METHOD_POST);

			}



				userId = (userId != null && !userId.isEmpty()) ? userId : "0";
			Integer stateLgdCode = (Integer) requestDAO.get("state_lgd_code");
			String seasonName = (String) requestDAO.get("season");
			String year = (String) requestDAO.get("year");
			String lastSyncDate = (String) requestDAO.get("last_sync_date");

			// Assuming requestDAO is your Map<String, Object>
			Map<String, Object> pagination = (Map<String, Object>) requestDAO.get("pagination");

			// Extracting page_number and page_size
			int pageNumber = (int) pagination.get("page_number");
			int pageSize = (int) pagination.get("page_size");

// Now you can use pageNumber and pageSize variables
			System.out.println("Page Number: " + pageNumber);
			System.out.println("Page Size: " + pageSize);

//			Integer pageSize = (Integer) requestDAO.get("page_size");
//			Integer pageNumber = (Integer) requestDAO.get("page_number");

			int seasonStartYear = Integer.parseInt(year.split("-")[0]);
			int seasonEndYear = Integer.parseInt(year.split("-")[1]);

			List<SowingSeason> seasons = seasonMasterRepository.findByIsDeletedFalseAndSeasonName(seasonName);

//			String stateLgdcode = StateLgdMasterRepository.

			userId = (userId != null && !userId.isEmpty()) ? userId : "2";
			//Optional<UserMaster> op = userRepository.findByUserIdAndIsDeletedAndIsActive(Long.valueOf(userId), false, true);


			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

			// Parse the string to LocalDateTime
			LocalDateTime localDateTime = LocalDateTime.parse(lastSyncDate, formatter);
			Timestamp timestamp = Timestamp.valueOf(localDateTime);
			System.out.println("timestamp "+timestamp);
			

			//System.out.println(requestDAO.getLast_sync_date());

			String response = misDashboardRepository.getCultivatedSummaryDetails_v3( "village", 1, seasonStartYear, seasonEndYear, 0L, pageNumber, pageSize);
//			String count = misDashboardRepository.getCultivatedSummaryDetailsCount(userId, "village", 1, seasonStartYear, seasonEndYear, 0L,timestamp);
			String total_count=misDashboardRepository.getCultivatedAggregatedTotalCount("village", 0L);

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<Object> resultObject = mapper.readValue(response,
					typeFactory.constructCollectionType(List.class, Object.class));

			pagination.put("total_count",total_count);

			Map<String,Object> resultMap = new HashMap<>();

			resultMap.put("area_details",resultObject);
			resultMap.put("pagination",pagination);
//			resultMap.put("page_size",pageNumber);
//			resultMap.put("page_limit",pageNumber);
			resultMap.put("year",year);
			resultMap.put("season",seasonName);
			resultMap.put("state_lgd_code",stateLgdCode);
			//resultMap.put("total_count",count);
			Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
			LocalDateTime timestamp1 = LocalDateTime.parse(lastSyncDate, formatter);
			resultMap.put("timestamp",timestamp1);

			ResponseWrapper responseWrapper = new ResponseWrapper();
			responseWrapper.setResultData(resultObject);
			//responseWrapper.setParameters(requestDAO);


			return new ResponseModel(resultMap, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);


//			return new ResponseModel(resultObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
//					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}
	public ResponseModel getCultivatedCropCategoryDetails(CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {
			String userId = CustomMessages.getUserId(request, jwtTokenUtil);

			userId = (userId != null && !userId.isEmpty()) ? userId : "0";
			
			String response = misDashboardRepository.getCultivatedCropCategoryDetails(userId,requestDAO.getTerritoryLevel(), requestDAO.getSeasonId(), requestDAO.getStartYear(),requestDAO.getEndYear(), requestDAO.getCode(), requestDAO.getTypeId());

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

	public ResponseModel getUsermanagementReport(CommonRequestDAO requestDAO, HttpServletRequest request) {
		try{
			String userId = CustomMessages.getUserId(request, jwtTokenUtil);
			userId = (userId != null && !userId.isEmpty()) ? userId : "0";

			String response = misDashboardRepository.getUsermanagementReport(userId,requestDAO.getTerritoryLevel(), requestDAO.getCode());

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<Object> resultObject = mapper.readValue(response,
					typeFactory.constructCollectionType(List.class, Object.class));

			return new ResponseModel(resultObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

		}catch(Exception e){
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	public ResponseModel getSurveyorProgressDetails(CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {

			String userId = CustomMessages.getUserId(request, jwtTokenUtil);

			userId = (userId != null && !userId.isEmpty()) ? userId : "0";

			String districtList = getJoinedValuefromList(requestDAO.getDistrictLgdCodeList());
			String subDistrictList = getJoinedValuefromList(requestDAO.getSubDistrictLgdCodeList());
			String villageList = getJoinedValuefromList(requestDAO.getVillageLgdCodeList());
			
			String response = misDashboardRepository.getSurveyorProgressDetails(Integer.valueOf(userId), requestDAO.getSeasonId(), requestDAO.getStartYear(),
					requestDAO.getEndYear(), districtList,subDistrictList,villageList);
			

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

	public ResponseModel getCropAreaData(Map<String, Object> requestDAO, HttpServletRequest request) {
		try {
			Integer stateLgdCode = (Integer) requestDAO.get("state_lgd_code");
			String seasonName = (String) requestDAO.get("season");
			String year = (String) requestDAO.get("year");
			String lastSyncDate = (String) requestDAO.get("last_sync_date");
			int seasonStartYear = Integer.parseInt(year.split("-")[0]);
			int seasonEndYear = Integer.parseInt(year.split("-")[1]);
// Assuming requestDAO is your Map<String, Object>
			Map<String, Object> pagination = (Map<String, Object>) requestDAO.get("pagination");

// Extracting page_number and page_size
			int pageNumber = (int) pagination.get("page_number");
			int pageSize = (int) pagination.get("page_size");

// Now you can use pageNumber and pageSize variables
			System.out.println("Page Number: " + pageNumber);
			System.out.println("Page Size: " + pageSize);

			List<SowingSeason> seasons = seasonMasterRepository.findByIsDeletedFalseAndSeasonName(seasonName);
			String requestTokenHeader = request.getHeader("Authorization");
			System.out.println("requestTokenHeader "+requestTokenHeader);

			if ( requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {

				String jwtToken = requestTokenHeader.substring(7);
				if(!jwtToken.equals(getActiveTokenByServiceName("central_api_access")))
					return new ResponseModel(null, CustomMessages.UNAUTHORIZED_MESSAGE, CustomMessages.UNAUTHORIZED_ERROR,
							CustomMessages.UNAUTHORIZED, CustomMessages.METHOD_POST);

			}
			if ( requestTokenHeader != null && !requestTokenHeader.startsWith("Bearer ")) {

				String jwtToken = requestTokenHeader.substring(7);
				if(!jwtToken.equals(getActiveTokenByServiceName("central_api_access")))
					return new ResponseModel(null, CustomMessages.UNAUTHORIZED_MESSAGE, CustomMessages.UNAUTHORIZED_ERROR,
							CustomMessages.UNAUTHORIZED, CustomMessages.METHOD_POST);

			}

			if ( requestTokenHeader == null) {
				return new ResponseModel(null, CustomMessages.UNAUTHORIZED_MESSAGE, CustomMessages.UNAUTHORIZED_ERROR,
						CustomMessages.UNAUTHORIZED, CustomMessages.METHOD_POST);

			}

			String response = misDashboardRepository.getCropAreaData(stateLgdCode, seasonName, seasonStartYear, seasonEndYear, pageSize, pageNumber,lastSyncDate);
			String total_count = misDashboardRepository.getCropTotalCount(stateLgdCode, seasonName, seasonStartYear, seasonEndYear);

			System.out.println("response "+response);
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			List<Object> resultObject = new ArrayList<>();
			TypeFactory typeFactory = mapper.getTypeFactory();
			if(response != null)
			 resultObject = mapper.readValue(response, typeFactory.constructCollectionType(List.class, Object.class));

			Map<String,Object> resultMap = new HashMap<>();
			pagination.put("total_count",total_count);

			resultMap.put("crop_sown",resultObject);
			resultMap.put("season_start_date",seasons.get(0).getStartingMonth());
			resultMap.put("season_end_date",seasons.get(0).getEndingMonth());
			resultMap.put("year",year);
			resultMap.put("season",seasonName);
			resultMap.put("state_lgd_code",stateLgdCode);
			resultMap.put("pagination",pagination);
			//resultMap.put("total_count",total_count);
			Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
			resultMap.put("timestamp",currentTimestamp);


			return new ResponseModel(resultMap, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

		} catch (Exception e) {
			System.out.println("Error executing query for getCropAreaData:"+e.getMessage());
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	public Map<String ,Object>  insertRecordWiseStatus(String requestDAO, HttpServletRequest request){

		ObjectMapper mapper = new ObjectMapper();
		Map<String ,Object> response = new HashMap<>();

		try {
			// Deserialize JSON into a Map<String, Object>
			System.out.println("requestDAO "+requestDAO);
			Map<String, Object> map = mapper.readValue(requestDAO, Map.class);
			String requestTokenHeader = request.getHeader("Authorization");


			if ( requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
				response.put("status",CustomMessages.UNAUTHORIZED);
				response.put("remarks","UNAUTHORIZED");
				String jwtToken = requestTokenHeader.substring(7);
				if(!jwtToken.equals(getActiveTokenByServiceName("central_api_access")))
					return response;

			}
			if ( requestTokenHeader != null && !requestTokenHeader.startsWith("Bearer ")) {
				response.put("status",CustomMessages.UNAUTHORIZED);
				response.put("remarks","UNAUTHORIZED");
				String jwtToken = requestTokenHeader.substring(7);
				if(!jwtToken.equals(getActiveTokenByServiceName("central_api_access")))
					return response;

			}

			if ( requestTokenHeader == null) {
				response.put("status",CustomMessages.UNAUTHORIZED);
				response.put("remarks","UNAUTHORIZED");
				return response;

			}

			System.out.println("map "+map);
			// Accessing the dataset as a Map
			Map<String, Object> dataset = (Map<String, Object>) map.get("dataset");
			System.out.println("dataset ::: "+dataset);
			System.out.println("Date: " + dataset.get("date"));
			System.out.println("API Name: " + dataset.get("api_name"));
			ValidationLog validationLog = new ValidationLog();
			validationLog.setResourceName((String) dataset.get("api_name"));
			validationLog.setStateLgdCode(32l);

			// Accessing the status list as a List of Maps
			List<Map<String, Object>> statusList = (List<Map<String, Object>>) map.get("status");
			System.out.println("statusList "+statusList);
			for (Map<String, Object> status : statusList) {
				System.out.println("Record ID: " + status.get("record_id"));
				System.out.println("Error Code: " + status.get("error_code"));
				System.out.println("Status: " + status.get("status"));
				validationLog.setRecordId((String) status.get("record_id"));
				validationLog.setStatus((String) status.get("status"));
				validationLog.setIssueCode((String) status.get("error_code"));
				validationLog.setErrorDescription((String) status.get("error_code"));
				validationLogRepository.save(validationLog);
			}

			response.put("status",CustomMessages.GET_DATA_SUCCESS);
			response.put("remarks","Received");
		} catch (JsonMappingException e) {
			throw new RuntimeException(e);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}


		return response;


	}

	public String getActiveTokenByServiceName(String serviceName) {
		return jwtTokenUtil.getActiveTokenByServiceName(serviceName);
	}

}
