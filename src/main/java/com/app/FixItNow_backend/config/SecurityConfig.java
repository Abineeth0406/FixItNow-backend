package com.app.FixItNow_backend.config;

import com.app.FixItNow_backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;


@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // ❌ Disable CSRF because we are using JWT (stateless)
                .csrf(csrf -> csrf.disable())

                // ✅ Enable CORS globally (we configure allowed origins in corsConfigurationSource)
                .cors(cors -> {})

                // ✅ Stateless session management for JWT
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ✅ Define role-based access rules
                .authorizeHttpRequests(auth -> auth

                        // ✅ Allow preflight OPTIONS requests (needed for CORS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // ✅ Public routes (login, registration, uploaded files)
                        .requestMatchers("/api/auth/**").permitAll()        // <- Important fix: login endpoint must be public
                        .requestMatchers("/uploads/**").permitAll()         // <- Optional: allow serving uploaded images

                        // ✅ Role-based routes
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/user/**").hasRole("USER")
                        .requestMatchers("/api/department/**").hasRole("DEPARTMENT_AUTHORITY")

                        // ❌ Any other request requires authentication
                        .anyRequest().authenticated()
                )

                // ✅ Add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ✅ Global CORS Configuration
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ✅ Only allow frontend origin
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));

        // ✅ Allowed HTTP methods
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // ✅ Allow all headers (Authorization, Content-Type, etc.)
        configuration.setAllowedHeaders(List.of("*"));

        // ✅ Allow sending cookies/credentials (optional for JWT if needed)
        configuration.setAllowCredentials(true);

        // ✅ Register the configuration for all paths
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    // ✅ Password encoder bean (BCrypt)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ✅ AuthenticationManager bean (needed if you want to inject it elsewhere)
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
