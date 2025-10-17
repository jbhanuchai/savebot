package com.savebot.controller.dto;

import jakarta.validation.constraints.NotNull;

public class ConsentUpdateRequest {
    @NotNull
    private Boolean consentAutoNegotiate;

    public Boolean getConsentAutoNegotiate() { return consentAutoNegotiate; }
    public void setConsentAutoNegotiate(Boolean consentAutoNegotiate) { this.consentAutoNegotiate = consentAutoNegotiate; }
}
