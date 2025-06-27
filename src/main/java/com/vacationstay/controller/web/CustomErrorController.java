package com.vacationstay.controller.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vacationstay.exception.ErrorResponse;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CustomErrorController implements ErrorController {
    
    private final ObjectMapper objectMapper;

    @RequestMapping("/error")
    public Object handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String requestURI = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        String errorMessage = (String) request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        
        int statusCode = 500; // Default to 500 if status is null
        if (status != null) {
            statusCode = Integer.parseInt(status.toString());
        }
        
        log.error("Error {} occurred for path {}: {}", statusCode, requestURI, errorMessage);
        
        // Check if this is an API request
        if (isApiRequest(request)) {
            return handleApiError(statusCode, errorMessage, requestURI, exception);
        }
        
        // Handle web request
        return handleWebError(statusCode, errorMessage, requestURI, exception, model);
    }
    
    private ResponseEntity<ErrorResponse> handleApiError(int statusCode, String errorMessage, 
                                                        String requestURI, Object exception) {
        String message = errorMessage;
        if (message == null && exception instanceof Throwable) {
            message = ((Throwable) exception).getMessage();
        }
        if (message == null) {
            message = getDefaultErrorMessage(statusCode);
        }
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(statusCode)
                .error(HttpStatus.valueOf(statusCode).getReasonPhrase())
                .message(message)
                .path(requestURI)
                .build();
                
        return new ResponseEntity<>(errorResponse, HttpStatus.valueOf(statusCode));
    }
    
    private String handleWebError(int statusCode, String errorMessage, String requestURI, 
                                 Object exception, Model model) {
        // Add common attributes to model
        model.addAttribute("status", statusCode);
        model.addAttribute("error", HttpStatus.valueOf(statusCode).getReasonPhrase());
        model.addAttribute("path", requestURI);
        
        // Add exception details if available
        if (exception instanceof Throwable) {
            model.addAttribute("message", ((Throwable) exception).getMessage());
        } else if (errorMessage != null) {
            model.addAttribute("message", errorMessage);
        } else {
            model.addAttribute("message", getDefaultErrorMessage(statusCode));
        }
        
        // Return specific error pages for common status codes
        switch (statusCode) {
            case 400:
                return "error/validation";
            case 401:
                return "error/401";
            case 403:
                return "error/403";
            case 404:
                return "error/404";
            case 500:
                return "error/500";
            default:
                return "error";
        }
    }
    
    private boolean isApiRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        String acceptHeader = request.getHeader("Accept");
        String contentType = request.getContentType();
        
        return path.startsWith("/api/") || 
               (acceptHeader != null && acceptHeader.contains("application/json")) ||
               (contentType != null && contentType.contains("application/json"));
    }
    
    private String getDefaultErrorMessage(int statusCode) {
        return switch (statusCode) {
            case 400 -> "Bad request. Please check your input.";
            case 401 -> "You need to log in to access this resource.";
            case 403 -> "You don't have permission to access this resource.";
            case 404 -> "The requested resource was not found.";
            case 500 -> "An internal server error occurred. Please try again later.";
            default -> "An unexpected error occurred.";
        };
    }
}