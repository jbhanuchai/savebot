package com.savebot.controller.dto;

public class UserProfileResponse {
    public Long id;
    public String fullName;
    public String email;

    public UserProfileResponse(Long id, String fullName, String email) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
    }
}
