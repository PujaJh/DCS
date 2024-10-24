package com.amnex.agristack.controller;

import com.amnex.agristack.dao.SurveyorInputDAO;
import com.amnex.agristack.dao.UserInputDAO;
import com.amnex.agristack.dao.common.ResponseModel;
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
@RequestMapping("/admin/surveyor")
public class SurveyorController {

    @Autowired
    SurveyorService surveyorService;

    /**
     * Adds a surveyor.
     *
     * @param inputDAO  The SurveyorInputDAO object containing surveyor details.
     * @param request   The HttpServletRequest object.
     * @return          The ResponseModel object containing the result of the operation.
     */
    @PostMapping(value = "/addSurveyor")
    public ResponseModel addSurveyor(@RequestBody SurveyorInputDAO inputDAO, HttpServletRequest request) {
        try {
            return surveyorService.addSurveyor(inputDAO, request);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseModel(null, e.getMessage(),
                    CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED, CustomMessages.METHOD_POST);
        }
    }

    /**
     * Allows a user to become a surveyor.
     *
     * @param inputDAO  The SurveyorInputDAO object containing surveyor details.
     * @param request   The HttpServletRequest object.
     * @return          The ResponseModel object containing the result of the operation.
     */
    @PostMapping(value = "/becomeSurveyor")
    public ResponseModel becomeSurveyor(@RequestBody SurveyorInputDAO inputDAO, HttpServletRequest request) {
        try {
            return surveyorService.becomeSurveyor(inputDAO, request);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseModel(null, e.getMessage(),
                    CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED, CustomMessages.METHOD_POST);
        }
    }

    /**
     * Updates a surveyor.
     *
     * @param inputDAO  The SurveyorInputDAO object containing surveyor details.
     * @param request   The HttpServletRequest object.
     * @return          The ResponseModel object containing the result of the operation.
     */
    @PostMapping(value = "/updateSurveyor")
    public ResponseModel updateSurveyor(@RequestBody SurveyorInputDAO inputDAO, HttpServletRequest request) {
        try {
            return surveyorService.updateSurveyor(inputDAO, request);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseModel(null, e.getMessage(),
                    CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED, CustomMessages.METHOD_POST);
        }
    }

    /**
     * get Surveyor list
     * @param requestInput  The SurveyorInputDAO object containing surveyor details.
     * @param request The HttpServletRequest object.
     * @return
     */
    @PostMapping(value = "/getAllSurveyor")
    public ResponseModel getAllSurveyor(@RequestBody SurveyorInputDAO requestInput, HttpServletRequest request) {
        try {
            return surveyorService.getAllSurveyor(requestInput,request);
//            return surveyorService.getAllSurveyorV2(requestInput, request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves the details of a surveyor.
     *
     * @param userId    The ID of the surveyor.
     * @param request   The HttpServletRequest object.
     * @return          The ResponseModel object containing the surveyor details.
     */
    @GetMapping(value = "/getSurveyorDetail/{userId}")
    public ResponseModel getSurveyorDetail(@PathVariable("userId") Long userId,HttpServletRequest request) {
        try {
            return surveyorService.getSurveyorDetail(userId, request);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseModel(null, e.getMessage(),
                    CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED, CustomMessages.METHOD_POST);
        }
    }
    
    /**
     * Update Surveyor Status
     *
     * @param surveyorInputDAO The SurveyorInputDAO object containing surveyor details.
     * @param request The HttpServletRequest object.
     * @return
     */
    @PostMapping(value = "/updateSurveyorStatus")
    public ResponseModel updateSurveyorStatus(@RequestBody SurveyorInputDAO surveyorInputDAO, HttpServletRequest request) {
        try {
            return surveyorService.updateSurveyorStatus(surveyorInputDAO, request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Delete Surveyor
     *
     * @param surveyorInputDAO The SurveyorInputDAO object containing surveyor details.
     * @param request The HttpServletRequest object.
     * @return
     */
    @PostMapping(value = "/deleteSurveyor")
    public ResponseModel deleteSurveyor(@RequestBody SurveyorInputDAO surveyorInputDAO, HttpServletRequest request) {
        try {
            return surveyorService.deleteSurveyor(surveyorInputDAO, request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * update user availability
     *
     * @param surveyorInput The SurveyorInputDAO object containing surveyor details.
     * @param request The HttpServletRequest object.
     * @return
     */
    @PostMapping(value = "/updateUserAvailability")
    public ResponseModel updateUserAvailability(@RequestBody SurveyorInputDAO surveyorInput, HttpServletRequest request) {
        try {
            return surveyorService.updateUserAvailability(surveyorInput, request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * update surveyor status
     *
     * @param surveyorInput The SurveyorInputDAO object containing surveyor details.
     * @param request The HttpServletRequest object.
     * @return
     */
    @PostMapping(value = "/updateBulkSurveyorStatus")
    public ResponseModel updateBulkSurveyorStatus(@RequestBody SurveyorInputDAO surveyorInput, HttpServletRequest request) {
        try {
            return surveyorService.updateBulkSurveyorStatus(surveyorInput, request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * get Surveyor list
     * @param requestInput The SurveyorInputDAO object containing surveyor details.
     * @param request The HttpServletRequest object.
     * @return
     */
    @PostMapping(value = "/getRegisteredSurveyor")
    public ResponseModel getRegisteredSurveyor(@RequestBody SurveyorInputDAO requestInput, HttpServletRequest request) {
        try {
            return surveyorService.getRegisteredSurveyor(requestInput,request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Checks if there has been a season change.
     *
     * @param request   The HttpServletRequest object.
     * @return          Boolean indicating if there has been a season change.
     */
    @GetMapping(value = "/checkSeasonChange")
    public Boolean checkSeasonChange(HttpServletRequest request) {
        try {
            return surveyorService.checkSeasonChange(request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * perform the clear user details by id.
     *
     * @param inputDAO The {@code UserInputDAO} object containing surveyor details.
     * @return  The ResponseModel object representing the response.
     */
    @PostMapping(value =  "/clearUserDetailsById")
	public ResponseModel clearUserDetailsById(@RequestBody UserInputDAO inputDAO) {
		return surveyorService.clearUserDetailsById(inputDAO);
	}

    /**
     * perform the update user status.
     *
     * @param surveyorInput The SurveyorInputDAO object containing surveyor details.
     * @param request The HttpServletRequest object.
     * @return  The ResponseModel object representing the response.
     */
    @PostMapping(value = "/updateUserStatus")
    public ResponseModel updateUserStatus(@RequestBody SurveyorInputDAO surveyorInput, HttpServletRequest request) {
        try {
            return surveyorService.updateUserStatus(surveyorInput, request);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
