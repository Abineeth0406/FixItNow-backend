package com.app.FixItNow_backend.dto;

import lombok.Data;

@Data
public class SignupRequest {
    private String fullName;
    private String phone;
    private String password;
    private Double latitude;
    private Double longitude;
    private String areaName;

}
