package com.vacationstay.repository;

import com.vacationstay.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByPropertyId(Long propertyId);
    
    List<Review> findByUserId(Long userId);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.property.id = :propertyId")
    Double getAverageRatingForProperty(@Param("propertyId") Long propertyId);
}