package com.app.FixItNow_backend.controller;

import com.app.FixItNow_backend.dto.AuthResponse;
import com.app.FixItNow_backend.dto.LoginRequest;
import com.app.FixItNow_backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import com.app.FixItNow_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.app.FixItNow_backend.dto.SignupRequest;
import com.app.FixItNow_backend.entity.Role;
import com.app.FixItNow_backend.entity.User;



@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

//    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {

        // 1️⃣ Find user by phone
        var user = userRepository.findByPhone(request.getPhone())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2️⃣ Check password using BCrypt
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // 3️⃣ Generate tokens
        String accessToken = jwtService.generateAccessToken(user.getPhone());
        String refreshToken = jwtService.generateRefreshToken(user.getPhone());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(user.getRole().name())  // assuming role is enum
                .build();
    }


    @PostMapping("/signup")
    public String signup(@RequestBody SignupRequest request) {

        // 1️⃣ Validate phone format (Indian format: 10 digits, starts 6-9)
        if (!request.getPhone().matches("^[6-9]\\d{9}$")) {
            throw new RuntimeException("Invalid phone number format");
        }

        // 2️⃣ Check if phone already exists
        if (userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new RuntimeException("Phone number already registered");
        }

        // 3️⃣ Create new user
        User user = User.builder()
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .phoneVerified(true)
                .emailVerified(false)

                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .areaName(request.getAreaName())
                .build();

        userRepository.save(user);

        return "User registered successfully";
    }



}
