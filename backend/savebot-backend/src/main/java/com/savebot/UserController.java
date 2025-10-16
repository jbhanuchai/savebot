package com.savebot;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository repo;
    public UserController(UserRepository repo) { this.repo = repo; }

    @GetMapping
    public List<User> list() { return repo.findAll(); }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody User in) {
        if (in.getName() == null || in.getEmail() == null)
            return ResponseEntity.badRequest().body("name and email are required");
        if (repo.existsByEmail(in.getEmail()))
            return ResponseEntity.status(409).body("email already exists");
        User saved = repo.save(in);
        return ResponseEntity.created(URI.create("/api/users/" + saved.getId())).body(saved);
    }
}
