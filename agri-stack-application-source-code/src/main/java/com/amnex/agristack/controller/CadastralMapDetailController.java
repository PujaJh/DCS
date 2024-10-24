package com.amnex.agristack.controller;


import javax.servlet.http.HttpServletRequest;

import com.amnex.agristack.dao.common.ResponseModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.amnex.agristack.dao.CadastralMapDetailInput;
import com.amnex.agristack.service.CadastralMapCheckDetailService;

/***
 * Controller class for managing Crop Categories.
 * Handles HTTP requests related to Crop Categories.
 *
 * Author: krupali.jogi
 **/
@RestController
@RequestMapping("/cadastral-map-detail")
public class CadastralMapDetailController {


    @Autowired
    CadastralMapCheckDetailService cadastralMapCheckDetailService;


    /**
     * Retrieves the cadastral map details.
     *
     * @param request the HttpServletRequest object representing the incoming request
     * @param input input dao of CadastralMapDetailInput
     * @return a ResponseModel object containing the cadastral map details
     */
    @PostMapping(value = "/addCadastralMapDetail")
    public ResponseModel addCadastralMapDetail(@RequestBody CadastralMapDetailInput input, HttpServletRequest request) {
        return cadastralMapCheckDetailService.addCadastralMapDetail(input,request);
    }

    /**
     * Retrieves the cadastral data by user
     *
     * @param request the HttpServletRequest object representing the incoming request
     * @param input input dao of CadastralMapDetailInput
     * @return a ResponseModel object containing the cadastral data by user
     */
    @PostMapping(value = "/getCadastrialDataByUser")
    public ResponseModel getCadastrialDataByUser(@RequestBody CadastralMapDetailInput input, HttpServletRequest request) {
        return cadastralMapCheckDetailService.getCadastrialDataByUser(input);
    }

    /**
     * Retrieves the cadastral survey report
     *
     * @param request the HttpServletRequest object representing the incoming request
     * @param input input dao of CadastralMapDetailInput
     * @return a ResponseModel object containing the cadastral survey report
     */
    @PostMapping(value = "/getCadastrialSurveyReport")
    public ResponseModel getCadastrialSurveyReport(@RequestBody CadastralMapDetailInput input, HttpServletRequest request) {
        return cadastralMapCheckDetailService.getCadastrialSurveyReport(input);
    }
}
