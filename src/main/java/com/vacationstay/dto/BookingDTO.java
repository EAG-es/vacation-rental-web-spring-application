package com.vacationstay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Object for Booking entities.
 * <p>
 * This class is used to transfer booking data between the service layer and controllers,
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
public class BookingDTO {
    /**
     * The unique identifier of the booking.
     */
    private Long id;
    
    /**
     * The ID of the property being booked.
     */
    private Long propertyId;
    
    /**
     * The ID of the user making the booking.
     */
    private Long userId;
    
    /**
     * The check-in date for this booking.
     */
    private LocalDate startDate;
    
    /**
     * The check-out date for this booking.
     */
    private LocalDate endDate;
    
    /**
     * The total price for the entire booking period.
     */
    private BigDecimal totalPrice;
    
    /**
     * The current status of the booking (e.g., "confirmed", "cancelled").
     */
    private String status;
    
    /**
     * The property details associated with this booking.
     */
    private PropertyDTO property;
    
    /**
     * The user details associated with this booking.
     */
    private UserDTO user;
    
    /**
     * Timestamp when the booking was created.
     */
    private LocalDateTime createdAt;
    
    /**
     * Timestamp when the booking was last updated.
     */
    private LocalDateTime updatedAt;
}