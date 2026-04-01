package com.isis.archivage.services;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class QrCodeServiceTest {

    private final QrCodeService qrCodeService = new QrCodeService();

    @Test
    void genererQrCodeImage_TexteValide_DoitRetournerImageBytes() throws Exception {
        // Exécution
        byte[] imageBytes = qrCodeService.genererQrCodeImage("https://isis.fr/test", 200, 200);

        // Vérification
        assertNotNull(imageBytes, "L'image générée ne doit pas être nulle");
        assertTrue(imageBytes.length > 0, "Le tableau d'octets de l'image ne doit pas être vide");
    }
}