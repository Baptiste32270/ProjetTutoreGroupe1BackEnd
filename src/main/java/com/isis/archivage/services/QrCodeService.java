package com.isis.archivage.services;

import java.io.ByteArrayOutputStream;

import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

@Service
public class QrCodeService {

    /**
     * Génère une image PNG d'un QR Code à partir d'un texte.
     * 
     * @param texte   Le contenu qui sera caché dans le QR Code (ex: ID du document
     *                ou nom de la boîte)
     * @param largeur Largeur de l'image en pixels
     * @param hauteur Hauteur de l'image en pixels
     * @return L'image sous forme de tableau d'octets (byte[]) prêt à être envoyé
     *         sur le web
     */
    public byte[] genererQrCodeImage(String texte, int largeur, int hauteur) throws Exception {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(texte, BarcodeFormat.QR_CODE, largeur, hauteur);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }
}