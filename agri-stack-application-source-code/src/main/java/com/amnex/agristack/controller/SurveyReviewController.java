package com.amnex.agristack.controller;

import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;

import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.amnex.agristack.dao.AssignedPlotResponseDTO;
import com.amnex.agristack.dao.CommonRequestDAO;
import com.amnex.agristack.dao.LandParcelSurveyReviewDTO;
import com.amnex.agristack.dao.ReviewSurveyInputDAO;
import com.amnex.agristack.dao.SurveyDetailMasterRequestDTO;
import com.amnex.agristack.dao.SurveyDetailRequestDTO;
import com.amnex.agristack.dao.SurveyReviewFetchRequestMobileDTO;
import com.amnex.agristack.dao.UnableToSurveyRequestDAO;
import com.amnex.agristack.service.ExceptionLogService;
import com.amnex.agristack.service.SurveyReviewService;
import com.amnex.agristack.utils.CustomMessages;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.google.gson.Gson;

/**
 * Controller class for managing survey review details.
 */
@RestController
@RequestMapping("/survey-review")
public class SurveyReviewController {

    @Autowired
    SurveyReviewService surveyReviewService;
    
    @Autowired
	ExceptionLogService exceptionLogService;


    static Gson gson = new Gson();

    // Handles the HTTP POST request to retrieve survey summary
    /**
	 * Retrieves the summary details of land surveys based on the provided input
	 * parameters.
	 *
	 * @param inputDao The ReviewSurveyInputDAO object containing the input
	 *                 parameters.
	 * @param request  The HttpServletRequest object containing the request
	 *                 information.
	 * @return The response model containing the survey summary details.
	 */
    @PostMapping("/web/getSurveySummary")
    public Object getSurveySummary(@RequestBody ReviewSurveyInputDAO inputDao, HttpServletRequest request) {
        return surveyReviewService.getSurveySummary(inputDao, request);
    }
    
    /**
	 * Retrieves the summary details of land surveys based on the provided input
	 * parameters.
	 *
	 * @param inputDao The ReviewSurveyInputDAO object containing the input
	 *                 parameters.
	 * @param request  The HttpServletRequest object containing the request
	 *                 information.
	 * @return The response model containing the survey summary details.
	 */
    @PostMapping("/web/getSurveySummaryPagination")
    public Object getSurveySummaryPagination(@RequestBody ReviewSurveyInputDAO inputDao, HttpServletRequest request) {
        return surveyReviewService.getSurveySummaryPagination(inputDao, request);
    }

    // Handles the HTTP POST request to retrieve summary details by master ID
    /**
	 * Retrieves summary details by master ID.
	 *
	 * @param inputDao The ReviewSurveyInputDAO object containing the input
     *                 parameters.
	 * @param request  The HttpServletRequest object.
	 * @return An Object containing the summary details.
	 */
    @PostMapping("/web/getSummaryDetailsByMasterId")
    public Object getSummaryDetailsByMasterId(@RequestBody ReviewSurveyInputDAO inputDao, HttpServletRequest request) {
        return surveyReviewService.getSummaryDetailsByMasterId(inputDao.getMasterId(), request);
    }

