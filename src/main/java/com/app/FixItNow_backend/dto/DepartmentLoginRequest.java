package com.app.FixItNow_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DepartmentLoginRequest {

    @NotBlank
    private String email;

//    @NotBlank
//    private String deptCode;

    @NotBlank
    private String password;
}