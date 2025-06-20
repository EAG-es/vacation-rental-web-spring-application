package com.vacationstay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entity class representing a user in the system.
 * <p>
 * This class stores user account information, authentication details,
 * and relationships to bookings and reviews.
 * </p>
 *
 * @author VacationStay Team
 * @version 1.0
 * @since 2023-06-18
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The user's full name.
     */
    @Column(nullable = false)
    private String name;

    /**
     * The user's email address, used for login.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * The user's password (hashed).
     */
    @Column
    private String password;

    /**
     * URL to the user's profile image.
     */
    @Column
    private String imageUrl;

    /**
     * Authentication provider (e.g., "local", "google").
     */
    @Column
    private String provider; // "local", "google", etc.

    /**
     * ID from the authentication provider.
     */
    @Column
    private String providerId;

    /**
     * Set of roles assigned to the user.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();

    /**
     * List of bookings made by this user.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    /**
     * List of reviews written by this user.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    /**
     * Timestamp when the user account was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the user account was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Checks if the user is authenticated via OAuth.
     *
     * @return true if the user is authenticated via an OAuth provider, false otherwise
     */
    public boolean isOauthUser() {
        return provider != null && !provider.equals("local");
    }
}