package com.vacationstay.service.impl;

import com.vacationstay.dto.UserDTO;
import com.vacationstay.model.User;
import com.vacationstay.repository.UserRepository;
import com.vacationstay.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        return convertToDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        return convertToDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        return user.getId();
    }

    @Override
    @Transactional
    public UserDTO registerUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalStateException("Email already in use");
        }
        
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        
        // Only set password for local users
        if ("local".equals(userDTO.getProvider())) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        user.setProvider(userDTO.getProvider());
        user.setProviderId(userDTO.getProviderId());
        user.setImageUrl(userDTO.getImageUrl());
        
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        user.setRoles(roles);
        
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Override
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new IllegalStateException("Email already in use");
        }
        
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        
        // Encode password if provided
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        // Set provider (default to "local" if not specified)
        user.setProvider(userDTO.getProvider() != null ? userDTO.getProvider() : "local");
        user.setProviderId(userDTO.getProviderId());
        user.setImageUrl(userDTO.getImageUrl());
        
        // Set default role to USER if no roles specified
        Set<String> roles = new HashSet<>();
        roles.add("USER");
        user.setRoles(roles);
        
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    @Override
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
        
        existingUser.setName(userDTO.getName());
        
        // Only update password for local users and if provided
        if ("local".equals(existingUser.getProvider()) && userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        
        // Update image URL if provided
        if (userDTO.getImageUrl() != null) {
            existingUser.setImageUrl(userDTO.getImageUrl());
        }
        
        User updatedUser = userRepository.save(existingUser);
        return convertToDTO(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles());
        dto.setImageUrl(user.getImageUrl());
        dto.setProvider(user.getProvider());
        dto.setProviderId(user.getProviderId());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}