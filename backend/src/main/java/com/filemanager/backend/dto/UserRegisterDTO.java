package com.filemanager.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO used for user self-registration.
 * Role is not specified — defaults to USER on backend.
 */
@Data
public class UserRegisterDTO {

    @NotBlank(message = "Username must not be blank")
    private String username;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password must not be blank")
    private String password;
}