package com.app.FixItNow_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "department")
    private List<User> authorities;

    @OneToMany(mappedBy = "assignedDepartment")
    private List<Complaint> complaints;
}
