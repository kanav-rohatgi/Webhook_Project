package com.webhook.webhookservice.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, String field, String fieldValue) {
        super(String.format("%s not found with %s : %s", resourceName, field, fieldValue));
    }

    public ResourceNotFoundException(String resourceName, String field, Long fieldId) {
        super(String.format("%s not found with %s : %d", resourceName, field, fieldId));
    }
}
