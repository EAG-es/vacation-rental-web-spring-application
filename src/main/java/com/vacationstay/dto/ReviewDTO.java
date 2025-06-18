package com.vacationstay.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Long id;
    private Long propertyId;
    private Long userId;
    private Integer rating;
    private String comment;
    private String userName;
    private LocalDateTime createdAt;
}