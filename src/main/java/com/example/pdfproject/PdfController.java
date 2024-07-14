package com.example.pdfproject;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @GetMapping("/generate/test")
    public ResponseEntity<byte[]> generatePdfTest() {
        Map<String, String> data = new HashMap<>();
        data.put("Txt1", "Jean Dupont");
        data.put("Txt2", "1234.56");
        data.put("Txt3", "01/07/2024");
        data.put("Txt4", "123 Rue Exemple, Ville, Pays");

        byte[] pdfBytes = pdfService.generatePdf(data);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=document.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}

