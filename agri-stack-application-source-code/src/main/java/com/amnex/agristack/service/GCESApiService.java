package com.amnex.agristack.service;

import com.amnex.agristack.dao.CommonRequestDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.repository.ExternalAPIRepository;
import com.amnex.agristack.utils.CustomMessages;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class GCESApiService {

	@Autowired
	private ExternalAPIRepository externalAPIRepository;


	public ResponseModel getSurveyNumberByVillage(CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {

			String response = externalAPIRepository.getSurveyNumberByVillage(requestDAO.getVillageLgdCode(), Long.valueOf(requestDAO.getSeasonId()),
					requestDAO.getStartingYear(), requestDAO.getEndingYear());


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

	public ResponseModel checkSurveyDoneByVillage(CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {

			String response = externalAPIRepository.checkSurveyDoneByVillage(requestDAO.getVillageLgdCode(), Long.valueOf(requestDAO.getSeasonId()),
					requestDAO.getStartingYear(), requestDAO.getEndingYear());


			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Boolean resultObject = mapper.readValue(response, Boolean.class);

			return new ResponseModel(resultObject, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);

		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	public ResponseModel getApprovedSurveyDetailsByVillage(CommonRequestDAO requestDAO, HttpServletRequest request) {
		try {

			String response = externalAPIRepository.getApprovedSurveyDetailsByVillage(requestDAO.getVillageLgdCode(), Long.valueOf(requestDAO.getSeasonId()),
					requestDAO.getStartingYear(), requestDAO.getEndingYear(),requestDAO.getFarmlandId(),requestDAO.getCropCode(),requestDAO.getSurveyNumber());


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
