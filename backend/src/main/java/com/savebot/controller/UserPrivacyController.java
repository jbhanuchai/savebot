package com.savebot.controller;

import com.savebot.controller.dto.ConsentUpdateRequest;
import com.savebot.controller.dto.UserResponse;
import com.savebot.model.User;
import com.savebot.service.PrivacyService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserPrivacyController {

    private final PrivacyService privacy;

    public UserPrivacyController(PrivacyService privacy) {
        this.privacy = privacy;
    }

    // PUT /users/{id}/consent  -> toggle consentAutoNegotiate
    @PutMapping("/{id}/consent")
    public ResponseEntity<UserResponse> updateConsent(@PathVariable Long id,
                                                      @Valid @RequestBody ConsentUpdateRequest req) {
        User u = privacy.updateConsent(id, req);
        return ResponseEntity.ok(new UserResponse(
                u.getId(), u.getName(), u.getEmail(), u.getRoles(),
                u.isConsentAutoNegotiate(), u.getCreatedAt()
        ));
    }

    // DELETE /users/{id} -> hard delete (self or ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        privacy.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
