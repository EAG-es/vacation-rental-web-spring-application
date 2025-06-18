package com.vacationstay.controller.web;

import com.vacationstay.dto.UserDTO;
import com.vacationstay.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String error, 
                       @RequestParam(required = false) String logout,
                       HttpServletRequest request,
                       Model model) {
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return "redirect:/";
        }
        
        // Store the intended URL in session if it exists
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.contains("/login") && !referer.contains("/register")) {
            request.getSession().setAttribute("REDIRECT_URI", referer);
        }
        
        if (error != null) {
            model.addAttribute("error", "Invalid username or password");
        }
        
        if (logout != null) {
            model.addAttribute("success", "You have been logged out successfully");
        }
        
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserDTO());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserDTO userDTO, 
                              BindingResult result, 
                              RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "register";
        }
        
        try {
            // Set provider to "local" for regular registrations
            userDTO.setProvider("local");
            userService.registerUser(userDTO);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }
    
    @GetMapping("/oauth2/success")
    public String oauth2LoginSuccess(HttpServletRequest request) {
        // Get the intended URL from the session if it exists
        String targetUrl = (String) request.getSession().getAttribute("REDIRECT_URI");
        if (targetUrl != null) {
            request.getSession().removeAttribute("REDIRECT_URI");
            return "redirect:" + targetUrl;
        }
        return "redirect:/";
    }
}