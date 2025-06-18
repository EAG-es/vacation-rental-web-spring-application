package com.vacationstay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingDTO {
    private Long id;
    private Long propertyId;
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalPrice;
    private String status;
    private PropertyDTO property;
    private UserDTO user;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}