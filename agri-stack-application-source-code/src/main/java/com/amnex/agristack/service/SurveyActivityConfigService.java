package com.amnex.agristack.service;

import com.amnex.agristack.config.JwtTokenUtil;
import com.amnex.agristack.dao.*;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.entity.SowingSeason;
import com.amnex.agristack.entity.SurveyActivityConfiguration;
import com.amnex.agristack.entity.SurveyActivityMaster;
import com.amnex.agristack.entity.YearMaster;
import com.amnex.agristack.repository.SeasonMasterRepository;
import com.amnex.agristack.repository.SurveyActivityConfigurationRepository;
import com.amnex.agristack.repository.SurveyActivityMasterRepository;
import com.amnex.agristack.repository.YearRepository;
import com.amnex.agristack.utils.CommonUtil;
import com.amnex.agristack.utils.CustomMessages;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author krupali.jogi
 */
@Service
public class SurveyActivityConfigService {
	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private GeneralService generalService;


	@Autowired
	private YearRepository yearRepository;

	@Autowired
	private SeasonMasterRepository seasonRepository;

	@Autowired
	private SurveyActivityMasterRepository surveyActivityRepository;


	@Autowired
	private SurveyActivityConfigurationRepository surveyActivityConfigRepository;

	/**
	 * Retrieves all activities configuration based on the provided HttpServletRequest.
	 *
	 * @param request The HttpServletRequest object representing the HTTP request.
	 * @return A ResponseModel {@linik SurveyActivityConfiguration} containing the activities configuration.
	 */
	public ResponseModel getAllActivitiesConfig(HttpServletRequest request) {
		try {
			List<SurveyActivityConfiguration> activitiesConfigList = surveyActivityConfigRepository.findAll();

			return CustomMessages.makeResponseModel(activitiesConfigList, CustomMessages.GET_SURVEY_ACTIVITY_SUCCESSFULLY,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * Retrieves the survey activity configuration list based on the provided HttpServletRequest and SurveyActivityInputDAO.
	 *
	 * @param request The HttpServletRequest object representing the HTTP request.
	 * @param surveyActivityInput The {@code SurveyActivityInputDAO} object containing the input for survey activity configuration.
	 * @return A ResponseModel {@link SurveyActivityOutputDAO} containing the survey activity configuration list.
	 */
	public ResponseModel getSurveyActivityConfigList(HttpServletRequest request, SurveyActivityInputDAO surveyActivityInput) {
		try {
			List<SurveyActivityOutputDAO> outputList = new ArrayList<>();
			List<SurveyActivityConfiguration> list = surveyActivityConfigRepository.getSurveyActivityConfigList(surveyActivityInput.getSeasonId(), surveyActivityInput.getActivityIds(), surveyActivityInput.getYear());
			list.stream().forEach(ele-> {
				SurveyActivityOutputDAO outputDAO = new  SurveyActivityOutputDAO();
				BeanUtils.copyProperties(ele,outputDAO);
				if(Objects.nonNull(outputDAO.getSeasonId())) {
					SowingSeason season = seasonRepository.findBySeasonId(outputDAO.getSeasonId());
					if(Objects.nonNull(season.getSeasonId())) {
						outputDAO.setSeasonName(season.getSeasonName());
					}
				}
				if(Objects.nonNull(outputDAO.getSurveyActivity())) {
					outputDAO.setActivityId(outputDAO.getSurveyActivity().getId());
					outputDAO.setActivityName(outputDAO.getSurveyActivity().getActivityName());
				}
				outputList.add(outputDAO);
			});
			return CustomMessages.makeResponseModel(outputList, CustomMessages.GET_SURVEY_ACTIVITY_SUCCESSFULLY,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * Retrieves the survey activity configuration based on the provided HttpServletRequest and SurveyActivityInputDAO.
	 *
	 * @param request The HttpServletRequest object representing the HTTP request.
	 * @param surveyActivityInput The {@code SurveyActivityInputDAO} object containing the input for survey activity configuration.
	 * @return A ResponseModel {@link SurveyActivityOutputDAO} containing the survey activity configuration.
	 */

	public ResponseModel getSurveyActivityConfig(HttpServletRequest request, SurveyActivityInputDAO surveyActivityInput) {
		try {
			List<SurveyActivityOutputDAO> outputList = new ArrayList<>();
			List<SurveyActivityConfiguration> list = surveyActivityConfigRepository.getSurveyActivityConfigList(surveyActivityInput.getSeasonId(), surveyActivityInput.getActivityIds(), surveyActivityInput.getYear());
			list.stream().forEach(ele-> {
				SurveyActivityOutputDAO outputDAO = new  SurveyActivityOutputDAO();
				if(Objects.nonNull(ele.getSeasonId())) {
					SowingSeason season = seasonRepository.findBySeasonId(ele.getSeasonId());
					if(Objects.nonNull(season.getSeasonId())) {
						outputDAO.setSeasonName(season.getSeasonName());
					}
				}
				if(Objects.nonNull(ele.getSurveyActivity())) {
					outputDAO.setActivityId(ele.getSurveyActivity().getId());
					outputDAO.setActivityName(ele.getSurveyActivity().getActivityName());
				}
				outputDAO.setStartDate(ele.getStartDate());
				outputDAO.setEndDate(ele.getEndDate());
				outputList.add(outputDAO);
			});
			return CustomMessages.makeResponseModel(outputList, CustomMessages.GET_SURVEY_ACTIVITY_SUCCESSFULLY,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * Adds a survey activity configuration based on the provided HttpServletRequest and SurveyActivityInputDAO.
	 *
	 * @param request The HttpServletRequest object representing the HTTP request.
	 * @param surveyActivityInput The {@code SurveyActivityInputDAO} object containing the input for survey activity configuration.
	 * @return A ResponseModel {@link SurveyActivityConfiguration}indicating the result of the survey activity configuration addition.
	 */
	@Transactional
	public ResponseModel addSurveyActivityConfig(HttpServletRequest request, SurveyActivityInputDAO surveyActivityInput) {
		try {
			SurveyActivityConfiguration surveyActivityConfig = new SurveyActivityConfiguration();
			BeanUtils.copyProperties(surveyActivityInput,surveyActivityConfig, "startDate","endDate");
			/* Set Activity */
			SurveyActivityMaster activity = surveyActivityRepository.findById(surveyActivityInput.getActivityId()).orElse(null);
			if(Objects.nonNull(activity)) {
				surveyActivityConfig.setSurveyActivity(activity);
			}
			/* Set Activity*/
			java.sql.Date startDate = generalService.getDateFromString(surveyActivityInput.getStartDate(), "yyyy-MM-dd");
			java.sql.Date endDate = generalService.getDateFromString(surveyActivityInput.getEndDate(), "yyyy-MM-dd");
			YearMaster yearId = yearRepository.findCurrentYear();
			surveyActivityConfig.setStartDate(startDate);
			surveyActivityConfig.setEndDate(endDate);
			surveyActivityConfig.setIsActive(true);
			surveyActivityConfig.setIsDeleted(false);
			surveyActivityConfig.setYear(yearId.getId());
			surveyActivityConfig.setCreatedOn(new Timestamp(new Date().getTime()));
			surveyActivityConfig.setCreatedIp(CommonUtil.getRequestIp(request));
			surveyActivityConfig.setCreatedBy(CustomMessages.getUserId(request, jwtTokenUtil));
			surveyActivityConfig.setModifiedOn(new Timestamp(new Date().getTime()));
			surveyActivityConfig.setModifiedIp(CommonUtil.getRequestIp(request));
			surveyActivityConfig.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
			surveyActivityConfigRepository.save(surveyActivityConfig);
			return CustomMessages.makeResponseModel(surveyActivityConfig, CustomMessages.ADD_SURVEY_ACTIVITY_SUCCESSFULLY,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * Updates a survey activity configuration based on the provided HttpServletRequest and SurveyActivityInputDAO.
	 *
	 * @param request The HttpServletRequest object representing the HTTP request.
	 * @param surveyActivityInput The {@code SurveyActivityInputDAO} object containing the input for survey activity configuration update.
	 * @return A ResponseModel {@link SurveyActivityConfiguration} indicating the result of the survey activity configuration update.
	 */
	@Transactional
	public ResponseModel updateSurveyActivityConfig(HttpServletRequest request, SurveyActivityInputDAO surveyActivityInput) {
		try {
			if(Objects.nonNull(surveyActivityInput.getId())) {
				SurveyActivityConfiguration surveyActivityConfig = surveyActivityConfigRepository.findById(surveyActivityInput.getId()).orElse(null);
				if(Objects.nonNull(surveyActivityConfig)) {
					SurveyActivityConfiguration dbObject = new SurveyActivityConfiguration();
					BeanUtils.copyProperties(surveyActivityInput,surveyActivityConfig, "startDate","endDate", "activityId", "createdOn", "createdByIp", "createdBy", "isActive", "isDeleted", "year");
					java.sql.Date startDate = generalService.getDateFromString(surveyActivityInput.getStartDate(), "yyyy-MM-dd");
					java.sql.Date endDate = generalService.getDateFromString(surveyActivityInput.getEndDate(), "yyyy-MM-dd");
					surveyActivityConfig.setStartDate(startDate);
					surveyActivityConfig.setEndDate(endDate);
					surveyActivityConfig.setYear(surveyActivityConfig.getYear());
					surveyActivityConfig.setModifiedOn(new Timestamp(new Date().getTime()));
					surveyActivityConfig.setModifiedIp(CommonUtil.getRequestIp(request));
					surveyActivityConfig.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
					surveyActivityConfigRepository.save(surveyActivityConfig);
					return CustomMessages.makeResponseModel(surveyActivityConfig, CustomMessages.UPDATE_SURVEY_ACTIVITY_SUCCESSFULLY,
							CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
				} else {
					return CustomMessages.makeResponseModel(null, CustomMessages.CONFIGURATION_NOT_FOUND,
							CustomMessages.NO_DATA_FOUND, CustomMessages.FAILED);
				}
			} else {
				return CustomMessages.makeResponseModel("", CustomMessages.FAILURE,
						CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}


	/**
	 * Checks if a configuration duration already exists based on the provided HttpServletRequest and SurveyActivityInputDAO.
	 *
	 * @param request The HttpServletRequest object representing the HTTP request.
	 * @param surveyActivityInput The {@code SurveyActivityInputDAO} object containing the input for configuration duration check.
	 * @return A ResponseModel indicating if the configuration duration exists or not.
	 */
	public ResponseModel checkConfigurationDurationExist(HttpServletRequest request, SurveyActivityInputDAO surveyActivityInput) {
		try {
			Optional<SurveyActivityConfiguration> data = surveyActivityConfigRepository.checkActivityAlreadyConfigured(surveyActivityInput.getSeasonId(), surveyActivityInput.getActivityId(), surveyActivityInput.getYear());
			if(data.isPresent()) {
				return CustomMessages.makeResponseModel(true, CustomMessages.SURVEY_ACTIVITY_ALREADY_EXIST,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			} else {
				return CustomMessages.makeResponseModel(false, CustomMessages.CONFIGURATION_NOT_FOUND,
						CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
				CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * Deletes a configuration duration based on the provided HttpServletRequest and duration ID.
	 *
	 * @param request The HttpServletRequest object representing the HTTP request.
	 * @param id The ID of the configuration duration to delete.
	 * @return A ResponseModel indicating the result of the configuration duration deletion.
	 */
	public ResponseModel deleteConfigurationDuration(HttpServletRequest request, Long id) {
		try {
			if(Objects.nonNull(id)) {
				Optional<SurveyActivityConfiguration> data = surveyActivityConfigRepository.findById(id);
				if(Objects.nonNull(data)) {
					SurveyActivityConfiguration dbObject = data.get();
					dbObject.setIsDeleted(true);
					dbObject.setIsActive(false);
					dbObject.setModifiedOn(new Timestamp(new Date().getTime()));
					dbObject.setModifiedIp(CommonUtil.getRequestIp(request));
					dbObject.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
					surveyActivityConfigRepository.save(dbObject);
					return CustomMessages.makeResponseModel(true, CustomMessages.UPDATE_SURVEY_ACTIVITY_SUCCESSFULLY,
							CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
				} else {
					return CustomMessages.makeResponseModel(false, CustomMessages.CONFIGURATION_NOT_FOUND,
							CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
				}
			} else {
				return CustomMessages.makeResponseModel(false, CustomMessages.INVALID_INPUT,
						CustomMessages.INPUT_FIELD_REQUIRED, CustomMessages.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}


	/**
	 * Updates the status of a survey configuration based on the provided HttpServletRequest and SurveyActivityInputDAO.
	 *
	 * @param request The HttpServletRequest object representing the HTTP request.
	 * @param surveyActivityInput The {@code SurveyActivityInputDAO} object containing the input for survey configuration status update.
	 * @return A ResponseModel {@link SurveyActivityConfiguration} indicating the result of the survey configuration status update.
	 */
	public ResponseModel updateSurveyConfigurationStatus(HttpServletRequest request, SurveyActivityInputDAO surveyActivityInput) {
		try {
			if(Objects.nonNull(surveyActivityInput.getId())) {
				Optional<SurveyActivityConfiguration> data = surveyActivityConfigRepository.findById(surveyActivityInput.getId());
				if(Objects.nonNull(data)) {
					SurveyActivityConfiguration dbObject = data.get();
					dbObject.setIsActive(surveyActivityInput.getIsActive());
					dbObject.setModifiedOn(new Timestamp(new Date().getTime()));
					dbObject.setModifiedIp(CommonUtil.getRequestIp(request));
					dbObject.setModifiedBy(CustomMessages.getUserId(request, jwtTokenUtil));
					surveyActivityConfigRepository.save(dbObject);
					return CustomMessages.makeResponseModel(dbObject.getId(), CustomMessages.UPDATE_SURVEY_ACTIVITY_SUCCESSFULLY,
							CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
				} else {
					return CustomMessages.makeResponseModel(false, CustomMessages.CONFIGURATION_NOT_FOUND,
							CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
				}
			} else {
				return CustomMessages.makeResponseModel(false, CustomMessages.INVALID_INPUT,
						CustomMessages.INPUT_FIELD_REQUIRED, CustomMessages.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * Retrieves the survey activity configuration based on the provided HttpServletRequest and ID.
	 *
	 * @param request The HttpServletRequest object representing the HTTP request.
	 * @param id The ID of the survey activity configuration to retrieve.
	 * @return A ResponseModel containing the survey activity configuration.
	 */
	public ResponseModel getSurveyActivityConfig(HttpServletRequest request, Long id) {
		try {
			if(Objects.nonNull(id)) {
				Optional<SurveyActivityConfiguration> data = surveyActivityConfigRepository.findById(id);
				if(Objects.nonNull(data)) {
					SurveyActivityConfiguration dbObject = data.get();
					return CustomMessages.makeResponseModel(dbObject, CustomMessages.GET_SURVEY_ACTIVITY_SUCCESSFULLY,
							CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
				} else {
					return CustomMessages.makeResponseModel(false, CustomMessages.CONFIGURATION_NOT_FOUND,
							CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
				}
			} else {
				return CustomMessages.makeResponseModel(false, CustomMessages.INVALID_INPUT,
						CustomMessages.INPUT_FIELD_REQUIRED, CustomMessages.SUCCESS);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}

	/**
	 * Retrieves all active survey activities based on the provided HttpServletRequest.
	 *
	 * @param request The HttpServletRequest object representing the HTTP request.
	 * @return A ResponseModel {@code SurveyActivityMaster} containing the list of active survey activities.
	 */
	public ResponseModel getAllActiveSurveyActivities(HttpServletRequest request) {
		try {
			List<SurveyActivityMaster> activitiesList = surveyActivityRepository.findByIsActiveTrueAndIsDeletedFalseOrderByActivityNameAsc();
			return CustomMessages.makeResponseModel(activitiesList, CustomMessages.GET_SURVEY_ACTIVITY_SUCCESSFULLY,
					CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS);
		} catch (Exception e) {
			return CustomMessages.makeResponseModel(e.getMessage(), CustomMessages.FAILURE,
					CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED);
		}
	}
}
