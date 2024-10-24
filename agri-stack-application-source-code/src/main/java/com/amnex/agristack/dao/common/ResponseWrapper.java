package com.amnex.agristack.dao.common;

import com.amnex.agristack.dao.CommonRequestDAO;

import java.util.List;

public class ResponseWrapper {
    private List<Object> resultData;
    private CommonRequestDAO parameters;

    private Long totalRecords;

    public List<Object> getResultData() {
        return resultData;
    }

    public void setResultData(List<Object> resultData) {
        this.resultData = resultData;
    }

    public CommonRequestDAO getParameters() {
        return parameters;
    }

    public void setParameters(CommonRequestDAO parameters) {
        this.parameters = parameters;
    }

    public ResponseWrapper(List<Object> resultData, CommonRequestDAO parameters) {
        this.resultData = resultData;
        this.parameters = parameters;
    }

    public Long getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Long totalRecords) {
        this.totalRecords = totalRecords;
    }

    public ResponseWrapper() {

    }


}