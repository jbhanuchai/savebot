package com.savebot.service;

import com.savebot.controller.dto.LoginRequest;
import com.savebot.controller.dto.SignupRequest;
import com.savebot.model.User;
import com.savebot.repository.UserRepository;
import com.savebot.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwt;

    public AuthService(UserRepository repo, PasswordEncoder passwordEncoder, JwtUtil jwt) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.jwt = jwt;
    }

    // register new user (throws if email exists)
    public void register(SignupRequest req) {
        if (repo.existsByEmail(req.email)) {
            throw new IllegalArgumentException("Email already in use");
        }
        User u = new User();
        u.setFullName(req.fullName);
        u.setEmail(req.email);
        u.setPasswordHash(passwordEncoder.encode(req.password)); // BCrypt hash
        u.getRoles().add("USER");
        repo.save(u);
    }

    // authenticate and return JWT
    public String login(LoginRequest req) {
        var u = repo.findByEmail(req.email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(req.password, u.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return jwt.generateToken(u.getEmail(), u.getRoles());
    }

}
