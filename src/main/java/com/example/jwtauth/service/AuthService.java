package com.example.jwtauth.service;

import com.example.jwtauth.dto.*;
import com.example.jwtauth.model.Role;
import com.example.jwtauth.model.TokenBlacklist;
import com.example.jwtauth.model.User;
import com.example.jwtauth.repository.TokenBlacklistRepository;
import com.example.jwtauth.repository.UserRepository;
import com.example.jwtauth.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository,
                       TokenBlacklistRepository tokenBlacklistRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.tokenBlacklistRepository = tokenBlacklistRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new RuntimeException("Username already taken: " + request.getUsername());
        if (userRepository.existsByEmail(request.getEmail()))
            throw new RuntimeException("Email already registered: " + request.getEmail());

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .role(Role.USER)
                .build();
        userRepository.save(user);

        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token).tokenType("Bearer")
                .username(user.getUsername()).role(user.getRole().name())
                .expiresIn(jwtService.getExpirationTime()).build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token).tokenType("Bearer")
                .username(user.getUsername()).role(user.getRole().name())
                .expiresIn(jwtService.getExpirationTime()).build();
    }

    public void logout(String token) {
        tokenBlacklistRepository.save(
                TokenBlacklist.builder().token(token).blacklistedAt(LocalDateTime.now()).build());
    }
}
