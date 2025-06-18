package com.vacationstay.service;

import com.vacationstay.dto.BookingDTO;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    List<BookingDTO> getAllBookings();
    BookingDTO getBookingById(Long id);
    BookingDTO createBooking(BookingDTO bookingDTO);
    BookingDTO updateBooking(Long id, BookingDTO bookingDTO);
    void deleteBooking(Long id);
    List<BookingDTO> getBookingsByUser(Long userId);
    List<BookingDTO> getBookingsByProperty(Long propertyId);
    boolean isPropertyAvailable(Long propertyId, LocalDate startDate, LocalDate endDate);
    BookingDTO cancelBooking(Long id);
}