package com.app.FixItNow_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileDTO {

    private String fullName;
    private String email;

    private Double latitude;
    private Double longitude;
    private String areaName;
}