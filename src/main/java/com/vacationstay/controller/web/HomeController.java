package com.vacationstay.controller.web;

import com.vacationstay.dto.PropertyDTO;
import com.vacationstay.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PropertyService propertyService;

    @GetMapping("/")
    public String home(Model model) {
        List<PropertyDTO> featuredProperties = propertyService.getAllProperties().stream()
                .limit(6)
                .toList();
        model.addAttribute("featuredProperties", featuredProperties);
        return "index";
    }
}