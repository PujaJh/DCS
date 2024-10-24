package com.amnex.agristack.dao;

import org.springframework.http.HttpStatus;

public class NotSupported implements MediaResponse {
    private String message;
    private HttpStatus status;
    private String fileName;

    public NotSupported(String message, HttpStatus status, String fileName) {
        this.message = message;
        this.status = status;
        this.fileName = fileName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}