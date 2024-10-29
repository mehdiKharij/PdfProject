package com.example.pdfproject;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@Service
public class PdfSouscription {

    @Autowired
    private com.example.pdfproject.ForfaitRepository forfaitRepository;

    // Cache for PDF template
    private byte[] cachedTemplate;

    public byte[] generatePdf(int forfaitId) {
        Instant start = Instant.now();

        // Ensure we are using the correct Forfait type
        Optional<Forfait> forfaitOpt = forfaitRepository.findById(forfaitId);

        if (forfaitOpt.isEmpty()) {
            System.err.println("Forfait non trouvé pour ID: " + forfaitId);
            throw new RuntimeException("Forfait non trouvé");
        }

        Forfait forfait = forfaitOpt.get();
        System.out.println("Forfait trouvé: " + forfait);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDateNaissance = (forfait.getDateNaissance() != null) ? dateFormat.format(forfait.getDateNaissance()) : "";

        Map<String, String> data = Map.ofEntries(
                Map.entry("nom_prenom", forfait.getNomPrenom()),
                Map.entry("ddn", formattedDateNaissance),
                Map.entry("ldn", forfait.getLieuNaissance()),
                Map.entry("Taux", forfait.getTaux() != null ? forfait.getTaux().toString() : ""),
                Map.entry("services", forfait.getServices()),
                Map.entry("Classique", "Classique".equals(forfait.getTypeCarte()) ? "Yes" : "Off"),
                Map.entry("Platinium", "Platinium".equals(forfait.getTypeCarte()) ? "Yes" : "Off"),
                Map.entry("Gold", "Gold".equals(forfait.getTypeCarte()) ? "Yes" : "Off"),
                Map.entry("Maried", "Maried".equals(forfait.getStatut()) ? "Yes" : "Off"),
                Map.entry("divorced", "Divorced".equals(forfait.getStatut()) ? "Yes" : "Off"),
                Map.entry("single", "Single".equals(forfait.getStatut()) ? "Yes" : "Off"),
                Map.entry("Veuf", "Veuf".equals(forfait.getStatut()) ? "Yes" : "Off"),
                Map.entry("chequie", "chequie".equals(forfait.getServices()) ? "Yes" : "Off"),
                Map.entry("paiement", "paiement".equals(forfait.getServices()) ? "Yes" : "Off"),
                Map.entry("releves", "releves".equals(forfait.getServices()) ? "Yes" : "Off"),
                Map.entry("distribu", "distribu".equals(forfait.getServices()) ? "Yes" : "Off"),
                Map.entry("assurance", "assurance".equals(forfait.getServices()) ? "Yes" : "Off")
        );

        System.out.println("Données à insérer dans le PDF: " + data);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             InputStream templateStream = getTemplateStream();
             PdfReader reader = new PdfReader(templateStream);
             PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdfDoc = new PdfDocument(reader, writer)) {

            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            Map<String, PdfFormField> fields = form.getFormFields();

            data.forEach((key, value) -> {
                PdfFormField field = fields.get(key);
                if (field != null) {
                    // Check if the field is a checkbox
                    if ("Yes".equals(value) || "Off".equals(value)) {
                        field.setValue(value);
                    } else {
                        field.setValue(value);
                    }
                } else {
                    System.err.println("Champ non trouvé dans le formulaire: " + key);
                }
            });

            form.flattenFields();

            pdfDoc.close();
            byte[] pdfBytes = outputStream.toByteArray();

            Instant end = Instant.now();
            Duration duration = Duration.between(start, end);
            System.out.println("Temps de génération du PDF: " + duration.toMillis() + " ms");

            return pdfBytes;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    private InputStream getTemplateStream() throws IOException {
        if (cachedTemplate == null) {
            cachedTemplate = new ClassPathResource("Forfait.pdf").getInputStream().readAllBytes();
        }
        return new ByteArrayInputStream(cachedTemplate);
    }
}
