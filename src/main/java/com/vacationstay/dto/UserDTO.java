package com.vacationstay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Data Transfer Object for User entities.
 * <p>
 * This class is used to transfer user data between the service layer and controllers,
 * as well as for API responses and registration requests.
 * </p>
 *
 * @author VacationStay Team
 * @version 1.0
 * @since 2023-06-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    /**
     * The unique identifier of the user.
     */
    private Long id;
    
    /**
     * The user's full name.
     */
    private String name;
    
    /**
     * The user's email address, used for login.
     */
    private String email;
    
    /**
     * The user's password (only used for registration/updates, never returned in responses).
     */
    private String password;
    
    /**
     * URL to the user's profile image.
     */
    private String imageUrl;
    
    /**
     * Authentication provider (e.g., "local", "google").
     */
    private String provider;
    
    /**
     * ID from the authentication provider.
     */
    private String providerId;
    
    /**
     * Set of roles assigned to the user.
     */
    private Set<String> roles;
    
    /**
     * Timestamp when the user account was created.
     */
    private LocalDateTime createdAt;
}