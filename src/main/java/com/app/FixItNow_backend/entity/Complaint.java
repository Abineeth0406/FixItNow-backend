package com.app.FixItNow_backend.entity;
import com.app.FixItNow_backend.entity.ComplaintStatus;


import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private String location;

    private String category;

    @Enumerated(EnumType.STRING)
    private ComplaintStatus status;
    // PENDING, APPROVED, REJECTED, IN_PROGRESS, RESOLVED

    private String userPhone; // phone of user who created complaint

    private String assignedDepartmentPhone;

    private Integer priority = 0;

    private String imagePath;

    @ManyToOne
    @JoinColumn(name = "assigned_department_id")
    private Department assignedDepartment;

    private Double latitude;
    private Double longitude;
    private String areaName;


}
