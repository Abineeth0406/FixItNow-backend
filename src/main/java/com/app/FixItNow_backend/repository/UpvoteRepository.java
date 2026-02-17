package com.app.FixItNow_backend.repository;

import com.app.FixItNow_backend.entity.Upvote;
import com.app.FixItNow_backend.entity.User;
import com.app.FixItNow_backend.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UpvoteRepository extends JpaRepository<Upvote, Long> {

    Optional<Upvote> findByUserAndComplaint(User user, Complaint complaint);

    long countByComplaint(Complaint complaint);
}
