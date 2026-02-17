package com.app.FixItNow_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    private String password;

    private boolean emailVerified;
    private boolean phoneVerified;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String publicIdentifier; // Citizen-AX91

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;


    private Double latitude;
    private Double longitude;
    private String areaName;

}
