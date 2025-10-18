package com.savebot.service;

import net.sourceforge.tess4j.Tesseract;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.nio.file.Files;

import javax.imageio.ImageIO;

@Service
public class OcrService {

    private static final int MAX_OCR_CHARS = 10_000; // safety cap

    public String extractText(File file) {
        try {
            String ext = FilenameUtils.getExtension(file.getName()).toLowerCase();
            switch (ext) {
                case "pdf":
                    return extractFromPdfWithFallback(file);
                case "png":
                case "jpg":
                case "jpeg":
                case "tif":
                case "tiff":
                    return extractFromImage(file);
                default:
                    // treat as text — helpful for .txt uploads
                    return cap(Files.readString(file.toPath()));
            }
        } catch (Throwable t) { // never let OCR crash the request
            t.printStackTrace();
            return "";
        }
    }

    private String extractFromPdfWithFallback(File file) throws Exception {
        try (PDDocument doc = PDDocument.load(file)) {
            // 1) Try direct text extraction (real PDFs)
            String text = new PDFTextStripper().getText(doc);
            if (text != null && text.strip().length() >= 20) {
                return cap(text);
            }

            // 2) Fallback: render + OCR (scanned PDFs)
            StringBuilder ocrAll = new StringBuilder();
            PDFRenderer renderer = new PDFRenderer(doc);
            for (int i = 0; i < doc.getNumberOfPages(); i++) {
                BufferedImage img = renderer.renderImageWithDPI(i, 300); // good balance for receipts
                String pageText = doTesseract(img);
                if (pageText != null && !pageText.isBlank()) {
                    ocrAll.append(pageText).append('\n');
                }
            }
            return cap(ocrAll.toString());
        }
    }

    private String extractFromImage(File file) {
        try {
            BufferedImage raw = ImageIO.read(file);
            if (raw == null) return "";
            BufferedImage prepped = preprocess(raw);
            String out = mkTesseract().doOCR(prepped);
            return cap(out);
        } catch (Throwable e) {
            System.err.println("Image OCR failed: " + e.getMessage());
            return "";
        }
    }

    private String doTesseract(BufferedImage img) {
        try {
            BufferedImage prepped = preprocess(img);
            String out = mkTesseract().doOCR(prepped);
            return cap(out);
        } catch (Throwable e) {
            System.err.println("BufferedImage OCR failed: " + e.getMessage());
            return "";
        }
    }

    // grayscale + light contrast/brightness boost improves OCR on receipts
    private BufferedImage preprocess(BufferedImage src) {
        // 1) grayscale
        BufferedImage gray = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = gray.createGraphics();
        g.drawImage(src, 0, 0, null);
        g.dispose();

        // 2) gentle contrast/brightness tweak
        RescaleOp rescale = new RescaleOp(1.2f, 5f, null); // scale, offset
        BufferedImage boosted = new BufferedImage(gray.getWidth(), gray.getHeight(), gray.getType());
        rescale.filter(gray, boosted);
        return boosted;
    }

    // trim giant outputs & normalize whitespace
    private String cap(String s) {
        if (s == null) return "";
        String normalized = s.replaceAll("[ \\t\\x0B\\f\\r]+", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
        if (normalized.length() > MAX_OCR_CHARS) {
            return normalized.substring(0, MAX_OCR_CHARS) + "...";
        }
        return normalized;
    }

    /** Build a resilient Tesseract configured for macOS/Homebrew. */
    private Tesseract mkTesseract() {
        Tesseract t = new Tesseract();
        t.setLanguage("eng");
        t.setPageSegMode(6);   // assume a block of text (good for receipts)
        t.setOcrEngineMode(1); // LSTM

        // Resolve tessdata path
        String env = System.getenv("TESSDATA_PREFIX");
        String datapath = null;
        String[] candidates = {
                env,
                "/opt/homebrew/share/tessdata",        // Apple Silicon (brew)
                "/usr/local/share/tessdata",           // Intel (brew)
                "/opt/homebrew/Cellar/tesseract/5.5.1/share/tessdata" // example fallback
        };
        for (String c : candidates) {
            if (c == null || c.isBlank()) continue;
            File f = new File(c);
            if (new File(f, "eng.traineddata").exists()) { datapath = f.getAbsolutePath(); break; }
            if (new File(f, "tessdata/eng.traineddata").exists()) {
                datapath = new File(f, "tessdata").getAbsolutePath(); break;
            }
        }
        if (datapath == null) datapath = "/opt/homebrew/share/tessdata";

        System.out.println("[OCR] Using tessdata at: " + datapath);
        t.setDatapath(datapath);
        return t;
    }
}
