package com.vacationstay.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity class representing a property booking.
 * <p>
 * This class stores information about a reservation made by a user for a specific property,
 * including dates, price, and status.
 * </p>
 *
 * @author VacationStay Team
 * @version 1.0
 * @since 2023-06-18
 */
@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    /**
     * Unique identifier for the booking.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The property associated with this booking.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    /**
     * The user who made this booking.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The check-in date for this booking.
     */
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    /**
     * The check-out date for this booking.
     */
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * The total price for the entire booking period.
     */
    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    /**
     * The current status of the booking (e.g., "confirmed", "cancelled").
     */
    @Column(nullable = false)
    private String status = "confirmed";

    /**
     * Timestamp when the booking was created.
     */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * Timestamp when the booking was last updated.
     */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}