package com.vacationstay.service;

import com.vacationstay.dto.UserDTO;
import com.vacationstay.model.User;
import com.vacationstay.repository.UserRepository;
import com.vacationstay.service.impl.UserServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for UserService implementation.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john.doe@example.com");
        testUser.setPassword("hashedPassword");
        testUser.setProvider("local");
        testUser.setRoles(new HashSet<>(Set.of("USER")));
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        testUserDTO = new UserDTO();
        testUserDTO.setId(1L);
        testUserDTO.setName("John Doe");
        testUserDTO.setEmail("john.doe@example.com");
        testUserDTO.setPassword("plainPassword");
        testUserDTO.setProvider("local");
        testUserDTO.setRoles(new HashSet<>(Set.of("USER")));
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void shouldGetUserByIdSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        UserDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("local", result.getProvider());
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user not found by ID")
    void shouldThrowExceptionWhenUserNotFoundById() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get user by email successfully")
    void shouldGetUserByEmailSuccessfully() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));

        UserDTO result = userService.getUserByEmail("john.doe@example.com");

        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("John Doe", result.getName());
        verify(userRepository).findByEmail("john.doe@example.com");
    }

    @Test
    @DisplayName("Should throw exception when user not found by email")
    void shouldThrowExceptionWhenUserNotFoundByEmail() {
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, 
            () -> userService.getUserByEmail("nonexistent@example.com"));
        verify(userRepository).findByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("Should get user ID by email successfully")
    void shouldGetUserIdByEmailSuccessfully() {
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(testUser));

        Long result = userService.getUserIdByEmail("john.doe@example.com");

        assertEquals(1L, result);
        verify(userRepository).findByEmail("john.doe@example.com");
    }

    @Test
    @DisplayName("Should register new user successfully")
    void shouldRegisterNewUserSuccessfully() {
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
        when(passwordEncoder.encode("plainPassword")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        UserDTO result = userService.registerUser(testUserDTO);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("john.doe@example.com", result.getEmail());
        
        verify(userRepository).existsByEmail("john.doe@example.com");
        verify(passwordEncoder).encode("plainPassword");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when registering user with existing email")
    void shouldThrowExceptionWhenRegisteringUserWithExistingEmail() {
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> userService.registerUser(testUserDTO));
        
        verify(userRepository).existsByEmail("john.doe@example.com");
        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Should update user successfully")
    void shouldUpdateUserSuccessfully() {
        UserDTO updateDTO = new UserDTO();
        updateDTO.setName("John Updated");
        updateDTO.setEmail("john.updated@example.com");
        
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("John Updated");
        updatedUser.setEmail("john.updated@example.com");
        updatedUser.setProvider("local");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDTO result = userService.updateUser(1L, updateDTO);

        assertNotNull(result);
        assertEquals("John Updated", result.getName());
        assertEquals("john.updated@example.com", result.getEmail());
        
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void shouldThrowExceptionWhenUpdatingNonExistentUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserDTO updateDTO = new UserDTO();
        updateDTO.setName("Updated Name");

        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(1L, updateDTO));
        
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void shouldDeleteUserSuccessfully() {
        // Arrange
        when(userRepository.existsById(1L)).thenReturn(true);

        // Act
        assertDoesNotThrow(() -> userService.deleteUser(1L));
        
        // Assert
        verify(userRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        when(userRepository.existsById(1L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should check if user exists by email")
    void shouldCheckIfUserExistsByEmail() {
        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        assertTrue(userService.existsByEmail("john.doe@example.com"));
        assertFalse(userService.existsByEmail("nonexistent@example.com"));
        
        verify(userRepository).existsByEmail("john.doe@example.com");
        verify(userRepository).existsByEmail("nonexistent@example.com");
    }

    @Test
    @DisplayName("Should handle OAuth user registration")
    void shouldHandleOAuthUserRegistration() {
        UserDTO oauthUserDTO = new UserDTO();
        oauthUserDTO.setName("Jane Smith");
        oauthUserDTO.setEmail("jane.smith@gmail.com");
        oauthUserDTO.setProvider("google");
        oauthUserDTO.setProviderId("google123");
        oauthUserDTO.setImageUrl("https://example.com/avatar.jpg");
        oauthUserDTO.setRoles(new HashSet<>(Set.of("USER")));

        User oauthUser = new User();
        oauthUser.setId(2L);
        oauthUser.setName("Jane Smith");
        oauthUser.setEmail("jane.smith@gmail.com");
        oauthUser.setProvider("google");
        oauthUser.setProviderId("google123");
        oauthUser.setImageUrl("https://example.com/avatar.jpg");
        oauthUser.setRoles(new HashSet<>(Set.of("USER")));

        when(userRepository.existsByEmail("jane.smith@gmail.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(oauthUser);

        UserDTO result = userService.registerUser(oauthUserDTO);

        assertNotNull(result);
        assertEquals("Jane Smith", result.getName());
        assertEquals("google", result.getProvider());
        assertEquals("google123", result.getProviderId());
        
        verify(userRepository).existsByEmail("jane.smith@gmail.com");
        verify(userRepository).save(any(User.class));
        // OAuth users don't have passwords encoded
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Should handle null and empty values gracefully")
    void shouldHandleNullAndEmptyValuesGracefully() {
        // Test with null email
        assertThrows(Exception.class, () -> userService.getUserByEmail(null));
        
        // Test with empty email
        when(userRepository.findByEmail("")).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUserByEmail(""));
    }

    @Test
    @DisplayName("Should not encode password for OAuth users during registration")
    void shouldNotEncodePasswordForOAuthUsersDuringRegistration() {
        UserDTO oauthUserDTO = new UserDTO();
        oauthUserDTO.setName("OAuth User");
        oauthUserDTO.setEmail("oauth@example.com");
        oauthUserDTO.setProvider("google");
        oauthUserDTO.setPassword(null); // OAuth users don't have passwords

        User savedOauthUser = new User();
        savedOauthUser.setId(3L);
        savedOauthUser.setName("OAuth User");
        savedOauthUser.setEmail("oauth@example.com");
        savedOauthUser.setProvider("google");

        when(userRepository.existsByEmail("oauth@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedOauthUser);

        UserDTO result = userService.registerUser(oauthUserDTO);

        assertNotNull(result);
        assertEquals("google", result.getProvider());
        
        verify(userRepository).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }
}