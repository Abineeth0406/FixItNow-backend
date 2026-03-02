package com.app.FixItNow_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminLoginRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}