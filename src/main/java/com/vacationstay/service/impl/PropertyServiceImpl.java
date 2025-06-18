package com.vacationstay.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vacationstay.dto.PropertyDTO;
import com.vacationstay.model.Property;
import com.vacationstay.repository.PropertyRepository;
import com.vacationstay.repository.ReviewRepository;
import com.vacationstay.service.PropertyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final PropertyRepository propertyRepository;
    private final ReviewRepository reviewRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(readOnly = true)
    public List<PropertyDTO> getAllProperties() {
        return propertyRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyDTO getPropertyById(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Property not found with id: " + id));
        return convertToDTO(property);
    }

    @Override
    @Transactional
    public PropertyDTO createProperty(PropertyDTO propertyDTO) {
        Property property = convertToEntity(propertyDTO);
        Property savedProperty = propertyRepository.save(property);
        return convertToDTO(savedProperty);
    }

    @Override
    @Transactional
    public PropertyDTO updateProperty(Long id, PropertyDTO propertyDTO) {
        Property existingProperty = propertyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Property not found with id: " + id));
        
        existingProperty.setTitle(propertyDTO.getTitle());
        existingProperty.setDescription(propertyDTO.getDescription());
        existingProperty.setLocation(propertyDTO.getLocation());
        existingProperty.setPrice(propertyDTO.getPrice());
        existingProperty.setBedrooms(propertyDTO.getBedrooms());
        existingProperty.setBathrooms(propertyDTO.getBathrooms());
        existingProperty.setMaxGuests(propertyDTO.getMaxGuests());
        
        try {
            existingProperty.setAmenities(objectMapper.writeValueAsString(propertyDTO.getAmenities()));
            existingProperty.setImages(objectMapper.writeValueAsString(propertyDTO.getImages()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON", e);
        }
        
        Property updatedProperty = propertyRepository.save(existingProperty);
        return convertToDTO(updatedProperty);
    }

    @Override
    @Transactional
    public void deleteProperty(Long id) {
        if (!propertyRepository.existsById(id)) {
            throw new EntityNotFoundException("Property not found with id: " + id);
        }
        propertyRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyDTO> searchProperties(String location, BigDecimal minPrice, BigDecimal maxPrice, 
                                             Integer bedrooms, Integer bathrooms, Integer guests) {
        return propertyRepository.findByFilters(location, minPrice, maxPrice, bedrooms, bathrooms, guests)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyDTO> getPropertiesByOwner(String ownerId) {
        return propertyRepository.findByOwnerId(ownerId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PropertyDTO convertToDTO(Property property) {
        PropertyDTO dto = new PropertyDTO();
        dto.setId(property.getId());
        dto.setTitle(property.getTitle());
        dto.setDescription(property.getDescription());
        dto.setLocation(property.getLocation());
        dto.setPrice(property.getPrice());
        dto.setBedrooms(property.getBedrooms());
        dto.setBathrooms(property.getBathrooms());
        dto.setMaxGuests(property.getMaxGuests());
        dto.setOwnerId(property.getOwnerId());
        dto.setCreatedAt(property.getCreatedAt());
        dto.setUpdatedAt(property.getUpdatedAt());
        
        // Get average rating
        Double avgRating = reviewRepository.getAverageRatingForProperty(property.getId());
        dto.setAverageRating(avgRating != null ? avgRating : 0.0);
        
        try {
            // Parse JSON strings to lists
            dto.setAmenities(objectMapper.readValue(property.getAmenities(), new TypeReference<List<String>>() {}));
            dto.setImages(objectMapper.readValue(property.getImages(), new TypeReference<List<String>>() {}));
        } catch (JsonProcessingException e) {
            dto.setAmenities(new ArrayList<>());
            dto.setImages(new ArrayList<>());
        }
        
        return dto;
    }

    private Property convertToEntity(PropertyDTO dto) {
        Property property = new Property();
        property.setTitle(dto.getTitle());
        property.setDescription(dto.getDescription());
        property.setLocation(dto.getLocation());
        property.setPrice(dto.getPrice());
        property.setBedrooms(dto.getBedrooms());
        property.setBathrooms(dto.getBathrooms());
        property.setMaxGuests(dto.getMaxGuests());
        property.setOwnerId(dto.getOwnerId());
        
        try {
            property.setAmenities(objectMapper.writeValueAsString(dto.getAmenities()));
            property.setImages(objectMapper.writeValueAsString(dto.getImages()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error processing JSON", e);
        }
        
        return property;
    }
}