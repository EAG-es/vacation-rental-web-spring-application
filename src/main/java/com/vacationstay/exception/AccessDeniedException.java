package com.vacationstay.exception;

/**
 * Exception thrown when user doesn't have permission to access a resource.
 */
public class AccessDeniedException extends RuntimeException {
    
    public AccessDeniedException(String message) {
        super(message);
    }
    
    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}