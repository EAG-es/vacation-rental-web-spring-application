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
     * Retrieves all bookings.
     *
     * @return ResponseEntity containing a list of all bookings
     */
    @GetMapping
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    /**
     * Retrieves a specific booking by its ID.
     *
     * @param id the ID of the booking to retrieve
     * @return ResponseEntity containing the booking details
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Booking ID must be a positive number");
        }
        
        BookingDTO booking = bookingService.getBookingById(id);
        if (booking == null) {
            throw new ResourceNotFoundException("Booking", "id", id);
        }
        
        return ResponseEntity.ok(booking);
    }

    /**
     * Creates a new booking.
     *
     * @param bookingDTO the booking data to create
     * @return ResponseEntity containing the created booking
     */
    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(@RequestBody BookingDTO bookingDTO) {
        BookingDTO createdBooking = bookingService.createBooking(bookingDTO);
        return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
    }

    /**
     * Updates an existing booking.
     *
     * @param id the ID of the booking to update
     * @param bookingDTO the updated booking data
     * @return ResponseEntity containing the updated booking
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookingDTO> updateBooking(
            @PathVariable Long id, 
            @RequestBody BookingDTO bookingDTO) {
        return ResponseEntity.ok(bookingService.updateBooking(id, bookingDTO));
    }

    /**
     * Deletes a booking.
     *
     * @param id the ID of the booking to delete
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all bookings made by a specific user.
     *
     * @param userId the ID of the user
     * @return ResponseEntity containing a list of bookings
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookingDTO>> getBookingsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getBookingsByUser(userId));
    }

    /**
     * Retrieves all bookings for a specific property.
     *
     * @param propertyId the ID of the property
     * @return ResponseEntity containing a list of bookings
     */
    @GetMapping("/property/{propertyId}")
    public ResponseEntity<List<BookingDTO>> getBookingsByProperty(@PathVariable Long propertyId) {
        return ResponseEntity.ok(bookingService.getBookingsByProperty(propertyId));
    }

    /**
     * Checks if a property is available for booking during the specified date range.
     *
     * @param propertyId the ID of the property to check
     * @param startDate the requested check-in date
     * @param endDate the requested check-out date
     * @return ResponseEntity containing a boolean indicating availability
     */
    @GetMapping("/property/{propertyId}/availability")
    public ResponseEntity<Boolean> checkPropertyAvailability(
            @PathVariable Long propertyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(bookingService.isPropertyAvailable(propertyId, startDate, endDate));
    }

    /**
     * Cancels a booking by changing its status to "cancelled".
     *
     * @param id the ID of the booking to cancel
     * @return ResponseEntity containing the updated booking
     */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<BookingDTO> cancelBooking(@PathVariable Long id) {
        return ResponseEntity.ok(bookingService.cancelBooking(id));
    }
}