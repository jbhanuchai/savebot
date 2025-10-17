// src/main/java/com/savebot/controller/AdminController.java
package com.savebot.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/ping")
    @PreAuthorize("hasRole('ADMIN')")
    public String ping() {
        return "pong (admin)";
    }
}
