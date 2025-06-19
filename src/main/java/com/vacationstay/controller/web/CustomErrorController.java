package com.vacationstay.controller.web;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            int statusCode = Integer.parseInt(status.toString());
            
            // Add the status code to the model
            model.addAttribute("status", statusCode);
            
            // Add error message to the model
            model.addAttribute("error", request.getAttribute(RequestDispatcher.ERROR_MESSAGE));
            
            // Add exception details if available
            Object exception = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
            if (exception != null) {
                model.addAttribute("message", ((Throwable) exception).getMessage());
            }
            
            // Add request URI
            model.addAttribute("path", request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI));
            
            // Return specific error pages for common status codes
            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                return "error/404";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                return "error/403";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                return "error/500";
            }
        }
        
        // Default to generic error page
        return "error";
    }
}