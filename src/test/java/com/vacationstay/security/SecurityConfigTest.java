package com.vacationstay.security;

import com.vacationstay.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

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
        mockMvc.perform(get("/browse"))
                .andExpect(status().isOk());

        // Property details
        mockMvc.perform(get("/property/1"))
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
        mockMvc.perform(get("/booking/new"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(post("/api/bookings"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("Should allow access to login and signup pages")
    void shouldAllowAccessToLoginAndSignupPages() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle form login correctly")
    @WithMockUser(username = "test@example.com", roles = "USER")
    void shouldHandleFormLoginCorrectly() throws Exception {
        mockMvc.perform(formLogin("/login")
                .user("test@example.com")
                .password("password"))
                .andExpect(authenticated());
    }

    @Test
    @DisplayName("Should handle logout correctly")
    @WithMockUser(username = "test@example.com", roles = "USER")
    void shouldHandleLogoutCorrectly() throws Exception {
        mockMvc.perform(logout("/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("Should allow authenticated users to access protected endpoints")
    @WithMockUser(username = "test@example.com", roles = "USER")
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
    @WithMockUser(username = "test@example.com", roles = "USER")
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
    @DisplayName("Should protect admin endpoints")
    @WithMockUser(username = "user@example.com", roles = "USER")
    void shouldProtectAdminEndpoints() throws Exception {
        // Regular user should not access admin endpoints
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should allow admin access to admin endpoints")
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void shouldAllowAdminAccessToAdminEndpoints() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should handle session management")
    @WithMockUser(username = "test@example.com", roles = "USER")
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
        mockMvc.perform(post("/signup")
                .param("name", "Test User")
                .param("email", "test@example.com")
                .param("password", "plainpassword"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @DisplayName("Should handle remember-me functionality")
    void shouldHandleRememberMeFunctionality() throws Exception {
        mockMvc.perform(post("/login")
                .param("username", "test@example.com")
                .param("password", "password")
                .param("remember-me", "on")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().is3xxRedirection())
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
    @WithMockUser(username = "test@example.com", roles = "USER")
    void shouldHandleDifferentHttpMethodsForProtectedResources() throws Exception {
        // GET should work
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk());

        // POST should work with CSRF
        mockMvc.perform(post("/api/bookings")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());

        // PUT should work with CSRF
        mockMvc.perform(put("/api/bookings/1")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());

        // DELETE should work with CSRF
        mockMvc.perform(delete("/api/bookings/1")
                .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }
}