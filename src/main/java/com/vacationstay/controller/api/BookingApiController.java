package com.vacationstay.controller.api;

import com.vacationstay.dto.BookingDTO;
import com.vacationstay.exception.ErrorResponse;
import com.vacationstay.exception.ResourceNotFoundException;
import com.vacationstay.exception.ValidationException;
import com.vacationstay.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for booking-related operations.
 * <p>
 * This controller provides API endpoints for managing bookings,
 * including CRUD operations and availability checking.
 * </p>
 *
 * @author VacationStay Team
 * @version 1.0
 * @since 2023-06-18
 */
@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingApiController {

    private final BookingService bookingService;

    /**
     * Retrieves all bookings (JWT authentication required).
     * 
     * @param authentication the JWT authentication context
     * @return ResponseEntity containing a list of all bookings
     */
    @GetMapping
    public ResponseEntity<List<BookingDTO>> getAllBookings(Authentication authentication) {
        validateJwtAuthentication(authentication);
        log.info("Fetching all bookings for authenticated user: {}", authentication.getName());
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    /**
     * Retrieves a specific booking by its ID (JWT authentication required).
     *
     * @param id the ID of the booking to retrieve
     * @param authentication the JWT authentication context
     * @return ResponseEntity containing the booking details
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long id, Authentication authentication) {
        validateJwtAuthentication(authentication);
        if (id == null || id <= 0) {
            throw new ValidationException("Booking ID must be a positive number");
        }
        
        log.info("Fetching booking {} for authenticated user: {}", id, authentication.getName());
        
        BookingDTO booking = bookingService.getBookingById(id);
        if (booking == null) {
            throw new ResourceNotFoundException("Booking", "id", id);
        }
        
        return ResponseEntity.ok(booking);
    }

    /**
     * Creates a new booking (JWT authentication required).
     *
     * @param bookingDTO the booking data to create
     * @param authentication the JWT authentication context
     * @return ResponseEntity containing the created booking
     */
    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(@RequestBody BookingDTO bookingDTO, Authentication authentication) {
        validateJwtAuthentication(authentication);
        log.info("Creating new booking for authenticated user: {}", authentication.getName());
        BookingDTO createdBooking = bookingService.createBooking(bookingDTO);
        return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
    }

    /**
     * Updates an existing booking (JWT authentication required).
     *
     * @param id the ID of the booking to update
     * @param bookingDTO the updated booking data
     * @param authentication the JWT authentication context
     * @return ResponseEntity containing the updated booking
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookingDTO> updateBooking(@PathVariable Long id,
                                                   @RequestBody BookingDTO bookingDTO,
                                                   Authentication authentication) {
        validateJwtAuthentication(authentication);
        log.info("Updating booking {} for authenticated user: {}", id, authentication.getName());
        var booking_DTO = bookingService.updateBooking(id, bookingDTO);
        return ResponseEntity.ok(booking_DTO);
    }

    /**
     * Deletes a booking (JWT authentication required).
     *
     * @param id the ID of the booking to delete
     * @param authentication the JWT authentication context
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id, Authentication authentication) {
        validateJwtAuthentication(authentication);
        log.info("Deleting booking {} for authenticated user: {}", id, authentication.getName());
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all bookings made by a specific user (JWT authentication required).
     *
     * @param userId the ID of the user
     * @param authentication the JWT authentication context
     * @return ResponseEntity containing a list of bookings
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingDTO>> getBookingsByUser(@PathVariable Long userId, Authentication authentication) {
        validateJwtAuthentication(authentication);
        log.info("Fetching bookings for user {} by authenticated user: {}", userId, authentication.getName());
        return ResponseEntity.ok(bookingService.getBookingsByUser(userId));
    }

    /**
     * Retrieves all bookings for a specific property (JWT authentication required).
     *
     * @param propertyId the ID of the property
     * @param authentication the JWT authentication context
     * @return ResponseEntity containing a list of bookings
     */
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<BookingDTO>> getBookingsByProperty(@PathVariable Long propertyId, Authentication authentication) {
        validateJwtAuthentication(authentication);
        log.info("Fetching bookings for property {} by authenticated user: {}", propertyId, authentication.getName());
        return ResponseEntity.ok(bookingService.getBookingsByProperty(propertyId));
    }

    /**
     * Checks if a property is available for booking during the specified date range (JWT authentication required).
     *
     * @param propertyId the ID of the property to check
     * @param startDate the requested check-in date
     * @param endDate the requested check-out date
     * @param authentication the JWT authentication context
     * @return ResponseEntity containing a boolean indicating availability
     */
    @GetMapping("/property/{propertyId}/availability")
    public ResponseEntity<Boolean> checkPropertyAvailability(
            @PathVariable Long propertyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {
        validateJwtAuthentication(authentication);
        log.info("Checking availability for property {} from {} to {} by authenticated user: {}", 
                propertyId, startDate, endDate, authentication.getName());
        return ResponseEntity.ok(bookingService.isPropertyAvailable(propertyId, startDate, endDate));
    }

    /**
     * Cancels a booking by changing its status to "cancelled" (JWT authentication required).
     *
     * @param id the ID of the booking to cancel
     * @param authentication the JWT authentication context
     * @return ResponseEntity containing the updated booking
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingDTO> cancelBooking(@PathVariable Long id, Authentication authentication) {
        validateJwtAuthentication(authentication);
        log.info("Cancelling booking {} for authenticated user: {}", id, authentication.getName());
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }

    /**
     * Validates that the request has proper JWT authentication.
     * Throws an exception if authentication is missing or invalid.
     * 
     * @param authentication the authentication context to validate
     * @throws ValidationException if authentication is missing or invalid
     */
    private void validateJwtAuthentication(Authentication authentication) {
        if (authentication == null) {
            log.warn("API request to /api/bookings/** without authentication context");
            throw new ValidationException("JWT authentication required for booking operations");
        }
        
        if (!authentication.isAuthenticated()) {
            log.warn("API request to /api/bookings/** with invalid authentication: {}", authentication.getName());
            throw new ValidationException("Invalid JWT authentication for booking operations");
        }
        
        // Additional check to ensure this is actually JWT authentication, not session-based
        String principal = authentication.getName();
        if (principal == null || principal.trim().isEmpty()) {
            log.warn("API request to /api/bookings/** with empty authentication principal");
            throw new ValidationException("Invalid JWT authentication principal");
        }
        
        log.debug("JWT authentication validated for user: {}", principal);
    }
}