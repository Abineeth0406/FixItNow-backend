package com.app.FixItNow_backend.dto;

import lombok.Data;

@Data
public class CreateDepartmentRequest {
    private String deptName;
    private String deptId;
    private String password;
    private Double latitude;
    private Double longitude;
    private String areaName;

}
