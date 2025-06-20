package com.vacationstay.repository;

import com.vacationstay.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Review entities.
 * <p>
 * This interface provides methods for database operations related to reviews.
 * </p>
 *
 * @author VacationStay Team
 * @version 1.0
 * @since 2023-06-18
 */
@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Finds all reviews for a specific property.
     *
     * @param propertyId the ID of the property
     * @return a list of reviews for the specified property
     */
    List<Review> findByPropertyId(Long propertyId);
    
    /**
     * Finds all reviews written by a specific user.
     *
     * @param userId the ID of the user
     * @return a list of reviews written by the specified user
     */
    List<Review> findByUserId(Long userId);
    
    /**
     * Calculates the average rating for a specific property.
     *
     * @param propertyId the ID of the property
     * @return the average rating as a Double, or null if there are no reviews
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.property.id = :propertyId")
    Double getAverageRatingForProperty(@Param("propertyId") Long propertyId);
}