package com.app.FixItNow_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserProfileDTO {
    private String fullName;
    private String email;
    private String areaName;
    private Double latitude;
    private Double longitude;
}