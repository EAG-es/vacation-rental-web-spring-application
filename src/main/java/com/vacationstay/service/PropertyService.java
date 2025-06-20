package com.vacationstay.service;

import com.vacationstay.dto.PropertyDTO;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for managing property operations.
 * <p>
 * This service provides methods for creating, retrieving, updating, and deleting
 * property listings, as well as searching for properties based on various criteria.
 * </p>
 *
 * @author VacationStay Team
 * @version 1.0
 * @since 2023-06-18
 */
public interface PropertyService {
    
    /**
     * Retrieves all properties in the system.
     *
     * @return a list of all property DTOs
     */
    List<PropertyDTO> getAllProperties();
    
    /**
     * Retrieves a specific property by its ID.
     *
     * @param id the unique identifier of the property
     * @return the property DTO
     * @throws jakarta.persistence.EntityNotFoundException if the property is not found
     */
    PropertyDTO getPropertyById(Long id);
    
    /**
     * Creates a new property listing.
     *
     * @param propertyDTO the property data to create
     * @return the created property DTO with assigned ID
     */
    PropertyDTO createProperty(PropertyDTO propertyDTO);
    
    /**
     * Updates an existing property.
     *
     * @param id the unique identifier of the property to update
     * @param propertyDTO the updated property data
     * @return the updated property DTO
     * @throws jakarta.persistence.EntityNotFoundException if the property is not found
     */
    PropertyDTO updateProperty(Long id, PropertyDTO propertyDTO);
    
    /**
     * Deletes a property by its ID.
     *
     * @param id the unique identifier of the property to delete
     * @throws jakarta.persistence.EntityNotFoundException if the property is not found
     */
    void deleteProperty(Long id);
    
    /**
     * Searches for properties based on various filter criteria.
     *
     * @param location the location to search for (optional)
     * @param minPrice the minimum price per night (optional)
     * @param maxPrice the maximum price per night (optional)
     * @param bedrooms the minimum number of bedrooms (optional)
     * @param bathrooms the minimum number of bathrooms (optional)
     * @param guests the minimum number of guests that can be accommodated (optional)
     * @return a list of property DTOs matching the criteria
     */
    List<PropertyDTO> searchProperties(String location, BigDecimal minPrice, BigDecimal maxPrice, 
                                      Integer bedrooms, Integer bathrooms, Integer guests);
    
    /**
     * Retrieves all properties owned by a specific user.
     *
     * @param ownerId the ID of the owner
     * @return a list of property DTOs owned by the specified user
     */
    List<PropertyDTO> getPropertiesByOwner(String ownerId);
}