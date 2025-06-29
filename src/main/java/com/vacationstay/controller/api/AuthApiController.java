package com.vacationstay.controller.api;

import com.vacationstay.dto.UserDTO;
import com.vacationstay.security.JwtUtil;
import com.vacationstay.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for authentication-related operations.
 * <p>
 * This controller provides API endpoints for user authentication,
 * JWT token generation, and user registration for API access.
 * </p>
 *
 * @author VacationStay Team
 * @version 1.0
 * @since 2023-06-18
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthApiController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    /**
     * Authenticates a user and returns a JWT token.
     *
     * @param loginRequest the login request containing email and password
     * @return ResponseEntity containing the JWT token and user information
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateToken(authentication);

            UserDTO userDTO = userService.getUserByEmail(loginRequest.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("token", jwt);
            response.put("type", "Bearer");
            response.put("user", userDTO);
            response.put("expiresIn", jwtUtil.getJwtExpirationMs());

            log.info("User {} successfully authenticated via API", loginRequest.getEmail());
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            log.warn("Failed login attempt for email: {}", loginRequest.getEmail());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid credentials");
            error.put("message", "Email or password is incorrect");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    /**
     * Registers a new user for API access.
     *
     * @param signUpRequest the signup request containing user information
     * @return ResponseEntity containing success message or error
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        try {
            // Check if email is already taken
            if (userService.existsByEmail(signUpRequest.getEmail())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Email already exists");
                error.put("message", "A user with this email already exists");
                return ResponseEntity.badRequest().body(error);
            }

            // Create new user
            UserDTO userDTO = new UserDTO();
            userDTO.setName(signUpRequest.getName());
            userDTO.setEmail(signUpRequest.getEmail());
            userDTO.setPassword(signUpRequest.getPassword());

            UserDTO createdUser = userService.createUser(userDTO);

            // Generate JWT token for the new user
            String jwt = jwtUtil.generateToken(createdUser.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("token", jwt);
            response.put("type", "Bearer");
            response.put("user", createdUser);
            response.put("expiresIn", jwtUtil.getJwtExpirationMs());

            log.info("New user registered via API: {}", signUpRequest.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            log.error("Error registering user: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Registration failed"); 
            error.put("message", "An error occurred during registration");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Gets the current authenticated user's information.
     *
     * @param authentication the current authentication
     * @return ResponseEntity containing user information
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unauthorized");
            error.put("message", "No authenticated user found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        try {
            String email = authentication.getName();
            UserDTO userDTO = userService.getUserByEmail(email);
            return ResponseEntity.ok(userDTO);
        } catch (Exception e) {
            log.error("Error getting current user: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found");
            error.put("message", "Could not retrieve user information");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    /**
     * Request class for user login.
     */
    public static class LoginRequest {
        private String email;
        private String password;

        // Getters and setters
        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    /**
     * Request class for user registration.
     */
    public static class SignUpRequest {
        private String name;
        private String email;
        private String password;

        // Getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}