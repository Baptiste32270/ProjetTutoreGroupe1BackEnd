package com.isis.archivage.services;

import com.isis.archivage.entities.Document;
import com.isis.archivage.entities.Utilisateur;
import com.isis.archivage.repositories.DocumentRepository;
import com.isis.archivage.repositories.HistoriqueActionRepository;
import com.isis.archivage.repositories.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private HistoriqueActionRepository historiqueActionRepository;

    @InjectMocks
    private DocumentService documentService;

    @Test
    void uploaderDocument_FichierExistant_DoitLeverException() {
        // Préparation d'un faux fichier
        MockMultipartFile fauxFichier = new MockMultipartFile(
                "fichier", "test.pdf", "application/pdf", "Contenu bidon".getBytes());

        // Simulation : le repository dit que le nom existe déjà
        when(documentRepository.existsByNomFichier("test.pdf")).thenReturn(true);

        // Exécution & Vérification
        Exception exception = assertThrows(Exception.class, () -> {
            documentService.uploaderDocument(fauxFichier, "Titre", "Description", 1L);
        });

        assertEquals("Un fichier nommé test.pdf existe déjà dans le système !", exception.getMessage());

        // On s'assure qu'on a jamais essayé de chercher l'utilisateur ou de sauvegarder
        verify(utilisateurRepository, never()).findById(anyLong());
        verify(documentRepository, never()).save(any(Document.class));
    }

    @Test
    void uploaderDocument_AuteurIntrouvable_DoitLeverException() {
        MockMultipartFile fauxFichier = new MockMultipartFile(
                "fichier", "nouveau.pdf", "application/pdf", "Contenu bidon".getBytes());

        when(documentRepository.existsByNomFichier(anyString())).thenReturn(false);
        // Simulation : l'utilisateur n'existe pas en base
        when(utilisateurRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            documentService.uploaderDocument(fauxFichier, "Titre", "Description", 1L);
        });

        assertEquals("Utilisateur introuvable", exception.getMessage());
    }
}