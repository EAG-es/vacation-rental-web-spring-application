package com.vacationstay.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Handles both web and API exceptions with appropriate responses.
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException for API requests.
     * Returns JSON response for API endpoints, HTML view for web requests.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleResourceNotFoundException(ResourceNotFoundException ex, 
                                                 HttpServletRequest request, 
                                                 WebRequest webRequest) {
        log.error("Resource not found: {}", ex.getMessage());
        
        if (isApiRequest(request)) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.NOT_FOUND.value())
                    .error("Not Found")
                    .message(ex.getMessage())
                    .path(request.getRequestURI())
                    .build();
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
        
        ModelAndView modelAndView = new ModelAndView("error/404");
        modelAndView.addObject("status", HttpStatus.NOT_FOUND.value());
        modelAndView.addObject("error", "Resource Not Found");
        modelAndView.addObject("message", ex.getMessage());
        modelAndView.addObject("path", request.getRequestURI());
        return modelAndView;
    }

    /**
     * Handles ValidationException for both API and web requests.
     */
    @ExceptionHandler(ValidationException.class)
    public Object handleValidationException(ValidationException ex, 
                                          HttpServletRequest request) {
        log.error("Validation error: {}", ex.getMessage());
        
        if (isApiRequest(request)) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .error("Validation Failed")
                    .message(ex.getMessage())
                    .path(request.getRequestURI())
                    .details(ex.getValidationErrors())
                    .build();
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        
        ModelAndView modelAndView = new ModelAndView("error/validation");
        modelAndView.addObject("status", HttpStatus.BAD_REQUEST.value());
        modelAndView.addObject("error", "Validation Error");
        modelAndView.addObject("message", ex.getMessage());
        modelAndView.addObject("details", ex.getValidationErrors());
        modelAndView.addObject("path", request.getRequestURI());
        return modelAndView;
    }

    /**
     * Handles Spring validation errors (e.g., @Valid annotations).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleMethodArgumentNotValid(MethodArgumentNotValidException ex, 
                                             HttpServletRequest request) {
        log.error("Method argument not valid: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.put(error.getField(), error.getDefaultMessage())
        );

        if (isApiRequest(request)) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.BAD_REQUEST.value())
                    .error("Validation Failed")
                    .message("Invalid input data")
                    .path(request.getRequestURI())
                    .details(errors)
                    .build();
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
        
        ModelAndView modelAndView = new ModelAndView("error/validation");
        modelAndView.addObject("status", HttpStatus.BAD_REQUEST.value());
        modelAndView.addObject("error", "Validation Error");
        modelAndView.addObject("message", "Please correct the following errors:");
        modelAndView.addObject("details", errors);
        modelAndView.addObject("path", request.getRequestURI());
        return modelAndView;
    }

    /**
     * Handles UnauthorizedException.
     */
    @ExceptionHandler(UnauthorizedException.class)
    public Object handleUnauthorizedException(UnauthorizedException ex, 
                                            HttpServletRequest request) {
        log.error("Unauthorized access: {}", ex.getMessage());
        
        if (isApiRequest(request)) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.UNAUTHORIZED.value())
                    .error("Unauthorized")
                    .message(ex.getMessage())
                    .path(request.getRequestURI())
                    .build();
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
        
        ModelAndView modelAndView = new ModelAndView("error/401");
        modelAndView.addObject("status", HttpStatus.UNAUTHORIZED.value());
        modelAndView.addObject("error", "Unauthorized");
        modelAndView.addObject("message", ex.getMessage());
        modelAndView.addObject("path", request.getRequestURI());
        return modelAndView;
    }

    /**
     * Handles AccessDeniedException.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Object handleAccessDeniedException(AccessDeniedException ex, 
                                            HttpServletRequest request) {
        log.error("Access denied: {}", ex.getMessage());
        
        if (isApiRequest(request)) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.FORBIDDEN.value())
                    .error("Access Denied")
                    .message(ex.getMessage())
                    .path(request.getRequestURI())
                    .build();
            return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
        }
        
        ModelAndView modelAndView = new ModelAndView("error/403");
        modelAndView.addObject("status", HttpStatus.FORBIDDEN.value());
        modelAndView.addObject("error", "Access Denied");
        modelAndView.addObject("message", ex.getMessage());
        modelAndView.addObject("path", request.getRequestURI());
        return modelAndView;
    }

    /**
     * Handles general runtime exceptions.
     */
    @ExceptionHandler(RuntimeException.class)
    public Object handleRuntimeException(RuntimeException ex, 
                                       HttpServletRequest request) {
        log.error("Runtime exception occurred: ", ex);
        
        if (isApiRequest(request)) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .error("Internal Server Error")
                    .message("An unexpected error occurred")
                    .path(request.getRequestURI())
                    .build();
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        ModelAndView modelAndView = new ModelAndView("error/500");
        modelAndView.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        modelAndView.addObject("error", "Internal Server Error");
        modelAndView.addObject("message", "An unexpected error occurred. Please try again later.");
        modelAndView.addObject("path", request.getRequestURI());
        return modelAndView;
    }

    /**
     * Handles all other exceptions.
     */
    @ExceptionHandler(Exception.class)
    public Object handleGenericException(Exception ex, 
                                       HttpServletRequest request) {
        log.error("Unexpected exception occurred: ", ex);
        
        if (isApiRequest(request)) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .error("Internal Server Error")
                    .message("An unexpected error occurred")
                    .path(request.getRequestURI())
                    .build();
            return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        
        ModelAndView modelAndView = new ModelAndView("error/500");
        modelAndView.addObject("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        modelAndView.addObject("error", "Internal Server Error");
        modelAndView.addObject("message", "An unexpected error occurred. Please try again later.");
        modelAndView.addObject("path", request.getRequestURI());
        return modelAndView;
    }

    /**
     * Determines if the request is an API request based on the request path and accept header.
     */
    private boolean isApiRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        String acceptHeader = request.getHeader("Accept");
        
        return path.startsWith("/api/") || 
               (acceptHeader != null && acceptHeader.contains("application/json"));
    }
}