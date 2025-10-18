package com.savebot.controller;

import com.savebot.model.Bill;
import com.savebot.service.BillService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/bills")
public class BillController {

    private final BillService bills;

    public BillController(BillService bills) { this.bills = bills; }

    // POST /bills/upload (multipart/form-data)
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(Authentication auth,
                                    @RequestParam("file") MultipartFile file) {
        // 1) basic checks
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is required"));
        }

        // 2) content-type allowlist (pdf + images)
        String ct = Optional.ofNullable(file.getContentType()).orElse("");
        boolean allowed = ct.equalsIgnoreCase("application/pdf") || ct.toLowerCase().startsWith("image/");
        if (!allowed) {
            return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(Map.of("error", "Only PDF and image files are allowed"));
        }

        try {
            String email = auth.getName();                 // current user
            Bill saved = bills.processUpload(email, file); // keep your service signature

            boolean ocrEmpty = saved.getOcrText() == null || saved.getOcrText().isBlank();
            return ResponseEntity.ok()
                    .header("X-OCR-Empty", String.valueOf(ocrEmpty))
                    .body(saved);

        } catch (MaxUploadSizeExceededException ex) {
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                    .body(Map.of("error", "File too large"));
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Upload failed"));
        }
    }

    // GET /bills (list for current user)
    @GetMapping
    public ResponseEntity<List<Bill>> myBills(Authentication auth) {
        String email = auth.getName();
        return ResponseEntity.ok(bills.listMyBills(email));
    }
}
