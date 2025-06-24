package com.filemanager.backend.dto;

import com.filemanager.backend.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO used for full user information transfer, especially by admin.
 * Includes role, password and ID.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    // Optional field used for responses or updates
    private Long id;

    @NotBlank(message = "Username must not be blank")
    private String username;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password must not be blank")
    private String password;

    @NotNull(message = "Role is required")
    private User.Role role;
}