    // Handles the HTTP POST request to retrieve assigned plot data for mobile
    /**
	 * Retrieves assigned plot details for a mobile application based on the
	 * provided parameters.
	 *
	 * @param surveyReviewFetchRequestMobileDTO The {@code SurveyReviewFetchRequestMobileDTO} containing the request
	 *                                          parameters.
	 * @return The result of executing the stored procedure.
	 */
    @PostMapping("/mobileGetAssignedPlot")
    public Object mobileGetAssignedPlot(
            @RequestBody SurveyReviewFetchRequestMobileDTO surveyReviewFetchRequestMobileDTO) {
        try {
            // Default values if plotIds and surveyMasterId are null
            if (surveyReviewFetchRequestMobileDTO.getPlotIds() == null) {
                surveyReviewFetchRequestMobileDTO.setPlotIds("0");
            }

            if (surveyReviewFetchRequestMobileDTO.getSurveyMasterId() == null) {
                surveyReviewFetchRequestMobileDTO.setSurveyMasterId("0");
            }
            String result = surveyReviewService.mobileGetAssignedPlot(surveyReviewFetchRequestMobileDTO).toString();

            // Parsing the JSON response
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            TypeFactory typeFactory = mapper.getTypeFactory();
            List<AssignedPlotResponseDTO> resultObject = mapper.readValue(result,
                    typeFactory.constructCollectionType(List.class, AssignedPlotResponseDTO.class));

            // Creating and returning a response model
            return new ResponseModel(resultObject,
                    CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS,
                    CustomMessages.METHOD_POST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseModel(null,
                    CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
                    CustomMessages.METHOD_POST);
        }
    }

    // Handles the HTTP POST request to retrieve assigned plot and status data for
    // mobile
    /**
	 * Retrieves assigned plot details along with their status for a mobile
	 * application based on the provided parameters.
	 *
	 * @param surveyReviewFetchRequestMobileDTO The DTO containing the request
	 *                                          parameters.
	 * @return The result of executing the stored procedure.
	 */
    @PostMapping("/mobileGetAssignedPlotAndStatus")
    public Object mobileGetAssignedPlotAndStatus(
            @RequestBody SurveyReviewFetchRequestMobileDTO surveyReviewFetchRequestMobileDTO) {
        try {
            String result = surveyReviewService.mobileGetAssignedPlotAndStatus(surveyReviewFetchRequestMobileDTO)
                    .toString();
            ObjectMapper mapper = new ObjectMapper();
            TypeFactory typeFactory = mapper.getTypeFactory();
            List<Object> resultObject = mapper.readValue(result,
                    typeFactory.constructCollectionType(List.class, Object.class));
            return new ResponseModel(resultObject,
                    CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS,
                    CustomMessages.METHOD_POST);
        } catch (Exception e) {
            return new ResponseModel(null,
                    CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
                    CustomMessages.METHOD_POST);
        }
    }

    // Handles the HTTP POST request to add a survey
    
    /**
	 * Performs add survey.
	 *
	 * @param jsonData The JSON data representing the survey.
	 * @param media    Optional media files associated with the survey.
	 * @param request  The HttpServletRequest object containing the request information.
	 * @return An Object representing the response to the survey addition request.
	 */
    @PostMapping("/addSurvey")
    public Object addSurvey(@RequestPart("data") String jsonData,
            @RequestPart(name = "media", required = false) List<MultipartFile> media,
            HttpServletRequest request)
            throws JsonMappingException, JsonProcessingException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            SurveyDetailMasterRequestDTO surveyDetailMasterRequestDTO = new SurveyDetailMasterRequestDTO();
            surveyDetailMasterRequestDTO.setData(jsonData);
            surveyDetailMasterRequestDTO.setMedia(media);
            List<HashMap<String, Object>> listmap = (List<HashMap<String, Object>>) surveyReviewService
                    .addSurvey(surveyDetailMasterRequestDTO, request);
           
            return new ResponseModel(listmap,
                    CustomMessages.RECORD_ADD, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS,
                    CustomMessages.METHOD_POST);
        } catch (NoSuchElementException e) {
        	 
        	exceptionLogService.addException(e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName(),
					e, jsonData);
        	
            return new ResponseModel(null,
            		e.getMessage(), HttpStatus.SC_NOT_FOUND, CustomMessages.FAILED,
                    CustomMessages.METHOD_POST);
        } catch (Exception e) {
        	
        	exceptionLogService.addException(e.getStackTrace()[0].getClassName(), e.getStackTrace()[0].getMethodName(),
					e, null);
            e.printStackTrace();
            return new ResponseModel(null,
            		e.getMessage(), CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
                    CustomMessages.METHOD_POST);
        }
    }

    // Handles the HTTP POST request to review survey details
    /**
	 * 
	 * Updates the survey status of a LandParcelSurveyMaster object based on the
	 * review number and review status.
	 *
	 * @param landParcelSurveyReviewDTO The {@code LandParcelSurveyReviewDTO} containing the request
	 *                                          parameters.
	 * @param request  The HttpServletRequest object containing the request information.
	 * @return
	 */
    @PostMapping("/reviewSurvey")
    public Object reviewSurveyDetails(@RequestBody LandParcelSurveyReviewDTO landParcelSurveyReviewDTO,
            HttpServletRequest request) {
        return surveyReviewService.reviewSurveyDetails(landParcelSurveyReviewDTO, request);
    }

    /**
	 * fetch mark unable to survey details
	 * @param requestDTO The {@code UnableToSurveyRequestDAO} containing the request
	 *                                          parameters.
	 * @param request  The HttpServletRequest object containing the request information.
	 * @return  The ResponseModel object representing the response.
	 */
    @PostMapping("/markUnableToSurvey")
    public Object markUnableToSurvey(@RequestBody UnableToSurveyRequestDAO requestDTO,
            HttpServletRequest request) {
        return surveyReviewService.markUnableToSurvey(requestDTO, request);
    }
    
    /**
	 * fetch survey summary count by user details
	 * @param request  The HttpServletRequest object containing the request information.
	 * @return  The ResponseModel object representing the response.
	 */
    @GetMapping(value = "/getSurveySummaryCountByUser")
	public ResponseModel getSurveySummaryCountByUser( HttpServletRequest request) {
		return surveyReviewService.getSurveySummaryCountByUser(request);
	}

    /**
	 * fetch survey summary count by user details
	 * @param request  The HttpServletRequest object containing the request information.
	 * @param requestDTO The {@code SurveyDetailRequestDTO} containing the request
	 *                                          parameters.
	 * @return  The ResponseModel object representing the response.
	 */
    @PostMapping(value = "/getSurveySummaryCountByUserV2")
    public ResponseModel getSurveySummaryCountByUserV2( @RequestBody SurveyDetailRequestDTO requestDTO,HttpServletRequest request) {
        return surveyReviewService.getSurveySummaryCountByUserV2(requestDTO,request);
    }
    
    /**
	 * fetch owner and cultivators details by parcel
	 * @param request  The HttpServletRequest object containing the request information.
	 * @param requestDTO The {@code SurveyDetailRequestDTO} containing the request
	 *                                          parameters.
	 * @return  The ResponseModel object representing the response.
	 */
    @PostMapping("/web/getOwnerAndCultivatorDetailsByParcel")
    public Object getOwnerAndCultivatorDetailsByParcel(@RequestBody SurveyDetailRequestDTO requestDTO,
            HttpServletRequest request) {
        return surveyReviewService.getOwnerAndCultivatorDetailsByParcel(requestDTO, request);
    }



    // Handles the HTTP POST request to retrieve assigned plot data for mobile
    /**
	 * fetch crop survey details for land
	 * @param surveyReviewFetchRequestMobileDTO The {@code SurveyReviewFetchRequestMobileDTO} containing the request
	 *                                          parameters.
	 * @return  The ResponseModel object representing the response.
	 */
    @PostMapping("/getCropSurveyDetailForLandId")
    public Object getCropSurveyDetailForLandId(
            @RequestBody SurveyReviewFetchRequestMobileDTO surveyReviewFetchRequestMobileDTO) {
        try {
            // Default values if plotIds and surveyMasterId are null
            if (surveyReviewFetchRequestMobileDTO.getYear() == null) {
                return CustomMessages.makeResponseModel(null, CustomMessages.INVALID_INPUT,
                        CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
            }
            if (surveyReviewFetchRequestMobileDTO.getSeasonId() == null) {
                return CustomMessages.makeResponseModel(null, CustomMessages.INVALID_INPUT,
                        CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
            }

            if (surveyReviewFetchRequestMobileDTO.getPlotIds() == null) {
                return CustomMessages.makeResponseModel(null, CustomMessages.INVALID_INPUT,
                        CustomMessages.GET_DATA_ERROR, CustomMessages.SUCCESS);
            }
            String result = surveyReviewService.getCropSurveyDetailForLandId(surveyReviewFetchRequestMobileDTO).toString();

            // Parsing the JSON response
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            TypeFactory typeFactory = mapper.getTypeFactory();
            List<AssignedPlotResponseDTO> resultObject = mapper.readValue(result,
                    typeFactory.constructCollectionType(List.class, AssignedPlotResponseDTO.class));

            // Creating and returning a response model
            return new ResponseModel(resultObject,
                    CustomMessages.GET_RECORD, CustomMessages.GET_DATA_SUCCESS, CustomMessages.SUCCESS,
                    CustomMessages.METHOD_POST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseModel(null,
                    CustomMessages.FAILURE, CustomMessages.INTERNAL_SERVER_ERROR, CustomMessages.FAILED,
                    CustomMessages.METHOD_POST);
        }
    }
    
    /**
	 * fetch all surveyor details
	 * @param request  The HttpServletRequest object containing the request information.
	 * @param requestDTO The {@code SurveyDetailRequestDTO} containing the request
	 *                                          parameters.
	 * @return  The ResponseModel object representing the response.
	 */
    @PostMapping(value = "/getAllSurveyorV2")
    public String getAllSurveyorV2( @RequestBody SurveyDetailRequestDTO requestDTO,HttpServletRequest request) {
        return surveyReviewService.getAllSurveyorV2(requestDTO,request);
    }

    /**
	 * fetch approved crop survey details
	 * @param request  The HttpServletRequest object containing the request information.
	 * @param requestDTO The {@code CommonRequestDAO} containing the request
	 *                                          parameters.
	 * @return  The ResponseModel object representing the response.
	 */
    @PostMapping(value = "/getApprovedCropSurvey")
    public String getApprovedCropSurveyForStateByDateRange( @RequestBody CommonRequestDAO requestDTO,HttpServletRequest request) {
        return surveyReviewService.getApprovedCropSurveyForStateByDateRange(requestDTO,request);
    }
}
