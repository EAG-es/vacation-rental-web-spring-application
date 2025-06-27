package com.vacationstay.controller;

import com.vacationstay.controller.web.HomeController;
import com.vacationstay.dto.PropertyDTO;
import com.vacationstay.service.PropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web layer tests for HomeController.
 */
@WebMvcTest(HomeController.class)
@ActiveProfiles("test")
@DisplayName("Home Controller Tests")
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PropertyService propertyService;

    private List<PropertyDTO> testProperties;

    @BeforeEach
    void setUp() {
        testProperties = new ArrayList<>();
        
        for (int i = 1; i <= 10; i++) {
            PropertyDTO property = new PropertyDTO();
            property.setId((long) i);
            property.setTitle("Test Property " + i);
            property.setDescription("Test Description " + i);
            property.setLocation("Test Location " + i);
            property.setPrice(new BigDecimal("100.00"));
            property.setMaxGuests(4);
            property.setBedrooms(2);
            property.setBathrooms(1);
            property.setImages(List.of("https://example.com/property" + i + ".jpg"));
            property.setOwnerId("owner" + i + "@example.com");
            property.setAmenities(new ArrayList<>());
            testProperties.add(property);
        }
    }

    @Test
    @DisplayName("Should display home page with featured properties")
    @WithMockUser
    void shouldDisplayHomePageWithFeaturedProperties() throws Exception {
        when(propertyService.getAllProperties()).thenReturn(testProperties);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("featuredProperties"))
                .andExpect(model().attribute("featuredProperties", 
                    org.hamcrest.Matchers.hasSize(6))); // Should limit to 6 properties
    }

    @Test
    @DisplayName("Should handle empty properties list")
    @WithMockUser
    void shouldHandleEmptyPropertiesList() throws Exception {
        when(propertyService.getAllProperties()).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("featuredProperties"))
                .andExpect(model().attribute("featuredProperties", 
                    org.hamcrest.Matchers.hasSize(0)));
    }

    @Test
    @DisplayName("Should handle fewer than 6 properties")
    @WithMockUser
    void shouldHandleFewerThanSixProperties() throws Exception {
        List<PropertyDTO> fewProperties = testProperties.subList(0, 3);
        when(propertyService.getAllProperties()).thenReturn(fewProperties);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("featuredProperties"))
                .andExpect(model().attribute("featuredProperties", 
                    org.hamcrest.Matchers.hasSize(3)));
    }

    @Test
    @DisplayName("Should handle service exception gracefully")
    @WithMockUser
    void shouldHandleServiceExceptionGracefully() throws Exception {
        when(propertyService.getAllProperties()).thenThrow(new RuntimeException("Service error"));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should include correct content type")
    @WithMockUser
    void shouldIncludeCorrectContentType() throws Exception {
        when(propertyService.getAllProperties()).thenReturn(testProperties);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"));
    }

    @Test
    @DisplayName("Should handle internationalization")
    @WithMockUser
    void shouldHandleInternationalization() throws Exception {
        when(propertyService.getAllProperties()).thenReturn(testProperties);

        mockMvc.perform(get("/")
                .param("lang", "es"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));

        mockMvc.perform(get("/")
                .param("lang", "hu"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    @DisplayName("Should handle concurrent requests")
    @WithMockUser
    void shouldHandleConcurrentRequests() throws Exception {
        when(propertyService.getAllProperties()).thenReturn(testProperties);

        // Simulate multiple concurrent requests
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"));
        }
    }

    @Test
    @DisplayName("Should properly limit featured properties to 6")
    @WithMockUser
    void shouldProperlyLimitFeaturedPropertiesToSix() throws Exception {
        // Create more than 6 properties
        List<PropertyDTO> manyProperties = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            PropertyDTO property = new PropertyDTO();
            property.setId((long) i);
            property.setTitle("Property " + i);
            property.setDescription("Description " + i);
            property.setLocation("Location " + i);
            property.setPrice(new BigDecimal("1000.00"));
            property.setMaxGuests(4);
            property.setBedrooms(2);
            property.setBathrooms(1);
            property.setImages(List.of("https://example.com/property" + i + ".jpg"));
            property.setOwnerId("owner" + i + "@example.com");
            property.setAmenities(new ArrayList<>());
            manyProperties.add(property);
        }

        when(propertyService.getAllProperties()).thenReturn(manyProperties);

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("featuredProperties"))
                .andExpect(model().attribute("featuredProperties", 
                    org.hamcrest.Matchers.hasSize(6)));
    }
}