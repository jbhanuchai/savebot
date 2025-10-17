package com.savebot.service;

import com.savebot.controller.dto.LoginRequest;
import com.savebot.controller.dto.SignupRequest;
import com.savebot.model.User;
import com.savebot.repository.UserRepository;
import com.savebot.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    // If your controller calls this "signup", you can keep the name `register` or rename it.
    public void register(SignupRequest req) {
        if (repo.findByEmail(req.getEmail()).isPresent()) {
            // 409 Conflict for duplicate email
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        User u = new User();
        u.setEmail(req.getEmail());
        u.setName(req.getName());
        u.setPasswordHash(encoder.encode(req.getPassword())); // store hash
        u.setRoles(Set.of("USER"));
        repo.save(u);
    }

    public String login(LoginRequest req) {
        // 401 Unauthorized for bad credentials
        User u = repo.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!encoder.matches(req.getPassword(), u.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return jwt.generateToken(u.getEmail(), u.getRoles());
    }
}
