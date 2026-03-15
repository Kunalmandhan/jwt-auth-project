package com.example.jwtauth;

import com.example.jwtauth.model.Role;
import com.example.jwtauth.model.User;
import com.example.jwtauth.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class JwtAuthApplication {

    public static void main(String[] args) {
        SpringApplication.run(JwtAuthApplication.class, args);
    }

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                userRepository.save(User.builder()
                        .username("admin").password(passwordEncoder.encode("admin123"))
                        .email("admin@example.com").role(Role.ADMIN).build());
                userRepository.save(User.builder()
                        .username("user").password(passwordEncoder.encode("user123"))
                        .email("user@example.com").role(Role.USER).build());

                System.out.println("===========================================");
                System.out.println("  Demo users created:");
                System.out.println("  admin / admin123  (role: ADMIN)");
                System.out.println("  user  / user123   (role: USER)");
                System.out.println("===========================================");
            }
        };
    }
}
