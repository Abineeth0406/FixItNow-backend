package com.app.FixItNow_backend.service;

import com.app.FixItNow_backend.entity.*;
import com.app.FixItNow_backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UpvoteService {

    private final UpvoteRepository upvoteRepository;
    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    public long toggleUpvote(Long complaintId, String userPhone) {

        User user = userRepository.findByPhone(userPhone)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        // 🔴 Rule: User cannot upvote their own complaint
        if (complaint.getUserPhone().equals(userPhone)) {
            throw new RuntimeException("You cannot upvote your own complaint");
        }

        return upvoteRepository.findByUserAndComplaint(user, complaint)
                .map(existingUpvote -> {
                    upvoteRepository.delete(existingUpvote);
                    return upvoteRepository.countByComplaint(complaint);
                })
                .orElseGet(() -> {
                    Upvote upvote = Upvote.builder()
                            .user(user)
                            .complaint(complaint)
                            .createdAt(LocalDateTime.now())
                            .build();

                    upvoteRepository.save(upvote);
                    return upvoteRepository.countByComplaint(complaint);
                });
    }
}
