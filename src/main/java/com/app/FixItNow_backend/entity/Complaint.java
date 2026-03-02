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

    private String userEmail; // phone of user who created complaint

    private String assignedDepartmentPhone;

    @Enumerated(EnumType.STRING)
    private PriorityLevel priority = PriorityLevel.LOW;

    private String imagePath;
//    private String departmentImagePath;

    @ManyToOne
    @JoinColumn(name = "assigned_department_id")
    private Department assignedDepartment;

    private Double latitude;
    private Double longitude;
    private String areaName;

    @Transient
    private Long upvotesCount;


    @OneToMany(
            mappedBy = "complaint",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<Upvote> upvotes = new HashSet<>();

    @Column(name = "resolved_image_url")
    private String resolvedImageUrl;

//    public int getUpvotes() {
//        return 0;
//    }
}
