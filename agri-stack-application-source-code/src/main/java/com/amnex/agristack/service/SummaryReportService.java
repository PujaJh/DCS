package com.amnex.agristack.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.CommonRequestDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.dao.common.ResponseWrapper;
import com.amnex.agristack.entity.*;
import com.amnex.agristack.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.dao.ReviewSurveyInputDAO;
import com.amnex.agristack.utils.CustomMessages;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

@Service
public class SummaryReportService {
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	SummaryReportRepository summaryReportRepository;

	@Autowired
	private UserMasterRepository userRepository;
	
	@Autowired
	private GeneralService generalService;

	@Autowired
	SeasonMasterRepository seasonMasterRepository;

	@Autowired
	ValidationLogRepository validationLogRepository;

	@Autowired
	private RolePatternMappingRepository rolePatternMappingRepository;

	public Object getHourlyStatusReport(HttpServletRequest request) {
		try {
			String userId = CustomMessages.getUserId(request, jwtTokenUtil);

			userId = (userId != null && !userId.isEmpty()) ? userId : "2";
			Optional<UserMaster> op = userRepository.findByUserIdAndIsDeletedAndIsActive(Long.valueOf(userId), false, true);
			UserMaster userMaster= new UserMaster();
			if (op.isPresent()) {
				userMaster = op.get();}
			RoleMaster roleMaster = userMaster.getRoleId();
			System.out.println("role "+roleMaster.getRoleId());
			SowingSeason sowingSeason=generalService.getCurrentSeason();
			YearMaster yearMaster=generalService.getCurrentYear();
			System.out.println(yearMaster.getStartYear());
			System.out.println(yearMaster.getStartYear());
			String response = null;
			if(sowingSeason!=null && yearMaster!=null) {
				response = summaryReportRepository.getMiscForHourlyStatusReport(sowingSeason.getSeasonId(), Long.valueOf(yearMaster.getStartYear()), Long.valueOf(yearMaster.getEndYear()));
			}
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<Object> resultObject = mapper.readValue(response,
					typeFactory	.constructCollectionType(List.class, Object.class));
			System.out.println("resultObject "+resultObject);
			if (roleMaster.getRoleId() == 204L || roleMaster.getRoleId() == 201L) { // Use 204L to ensure it's a long literal
				return new ResponseModel(resultObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
						CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
			}

			Optional<RolePatternMapping> rolePatternMapping= Optional.ofNullable(rolePatternMappingRepository.findByRole(userMaster.getRoleId()));
			Integer lgdcode = null;
			String territoryLevel = rolePatternMapping.get().getTerritoryLevel();
			System.out.println(rolePatternMapping);

			if(rolePatternMapping.isPresent()) {
				 territoryLevel = rolePatternMapping.get().getTerritoryLevel();
			}

			List<Object> filteredResult = resultObject;


			System.out.println("resultObject "+resultObject);
			System.out.println("territoryLevel "+territoryLevel);

			if (territoryLevel.equals("state")) {
				return new ResponseModel(resultObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
						CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
			} else if (territoryLevel.equals("village")) {
				lgdcode = userMaster.getUserVillageLGDCode();
			} else if (territoryLevel.equals("district")) {
				lgdcode = userMaster.getUserDistrictLGDCode();
			} else if (territoryLevel.equals("subdistrict")) {
				lgdcode = userMaster.getUserTalukaLGDCode();
			}
			System.out.println("lgdcode "+lgdcode);
			if (lgdcode != null) {
				Integer finalLgdcode = lgdcode;
				String territoryLevel2 = territoryLevel;
				try {
					filteredResult = resultObject.stream()
							.filter(obj -> {
								if (obj instanceof Map) {
									Map<String, Object> map = (Map<String, Object>) obj;
									String key = null;
									if (territoryLevel2.equals("village")) {
										key = "village_lgd_code";
									} else if (territoryLevel2.equals("district")) {
										key = "district_lgd_code";
									} else if (territoryLevel2.equals("subdistrict")) {
										key = "sub_district_lgd_code";
									}
									// Safely cast and compare
									return map.get(key) != null && ((Number) map.get(key)).intValue() == finalLgdcode;
								}
								return false;
							})
							.collect(Collectors.toList());
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("Error processing the filter operation", e);
				}

			}
			System.out.println("filteredResult "+filteredResult);
			return new ResponseModel(filteredResult, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(null, CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}


	public Object getSurveyorActivityReport(Map<String,Object> requestDAO, HttpServletRequest request) {
		try {
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
//			String userId = CustomMessages.getUserId(request, jwtTokenUtil);

			Integer stateLgdCode = (Integer) requestDAO.get("state_lgd_code");
			String seasonName = (String) requestDAO.get("season");
			String year = (String) requestDAO.get("year");
			String lastSyncDate = (String) requestDAO.get("last_sync_date");
			int seasonStartYear = Integer.parseInt(year.split("-")[0]);
			int seasonEndYear = Integer.parseInt(year.split("-")[1]);

//			List<SowingSeason> seasons = seasonMasterRepository.findByIsDeletedFalseAndSeasonName(seasonName);

			//userId = (userId != null && !userId.isEmpty()) ? userId : "2";
//			Optional<UserMaster> op = userRepository.findByUserIdAndIsDeletedAndIsActive(Long.valueOf(userId), false, true);
//			UserMaster userMaster= new UserMaster();
//			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

			// Parse the string to LocalDateTime
//			LocalDateTime localDateTime = LocalDateTime.parse(lastSyncDate, formatter);

			// Convert LocalDateTime to Timestamp
//			Timestamp timestamp = Timestamp.valueOf(localDateTime);
//			if (op.isPresent()) {
//				userMaster = op.get();}
//			RoleMaster roleMaster = userMaster.getRoleId();
//			System.out.println("role "+roleMaster.getRoleId());
			SowingSeason sowingSeason=generalService.getCurrentSeason();
			YearMaster yearMaster=generalService.getCurrentYear();
			System.out.println("yearMaster "+yearMaster);
			System.out.println(yearMaster.getStartYear());
			String response = null;
			if(sowingSeason!=null && yearMaster!=null) {
				response = summaryReportRepository.getSurveyorActivityReport(Long.valueOf(1), Long.valueOf(seasonStartYear), Long.valueOf(seasonEndYear));
			}
			System.out.println("response "+response);
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			String total_count = summaryReportRepository.getSurveyorActivityTotalCount(Long.valueOf(1), Long.valueOf(seasonStartYear), Long.valueOf(seasonEndYear));

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<Object> resultObject = mapper.readValue(response,
					typeFactory	.constructCollectionType(List.class, Object.class));
			ResponseWrapper responseWrapper = new ResponseWrapper();
			responseWrapper.setResultData(resultObject);
			//responseWrapper.setParameters(requestDAO);
			System.out.println("resultObject "+resultObject);
//			System.out.println("roleMaster.getRoleId() "+roleMaster.getRoleId());

			Map<String,Object> resultMap = new HashMap<>();
			resultMap.put("year",year);
			resultMap.put("season",seasonName);
			resultMap.put("state_lgd_code",stateLgdCode);
			resultMap.put("surveyor_details",resultObject);
			//resultMap.put("total_count",total_count);
			Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
			resultMap.put("timestamp",currentTimestamp);
			//resultMap.put("season_start_date",seasons.get(0).getStartingMonth());
			//resultMap.put("season_end_date",seasons.get(0).getEndingMonth());



//			if (roleMaster.getRoleId() == 204L || roleMaster.getRoleId() == 201l) { // Use 204L to ensure it's a long literal
				return new ResponseModel(resultMap, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
						CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
//			}

//			Optional<RolePatternMapping> rolePatternMapping= Optional.ofNullable(rolePatternMappingRepository.findByRole(userMaster.getRoleId()));
//			System.out.println("rolePatternMapping" +rolePatternMapping);
//
//			Integer lgdcode = null;
//			String territoryLevel = rolePatternMapping.get().getTerritoryLevel();
//			System.out.println("rolePatternMapping" +rolePatternMapping);
//
//			if(rolePatternMapping.isPresent()) {
//				territoryLevel = rolePatternMapping.get().getTerritoryLevel();
//			}
//
//			List<Object> filteredResult = resultObject;
//
//
//			//System.out.println("resultObject "+resultObject);
//			System.out.println("territoryLevel "+territoryLevel);
//
//			if (territoryLevel.equals("state")) {
//				return new ResponseModel(resultObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
//						CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
//			} else if (territoryLevel.equals("village")) {
//				lgdcode = userMaster.getUserVillageLGDCode();
//			} else if (territoryLevel.equals("district")) {
//				lgdcode = userMaster.getUserDistrictLGDCode();
//			} else if (territoryLevel.equals("subdistrict")) {
//				lgdcode = userMaster.getUserTalukaLGDCode();
//			}
//			System.out.println("lgdcode "+lgdcode);
//			if (lgdcode != null) {
//				Integer finalLgdcode = lgdcode;
//				String territoryLevel2 = territoryLevel;
//				try {
//					filteredResult = resultObject.stream()
//							.filter(obj -> {
//								if (obj instanceof Map) {
//									Map<String, Object> map = (Map<String, Object>) obj;
//									String key = null;
//									if (territoryLevel2.equals("village")) {
//										key = "village_lgd_code";
//									} else if (territoryLevel2.equals("district")) {
//										key = "district_lgd_code";
//									} else if (territoryLevel2.equals("subdistrict")) {
//										key = "sub_district_lgd_code";
//									}
//									// Safely cast and compare
//									return map.get(key) != null && ((Number) map.get(key)).intValue() == finalLgdcode;
//								}
//								return false;
//							})
//							.collect(Collectors.toList());
//				} catch (Exception e) {
//					e.printStackTrace();
//					throw new RuntimeException("Error processing the filter operation", e);
//				}
//
//			}
//			System.out.println("filteredResult "+filteredResult);
//			return new ResponseModel(filteredResult, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
//					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(null, CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	public String getActiveTokenByServiceName(String serviceName) {
		return jwtTokenUtil.getActiveTokenByServiceName(serviceName);
	}
}
