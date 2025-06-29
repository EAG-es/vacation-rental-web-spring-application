package com.vacationstay.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.vacationstay.controller.api.AuthApiController.LoginRequest;
import com.vacationstay.dto.BookingDTO;
import com.vacationstay.security.JwtUtil;
import com.vacationstay.service.BookingService;
import com.vacationstay.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for BookingApiController with JWT authentication.
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@DisplayName("Booking API Controller JWT Tests")
class BookingApiControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private String jwtToken;
    private final String testEmail = "john@example.com";
    private final String testPassword = "john_doe_password";

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // Generate JWT token for testing
        jwtToken = jwtUtil.generateToken(testEmail);
    }

    /**
     * Helper method to perform login and get JWT token from response.
     */
    private String performLoginAndGetToken() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(testEmail);
        loginRequest.setPassword(testPassword);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        Map<String, Object> loginResponse = objectMapper.readValue(responseBody, Map.class);
        return (String) loginResponse.get("token");
    }

    @Test
    @DisplayName("Should reject request to GET /api/bookings without JWT token")
    void shouldRejectGetAllBookingsWithoutJwtToken() throws Exception {
        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Authentication required to access this resource"))
                .andExpect(jsonPath("$.path").value("/api/bookings"));
    }

    @Test
    @DisplayName("Should reject request to GET /api/bookings with invalid JWT token")
    void shouldRejectGetAllBookingsWithInvalidJwtToken() throws Exception {
        mockMvc.perform(get("/api/bookings")
                .header("Authorization", "Bearer invalid-token-here"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    @DisplayName("Should allow GET /api/bookings with valid JWT token")
    void shouldAllowGetAllBookingsWithValidJwtToken() throws Exception {
        mockMvc.perform(get("/api/bookings")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should reject GET /api/bookings/{id} without JWT token")
    void shouldRejectGetBookingByIdWithoutJwtToken() throws Exception {
        mockMvc.perform(get("/api/bookings/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("JWT authentication required to access booking operations. Please include a valid Bearer token in the Authorization header."));
    }

    @Test
    @DisplayName("Should allow GET /api/bookings/{id} with valid JWT token")
    void shouldAllowGetBookingByIdWithValidJwtToken() throws Exception {
        mockMvc.perform(get("/api/bookings/1")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should reject POST /api/bookings without JWT token")
    void shouldRejectCreateBookingWithoutJwtToken() throws Exception {
        BookingDTO bookingDTO = createTestBookingDTO();
        
        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Authentication required to access this resource"));
    }

    @Test
    @DisplayName("Should allow POST /api/bookings with valid JWT token")
    void shouldAllowCreateBookingWithValidJwtToken() throws Exception {
        BookingDTO bookingDTO = createTestBookingDTO();
        
        mockMvc.perform(post("/api/bookings")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should reject PUT /api/bookings/{id} without JWT token")
    void shouldRejectUpdateBookingWithoutJwtToken() throws Exception {
        BookingDTO bookingDTO = createTestBookingDTO();
        bookingDTO.setId(1L);
        
        mockMvc.perform(put("/api/bookings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("JWT authentication required to access booking operations. Please include a valid Bearer token in the Authorization header."));
    }

    @Test
    @DisplayName("Should allow PUT /api/bookings/{id} with valid JWT token")
    void shouldAllowUpdateBookingWithValidJwtToken() throws Exception {
        BookingDTO bookingDTO = createTestBookingDTO();
        bookingDTO.setId(1L);
        
        mockMvc.perform(put("/api/bookings/1")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should reject DELETE /api/bookings/{id} without JWT token")
    void shouldRejectDeleteBookingWithoutJwtToken() throws Exception {
        mockMvc.perform(delete("/api/bookings/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("JWT authentication required to access booking operations. Please include a valid Bearer token in the Authorization header."));
    }

    @Test
    @DisplayName("Should allow DELETE /api/bookings/{id} with valid JWT token")
    void shouldAllowDeleteBookingWithValidJwtToken() throws Exception {
        mockMvc.perform(delete("/api/bookings/1")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should reject GET /api/bookings/user/{userId} without JWT token")
    void shouldRejectGetBookingsByUserWithoutJwtToken() throws Exception {
        mockMvc.perform(get("/api/bookings/user/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("JWT authentication required to access booking operations. Please include a valid Bearer token in the Authorization header."));
    }

    @Test
    @DisplayName("Should allow GET /api/bookings/user/{userId} with valid JWT token")
    void shouldAllowGetBookingsByUserWithValidJwtToken() throws Exception {
        mockMvc.perform(get("/api/bookings/user/1")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should reject GET /api/bookings/property/{propertyId} without JWT token")
    void shouldRejectGetBookingsByPropertyWithoutJwtToken() throws Exception {
        mockMvc.perform(get("/api/bookings/property/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("JWT authentication required to access booking operations. Please include a valid Bearer token in the Authorization header."));
    }

    @Test
    @DisplayName("Should allow GET /api/bookings/property/{propertyId} with valid JWT token")
    void shouldAllowGetBookingsByPropertyWithValidJwtToken() throws Exception {
        mockMvc.perform(get("/api/bookings/property/1")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should reject GET /api/bookings/property/{propertyId}/availability without JWT token")
    void shouldRejectCheckPropertyAvailabilityWithoutJwtToken() throws Exception {
        mockMvc.perform(get("/api/bookings/property/1/availability")
                .param("startDate", "2024-01-15")
                .param("endDate", "2024-01-20"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("JWT authentication required to access booking operations. Please include a valid Bearer token in the Authorization header."));
    }

    @Test
    @DisplayName("Should allow GET /api/bookings/property/{propertyId}/availability with valid JWT token")
    void shouldAllowCheckPropertyAvailabilityWithValidJwtToken() throws Exception {
        mockMvc.perform(get("/api/bookings/property/1/availability")
                .header("Authorization", "Bearer " + jwtToken)
                .param("startDate", "2024-01-15")
                .param("endDate", "2024-01-20"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should reject PATCH /api/bookings/{id}/cancel without JWT token")
    void shouldRejectCancelBookingWithoutJwtToken() throws Exception {
        mockMvc.perform(patch("/api/bookings/1/cancel"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("JWT authentication required to access booking operations. Please include a valid Bearer token in the Authorization header."));
    }

    @Test
    @DisplayName("Should allow PATCH /api/bookings/{id}/cancel with valid JWT token")
    void shouldAllowCancelBookingWithValidJwtToken() throws Exception {
        mockMvc.perform(patch("/api/bookings/1/cancel")
                .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should authenticate using login endpoint and use returned JWT token")
    void shouldAuthenticateUsingLoginEndpointAndUseJwtToken() throws Exception {
        // First, perform login and get JWT token
        String authToken = performLoginAndGetToken();
        
        // Use the token to access protected endpoint
        mockMvc.perform(get("/api/bookings")
                .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should handle JWT token expiration properly")
    void shouldHandleJwtTokenExpirationProperly() throws Exception {
        // Create an expired token (this is a simplified test - in real scenario you'd manipulate time)
        String expiredToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJqb2huQGV4YW1wbGUuY29tIiwiaWF0IjoxNTc3ODM2ODAwLCJleHAiOjE1Nzc4MzY4MDB9.invalid";
        
        mockMvc.perform(get("/api/bookings")
                .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("Should handle malformed JWT token properly")
    void shouldHandleMalformedJwtTokenProperly() throws Exception {
        mockMvc.perform(get("/api/bookings")
                .header("Authorization", "Bearer malformed.jwt.token"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("Should handle missing Bearer prefix in Authorization header")
    void shouldHandleMissingBearerPrefixInAuthorizationHeader() throws Exception {
        mockMvc.perform(get("/api/bookings")
                .header("Authorization", jwtToken)) // Missing "Bearer " prefix
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    @DisplayName("Should validate that JWT token contains correct email")
    void shouldValidateJwtTokenContainsCorrectEmail() throws Exception {
        // Create a token for a different user
        String differentUserToken = jwtUtil.generateToken("different@example.com");
        
        // The token should still be valid (but for a different user)
        mockMvc.perform(get("/api/bookings")
                .header("Authorization", "Bearer " + differentUserToken))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("Should test all HTTP methods require JWT authentication")
    void shouldTestAllHttpMethodsRequireJwtAuthentication() throws Exception {
        BookingDTO bookingDTO = createTestBookingDTO();
        
        // Test GET
        mockMvc.perform(get("/api/bookings/1"))
                .andExpect(status().isUnauthorized());
        
        // Test POST
        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isUnauthorized());
        
        // Test PUT
        mockMvc.perform(put("/api/bookings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookingDTO)))
                .andExpect(status().isUnauthorized());
        
        // Test DELETE
        mockMvc.perform(delete("/api/bookings/1"))
                .andExpect(status().isUnauthorized());
        
        // Test PATCH
        mockMvc.perform(patch("/api/bookings/1/cancel"))
                .andExpect(status().isUnauthorized());
    }

    /**
     * Helper method to create a test BookingDTO.
     */
    private BookingDTO createTestBookingDTO() {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setPropertyId(1L);
        bookingDTO.setUserId(2L);
        bookingDTO.setStartDate(LocalDate.of(2024, 1, 15));
        bookingDTO.setEndDate(LocalDate.of(2024, 1, 20));
        bookingDTO.setTotalPrice(BigDecimal.valueOf(750.00));
        bookingDTO.setStatus("pending");
        return bookingDTO;
    }
}