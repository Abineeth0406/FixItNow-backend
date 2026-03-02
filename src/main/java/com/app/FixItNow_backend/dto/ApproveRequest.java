package com.app.FixItNow_backend.dto;

import com.app.FixItNow_backend.entity.PriorityLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApproveRequest {

    private String departmentEmail;
    private PriorityLevel priority;
}