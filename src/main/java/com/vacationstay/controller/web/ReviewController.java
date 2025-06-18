package com.vacationstay.controller.web;

import com.vacationstay.dto.ReviewDTO;
import com.vacationstay.service.ReviewService;
import com.vacationstay.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserService userService;

    @PostMapping("/add")
    public String addReview(
            @ModelAttribute ReviewDTO reviewDTO,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        
        try {
            Long userId = userService.getUserIdByEmail(userDetails.getUsername());
            reviewDTO.setUserId(userId);
            
            ReviewDTO createdReview = reviewService.createReview(reviewDTO);
            redirectAttributes.addFlashAttribute("success", "Review submitted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
        return "redirect:/properties/" + reviewDTO.getPropertyId();
    }
}