package com.vacationstay.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the User entity.
 */
@DisplayName("User Entity Tests")
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setPassword("hashedPassword");
        user.setProvider("local");
        user.setProviderId("123");
        user.setImageUrl("https://example.com/avatar.jpg");
        user.setRoles(new HashSet<>(Set.of("USER")));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("Should create user with all properties")
    void shouldCreateUserWithAllProperties() {
        assertNotNull(user);
        assertEquals(1L, user.getId());
        assertEquals("John Doe", user.getName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("hashedPassword", user.getPassword());
        assertEquals("local", user.getProvider());
        assertEquals("123", user.getProviderId());
        assertEquals("https://example.com/avatar.jpg", user.getImageUrl());
        assertEquals(Set.of("USER"), user.getRoles());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getUpdatedAt());
    }

    @Test
    @DisplayName("Should identify OAuth user correctly")
    void shouldIdentifyOAuthUserCorrectly() {
        // Test local user
        user.setProvider("local");
        assertFalse(user.isOauthUser());

        // Test OAuth user
        user.setProvider("google");
        assertTrue(user.isOauthUser());

        // Test user with null provider
        user.setProvider(null);
        assertFalse(user.isOauthUser());
    }

    @Test
    @DisplayName("Should initialize collections properly")
    void shouldInitializeCollectionsProperly() {
        User newUser = new User();
        assertNotNull(newUser.getRoles());
        assertNotNull(newUser.getBookings());
        assertNotNull(newUser.getReviews());
        assertTrue(newUser.getRoles().isEmpty());
        assertTrue(newUser.getBookings().isEmpty());
        assertTrue(newUser.getReviews().isEmpty());
    }

    @Test
    @DisplayName("Should handle roles management")
    void shouldHandleRolesManagement() {
        User newUser = new User();
        
        // Add roles
        newUser.getRoles().add("USER");
        newUser.getRoles().add("ADMIN");
        
        assertEquals(2, newUser.getRoles().size());
        assertTrue(newUser.getRoles().contains("USER"));
        assertTrue(newUser.getRoles().contains("ADMIN"));
        
        // Remove role
        newUser.getRoles().remove("ADMIN");
        assertEquals(1, newUser.getRoles().size());
        assertFalse(newUser.getRoles().contains("ADMIN"));
    }

    @Test
    @DisplayName("Should support equality based on id")
    void shouldSupportEqualityBasedOnId() {
        User user1 = new User();
        User user2 = new User();
        
        // Without ID
        assertNotEquals(user1, user2);
        
        // With same ID
        user1.setId(1L);
        user2.setId(1L);
        assertEquals(user1, user2);
        
        // With different IDs
        user2.setId(2L);
        assertNotEquals(user1, user2);
    }

    @Test
    @DisplayName("Should create user with constructor")
    void shouldCreateUserWithConstructor() {
        User constructedUser = new User(
            1L,
            "Jane Doe",
            "jane@example.com",
            "password",
            "avatar.jpg",
            "google",
            "456",
            new HashSet<>(Set.of("USER")),
            null,
            null,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        
        assertEquals(1L, constructedUser.getId());
        assertEquals("Jane Doe", constructedUser.getName());
        assertEquals("jane@example.com", constructedUser.getEmail());
        assertEquals("google", constructedUser.getProvider());
    }

    @Test
    @DisplayName("Should handle null values gracefully")
    void shouldHandleNullValuesGracefully() {
        User nullUser = new User();
        
        assertNull(nullUser.getId());
        assertNull(nullUser.getName());
        assertNull(nullUser.getEmail());
        assertNull(nullUser.getPassword());
        assertNull(nullUser.getProvider());
        assertNull(nullUser.getProviderId());
        assertNull(nullUser.getImageUrl());
        
        // Test isOauthUser with null provider
        assertFalse(nullUser.isOauthUser());
    }
}