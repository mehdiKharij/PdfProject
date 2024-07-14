package com.example.pdfproject;



import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Service
public class PdfService {

    public byte[] generatePdf(Map<String, String> data) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             InputStream templateStream = new ClassPathResource("sous2.pdf").getInputStream();
             PdfReader reader = new PdfReader(templateStream);
             PdfWriter writer = new PdfWriter(outputStream);
             PdfDocument pdfDoc = new PdfDocument(reader, writer)) {

            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            Map<String, PdfFormField> fields = form.getFormFields();


            // Remplir les champs dynamiquement
            data.forEach((key, value) -> {
                if (fields.containsKey(key)) {
                    PdfFormField field = fields.get(key);
                    field.setValue(value);
                }
            });

            form.flattenFields(); // Facultatif : aplatisse les champs pour les rendre non modifiables

            pdfDoc.close();

            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
