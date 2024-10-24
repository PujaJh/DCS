package com.amnex.agristack.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MyMediaNotFoundException extends RuntimeException {
    public MyMediaNotFoundException(String message) {
        super(message);
    }

    public MyMediaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}