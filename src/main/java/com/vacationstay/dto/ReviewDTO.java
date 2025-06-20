package com.vacationstay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object for Review entities.
 * <p>
 * This class is used to transfer review data between the service layer and controllers,
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
public class ReviewDTO {
    /**
     * The unique identifier of the review.
     */
    private Long id;
    
    /**
     * The ID of the property being reviewed.
     */
    private Long propertyId;
    
    /**
     * The ID of the user submitting the review.
     */
    private Long userId;
    
    /**
     * The numerical rating given (typically 1-5).
     */
    private Integer rating;
    
    /**
     * The text comment provided with the review.
     */
    private String comment;
    
    /**
     * The name of the user who submitted the review.
     */
    private String userName;
    
    /**
     * Timestamp when the review was created.
     */
    private LocalDateTime createdAt;
}