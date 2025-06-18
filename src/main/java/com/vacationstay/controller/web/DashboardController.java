package com.vacationstay.controller.web;

import com.vacationstay.dto.BookingDTO;
import com.vacationstay.dto.PropertyDTO;
import com.vacationstay.service.BookingService;
import com.vacationstay.service.PropertyService;
import com.vacationstay.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final BookingService bookingService;
    private final PropertyService propertyService;
    private final UserService userService;

    @GetMapping
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long userId = userService.getUserIdByEmail(userDetails.getUsername());
        
        List<BookingDTO> userBookings = bookingService.getBookingsByUser(userId);
        List<PropertyDTO> userProperties = propertyService.getPropertiesByOwner(userId.toString());
        
        model.addAttribute("userBookings", userBookings);
        model.addAttribute("userProperties", userProperties);
        model.addAttribute("user", userService.getUserById(userId));
        
        return "dashboard";
    }
}