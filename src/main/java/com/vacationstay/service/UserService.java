package com.vacationstay.service;

import com.vacationstay.dto.UserDTO;

public interface UserService {
    UserDTO getUserById(Long id);
    UserDTO getUserByEmail(String email);
    Long getUserIdByEmail(String email);
    UserDTO registerUser(UserDTO userDTO);
    UserDTO updateUser(Long id, UserDTO userDTO);
    void deleteUser(Long id);
    boolean existsByEmail(String email);
}