package com.vacationstay.controller.api;

import com.vacationstay.dto.PropertyDTO;
import com.vacationstay.exception.ErrorResponse;
import com.vacationstay.exception.ResourceNotFoundException;
import com.vacationstay.exception.ValidationException;
import com.vacationstay.service.PropertyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@Slf4j
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
        if (id == null || id <= 0) {
            throw new ValidationException("Property ID must be a positive number");
        }
        
        PropertyDTO property = propertyService.getPropertyById(id);
        if (property == null) {
            throw new ResourceNotFoundException("Property", "id", id);
        }
        
        return ResponseEntity.ok(property);
    }

    /**
     * Creates a new property.
     *
     * @param propertyDTO the property data to create
     * @return ResponseEntity containing the created property
     */
    @PostMapping
    public ResponseEntity<PropertyDTO> createProperty(@RequestBody PropertyDTO propertyDTO) {
        validatePropertyDTO(propertyDTO);
        
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
        if (ownerId == null || ownerId.trim().isEmpty()) {
            throw new ValidationException("Owner ID cannot be null or empty");
        }
        
        return ResponseEntity.ok(propertyService.getPropertiesByOwner(ownerId));
    }

    /**
     * Individual exception handler for property-specific validation errors.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handlePropertyValidationException(
            ValidationException ex, HttpServletRequest request) {
        log.error("Property validation error: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Property Validation Failed")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .details(ex.getValidationErrors())
                .build();
                
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Individual exception handler for property not found errors.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePropertyNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        log.error("Property not found: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error("Property Not Found")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
                
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * Individual exception handler for illegal arguments in property operations.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, HttpServletRequest request) {
        log.error("Illegal argument in property operation: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Invalid Property Data")
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();
                
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Validates property data before creation or update.
     */
    private void validatePropertyDTO(PropertyDTO propertyDTO) {
        Map<String, String> errors = new HashMap<>();
        
        if (propertyDTO == null) {
            throw new ValidationException("Property data cannot be null");
        }
        
        if (propertyDTO.getTitle() == null || propertyDTO.getTitle().trim().isEmpty()) {
            errors.put("title", "Property title is required");
        }
        
        if (propertyDTO.getDescription() == null || propertyDTO.getDescription().trim().isEmpty()) {
            errors.put("description", "Property description is required");
        }
        
        if (propertyDTO.getLocation() == null || propertyDTO.getLocation().trim().isEmpty()) {
            errors.put("location", "Property location is required");
        }
        
        if (propertyDTO.getPrice() == null || propertyDTO.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            errors.put("price", "Property price must be greater than 0");
        }
        
        if (propertyDTO.getMaxGuests() == null || propertyDTO.getMaxGuests() <= 0) {
            errors.put("maxGuests", "Maximum guests must be greater than 0");
        }
        
        if (propertyDTO.getBedrooms() == null || propertyDTO.getBedrooms() < 0) {
            errors.put("bedrooms", "Number of bedrooms cannot be negative");
        }
        
        if (propertyDTO.getBathrooms() == null || propertyDTO.getBathrooms() < 0) {
            errors.put("bathrooms", "Number of bathrooms cannot be negative");
        }
        
        if (!errors.isEmpty()) {
            throw new ValidationException("Property validation failed", errors);
        }
    }
}