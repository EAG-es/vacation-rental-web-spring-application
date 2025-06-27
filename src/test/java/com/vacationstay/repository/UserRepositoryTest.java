package com.vacationstay.repository;

import com.vacationstay.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for UserRepository.
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("User Repository Tests")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("hashedPassword");
        testUser.setProvider("local");
        testUser.setRoles(new HashSet<>(Set.of("USER")));
    }

    @Test
    @DisplayName("Should save and find user by email")
    void shouldSaveAndFindUserByEmail() {
        // Save user
        User savedUser = userRepository.save(testUser);
        entityManager.flush();

        // Find by email
        Optional<User> foundUser = userRepository.findByEmail("john.doe@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals(savedUser.getId(), foundUser.get().getId());
        assertEquals("John Doe", foundUser.get().getName());
        assertEquals("john.doe@example.com", foundUser.get().getEmail());
        assertEquals("local", foundUser.get().getProvider());
    }

    @Test
    @DisplayName("Should return empty when user not found by email")
    void shouldReturnEmptyWhenUserNotFoundByEmail() {
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@example.com");
        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Should check if user exists by email")
    void shouldCheckIfUserExistsByEmail() {
        // Initially should not exist
        assertFalse(userRepository.existsByEmail("john.doe@example.com"));

        // Save user
        userRepository.save(testUser);
        entityManager.flush();

        // Now should exist
        assertTrue(userRepository.existsByEmail("john.doe@example.com"));
        assertFalse(userRepository.existsByEmail("nonexistent@example.com"));
    }

    @Test
    @DisplayName("Should find user by provider and provider ID")
    void shouldFindUserByProviderAndProviderId() {
        testUser.setProvider("google");
        testUser.setProviderId("google123");
        
        userRepository.save(testUser);
        entityManager.flush();

        Optional<User> foundUser = userRepository.findByProviderAndProviderId("google", "google123");

        assertTrue(foundUser.isPresent());
        assertEquals("google", foundUser.get().getProvider());
        assertEquals("google123", foundUser.get().getProviderId());
    }

    @Test
    @DisplayName("Should return empty when provider combination not found")
    void shouldReturnEmptyWhenProviderCombinationNotFound() {
        Optional<User> foundUser = userRepository.findByProviderAndProviderId("google", "nonexistent");
        assertFalse(foundUser.isPresent());
    }

    @Test
    @DisplayName("Should handle email case sensitivity")
    void shouldHandleEmailCaseSensitivity() {
        userRepository.save(testUser);
        entityManager.flush();

        // Test different cases - this depends on database collation
        Optional<User> foundUser1 = userRepository.findByEmail("JOHN.DOE@EXAMPLE.COM");
        Optional<User> foundUser2 = userRepository.findByEmail("john.doe@EXAMPLE.com");

        // For H2 database, email search is typically case-insensitive
        // Adjust these assertions based on your database configuration
        assertTrue(foundUser1.isPresent() || foundUser2.isPresent());
    }

    @Test
    @DisplayName("Should save user with OAuth provider")
    void shouldSaveUserWithOAuthProvider() {
        User oauthUser = new User();
        oauthUser.setName("Jane Smith");
        oauthUser.setEmail("jane.smith@gmail.com");
        oauthUser.setProvider("google");
        oauthUser.setProviderId("google456");
        oauthUser.setImageUrl("https://example.com/avatar.jpg");
        oauthUser.setRoles(new HashSet<>(Set.of("USER")));

        User savedUser = userRepository.save(oauthUser);
        entityManager.flush();

        assertNotNull(savedUser.getId());
        assertEquals("google", savedUser.getProvider());
        assertEquals("google456", savedUser.getProviderId());
        assertNull(savedUser.getPassword()); // OAuth users don't have passwords
        assertTrue(savedUser.isOauthUser());
    }

    @Test
    @DisplayName("Should maintain referential integrity with roles")
    void shouldMaintainReferentialIntegrityWithRoles() {
        testUser.getRoles().add("ADMIN");
        testUser.getRoles().add("MODERATOR");

        User savedUser = userRepository.save(testUser);
        entityManager.flush();
        entityManager.clear();

        Optional<User> foundUser = userRepository.findById(savedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals(3, foundUser.get().getRoles().size());
        assertTrue(foundUser.get().getRoles().contains("USER"));
        assertTrue(foundUser.get().getRoles().contains("ADMIN"));
        assertTrue(foundUser.get().getRoles().contains("MODERATOR"));
    }

    @Test
    @DisplayName("Should handle user update operations")
    void shouldHandleUserUpdateOperations() {
        User savedUser = userRepository.save(testUser);
        entityManager.flush();

        // Update user details
        savedUser.setName("John Updated");
        savedUser.setImageUrl("https://example.com/new-avatar.jpg");
        
        User updatedUser = userRepository.save(savedUser);
        entityManager.flush();

        Optional<User> foundUser = userRepository.findById(updatedUser.getId());
        assertTrue(foundUser.isPresent());
        assertEquals("John Updated", foundUser.get().getName());
        assertEquals("https://example.com/new-avatar.jpg", foundUser.get().getImageUrl());
        assertNotNull(foundUser.get().getUpdatedAt());
    }

    @Test
    @DisplayName("Should handle user deletion")
    void shouldHandleUserDeletion() {
        User savedUser = userRepository.save(testUser);
        entityManager.flush();

        Long userId = savedUser.getId();
        assertTrue(userRepository.existsById(userId));

        userRepository.delete(savedUser);
        entityManager.flush();

        assertFalse(userRepository.existsById(userId));
        assertFalse(userRepository.findByEmail("john.doe@example.com").isPresent());
    }

    @Test
    @DisplayName("Should handle constraint violations gracefully")
    void shouldHandleConstraintViolationsGracefully() {
        // Save first user
        userRepository.save(testUser);
        entityManager.flush();

        // Try to save another user with same email
        User duplicateUser = new User();
        duplicateUser.setName("Jane Doe");
        duplicateUser.setEmail("john.doe@example.com"); // Same email
        duplicateUser.setPassword("anotherPassword");
        duplicateUser.setProvider("local");
        duplicateUser.setRoles(new HashSet<>(Set.of("USER")));

        // This should throw an exception due to unique constraint
        assertThrows(Exception.class, () -> {
            userRepository.save(duplicateUser);
            entityManager.flush();
        });
    }
}