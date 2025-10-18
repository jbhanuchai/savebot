package com.savebot.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "bills", indexes = {
        @Index(name = "idx_bills_user_id", columnList = "user_id"),
        @Index(name = "idx_bills_due_date", columnList = "dueDate")
})
public class Bill {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // owner of this bill
    @Column(name = "user_id", nullable = false)
    private Long userId;

    // extracted fields
    private String provider;

    @Column(precision = 14, scale = 2)
    private BigDecimal amount;

    private LocalDate dueDate;

    // metadata
    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false, updatable = false)
    private Instant uploadedAt;

    @Column(name = "ocr_text", columnDefinition = "text")
    private String ocrText;

    @PrePersist
    public void onCreate() {
        if (uploadedAt == null) uploadedAt = Instant.now();
    }

    // getters/setters
    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public String getOriginalFilename() { return originalFilename; }
    public void setOriginalFilename(String originalFilename) { this.originalFilename = originalFilename; }
    public Instant getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; }
    public String getOcrText() { return ocrText; }
    public void setOcrText(String ocrText) { this.ocrText = ocrText; }
}
