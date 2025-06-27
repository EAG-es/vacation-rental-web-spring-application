package com.vacationstay.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Property entity.
 */
@DisplayName("Property Entity Tests")
class PropertyTest {

    private Property property;

    @BeforeEach
    void setUp() {
        property = new Property();
        property.setId(1L);
        property.setTitle("Beautiful Beach House");
        property.setDescription("A stunning beachfront property with amazing views");
        property.setLocation("Miami, FL");
        property.setPrice(new BigDecimal("200.00"));
        property.setMaxGuests(6);
        property.setBedrooms(3);
        property.setBathrooms(2);
        property.setAmenities("{\"wifi\": true, \"parking\": true, \"pool\": false}");
        property.setImages("{\"urls\": [\"https://example.com/property.jpg\"]}");
        property.setOwnerId("owner123");
        property.setCreatedAt(LocalDateTime.now());
        property.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create property with all properties")
    void shouldCreatePropertyWithAllProperties() {
        assertNotNull(property);
        assertEquals(1L, property.getId());
        assertEquals("Beautiful Beach House", property.getTitle());
        assertEquals("A stunning beachfront property with amazing views", property.getDescription());
        assertEquals("Miami, FL", property.getLocation());
        assertEquals(new BigDecimal("200.00"), property.getPrice());
        assertEquals(6, property.getMaxGuests());
        assertEquals(3, property.getBedrooms());
        assertEquals(2, property.getBathrooms());
        assertEquals("{\"urls\": [\"https://example.com/property.jpg\"]}", property.getImages());
        assertEquals("owner123", property.getOwnerId());
        assertNotNull(property.getCreatedAt());
        assertNotNull(property.getUpdatedAt());
    }

    @Test
    @DisplayName("Should initialize collections properly")
    void shouldInitializeCollectionsProperly() {
        Property newProperty = new Property();
        assertNotNull(newProperty.getBookings());
        assertNotNull(newProperty.getReviews());
        assertTrue(newProperty.getBookings().isEmpty());
        assertTrue(newProperty.getReviews().isEmpty());
    }

    @Test
    @DisplayName("Should handle price calculations")
    void shouldHandlePriceCalculations() {
        property.setPrice(new BigDecimal("150.50"));
        assertEquals(new BigDecimal("150.50"), property.getPrice());
        
        // Test precision
        property.setPrice(new BigDecimal("99.99"));
        assertEquals(new BigDecimal("99.99"), property.getPrice());
    }

    @Test
    @DisplayName("Should validate guest capacity")
    void shouldValidateGuestCapacity() {
        property.setMaxGuests(8);
        assertEquals(8, property.getMaxGuests());
        
        property.setMaxGuests(1);
        assertEquals(1, property.getMaxGuests());
    }

    @Test
    @DisplayName("Should handle bedroom and bathroom counts")
    void shouldHandleBedroomAndBathroomCounts() {
        property.setBedrooms(4);
        property.setBathrooms(3);
        
        assertEquals(4, property.getBedrooms());
        assertEquals(3, property.getBathrooms());
        
        // Test studio apartment (0 bedrooms)
        property.setBedrooms(0);
        assertEquals(0, property.getBedrooms());
    }

    @Test
    @DisplayName("Should support equality based on id")
    void shouldSupportEqualityBasedOnId() {
        Property property1 = new Property();
        Property property2 = new Property();
        
        // Without ID
        assertNotEquals(property1, property2);
        
        // With same ID
        property1.setId(1L);
        property2.setId(1L);
        assertEquals(property1, property2);
        
        // With different IDs
        property2.setId(2L);
        assertNotEquals(property1, property2);
    }

    @Test
    @DisplayName("Should create property with constructor")
    void shouldCreatePropertyWithConstructor() {
        Property constructedProperty = new Property(
            1L,
            "Test Property",
            "Test Description",
            "Test Location",
            new BigDecimal("100.00"),
            2,
            1,
            4,
            "{\"wifi\": true, \"parking\": true}",
            "{\"urls\": [\"test.jpg\"]}",
            "testOwner123",
            new ArrayList<>(),
            new ArrayList<>(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        assertEquals(1L, constructedProperty.getId());
        assertEquals("Test Property", constructedProperty.getTitle());
        assertEquals("Test Description", constructedProperty.getDescription());
        assertEquals("Test Location", constructedProperty.getLocation());
        assertEquals(new BigDecimal("100.00"), constructedProperty.getPrice());
        assertEquals(4, constructedProperty.getMaxGuests());
        assertEquals(2, constructedProperty.getBedrooms());
        assertEquals(1, constructedProperty.getBathrooms());
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void shouldHandleNullValuesGracefully() {
        Property nullProperty = new Property();
        
        assertNull(nullProperty.getId());
        assertNull(nullProperty.getTitle());
        assertNull(nullProperty.getDescription());
        assertNull(nullProperty.getLocation());
        assertNull(nullProperty.getPrice());
        assertNull(nullProperty.getImages());
        assertNull(nullProperty.getOwnerId());
        assertEquals(0, nullProperty.getMaxGuests());
        assertEquals(0, nullProperty.getBedrooms());
        assertEquals(0, nullProperty.getBathrooms());
    }

    @Test
    @DisplayName("Should handle string fields validation")
    void shouldHandleStringFieldsValidation() {
        // Test empty strings
        property.setTitle("");
        property.setDescription("");
        property.setLocation("");
        
        assertEquals("", property.getTitle());
        assertEquals("", property.getDescription());
        assertEquals("", property.getLocation());
        
        // Test long strings
        String longString = "x".repeat(1000);
        property.setTitle(longString);
        assertEquals(longString, property.getTitle());
    }
}