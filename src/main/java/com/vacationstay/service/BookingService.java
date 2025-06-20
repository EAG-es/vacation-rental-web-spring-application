package com.vacationstay.service;

import com.vacationstay.dto.BookingDTO;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for managing booking operations.
 * <p>
 * This service provides methods for creating, retrieving, updating, and deleting
 * property bookings, as well as checking property availability.
 * </p>
 *
 * @author VacationStay Team
 * @version 1.0
 * @since 2023-06-18
 */
public interface BookingService {
    
    /**
     * Retrieves all bookings in the system.
     *
     * @return a list of all booking DTOs
     */
    List<BookingDTO> getAllBookings();
    
    /**
     * Retrieves a specific booking by its ID.
     *
     * @param id the unique identifier of the booking
     * @return the booking DTO
     * @throws jakarta.persistence.EntityNotFoundException if the booking is not found
     */
    BookingDTO getBookingById(Long id);
    
    /**
     * Creates a new booking.
     *
     * @param bookingDTO the booking data to create
     * @return the created booking DTO with assigned ID
     * @throws IllegalStateException if the property is not available for the requested dates
     */
    BookingDTO createBooking(BookingDTO bookingDTO);
    
    /**
     * Updates an existing booking.
     *
     * @param id the unique identifier of the booking to update
     * @param bookingDTO the updated booking data
     * @return the updated booking DTO
     * @throws jakarta.persistence.EntityNotFoundException if the booking is not found
     * @throws IllegalStateException if the property is not available for the updated dates
     */
    BookingDTO updateBooking(Long id, BookingDTO bookingDTO);
    
    /**
     * Deletes a booking by its ID.
     *
     * @param id the unique identifier of the booking to delete
     * @throws jakarta.persistence.EntityNotFoundException if the booking is not found
     */
    void deleteBooking(Long id);
    
    /**
     * Retrieves all bookings made by a specific user.
     *
     * @param userId the ID of the user
     * @return a list of booking DTOs made by the specified user
     */
    List<BookingDTO> getBookingsByUser(Long userId);
    
    /**
     * Retrieves all bookings for a specific property.
     *
     * @param propertyId the ID of the property
     * @return a list of booking DTOs for the specified property
     */
    List<BookingDTO> getBookingsByProperty(Long propertyId);
    
    /**
     * Checks if a property is available for booking during the specified date range.
     *
     * @param propertyId the ID of the property to check
     * @param startDate the requested check-in date
     * @param endDate the requested check-out date
     * @return true if the property is available, false otherwise
     */
    boolean isPropertyAvailable(Long propertyId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Cancels a booking by changing its status to "cancelled".
     *
     * @param id the unique identifier of the booking to cancel
     * @return the updated booking DTO
     * @throws jakarta.persistence.EntityNotFoundException if the booking is not found
     */
    BookingDTO cancelBooking(Long id);
}