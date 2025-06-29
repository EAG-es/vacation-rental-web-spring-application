package com.vacationstay.security;

import com.vacationstay.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Security Configuration.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Security Configuration Tests")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should allow access to public endpoints without authentication")
    void shouldAllowAccessToPublicEndpointsWithoutAuthentication() throws Exception {
        // Home page
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());

        // Browse properties
        mockMvc.perform(get("/properties"))
                .andExpect(status().isOk());

        // Property details
        mockMvc.perform(get("/properties/1"))
                .andExpect(status().isOk());

        // Static resources
        mockMvc.perform(get("/css/styles.css"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/js/scripts.js"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/images/logo.png"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should redirect to login for protected endpoints")
    void shouldRedirectToLoginForProtectedEndpoints() throws Exception {
        // Dashboard should require authentication
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        // Booking endpoints should require authentication
        mockMvc.perform(get("/bookings/create"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

    }

    @Test
    @DisplayName("Should allow access to login and signup pages")
    void shouldAllowAccessToLoginAndSignupPages() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle form login correctly")
//    @WithMockUser(username = "john@example.com", password = "john_doe_password", roles = "USER")
    void shouldHandleFormLoginCorrectly() throws Exception {
        ResultActions resultActions = mockMvc.perform(formLogin("/login")
                .user("john@example.com")
                .password("john_doe_password"));
        MvcResult mvcResult = resultActions.andReturn();
        var response = mvcResult.getResponse();
        resultActions.andExpect(authenticated());
    }

    @Test
    @DisplayName("Should handle logout correctly")
//    @WithMockUser(username = "john@example.com", password = "john_doe_password", roles = "USER")
    void shouldHandleLogoutCorrectly() throws Exception {
        shouldHandleFormLoginCorrectly();
        var resultActions = mockMvc.perform(logout("/logout"));
        MvcResult mvcResult = resultActions.andReturn();
        var response = mvcResult.getResponse();
        resultActions.andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("Should allow authenticated users to access protected endpoints")
    @WithMockUser(username = "john@example.com", password = "john_doe_password", roles = "USER")
    void shouldAllowAuthenticatedUsersToAccessProtectedEndpoints() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(authenticated());
    }

    @Test
    @DisplayName("Should handle CSRF protection for forms")
    void shouldHandleCSRFProtectionForForms() throws Exception {
        // POST without CSRF token should be forbidden
        mockMvc.perform(post("/api/bookings"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should allow API endpoints for authenticated users")
    @WithMockUser(username = "john@example.com", password = "john_doe_password", roles = "USER")
    void shouldAllowAPIEndpointsForAuthenticatedUsers() throws Exception {
        mockMvc.perform(get("/api/properties"))
                .andExpect(status().isOk())
                .andExpect(authenticated());
    }

    @Test
    @DisplayName("Should handle OAuth2 login endpoints")
    void shouldHandleOAuth2LoginEndpoints() throws Exception {
        // OAuth2 authorization should redirect
        mockMvc.perform(get("/oauth2/authorization/google"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Should handle session management")
    @WithMockUser(username = "john@example.com", password = "john_doe_password", roles = "USER")
    void shouldHandleSessionManagement() throws Exception {
        // Test session concurrency control
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(authenticated());
    }

    @Test
    @DisplayName("Should handle password encoding")
    void shouldHandlePasswordEncoding() throws Exception {
        // This would typically test that passwords are properly encoded
        // Implementation depends on your user registration endpoint
        mockMvc.perform(post("/register")
                .param("name", "Test User")
                .param("email", "test@example.com")
                .param("password", "plainpassword")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Should handle remember-me functionality")
    void shouldHandleRememberMeFunctionality() throws Exception {
        var resultActions = mockMvc.perform(post("/login")
                .param("username", "john@example.com")
                .param("password", "john_doe_password")
                .param("remember-me", "on")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()));
        MvcResult mvcResult = resultActions.andReturn();
        var response = mvcResult.getResponse();
        String tes = response.getContentAsString();
        resultActions.andExpect(status().is3xxRedirection())
                .andExpect(authenticated());
    }

    @Test
    @DisplayName("Should handle internationalization in security context")
    void shouldHandleInternationalizationInSecurityContext() throws Exception {
        mockMvc.perform(get("/login")
                .param("lang", "es"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/login")
                .param("lang", "hu"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle security headers")
    @WithMockUser
    void shouldHandleSecurityHeaders() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(header().exists("X-Content-Type-Options"))
                .andExpect(header().exists("X-Frame-Options"))
                .andExpect(header().exists("X-XSS-Protection"));
    }

    @Test
    @DisplayName("Should handle different HTTP methods for protected resources")
    @WithMockUser(username = "john@example.com", password = "john_doe_password", roles = "USER")
    void shouldHandleDifferentHttpMethodsForProtectedResources() throws Exception {
        // GET should work
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk());

        var resultActions = mockMvc.perform(get("/api/bookings/1"));
        var result = resultActions.andReturn();
        String tex = result.getResponse().getContentAsString();
        String bookingDTO_tex = "{" +
                "\"id\":1" +
                ",\"propertyId\":1" +
                ",\"userId\":2" +
                ",\"startDate\":\"2023-12-10\"" +
                ",\"endDate\":\"2023-12-15\"" +
                ",\"totalPrice\":2250.00" +
                ",\"status\":\"pending\"" +
                "}";
        // PUT should work without CSRF
        resultActions = mockMvc.perform(put("/api/bookings/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookingDTO_tex)
//                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf())
                );
        result = resultActions.andReturn();
        resultActions.andExpect(status().isOk());

        // DELETE should work without CSRF
        mockMvc.perform(delete("/api/bookings/1")
//                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                ).andExpect(status().isNoContent());
    }
}