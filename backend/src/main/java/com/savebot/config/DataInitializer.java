package com.savebot.config;

import com.savebot.model.User;
import com.savebot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import java.util.Set;

@Component
public class DataInitializer {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    @Value("${app.admin.email}")
    private String adminEmail;
    @Value("${app.admin.password}")
    private String adminPassword;
    @Value("${app.admin.full-name:Admin}")
    private String adminFullName;

    public DataInitializer(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo; this.encoder = encoder;
    }

    @PostConstruct
    public void ensureAdmin() {
        repo.findByEmail(adminEmail).ifPresentOrElse(
                u -> {
                    // ensure ADMIN in roles
                    if (u.getRoles() == null || !u.getRoles().contains("ADMIN")) {
                        u.getRoles().add("ADMIN");
                        repo.save(u);
                    }
                },
                () -> {
                    User admin = new User();
                    admin.setEmail(adminEmail);
                    admin.setName(adminFullName);
                    admin.setPasswordHash(encoder.encode(adminPassword));
                    admin.setRoles(Set.of("ADMIN")); // admin only
                    repo.save(admin);
                }
        );
    }
}
