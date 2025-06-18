package com.vacationstay.service.impl;

import com.vacationstay.dto.ReviewDTO;
import com.vacationstay.model.Property;
import com.vacationstay.model.Review;
import com.vacationstay.model.User;
import com.vacationstay.repository.PropertyRepository;
import com.vacationstay.repository.ReviewRepository;
import com.vacationstay.repository.UserRepository;
import com.vacationstay.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewDTO getReviewById(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + id));
        return convertToDTO(review);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsByProperty(Long propertyId) {
        return reviewRepository.findByPropertyId(propertyId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsByUser(Long userId) {
        return reviewRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReviewDTO createReview(ReviewDTO reviewDTO) {
        Review review = new Review();
        
        Property property = propertyRepository.findById(reviewDTO.getPropertyId())
                .orElseThrow(() -> new EntityNotFoundException("Property not found with id: " + reviewDTO.getPropertyId()));
        
        User user = userRepository.findById(reviewDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + reviewDTO.getUserId()));
        
        review.setProperty(property);
        review.setUser(user);
        review.setRating(reviewDTO.getRating());
        review.setComment(reviewDTO.getComment());
        
        Review savedReview = reviewRepository.save(review);
        return convertToDTO(savedReview);
    }

    @Override
    @Transactional
    public ReviewDTO updateReview(Long id, ReviewDTO reviewDTO) {
        Review existingReview = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with id: " + id));
        
        existingReview.setRating(reviewDTO.getRating());
        existingReview.setComment(reviewDTO.getComment());
        
        Review updatedReview = reviewRepository.save(existingReview);
        return convertToDTO(updatedReview);
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new EntityNotFoundException("Review not found with id: " + id);
        }
        reviewRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRatingForProperty(Long propertyId) {
        return reviewRepository.getAverageRatingForProperty(propertyId);
    }

    private ReviewDTO convertToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setPropertyId(review.getProperty().getId());
        dto.setUserId(review.getUser().getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setUserName(review.getUser().getName());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }
}