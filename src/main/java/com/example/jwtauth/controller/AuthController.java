package com.example.jwtauth.controller;

import com.example.jwtauth.dto.*;
import com.example.jwtauth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody RegisterRequest request) {
        try {
            AuthResponse authResponse = authService.register(request);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true).message("User registered successfully").data(authResponse).build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .success(false).message(e.getMessage()).build());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse authResponse = authService.login(request);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true).message("Login successful").data(authResponse).build());
        } catch (Exception e) {
            return ResponseEntity.status(401).body(ApiResponse.builder()
                    .success(false).message("Invalid username or password").build());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .success(false).message("No token provided").build());
        }
        authService.logout(authHeader.substring(7));
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("Logged out successfully. Token has been invalidated.").build());
    }
}
