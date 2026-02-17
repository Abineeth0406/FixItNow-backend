package com.app.FixItNow_backend.controller;

import com.app.FixItNow_backend.service.UpvoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/upvotes")
@RequiredArgsConstructor
public class UpvoteController {

    private final UpvoteService upvoteService;

    @PostMapping("/{complaintId}")
    public ResponseEntity<Long> toggleUpvote(
            @PathVariable Long complaintId,
            @AuthenticationPrincipal User user
    ) {

        String phone = user.getUsername(); // username = phone

        long count = upvoteService.toggleUpvote(
                complaintId,
                phone
        );

        return ResponseEntity.ok(count);
    }
}
