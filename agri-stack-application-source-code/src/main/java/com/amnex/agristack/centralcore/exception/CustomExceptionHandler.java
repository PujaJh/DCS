package com.amnex.agristack.centralcore.exception;


import com.amnex.agristack.centralcore.dao.ErrorDAO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Object> handleUnauthorizedException(UnauthorizedException ex) {
        Map<String, Object> responseBody = new HashMap<>();
//        responseBody.put("status", HttpStatus.UNAUTHORIZED.value());

//        responseBody.put("message", ex.getMessage());
        ErrorDAO errorDAO=new ErrorDAO();
        errorDAO.setCode("401");
        errorDAO.setMessage(ex.getMessage());
        responseBody.put("error", errorDAO);
        return new ResponseEntity<>(responseBody, HttpStatus.UNAUTHORIZED);
    }
}
