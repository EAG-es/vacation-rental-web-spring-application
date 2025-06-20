package com.vacationstay.controller.api;

import com.vacationstay.dto.PropertyDTO;
import com.vacationstay.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST controller for property-related operations.
 * <p>
 * This controller provides API endpoints for managing properties,
 * including CRUD operations and search functionality.
 * </p>
 *
 * @author VacationStay Team
 * @version 1.0
 * @since 2023-06-18
 */
@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyApiController {

    private final PropertyService propertyService;

    /**
     * Retrieves all properties or searches for properties based on filter criteria.
     *
     * @param location optional location filter
     * @param minPrice optional minimum price filter
     * @param maxPrice optional maximum price filter
     * @param bedrooms optional minimum bedrooms filter
     * @param bathrooms optional minimum bathrooms filter
     * @param guests optional minimum guests filter
     * @return ResponseEntity containing a list of properties
     */
    @GetMapping
    public ResponseEntity<List<PropertyDTO>> getAllProperties(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer bedrooms,
            @RequestParam(required = false) Integer bathrooms,
            @RequestParam(required = false) Integer guests) {
        
        if (location != null || minPrice != null || maxPrice != null || 
            bedrooms != null || bathrooms != null || guests != null) {
            return ResponseEntity.ok(propertyService.searchProperties(
                    location, minPrice, maxPrice, bedrooms, bathrooms, guests));
        }
        
        return ResponseEntity.ok(propertyService.getAllProperties());
    }

    /**
     * Retrieves a specific property by its ID.
     *
     * @param id the ID of the property to retrieve
     * @return ResponseEntity containing the property details
     */
    @GetMapping("/{id}")
    public ResponseEntity<PropertyDTO> getPropertyById(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getPropertyById(id));
    }

    /**
     * Creates a new property.
     *
     * @param propertyDTO the property data to create
     * @return ResponseEntity containing the created property
     */
    @PostMapping
    public ResponseEntity<PropertyDTO> createProperty(@RequestBody PropertyDTO propertyDTO) {
        PropertyDTO createdProperty = propertyService.createProperty(propertyDTO);
        return new ResponseEntity<>(createdProperty, HttpStatus.CREATED);
    }

    /**
     * Updates an existing property.
     *
     * @param id the ID of the property to update
     * @param propertyDTO the updated property data
     * @return ResponseEntity containing the updated property
     */
    @PutMapping("/{id}")
    public ResponseEntity<PropertyDTO> updateProperty(
            @PathVariable Long id, 
            @RequestBody PropertyDTO propertyDTO) {
        return ResponseEntity.ok(propertyService.updateProperty(id, propertyDTO));
    }

    /**
     * Deletes a property.
     *
     * @param id the ID of the property to delete
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves all properties owned by a specific user.
     *
     * @param ownerId the ID of the owner
     * @return ResponseEntity containing a list of properties
     */
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<PropertyDTO>> getPropertiesByOwner(@PathVariable String ownerId) {
        return ResponseEntity.ok(propertyService.getPropertiesByOwner(ownerId));
    }
}