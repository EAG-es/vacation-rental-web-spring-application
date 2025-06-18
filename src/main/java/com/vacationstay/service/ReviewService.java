package com.vacationstay.service;

import com.vacationstay.dto.ReviewDTO;

import java.util.List;

public interface ReviewService {
    List<ReviewDTO> getAllReviews();
    ReviewDTO getReviewById(Long id);
    List<ReviewDTO> getReviewsByProperty(Long propertyId);
    List<ReviewDTO> getReviewsByUser(Long userId);
    ReviewDTO createReview(ReviewDTO reviewDTO);
    ReviewDTO updateReview(Long id, ReviewDTO reviewDTO);
    void deleteReview(Long id);
    Double getAverageRatingForProperty(Long propertyId);
}