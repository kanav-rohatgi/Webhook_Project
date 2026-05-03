package com.webhook.webhookservice.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webhook.webhookservice.payload.APIResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> handleApiException(APIException e) {
        return new ResponseEntity<>(new APIResponse(e.getMessage(), false), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> handleResourceNotFoundException(ResourceNotFoundException e) {
        return new ResponseEntity<>(new APIResponse(e.getMessage(), false), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(JsonProcessingException.class)
    public ResponseEntity<APIResponse> handleJsonProcessingException(JsonProcessingException e) {
        return new ResponseEntity<>(new APIResponse("Failed to process JSON: " + e.getOriginalMessage(), false), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<APIResponse> handleRuntime(RuntimeException e) {
        return new ResponseEntity<>(new APIResponse(e.getMessage(), false), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
