package com.amnex.agristack.controller;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.dao.CropSurveyConfigModeRequestDTO;
import com.amnex.agristack.dao.SurveyorInputDAO;
import com.amnex.agristack.service.CropSurveyModeConfigService;
import com.amnex.agristack.utils.CustomMessages;

/**
 * Controller class for managing crop survey mode config.
 */
@RestController
@RequestMapping("/survey-config")
public class CropSurveyModeConfigController {

    @Autowired
    CropSurveyModeConfigService cropSurveyModeConfigService;

    /**
     * Updates the Crop Survey Mode configuration.
     *
     * @param payload the CropSurveyConfigModeRequestDTO object containing the updated configuration
     * @return a ResponseModel indicating the result of the configuration update
     */
    @PostMapping("/updateConfig")
    public ResponseModel updateCropSurveyModeConfig(@RequestBody CropSurveyConfigModeRequestDTO payload) {
        try {
            cropSurveyModeConfigService.updateCropSurveyModeConfig(payload);
            return new ResponseModel(payload,
                    CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS,
                    CustomMessages.METHOD_POST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseModel(e.getMessage(),
                    CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
                    CustomMessages.METHOD_POST);
        }
    }

    /**
     * Retrieves the Crop Survey Mode configuration.
     *
     * @param payload the CropSurveyConfigModeRequestDTO object containing the request parameters
     * @param request the HttpServletRequest object representing the incoming request
     * @return a ResponseModel containing the Crop Survey Mode configuration
     */
    @PostMapping("/getConfig")
    public ResponseModel getConfig(@RequestBody CropSurveyConfigModeRequestDTO payload, HttpServletRequest request) {
        try {
            return new ResponseModel(cropSurveyModeConfigService.getConfig(payload, request),
                    CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS,
                    CustomMessages.METHOD_POST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseModel(e.getMessage(),
                    CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
                    CustomMessages.METHOD_POST);
        }
    }

    /**
     * Retrieves the Crop Survey Mode configuration by surveyor for mobile.
     *
     * @param surveyorInput the SurveyorInputDAO object containing the surveyor details
     * @return a ResponseModel containing the Crop Survey Mode configuration for the specified surveyor for mobile
     */
    @PostMapping("/mobile/getCropSurveyConfigBySurveyor")
    public ResponseModel getCropSurveyConfigBySurveyor(@RequestBody SurveyorInputDAO surveyorInput) {
        try {
            return new ResponseModel(cropSurveyModeConfigService.getCropSurveyConfigBySurveyor(surveyorInput),
                    CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS,
                    CustomMessages.METHOD_POST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseModel(e.getMessage(),
                    CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
                    CustomMessages.METHOD_POST);
        }
    }
}
