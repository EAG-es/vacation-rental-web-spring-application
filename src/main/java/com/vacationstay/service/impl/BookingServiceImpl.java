package com.vacationstay.service.impl;

import com.vacationstay.dto.BookingDTO;
import com.vacationstay.dto.PropertyDTO;
import com.vacationstay.dto.UserDTO;
import com.vacationstay.model.Booking;
import com.vacationstay.model.Property;
import com.vacationstay.model.User;
import com.vacationstay.repository.BookingRepository;
import com.vacationstay.repository.PropertyRepository;
import com.vacationstay.repository.UserRepository;
import com.vacationstay.service.BookingService;
import com.vacationstay.service.PropertyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final PropertyService propertyService;

    @Override
    @Transactional(readOnly = true)
    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDTO getBookingById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + id));
        return convertToDTO(booking);
    }

    @Override
    @Transactional
    public BookingDTO createBooking(BookingDTO bookingDTO) {
        // Check if property is available for the requested dates
        if (!isPropertyAvailable(bookingDTO.getPropertyId(), bookingDTO.getStartDate(), bookingDTO.getEndDate())) {
            throw new IllegalStateException("Property is not available for the selected dates");
        }
        
        Booking booking = convertToEntity(bookingDTO);
        Booking savedBooking = bookingRepository.save(booking);
        return convertToDTO(savedBooking);
    }

    @Override
    @Transactional
    public BookingDTO updateBooking(Long id, BookingDTO bookingDTO) {
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + id));
        
        // Check if dates are being updated and if property is available
        if (!existingBooking.getStartDate().equals(bookingDTO.getStartDate()) || 
            !existingBooking.getEndDate().equals(bookingDTO.getEndDate())) {
            
            // Exclude current booking from availability check
            List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(
                    bookingDTO.getPropertyId(), 
                    bookingDTO.getStartDate(), 
                    bookingDTO.getEndDate());
            
            boolean isAvailable = overlappingBookings.stream()
                    .allMatch(booking -> booking.getId().equals(id));
            
            if (!isAvailable) {
                throw new IllegalStateException("Property is not available for the selected dates");
            }
        }
        
        existingBooking.setStartDate(bookingDTO.getStartDate());
        existingBooking.setEndDate(bookingDTO.getEndDate());
        existingBooking.setTotalPrice(bookingDTO.getTotalPrice());
        existingBooking.setStatus(bookingDTO.getStatus());
        
        Booking updatedBooking = bookingRepository.save(existingBooking);
        return convertToDTO(updatedBooking);
    }

    @Override
    @Transactional
    public void deleteBooking(Long id) {
        if (!bookingRepository.existsById(id)) {
            throw new EntityNotFoundException("Booking not found with id: " + id);
        }
        bookingRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDTO> getBookingsByUser(Long userId) {
        return bookingRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDTO> getBookingsByProperty(Long propertyId) {
        return bookingRepository.findByPropertyId(propertyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPropertyAvailable(Long propertyId, LocalDate startDate, LocalDate endDate) {
        List<Booking> overlappingBookings = bookingRepository.findOverlappingBookings(propertyId, startDate, endDate);
        return overlappingBookings.isEmpty();
    }

    @Override
    @Transactional
    public BookingDTO cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + id));
        
        booking.setStatus("cancelled");
        Booking updatedBooking = bookingRepository.save(booking);
        return convertToDTO(updatedBooking);
    }

    private BookingDTO convertToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setPropertyId(booking.getProperty().getId());
        dto.setUserId(booking.getUser().getId());
        dto.setStartDate(booking.getStartDate());
        dto.setEndDate(booking.getEndDate());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setStatus(booking.getStatus());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setUpdatedAt(booking.getUpdatedAt());
        
        // Include property details
        PropertyDTO propertyDTO = propertyService.getPropertyById(booking.getProperty().getId());
        dto.setProperty(propertyDTO);
        
        // Include basic user info
        UserDTO userDTO = new UserDTO();
        userDTO.setId(booking.getUser().getId());
        userDTO.setName(booking.getUser().getName());
        userDTO.setEmail(booking.getUser().getEmail());
        dto.setUser(userDTO);
        
        return dto;
    }

    private Booking convertToEntity(BookingDTO dto) {
        Booking booking = new Booking();
        
        Property property = propertyRepository.findById(dto.getPropertyId())
                .orElseThrow(() -> new EntityNotFoundException("Property not found with id: " + dto.getPropertyId()));
        
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + dto.getUserId()));
        
        booking.setProperty(property);
        booking.setUser(user);
        booking.setStartDate(dto.getStartDate());
        booking.setEndDate(dto.getEndDate());
        booking.setTotalPrice(dto.getTotalPrice());
        booking.setStatus(dto.getStatus());
        
        return booking;
    }
}