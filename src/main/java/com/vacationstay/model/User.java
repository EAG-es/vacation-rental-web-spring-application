package com.vacationstay.model;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
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
     * Constructor for creating a local user (regular registration).
     * The ID will be auto-generated when the entity is persisted.
     */
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.provider = "local";
        this.roles = new HashSet<>();
        this.bookings = new ArrayList<>();
        this.reviews = new ArrayList<>();
        // Add default USER role
        this.roles.add("USER");
    }

    /**
     * Constructor for creating an OAuth user (social login).
     * The ID will be auto-generated when the entity is persisted.
     */
    public User(String name, String email, String imageUrl, String provider, String providerId) {
        this.name = name;
        this.email = email;
        this.imageUrl = imageUrl;
        this.provider = provider;
        this.providerId = providerId;
        this.roles = new HashSet<>();
        this.bookings = new ArrayList<>();
        this.reviews = new ArrayList<>();
        // Add default USER role
        this.roles.add("USER");
    }

    /**
     * Constructor for creating a user with specific roles.
     * The ID will be auto-generated when the entity is persisted.
     */
    public User(String name, String email, String password, String provider, 
                String providerId, String imageUrl, Set<String> roles) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.provider = provider != null ? provider : "local";
        this.providerId = providerId;
        this.imageUrl = imageUrl;
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        this.bookings = new ArrayList<>();
        this.reviews = new ArrayList<>();
        
        // Ensure at least USER role exists
        if (this.roles.isEmpty()) {
            this.roles.add("USER");
        }
    }

    /**
     * Full constructor including ID (for testing purposes or when ID is known).
     * Note: ID will be overridden by auto-generation if entity is persisted without an ID.
     */
    public User(Long id, String name, String email, String password, String imageUrl,
                String provider, String providerId, Set<String> roles,
                List<Booking> bookings, List<Review> reviews,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.imageUrl = imageUrl;
        this.provider = provider != null ? provider : "local";
        this.providerId = providerId;
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        this.bookings = bookings != null ? bookings : new ArrayList<>();
        this.reviews = reviews != null ? reviews : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        
        // Ensure at least USER role exists
        if (this.roles.isEmpty()) {
            this.roles.add("USER");
        }
    }

    /**
     * Ensures collections are initialized when entity is loaded.
     */
    @PostConstruct
    public void initializeCollections() {
        if (this.roles == null) {
            this.roles = new HashSet<>();
            this.roles.add("USER");
        }
        if (this.bookings == null) {
            this.bookings = new ArrayList<>();
        }
        if (this.reviews == null) {
            this.reviews = new ArrayList<>();
        }
    }

    /**
     * Checks if the user is authenticated via OAuth.
     *
     * @return true if the user is authenticated via an OAuth provider, false otherwise
     */
    public boolean isOauthUser() {
        return provider != null && !provider.equals("local");
    }

    /**
     * Checks if the user has a specific role.
     *
     * @param role the role to check
     * @return true if the user has the role, false otherwise
     */
    public boolean hasRole(String role) {
        return roles != null && roles.contains(role);
    }

    /**
     * Adds a role to the user.
     *
     * @param role the role to add
     */
    public void addRole(String role) {
        if (this.roles == null) {
            this.roles = new HashSet<>();
        }
        this.roles.add(role);
    }

    /**
     * Removes a role from the user.
     *
     * @param role the role to remove
     */
    public void removeRole(String role) {
        if (this.roles != null) {
            this.roles.remove(role);
        }
    }

    /**
     * Checks if the user is an admin.
     *
     * @return true if the user has ADMIN role, false otherwise
     */
    public boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Override equals to use ID for comparison when ID is present.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        User user = (User) o;
        
        // If both objects have IDs, compare by ID
        if (id != null && user.id != null) {
            return id.equals(user.id);
        }
        
        // If no IDs, compare by email (unique identifier)
        if (email != null && user.email != null) {
            return email.equals(user.email);
        }
        
        // If no email, use default equals (reference equality)
        return false;
    }

    /**
     * Override hashCode to be consistent with equals.
     */
    @Override
    public int hashCode() {
        if (id != null) {
            return id.hashCode();
        }
        if (email != null) {
            return email.hashCode();
        }
        return super.hashCode();
    }

    /**
     * Returns a string representation of the user for debugging.
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", provider='" + provider + '\'' +
                ", roles=" + roles +
                ", createdAt=" + createdAt +
                '}';
    }
}