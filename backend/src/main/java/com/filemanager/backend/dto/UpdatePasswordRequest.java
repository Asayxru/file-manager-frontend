package com.filemanager.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePasswordRequest {

    @NotBlank(message = "Password cannot be blank")
    private String password;
}
