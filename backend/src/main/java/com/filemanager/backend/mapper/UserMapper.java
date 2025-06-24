package com.filemanager.backend.mapper;

import com.filemanager.backend.dto.UserDTO;
import com.filemanager.backend.entity.User;

/**
 * Mapper to convert between User entity and UserDTO.
 */
public class UserMapper {

    // Convert UserDTO to User entity (used for saving or updating)
    public static User toEntity(UserDTO dto) {
        return User.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(dto.getPassword()) // Will be encrypted in service layer
                .role(dto.getRole())
                .build();
    }

    // Convert User entity to UserDTO for safe response
    public static UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setPassword("******");   // Пароль маскується
        return dto;
    }
}
