package com.savebot.service;

import com.savebot.controller.dto.UserResponse;
import com.savebot.model.User;
import com.savebot.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AdminService {

    private final UserRepository repo;

    public AdminService(UserRepository repo) { this.repo = repo; }

    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> listUsers() {
        return repo.findAll().stream()
                .map(u -> new UserResponse(
                        u.getId(), u.getName(), u.getEmail(), u.getRoles(),
                        u.isConsentAutoNegotiate(), u.getCreatedAt()))
                .toList();
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse promoteToAdmin(Long id) {
        User u = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        u.getRoles().add("ADMIN");
        repo.save(u);
        return new UserResponse(
                u.getId(), u.getName(), u.getEmail(), u.getRoles(),
                u.isConsentAutoNegotiate(), u.getCreatedAt());
    }

    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse demoteAdmin(Long id) {
        User u = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        u.getRoles().remove("ADMIN");
        repo.save(u);
        return new UserResponse(
                u.getId(), u.getName(), u.getEmail(), u.getRoles(),
                u.isConsentAutoNegotiate(), u.getCreatedAt());
    }
}
