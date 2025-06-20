package com.vacationstay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for Property entities.
 * <p>
 * This class is used to transfer property data between the service layer and controllers,
 * as well as for API responses.
 * </p>
 *
 * @author VacationStay Team
 * @version 1.0
 * @since 2023-06-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDTO {
    /**
     * The unique identifier of the property.
     */
    private Long id;
    
    /**
     * The title/name of the property.
     */
    private String title;
    
    /**
     * Detailed description of the property.
     */
    private String description;
    
    /**
     * Geographic location of the property.
     */
    private String location;
    
    /**
     * Nightly rental price of the property.
     */
    private BigDecimal price;
    
    /**
     * Number of bedrooms in the property.
     */
    private Integer bedrooms;
    
    /**
     * Number of bathrooms in the property.
     */
    private Integer bathrooms;
    
    /**
     * Maximum number of guests the property can accommodate.
     */
    private Integer maxGuests;
    
    /**
     * List of amenities offered by the property.
     */
    private List<String> amenities;
    
    /**
     * URLs of property images.
     */
    private List<String> images;
    
    /**
     * ID of the user who owns this property.
     */
    private String ownerId;
    
    /**
     * Average rating of the property based on reviews.
     */
    private Double averageRating;
    
    /**
     * Timestamp when the property was created.
     */
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the property was last updated.
     */
    private LocalDateTime updatedAt;
}