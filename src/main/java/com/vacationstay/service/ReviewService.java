package com.vacationstay.service;

import com.vacationstay.dto.ReviewDTO;

import java.util.List;

/**
 * Service interface for managing review operations.
 * <p>
 * This service provides methods for creating, retrieving, updating, and deleting
 * property reviews, as well as calculating average ratings.
 * </p>
 *
 * @author VacationStay Team
 * @version 1.0
 * @since 2023-06-18
 */
public interface ReviewService {
    
    /**
     * Retrieves all reviews in the system.
     *
     * @return a list of all review DTOs
     */
    List<ReviewDTO> getAllReviews();
    
    /**
     * Retrieves a specific review by its ID.
     *
     * @param id the unique identifier of the review
     * @return the review DTO
     * @throws jakarta.persistence.EntityNotFoundException if the review is not found
     */
    ReviewDTO getReviewById(Long id);
    
    /**
     * Retrieves all reviews for a specific property.
     *
     * @param propertyId the ID of the property
     * @return a list of review DTOs for the specified property
     */
    List<ReviewDTO> getReviewsByProperty(Long propertyId);
    
    /**
     * Retrieves all reviews written by a specific user.
     *
     * @param userId the ID of the user
     * @return a list of review DTOs written by the specified user
     */
    List<ReviewDTO> getReviewsByUser(Long userId);
    
    /**
     * Creates a new review.
     *
     * @param reviewDTO the review data to create
     * @return the created review DTO with assigned ID
     */
    ReviewDTO createReview(ReviewDTO reviewDTO);
    
    /**
     * Updates an existing review.
     *
     * @param id the unique identifier of the review to update
     * @param reviewDTO the updated review data
     * @return the updated review DTO
     * @throws jakarta.persistence.EntityNotFoundException if the review is not found
     */
    ReviewDTO updateReview(Long id, ReviewDTO reviewDTO);
    
    /**
     * Deletes a review by its ID.
     *
     * @param id the unique identifier of the review to delete
     * @throws jakarta.persistence.EntityNotFoundException if the review is not found
     */
    void deleteReview(Long id);
    
    /**
     * Calculates the average rating for a specific property.
     *
     * @param propertyId the ID of the property
     * @return the average rating as a Double, or null if there are no reviews
     */
    Double getAverageRatingForProperty(Long propertyId);
}