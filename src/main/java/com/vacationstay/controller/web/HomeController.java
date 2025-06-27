package com.vacationstay.controller.web;

import com.vacationstay.dto.PropertyDTO;
import com.vacationstay.service.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final PropertyService propertyService;

    @GetMapping("/")
    public String home(Model model) {
        try {
            List<PropertyDTO> featuredProperties = propertyService.getAllProperties().stream()
                    .limit(6)
                    .toList();
            model.addAttribute("featuredProperties", featuredProperties);
            return "index";
        } catch (Exception e) {
            log.error("Error loading home page: ", e);
            // Fallback to empty list to prevent page from breaking
            model.addAttribute("featuredProperties", new ArrayList<PropertyDTO>());
            model.addAttribute("error", "Some content may not be available at the moment");
            return "index";
        }
    }

    /**
     * Individual exception handler for this controller.
     * Handles exceptions specific to home page operations.
     */
    @ExceptionHandler(Exception.class)
    public ModelAndView handleHomeException(Exception ex, HttpServletRequest request) {
        log.error("Exception in HomeController: ", ex);
        
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("featuredProperties", new ArrayList<PropertyDTO>());
        modelAndView.addObject("error", "Unable to load featured properties. Please try again later.");
        
        return modelAndView;
    }
}