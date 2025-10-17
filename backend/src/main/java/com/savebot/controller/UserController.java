package com.savebot.controller;

import com.savebot.controller.dto.UserProfileResponse;
import com.savebot.model.User;
import com.savebot.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserRepository repo;

    public UserController(UserRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponse> profile() {
        String email = (String) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        User u = repo.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(new UserProfileResponse(u.getId(), u.getFullName(), u.getEmail()));
    }
}
