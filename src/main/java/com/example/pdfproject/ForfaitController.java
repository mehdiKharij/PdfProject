package com.example.pdfproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/forfaits")
public class ForfaitController {

    @Autowired
    private ForfaitRepository forfaitRepository;

    @Autowired
    private PdfSouscription pdfSouscription;

    // Enregistre un forfait et retourne l'ID généré
    @PostMapping
    public ResponseEntity<?> saveForfait(@RequestBody Forfait forfait) {
        try {
            Forfait savedForfait = forfaitRepository.save(forfait);
            return ResponseEntity.ok(Map.of("id", savedForfait.getId()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la sauvegarde");
        }
    }

    // Génère le PDF pour un forfait donné par son ID
    @GetMapping("/generate/{id}")
    public void generatePdf(@PathVariable int id, HttpServletResponse response) {
        try {
            if (!forfaitRepository.existsById(id)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Forfait non trouvé");
                return;
            }

            byte[] pdfBytes = pdfSouscription.generatePdf(id);
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=forfait_" + id + ".pdf");
            response.setContentLength(pdfBytes.length);
            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur lors de la génération du PDF");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
