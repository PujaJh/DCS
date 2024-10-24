package com.amnex.agristack.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.amnex.agristack.dao.CommonRequestDAO;
import com.amnex.agristack.repository.MisCenterRepository;
import com.amnex.agristack.utils.CustomMessages;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

@Service
public class MISCenterService {
	
	@Autowired
	private MisCenterRepository misCenterRepository;
	
	public ResponseModel getCultivatedSummaryDetails(CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {
			
			
			String response = misCenterRepository.getCultivatedSummaryDetails(requestDAO.getSeasonId(), requestDAO.getStartYear(), requestDAO.getEndYear());

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

	public ResponseModel getAggregatedDetails(CommonRequestDAO requestDAO, HttpServletRequest request) {
		
		try {
			
			String response = misCenterRepository.getAggregatedDetails(requestDAO.getSeasonId(), requestDAO.getStartYear(), requestDAO.getEndYear());

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

	public ResponseModel getCropSummaryDetails(CommonRequestDAO requestDAO, HttpServletRequest request) {
		
			try {
			
			String response = misCenterRepository.getCropSummaryDetails(requestDAO.getSeasonId(), requestDAO.getStartYear(), requestDAO.getEndYear());

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

	public ResponseModel getSurveyorsDepartmentDetails(CommonRequestDAO requestDAO, HttpServletRequest request) {
		
			try {
			
			String response = misCenterRepository.getSurveyorsDepartmentDetails(requestDAO.getSeasonId(), requestDAO.getStartYear(), requestDAO.getEndYear());

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

	public ResponseModel getIrrigationSourceDetails(CommonRequestDAO requestDAO, HttpServletRequest request) {
		
			try {
			
			String response = misCenterRepository.getIrrigationSourceDetails(requestDAO.getSeasonId(), requestDAO.getStartYear(), requestDAO.getEndYear());

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

	public ResponseModel getVillageWisePlotCount( HttpServletRequest request) {
			try {
			
				String response = misCenterRepository.getVillageWisePlotCount();
		
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
