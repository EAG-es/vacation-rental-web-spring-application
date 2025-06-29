package com.vacationstay.service;

import com.vacationstay.dto.UserDTO;

/**
 * Service interface for managing user operations.
 * <p>
 * This service provides methods for user registration, retrieval, and management.
 * </p>
 *
 * @author VacationStay Team
 * @version 1.0
 * @since 2023-06-18
 */
public interface UserService {
    
    /**
     * Retrieves a user by their ID.
     *
     * @param id the unique identifier of the user
     * @return the user DTO
     * @throws jakarta.persistence.EntityNotFoundException if the user is not found
     */
    UserDTO getUserById(Long id);
    
    /**
     * Retrieves a user by their email address.
     *
     * @param email the email address of the user
     * @return the user DTO
     * @throws jakarta.persistence.EntityNotFoundException if the user is not found
     */
    UserDTO getUserByEmail(String email);
    
    /**
     * Retrieves a user's ID by their email address.
     *
     * @param email the email address of the user
     * @return the user's ID
     * @throws jakarta.persistence.EntityNotFoundException if the user is not found
     */
    Long getUserIdByEmail(String email);
    
    /**
     * Registers a new user in the system.
     *
     * @param userDTO the user data to register
     * @return the registered user DTO with assigned ID
     * @throws IllegalStateException if the email is already in use
     */
    UserDTO registerUser(UserDTO userDTO);
    
    /**
     * Creates a new user in the system.
     *
     * @param userDTO the user data to create
     * @return the created user DTO with assigned ID
     * @throws IllegalStateException if the email is already in use
     */
    UserDTO createUser(UserDTO userDTO);
    
    /**
     * Updates an existing user's information.
     *
     * @param id the unique identifier of the user to update
     * @param userDTO the updated user data
     * @return the updated user DTO
     * @throws jakarta.persistence.EntityNotFoundException if the user is not found
     */
    UserDTO updateUser(Long id, UserDTO userDTO);
    
    /**
     * Deletes a user by their ID.
     *
     * @param id the unique identifier of the user to delete
     * @throws jakarta.persistence.EntityNotFoundException if the user is not found
     */
    void deleteUser(Long id);
    
    /**
     * Checks if a user with the given email already exists.
     *
     * @param email the email address to check
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(String email);
}