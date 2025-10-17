package com.savebot.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignupRequest {
    @NotBlank @Size(min = 2, max = 80)
    public String fullName;

    @NotBlank @Email
    public String email;

    @NotBlank @Size(min = 8, max = 100)
    public String password;
}
