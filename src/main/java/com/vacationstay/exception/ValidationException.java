package com.vacationstay.exception;

import lombok.Getter;
import java.util.Map;

/**
 * Exception thrown when validation fails.
 */
@Getter
public class ValidationException extends RuntimeException {
    
    private final Map<String, String> validationErrors;
    
    public ValidationException(String message) {
        super(message);
        this.validationErrors = null;
    }
    
    public ValidationException(String message, Map<String, String> validationErrors) {
        super(message);
        this.validationErrors = validationErrors;
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
        this.validationErrors = null;
    }
}