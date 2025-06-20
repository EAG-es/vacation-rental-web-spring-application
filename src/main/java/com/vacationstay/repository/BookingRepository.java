package com.vacationstay.repository;

import com.vacationstay.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Booking entities.
 * <p>
 * This interface provides methods for database operations related to bookings.
 * </p>
 *
 * @author VacationStay Team
 * @version 1.0
 * @since 2023-06-18
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Finds all bookings made by a specific user.
     *
     * @param userId the ID of the user
     * @return a list of bookings made by the specified user
     */
    List<Booking> findByUserId(Long userId);
    
    /**
     * Finds all bookings for a specific property.
     *
     * @param propertyId the ID of the property
     * @return a list of bookings for the specified property
     */
    List<Booking> findByPropertyId(Long propertyId);
    
    /**
     * Finds bookings that overlap with the specified date range for a property.
     * <p>
     * This query checks for any existing bookings that would conflict with a new booking
     * for the specified date range.
     * </p>
     *
     * @param propertyId the ID of the property
     * @param startDate the requested check-in date
     * @param endDate the requested check-out date
     * @return a list of overlapping bookings
     */
    @Query("SELECT b FROM Booking b WHERE b.property.id = :propertyId AND " +
           "((b.startDate <= :endDate AND b.endDate >= :startDate) OR " +
           "(b.startDate >= :startDate AND b.startDate <= :endDate))")
    List<Booking> findOverlappingBookings(
            @Param("propertyId") Long propertyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    /**
     * Finds bookings for a specific property with a specific status.
     *
     * @param propertyId the ID of the property
     * @param status the booking status to filter by
     * @return a list of bookings matching the criteria
     */
    List<Booking> findByPropertyIdAndStatus(Long propertyId, String status);
    
    /**
     * Finds bookings made by a specific user with a specific status.
     *
     * @param userId the ID of the user
     * @param status the booking status to filter by
     * @return a list of bookings matching the criteria
     */
    List<Booking> findByUserIdAndStatus(Long userId, String status);
}