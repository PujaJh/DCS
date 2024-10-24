package com.amnex.agristack.service;

import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.dao.*;
import com.amnex.agristack.dao.UpVillageDAO.PlotLevelInfo;
import com.amnex.agristack.dao.UpVillageDAO.UpVillageDetailDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.dao.externalDAO.FarmLandPlotDAO;
import com.amnex.agristack.dao.externalDAO.SurveyDetailDAO;
import com.amnex.agristack.repository.*;
import com.amnex.agristack.utils.CustomMessages;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

@Service
public class ExternalAPIService {

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private ExternalAPIRepository externalAPIRepository;

	public ResponseModel getVillageWisePlotAndCropDetail(PaginationDao inputDao, HttpServletRequest request) {
		try {
			String response = externalAPIRepository.getVillageWisePlotAndCropDetail(inputDao.getVillageLgdCode(), inputDao.getYear(), inputDao.getSeasonId().intValue());
			Gson g = new Gson();
			UpVillageDetailDAO finalObject = g.fromJson(response, UpVillageDetailDAO.class);
			return new ResponseModel(finalObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
			} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}
	
	public ResponseModel getVillageWisePlotAndCropDetailByDate(PaginationDao inputDao, HttpServletRequest request) {
		try {
			String response = externalAPIRepository.getVillageWisePlotAndCropDetailByDate(inputDao.getVillageLgdCode(), inputDao.getYear(), inputDao.getSeasonId().intValue(),inputDao.getStartDate(),inputDao.getEndDate());
			Gson g = new Gson();
			UpVillageDetailDAO finalObject = g.fromJson(response, UpVillageDetailDAO.class);
			return new ResponseModel(finalObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
			} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

public ResponseModel getSubSurveyNumber(LandDetailDAO detailDAO, HttpServletRequest request) {
		
		try {
			String response = externalAPIRepository.getSubSurveyNumberDetail(detailDAO.getVillageLgdCode(),detailDAO.surveyNumber);
			Gson g = new Gson();
			Object finalObject = g.fromJson(response, Object.class);
			return new ResponseModel(finalObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
		
	}

	public ResponseModel landOwnerShipDetail(LandDetailDAO detailDAO, HttpServletRequest request) {
		try {
			String response = externalAPIRepository.landOwnerShipDetail(detailDAO.getVillageLgdCode(),detailDAO.surveyNumber,detailDAO.getSubSurveyNumber());
//			Gson g = new Gson();
//			Object finalObject = g.fromJson(response, Object.class);
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

	public ResponseModel getVillageWiseFarmlandPlotDetail(PaginationDao inputDao, HttpServletRequest request) {
		try {
			String response = externalAPIRepository.getVillageWiseFarmlandPlotDetail(inputDao.getVillageLgdCode());
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<FarmLandPlotDAO> finalResult = mapper.readValue(response,
					typeFactory.constructCollectionType(List.class, FarmLandPlotDAO.class));
			return new ResponseModel(finalResult, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	public ResponseModel getVillageLevelSurveyDetailForGCES(PaginationDao inputDao, HttpServletRequest request) {
		try {
			String response = externalAPIRepository.getVillageLevelSurveyDetailForGCES(inputDao.getVillageLgdCode(),inputDao.getYear(),inputDao.getSeasonId());
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			TypeFactory typeFactory = mapper.getTypeFactory();
			List<SurveyDetailDAO> finalResult = mapper.readValue(response,
					typeFactory.constructCollectionType(List.class, SurveyDetailDAO.class));
			return new ResponseModel(finalResult, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
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

	public ResponseModel getVillageWiseCropAndMediaDetails(CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {

//			String villageList = getJoinedValuefromList(requestDAO.getVillageLgdCodeList());
			
			String response = externalAPIRepository.getVillageWiseCropAndMediaDetails(requestDAO.getVillageLgdCode(),requestDAO.getSeasonId(), requestDAO.getStartYear(),
					requestDAO.getEndYear());
			

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

	public ResponseModel getVillageCropIdentificationDetails(CommonRequestDAO requestDAO, HttpServletRequest request) {
		
		try {
		String response = externalAPIRepository.getVillageCropIdentificationDetails(requestDAO.getVillageLgdCode(),requestDAO.getSeasonId(), requestDAO.getStartYear(),
				requestDAO.getEndYear());
		

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


}
