package com.vacationstay.service;

import com.vacationstay.dto.PropertyDTO;

import java.math.BigDecimal;
import java.util.List;

public interface PropertyService {
    List<PropertyDTO> getAllProperties();
    PropertyDTO getPropertyById(Long id);
    PropertyDTO createProperty(PropertyDTO propertyDTO);
    PropertyDTO updateProperty(Long id, PropertyDTO propertyDTO);
    void deleteProperty(Long id);
    List<PropertyDTO> searchProperties(String location, BigDecimal minPrice, BigDecimal maxPrice, 
                                      Integer bedrooms, Integer bathrooms, Integer guests);
    List<PropertyDTO> getPropertiesByOwner(String ownerId);
}