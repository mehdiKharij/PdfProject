package com.example.pdfproject;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/pdf")
public class PdfGenerationController {

    // Chemin vers votre modèle PDF avec les champs de formulaire
    private static final String TEMPLATE_PDF_PATH = "src/main/resources/modele_standard.pdf";

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generatePdf(@RequestParam Map<String, String> formData) {
        try {
            // Générer le PDF avec les données reçues
            ByteArrayInputStream pdfStream = fillPdfTemplate(formData);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=filled_template.pdf");

            return new ResponseEntity<>(pdfStream.readAllBytes(), headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    private ByteArrayInputStream fillPdfTemplate(Map<String, String> formData) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Charger le modèle PDF
        PdfReader pdfReader = new PdfReader(TEMPLATE_PDF_PATH);
        PdfWriter pdfWriter = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(pdfReader, pdfWriter);

        // Accéder aux champs de formulaire du modèle PDF
        PdfAcroForm pdfAcroForm = PdfAcroForm.getAcroForm(pdfDoc, true);

        // Définir la couleur de fond des champs de formulaire
        DeviceRgb whiteColor = new DeviceRgb(255, 255, 255);

        // Remplir le champ "Title" si disponible
        String title = formData.get("title");
        if (title != null) {
            PdfFormField titleField = pdfAcroForm.getField("Title");
            if (titleField != null) {
                titleField.setValue(title);
                // Définir la couleur de fond sur blanc pour le champ Title
                titleField.setBackgroundColor(whiteColor);
            }
        }

        // Initialiser les index pour les champs key et value
        int keyIndex = 1;
        int valueIndex = 1;

        // Remplir les champs de formulaire avec les données du formulaire
        for (Map.Entry<String, String> entry : formData.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (key.equals("title")) {
                continue; // Éviter de réajouter le titre
            }

            // Remplir les champs key comme key1, key2, etc.
            if (!key.equals("title")) {
                String keyFieldName = "key" + keyIndex;
                PdfFormField keyField = pdfAcroForm.getField(keyFieldName);
                if (keyField != null) {
                    keyField.setValue(key + ":"); // Ajouter les deux-points après la valeur
                }

                keyIndex++;

                // Remplir les champs value comme value1, value2, etc.
                String valueFieldName = "value" + valueIndex;
                PdfFormField valueField = pdfAcroForm.getField(valueFieldName);
                if (valueField != null) {
                    valueField.setValue(value);
                }

                valueIndex++;
            }
        }

        // Aplatir les champs de formulaire pour rendre le PDF non modifiable
        pdfAcroForm.flattenFields();

        // Fermer le document PDF
        pdfDoc.close();

        return new ByteArrayInputStream(out.toByteArray());
    }
}
