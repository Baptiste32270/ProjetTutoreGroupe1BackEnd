package com.isis.archivage.services;

import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final QrCodeService qrCodeService;

    // Génère un fichier PDF contenant l'étiquette et le QR Code à imprimer
    public byte[] genererEtiquettePdf(com.isis.archivage.entities.Document documentIsis) throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        com.lowagie.text.Document pdfDoc = new com.lowagie.text.Document();
        PdfWriter.getInstance(pdfDoc, out);

        pdfDoc.open();

        Font policeTitre = new Font(Font.HELVETICA, 18, Font.BOLD);
        Paragraph titre = new Paragraph("ÉTIQUETTE D'ARCHIVAGE ISIS", policeTitre);
        titre.setAlignment(Element.ALIGN_CENTER);
        titre.setSpacingAfter(20);
        pdfDoc.add(titre);

        pdfDoc.add(new Paragraph("ID Archive : " + documentIsis.getIdDocument()));
        pdfDoc.add(new Paragraph("Titre : " + documentIsis.getTitre()));
        pdfDoc.add(new Paragraph("Date de dépôt : " + documentIsis.getDateDepot()));

        String urlFrontend = "http://localhost:5173/documents/details/" + documentIsis.getIdDocument();
        byte[] imageQrCode = qrCodeService.genererQrCodeImage(urlFrontend, 200, 200);

        Image imagePdf = Image.getInstance(imageQrCode);
        imagePdf.setAlignment(Element.ALIGN_CENTER);
        imagePdf.setSpacingBefore(30);
        pdfDoc.add(imagePdf);

        pdfDoc.close();

        return out.toByteArray();
    }
}