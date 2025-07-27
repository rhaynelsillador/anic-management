package com.sillador.strecs.admin.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginRequest {

    @NotBlank
    private String identifier; // username or email

    @NotBlank
    private String password;

    // Getters and setters
}
