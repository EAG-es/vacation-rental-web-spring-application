package com.vacationstay.repository;

import com.vacationstay.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);
    
    List<Booking> findByPropertyId(Long propertyId);
    
    @Query("SELECT b FROM Booking b WHERE b.property.id = :propertyId AND " +
           "((b.startDate <= :endDate AND b.endDate >= :startDate) OR " +
           "(b.startDate >= :startDate AND b.startDate <= :endDate))")
    List<Booking> findOverlappingBookings(
            @Param("propertyId") Long propertyId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    List<Booking> findByPropertyIdAndStatus(Long propertyId, String status);
    
    List<Booking> findByUserIdAndStatus(Long userId, String status);
}