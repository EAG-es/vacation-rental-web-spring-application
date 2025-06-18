package com.vacationstay.controller.api;

import com.vacationstay.dto.PropertyDTO;
import com.vacationstay.service.PropertyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/properties")
@RequiredArgsConstructor
public class PropertyApiController {

    private final PropertyService propertyService;

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

    @GetMapping("/{id}")
    public ResponseEntity<PropertyDTO> getPropertyById(@PathVariable Long id) {
        return ResponseEntity.ok(propertyService.getPropertyById(id));
    }

    @PostMapping
    public ResponseEntity<PropertyDTO> createProperty(@RequestBody PropertyDTO propertyDTO) {
        PropertyDTO createdProperty = propertyService.createProperty(propertyDTO);
        return new ResponseEntity<>(createdProperty, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PropertyDTO> updateProperty(
            @PathVariable Long id, 
            @RequestBody PropertyDTO propertyDTO) {
        return ResponseEntity.ok(propertyService.updateProperty(id, propertyDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<PropertyDTO>> getPropertiesByOwner(@PathVariable String ownerId) {
        return ResponseEntity.ok(propertyService.getPropertiesByOwner(ownerId));
    }
}