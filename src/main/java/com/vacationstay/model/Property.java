package com.vacationstay.model;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.*;
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
    private Integer bedrooms = 0;

    /**
     * Number of bathrooms in the property.
     */
    @Column(nullable = false)
    private Integer bathrooms = 0;

    /**
     * Maximum number of guests the property can accommodate.
     */
    @Column(nullable = false)
    private Integer maxGuests = 0;

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

    /**
     * Constructor that initializes collections and sets up the property.
     * The ID will be auto-generated when the entity is persisted.
     */
    public Property(String title, String description, String location, BigDecimal price,
                   Integer bedrooms, Integer bathrooms, Integer maxGuests, 
                   String amenities, String images, String ownerId) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.price = price;
        this.bedrooms = bedrooms != null ? bedrooms : 0;
        this.bathrooms = bathrooms != null ? bathrooms : 0;
        this.maxGuests = maxGuests != null ? maxGuests : 0;
        this.amenities = amenities;
        this.images = images;
        this.ownerId = ownerId;
        this.bookings = new ArrayList<>();
        this.reviews = new ArrayList<>();
    }

    /**
     * Full constructor including ID (for testing purposes or when ID is known).
     * Note: ID will be overridden by auto-generation if entity is persisted without an ID.
     */
    public Property(Long id, String title, String description, String location, BigDecimal price,
                   Integer bedrooms, Integer bathrooms, Integer maxGuests, 
                   String amenities, String images, String ownerId,
                   List<Booking> bookings, List<Review> reviews,
                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.price = price;
        this.bedrooms = bedrooms != null ? bedrooms : 0;
        this.bathrooms = bathrooms != null ? bathrooms : 0;
        this.maxGuests = maxGuests != null ? maxGuests : 0;
        this.amenities = amenities;
        this.images = images;
        this.ownerId = ownerId;
        this.bookings = bookings != null ? bookings : new ArrayList<>();
        this.reviews = reviews != null ? reviews : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    /**
     * Ensures collections are initialized when entity is loaded.
     */
    @PostConstruct
    public void initializeCollections() {
        if (this.bookings == null) {
            this.bookings = new ArrayList<>();
        }
        if (this.reviews == null) {
            this.reviews = new ArrayList<>();
        }
    }

    /**
     * Override equals to use ID for comparison when ID is present.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Property property = (Property) o;
        
        // If both objects have IDs, compare by ID
        if (id != null && property.id != null) {
            return id.equals(property.id);
        }
        
        // If no IDs, use default equals (reference equality)
        return false;
    }

    /**
     * Override hashCode to be consistent with equals.
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : super.hashCode();
    }
}