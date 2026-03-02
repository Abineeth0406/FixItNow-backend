package com.app.FixItNow_backend.service;

import com.app.FixItNow_backend.dto.UpdateProfileDTO;
import com.app.FixItNow_backend.dto.UserProfileDTO;
import com.app.FixItNow_backend.entity.User;
import com.app.FixItNow_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void updateProfile(String currentEmail, UpdateProfileDTO dto) {

        User user = userRepository.findByEmail(currentEmail)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (dto.getFullName() != null && !dto.getFullName().isBlank()) {
            user.setFullName(dto.getFullName());
        }

        if (dto.getAreaName() != null && !dto.getAreaName().isBlank()) {
            user.setAreaName(dto.getAreaName());
        }

        if (dto.getLatitude() != null) {
            user.setLatitude(dto.getLatitude());
        }

        if (dto.getLongitude() != null) {
            user.setLongitude(dto.getLongitude());
        }

        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Email already in use"
                );
            }
            user.setEmail(dto.getEmail());
        }

        userRepository.save(user);
    }

    public UserProfileDTO getProfile(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return new UserProfileDTO(
                user.getFullName(),
                user.getEmail(),
                user.getAreaName(),
                user.getLatitude(),
                user.getLongitude()
        );
    }
}