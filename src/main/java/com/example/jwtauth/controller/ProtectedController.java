package com.example.jwtauth.controller;

import com.example.jwtauth.dto.ApiResponse;
import com.example.jwtauth.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
public class ProtectedController {

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }

    @GetMapping("/api/public/info")
    public ResponseEntity<ApiResponse> publicInfo() {
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Public endpoint – no token required.")
                .data(Map.of("version", "1.0.0", "service", "JWT Auth Demo")).build());
    }

    @GetMapping("/api/protected/profile")
    public ResponseEntity<ApiResponse> getProfile() {
        User user = getCurrentUser();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Profile retrieved successfully")
                .data(Map.of("id", user.getId(), "username", user.getUsername(),
                             "email", user.getEmail(), "role", user.getRole().name())).build());
    }

    @GetMapping("/api/protected/dashboard")
    public ResponseEntity<ApiResponse> getDashboard() {
        User user = getCurrentUser();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Welcome to the dashboard!")
                .data(Map.of("username", user.getUsername(), "role", user.getRole().name(),
                             "message", "You have successfully accessed a protected route.")).build());
    }

    @GetMapping("/api/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> adminOnly() {
        User user = getCurrentUser();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Admin area accessed successfully")
                .data(Map.of("adminUser", user.getUsername(),
                             "message", "You have ADMIN privileges.")).build());
    }
}
