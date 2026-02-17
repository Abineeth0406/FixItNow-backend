package com.app.FixItNow_backend.config;

import com.app.FixItNow_backend.entity.Role;
import com.app.FixItNow_backend.entity.User;
import com.app.FixItNow_backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        String adminPhone = "9999999999";

        if (userRepository.findByPhone(adminPhone).isEmpty()) {

            User admin = User.builder()
                    .fullName("Super Admin")
                    .phone(adminPhone)
                    .password(passwordEncoder.encode("admin123"))
                    .role(Role.ADMIN)
                    .phoneVerified(true)
                    .emailVerified(false)
                    .build();

            userRepository.save(admin);

            System.out.println("Admin account created successfully");
        }
    }
}
