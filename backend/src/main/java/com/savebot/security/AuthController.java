package com.savebot.security;

import com.savebot.controller.dto.AuthResponse;
import com.savebot.controller.dto.LoginRequest;
import com.savebot.controller.dto.SignupRequest;
import com.savebot.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest req) {
        auth.register(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        String token = auth.login(req);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
