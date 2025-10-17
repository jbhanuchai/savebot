package com.savebot.controller.dto;

import java.time.Instant;
import java.util.Set;

public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private Set<String> roles;
    private boolean consentAutoNegotiate;
    private Instant createdAt;

    public UserResponse(Long id, String fullName, String email, Set<String> roles,
                        boolean consentAutoNegotiate, Instant createdAt) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.roles = roles;
        this.consentAutoNegotiate = consentAutoNegotiate;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public Set<String> getRoles() { return roles; }
    public boolean isConsentAutoNegotiate() { return consentAutoNegotiate; }
    public Instant getCreatedAt() { return createdAt; }
}
