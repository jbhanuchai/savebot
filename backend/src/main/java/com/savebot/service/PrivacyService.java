package com.savebot.service;

import com.savebot.controller.dto.ConsentUpdateRequest;
import com.savebot.model.User;
import com.savebot.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PrivacyService {

    private final UserRepository repo;

    public PrivacyService(UserRepository repo) {
        this.repo = repo;
    }

    public User updateConsent(Long id, ConsentUpdateRequest req) {
        User target = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ensureSelfOrAdmin(target);
        target.setConsentAutoNegotiate(Boolean.TRUE.equals(req.getConsentAutoNegotiate()));
        return repo.save(target);
    }

    public void deleteUser(Long id) {
        User target = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        ensureSelfOrAdmin(target);
        repo.deleteById(id); // hard delete for privacy
    }

    private void ensureSelfOrAdmin(User target) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String currentEmail = auth.getPrincipal().toString();
        User actor = repo.findByEmail(currentEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        boolean isAdmin = actor.getRoles() != null && actor.getRoles().contains("ADMIN");
        boolean isSelf = actor.getId().equals(target.getId());

        if (!isAdmin && !isSelf) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }
    }
}
