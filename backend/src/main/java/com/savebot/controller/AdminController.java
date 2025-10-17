package com.savebot.controller;

import com.savebot.controller.dto.UserResponse;
import com.savebot.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService admin;

    public AdminController(AdminService admin) { this.admin = admin; }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> allUsers() {
        return ResponseEntity.ok(admin.listUsers());
    }

    @PostMapping("/users/{id}/promote")
    public ResponseEntity<UserResponse> promote(@PathVariable Long id) {
        return ResponseEntity.ok(admin.promoteToAdmin(id));
    }

    @PostMapping("/users/{id}/demote")
    public ResponseEntity<UserResponse> demote(@PathVariable Long id) {
        return ResponseEntity.ok(admin.demoteAdmin(id));
    }
}
