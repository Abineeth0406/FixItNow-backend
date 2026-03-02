package com.app.FixItNow_backend.controller;

import com.app.FixItNow_backend.dto.UpdateProfileDTO;
import com.app.FixItNow_backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody UpdateProfileDTO dto,
            Authentication authentication
    ) {
        String email = authentication.getName();
        userService.updateProfile(email, dto);
        return ResponseEntity.ok("Profile updated successfully");
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(userService.getProfile(email));
    }
}