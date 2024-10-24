package com.amnex.agristack.controller;

import com.amnex.agristack.dao.SurveyorInputDAO;
import com.amnex.agristack.dao.common.ResponseModel;
import com.amnex.agristack.service.SurveyorAvailabilityService;
import com.amnex.agristack.service.SurveyorService;
import com.amnex.agristack.utils.CustomMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Surveyor Controller
 * this file will have APIs of Surveyor Operation
 * @author krupali.jogi
 */
@RestController
@RequestMapping("/surveyor/mobile")
public class SurveyorMobileController {

    @Autowired
    SurveyorService surveyorService;

    @Autowired
    SurveyorAvailabilityService surveyorAvailabilityService;

    /**
     * Adds a self-registered surveyor from mobile.
     *
     * @param inputDAO The SurveyorInputDAO input data.
     * @param request  The HttpServletRequest object containing the request information.
     * @return The ResponseModel containing the response data.
     */
    @PostMapping(value = "/authenticate/addSurveyor")
    public ResponseModel addSelfRegisteredSurveyor(@RequestBody SurveyorInputDAO inputDAO, HttpServletRequest request) {
        try {
            return surveyorService.addSelfRegisteredSurveyor(inputDAO, request);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseModel(null, e.getMessage(),
                    CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED, CustomMessages.METHOD_POST);
        }
    }
    
    /**
     * Updates surveyor availability.
     *
     * @param inputDAO The SurveyorInputDAO input data.
     * @param request  The HttpServletRequest object containing the request information.
     * @return The ResponseModel containing the response data.
     */
    @PutMapping(value = "/surveyorAvailability")
    public ResponseModel surveyorAvailability(@RequestBody SurveyorInputDAO inputDAO, HttpServletRequest request) {
        try {
            return surveyorService.surveyorAvailability(inputDAO, request);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseModel(null, e.getMessage(),
                    CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED, CustomMessages.METHOD_POST);
        }
    }

    /**
     * Adds user availability for seasons.
     *
     * @param surveyorInput The surveyor input data.
     * @param request       The HttpServletRequest object containing the request information.
     * @return The ResponseModel containing the response data.
     */
    @PostMapping(value = "/addUserAvailabilityForSeasons")
    public ResponseModel addUserAvailabilityForSeasons(@RequestBody SurveyorInputDAO surveyorInput, HttpServletRequest request) {
        try {
            return surveyorAvailabilityService.addUserAvailabilityForSeasons(surveyorInput, request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves user availability.
     *
     * @param surveyorInput The surveyor input data.
     * @param request       The HttpServletRequest object containing the request information.
     * @return The ResponseModel containing the response data.
     */
    @PostMapping(value = "/getUserAvailability")
    public ResponseModel getUserAvailability(@RequestBody SurveyorInputDAO surveyorInput, HttpServletRequest request) {
        try {
            return surveyorAvailabilityService.getUserAvailability(surveyorInput, request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
