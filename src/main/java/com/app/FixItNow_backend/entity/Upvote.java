package com.app.FixItNow_backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "upvotes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "complaint_id"})
        }
)
public class Upvote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "complaint_id")
    private Complaint complaint;

    private LocalDateTime createdAt;
}
