package com.savebot.service;

import com.savebot.model.Bill;
import com.savebot.model.User;
import com.savebot.repository.BillRepository;
import com.savebot.repository.UserRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class BillService {

    private final OcrService ocr;
    private final BillParsingService parser;
    private final BillRepository billRepo;
    private final UserRepository userRepo;

    public BillService(OcrService ocr, BillParsingService parser, BillRepository billRepo, UserRepository userRepo) {
        this.ocr = ocr;
        this.parser = parser;
        this.billRepo = billRepo;
        this.userRepo = userRepo;
    }

    public Bill processUpload(String userEmail, MultipartFile upload) {
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));

        File tmp = null;
        try {
            String ext = FilenameUtils.getExtension(upload.getOriginalFilename());
            tmp = File.createTempFile("bill-", ext != null && !ext.isBlank() ? "." + ext : ".bin");
            upload.transferTo(tmp);

            String text = ocr.extractText(tmp);
            String provider = parser.guessProvider(text);
            BigDecimal amount = parser.parseAmount(text);
            LocalDate due = parser.parseDueDate(text);

            Bill b = new Bill();
            b.setUserId(user.getId());
            b.setOriginalFilename(upload.getOriginalFilename());
            b.setOcrText(text);
            b.setProvider(provider);
            b.setAmount(amount);
            b.setDueDate(due);

            return billRepo.save(b);
        } catch (ResponseStatusException rse) {
            throw rse;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to process file");
        } finally {
            if (tmp != null && tmp.exists()) tmp.delete();
        }
    }

    public List<Bill> listMyBills(String userEmail) {
        User user = userRepo.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
        return billRepo.findByUserIdOrderByUploadedAtDesc(user.getId());
    }
}
