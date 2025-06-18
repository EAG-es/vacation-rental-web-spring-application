package com.vacationstay.controller.web;

import com.vacationstay.dto.PropertyDTO;
import com.vacationstay.service.BookingService;
import com.vacationstay.service.PropertyService;
import com.vacationstay.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/properties")
@RequiredArgsConstructor
public class PropertyController {

    private final PropertyService propertyService;
    private final BookingService bookingService;
    private final ReviewService reviewService;

    @GetMapping
    public String listProperties(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer bedrooms,
            @RequestParam(required = false) Integer bathrooms,
            @RequestParam(required = false) Integer guests,
            Model model) {
        
        List<PropertyDTO> properties;
        
        if (location != null || minPrice != null || maxPrice != null || 
            bedrooms != null || bathrooms != null || guests != null) {
            properties = propertyService.searchProperties(
                    location, minPrice, maxPrice, bedrooms, bathrooms, guests);
        } else {
            properties = propertyService.getAllProperties();
        }
        
        model.addAttribute("properties", properties);
        model.addAttribute("location", location);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("bedrooms", bedrooms);
        model.addAttribute("bathrooms", bathrooms);
        model.addAttribute("guests", guests);
        
        return "properties";
    }

    @GetMapping("/{id}")
    public String getPropertyDetails(@PathVariable Long id, Model model) {
        PropertyDTO property = propertyService.getPropertyById(id);
        model.addAttribute("property", property);
        model.addAttribute("reviews", reviewService.getReviewsByProperty(id));
        return "property-detail";
    }
}