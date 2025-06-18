package com.vacationstay.controller.web;

import com.vacationstay.dto.BookingDTO;
import com.vacationstay.dto.PropertyDTO;
import com.vacationstay.service.BookingService;
import com.vacationstay.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final PropertyService propertyService;

    @GetMapping("/create/{propertyId}")
    public String showBookingForm(
            @PathVariable Long propertyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Model model) {
        
        PropertyDTO property = propertyService.getPropertyById(propertyId);
        
        // Check if property is available for these dates
        boolean isAvailable = bookingService.isPropertyAvailable(propertyId, startDate, endDate);
        
        if (!isAvailable) {
            return "redirect:/properties/" + propertyId + "?error=unavailable";
        }
        
        // Calculate total price
        long nights = ChronoUnit.DAYS.between(startDate, endDate);
        BigDecimal totalPrice = property.getPrice().multiply(BigDecimal.valueOf(nights));
        
        model.addAttribute("property", property);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("nights", nights);
        model.addAttribute("totalPrice", totalPrice);
        
        return "booking-form";
    }

    @PostMapping("/create")
    public String createBooking(
            @ModelAttribute BookingDTO bookingDTO,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        
        try {
            BookingDTO createdBooking = bookingService.createBooking(bookingDTO);
            redirectAttributes.addFlashAttribute("success", "Booking confirmed successfully!");
            return "redirect:/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/properties/" + bookingDTO.getPropertyId();
        }
    }

    @PostMapping("/{id}/cancel")
    public String cancelBooking(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        
        try {
            bookingService.cancelBooking(id);
            redirectAttributes.addFlashAttribute("success", "Booking cancelled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/dashboard";
    }
}