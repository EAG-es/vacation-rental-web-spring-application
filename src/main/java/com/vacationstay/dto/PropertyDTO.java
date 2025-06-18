package com.vacationstay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PropertyDTO {
    private Long id;
    private String title;
    private String description;
    private String location;
    private BigDecimal price;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer maxGuests;
    private List<String> amenities;
    private List<String> images;
    private String ownerId;
    private Double averageRating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}