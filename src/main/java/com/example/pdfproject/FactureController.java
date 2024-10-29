package com.example.pdfproject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/factures")
@CrossOrigin(origins = "http://localhost:3000") // Remplacez par l'URL de votre frontend
public class FactureController {

    @Autowired
    private ForfaitRepository forfaitRepository;

    @Autowired
    private PdfSouscription pdfSouscription;

    @PostMapping
    public ResponseEntity<?> generateFacture(@RequestBody Forfait forfait) {
        try {
            // Sauvegarder ou traiter le forfait ici, si nécessaire
            Forfait savedForfait = forfaitRepository.save(forfait);

            // Générer le PDF à partir de l'ID du forfait
            byte[] pdfBytes = pdfSouscription.generatePdf(savedForfait.getId());

            // Vous pouvez éventuellement renvoyer le PDF directement ou un message de succès
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "attachment; filename=facture.pdf")
                    .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la génération de la facture : " + e.getMessage());
        }
    }
}
