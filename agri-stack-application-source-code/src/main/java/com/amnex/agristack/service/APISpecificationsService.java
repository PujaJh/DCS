package com.amnex.agristack.service;

import com.amnex.agristack.dao.APISpecificationsRequestDao;
import com.amnex.agristack.dao.SurveySeasonRequestDao;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.repository.*;
import com.amnex.agristack.utils.CustomMessages;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * @author Seet.Mansura
 */
@Service
public class APISpecificationsService {

	@Autowired
	CropMasterRepository cropMasterRepository;

	public ResponseModel getGeoReferencedMaps(HttpServletRequest request, APISpecificationsRequestDao requestDAO) {
		try {
			String surveyNumber = requestDAO.getLand_identifier().getSurvey_number() != null ?
					requestDAO.getLand_identifier().getSurvey_number() : "";
			String uniqueLandCode = requestDAO.getLand_identifier().getUnique_land_code() != null ?
					requestDAO.getLand_identifier().getUnique_land_code() : "";

			String response = cropMasterRepository.getGeorReferencedMaps(
					Long.valueOf(requestDAO.getVillage_lgd_code()),
					surveyNumber, uniqueLandCode);

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Object resultObject = mapper.readValue(response, Object.class);

			Object finalResponse = !((ArrayList<?>) resultObject).isEmpty() ?
					((ArrayList<?>) resultObject).get(0) : "{}";

			return new ResponseModel(finalResponse, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED, CustomMessages.METHOD_POST);
		}
	}


	public ResponseModel getCropSurveyData(HttpServletRequest request, APISpecificationsRequestDao requestDAO) {
		try {
			String surveyNumber = requestDAO.getLand_identifier().getSurvey_number() != null ?
					requestDAO.getLand_identifier().getSurvey_number() : "";
			String uniqueLandCode = requestDAO.getLand_identifier().getUnique_land_code() != null ?
					requestDAO.getLand_identifier().getUnique_land_code() : "";

			List<Object> resultListObject = new ArrayList<>();

			for (SurveySeasonRequestDao surveySeasonRequestDao: requestDAO.getSurvey_season()) {

				String response = cropMasterRepository.getCropSurveyData(
						Long.valueOf(requestDAO.getVillage_lgd_code()),
						surveyNumber, uniqueLandCode, surveySeasonRequestDao.getYear(), surveySeasonRequestDao.getSeason());

				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				Object resultObject = mapper.readValue(response, Object.class);
				if (resultObject != null && !((ArrayList<?>) resultObject).isEmpty()){
					resultListObject.add(((ArrayList<?>) resultObject).get(0));
				}
			}
			HashMap<String, Object> finalResponse = new HashMap<>();
			finalResponse.put("crop_sown_data", resultListObject);
			return new ResponseModel(finalResponse, CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS,
					CustomMessages.SUCCESS, CustomMessages.METHOD_POST);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseModel(null, CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR,
					CustomMessages.FAILED, CustomMessages.METHOD_POST);
		}
	}



}
