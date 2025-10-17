package com.savebot.service;

import com.savebot.controller.dto.LoginRequest;
import com.savebot.controller.dto.SignupRequest;
import com.savebot.model.User;
import com.savebot.repository.UserRepository;
import com.savebot.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    public AuthService(UserRepository repo, PasswordEncoder encoder, JwtUtil jwt) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public void register(SignupRequest req) {
        if (repo.findByEmail(req.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        User u = new User();
        u.setEmail(req.getEmail());
        u.setName(req.getName());
        u.setPasswordHash(encoder.encode(req.getPassword())); // store hash
        u.setRoles(Set.of("USER"));
        repo.save(u);
    }

    public String login(LoginRequest req) {
        User u = repo.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!encoder.matches(req.getPassword(), u.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        // include roles if you want; JwtUtil supports both overloads
        return jwt.generateToken(u.getEmail(), u.getRoles());
    }
}
