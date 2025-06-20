package com.vacationstay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity class representing a vacation rental property.
 * <p>
 * This class stores all information related to a property listing including
 * its details, amenities, images, and relationships to bookings and reviews.
 * </p>
 *
 * @author VacationStay Team
 * @version 1.0
 * @since 2023-06-18
 */
@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Property {

    /**
     * Unique identifier for the property.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The title/name of the property.
     */
    @Column(nullable = false)
    private String title;

    /**
     * Detailed description of the property.
     */
    @Column(nullable = false, length = 2000)
    private String description;

    /**
     * Geographic location of the property.
     */
    @Column(nullable = false)
    private String location;

    /**
     * Nightly rental price of the property.
     */
    @Column(nullable = false)
    private BigDecimal price;

    /**
     * Number of bedrooms in the property.
     */
    @Column(nullable = false)
    private Integer bedrooms;

    /**
     * Number of bathrooms in the property.
     */
    @Column(nullable = false)
    private Integer bathrooms;

    /**
     * Maximum number of guests the property can accommodate.
     */
    @Column(nullable = false)
    private Integer maxGuests;

    /**
     * List of amenities offered by the property, stored as a JSON string.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String amenities; // Stored as JSON string

    /**
     * URLs of property images, stored as a JSON string.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String images; // Stored as JSON string

    /**
     * ID of the user who owns this property.
     */
    @Column(name = "owner_id")
    private String ownerId;

    /**
     * List of bookings associated with this property.
     */
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    /**
     * List of reviews for this property.
     */
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    /**
     * Timestamp when the property was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the property was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}