package com.amnex.agristack.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amnex.agristack.dao.FarmerGrievanceWithMediaRequestDTO;
import com.amnex.agristack.dao.ReviewSurveyInputDAO;
import com.amnex.agristack.entity.FarmerGrievance;
import com.amnex.agristack.service.FarmerGrievanceService;
import com.amnex.agristack.utils.CustomMessages;

@RestController
@RequestMapping("/farmer-grievance")
public class FarmerGrievanceController {

    @Autowired
    FarmerGrievanceService farmerGrievanceService;

    /**
     * Adds a new Farmer Grievance.
     *
     * @param jsonData  the JSON data representing the Farmer Grievance
     * @param media     the list of media files associated with the Farmer Grievance
     * @param request   the HttpServletRequest object representing the incoming request
     * @return the ResponseModel indicating the result of the Farmer Grievance addition
     */
    @PostMapping("/addGrievance")
    public ResponseModel addGrievance(@RequestPart("data") String jsonData,
                                      @RequestPart(name = "media", required = false) List<MultipartFile> media, HttpServletRequest request) {
        try {
            FarmerGrievanceWithMediaRequestDTO requestDto = new FarmerGrievanceWithMediaRequestDTO();
            requestDto.setData(jsonData);
            requestDto.setMedia(media);
            return new ResponseModel(farmerGrievanceService.addFarmerGrievance(requestDto, request),
                    CustomMessages.RECORD_ADD, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS,
                    CustomMessages.METHOD_POST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseModel(e.getMessage(),
                    CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
                    CustomMessages.METHOD_POST);
        }
    }

    /**
     * Updates the status of a Farmer Grievance.
     *
     * @param grievance the FarmerGrievance object containing the updated Grievance details
     * @param request   the HttpServletRequest object representing the incoming request
     * @return the ResponseModel indicating the result of the status update
     */
    @PostMapping("/updateGrievanceStatus")
    public ResponseModel updateGrievanceStatus(@RequestBody FarmerGrievance grievance, HttpServletRequest request) {
        try {
            return new ResponseModel(farmerGrievanceService.updateFarmerGrievanceStatus(grievance, request),
                    CustomMessages.RECORD_UPDATE, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS,
                    CustomMessages.METHOD_POST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseModel(e.getMessage(),
                    CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
                    CustomMessages.METHOD_POST);
        }
    }

    /**
     * Retrieves all Farmer Grievances for a specific user.
     *
     * @param inputDao  the ReviewSurveyInputDAO object containing the input parameters
     * @param request   the HttpServletRequest object representing the incoming request
     * @return the ResponseModel containing the list of Farmer Grievances
     */
    @PostMapping("/getAllGrievances")
    public ResponseModel getAllGrievancesByUserId(@RequestBody ReviewSurveyInputDAO inputDao, HttpServletRequest request) {
        try {
            return new ResponseModel(farmerGrievanceService.getAllGrievancesByUserId(inputDao,request),
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
     * Retrieves the survey summary for a farmer.
     *
     * @param inputDao  the ReviewSurveyInputDAO object containing the input parameters
     * @param request   the HttpServletRequest object representing the incoming request
     * @return the ResponseModel containing the survey summary
     */
    @PostMapping("/getSurveySummaryForFarmer")
    public ResponseModel getSurveySummaryForFarmer(@RequestBody ReviewSurveyInputDAO inputDao, HttpServletRequest request) {
        return farmerGrievanceService.getSurveySummaryForFarmer(inputDao, request);
    }

    /**
     * Retrieves the survey summary for a supervisor.
     *
     * @param inputDao  the ReviewSurveyInputDAO object containing the input parameters
     * @param request   the HttpServletRequest object representing the incoming request
     * @return the ResponseModel containing the survey summary
     */
    @PostMapping("/getSurveySummaryForSupervisor")
    public ResponseModel getSurveySummaryForSupervisor(@RequestBody ReviewSurveyInputDAO inputDao, HttpServletRequest request) {
        return farmerGrievanceService.getSurveySummaryForSupervisor(inputDao, request);
    }
}
