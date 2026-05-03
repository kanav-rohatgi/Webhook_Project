package com.webhook.userservice.exception;

import com.webhook.userservice.payload.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<APIResponse> handleUserAlreadyExists(UserAlreadyExistsException e) {
        return new ResponseEntity<>(new APIResponse(e.getMessage(), false), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<APIResponse> handleRuntime(RuntimeException e) {
        return new ResponseEntity<>(new APIResponse(e.getMessage(), false), HttpStatus.BAD_REQUEST);
    }
}
