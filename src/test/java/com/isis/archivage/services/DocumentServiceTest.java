package com.isis.archivage.services;

import com.isis.archivage.entities.Document;
import com.isis.archivage.enums.CategorieArchive;
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
        MockMultipartFile fauxFichier = new MockMultipartFile(
                "fichier", "test.pdf", "application/pdf", "Contenu bidon".getBytes());

        when(documentRepository.existsByNomFichier("test.pdf")).thenReturn(true);

        Exception exception = assertThrows(Exception.class, () -> {
            // AJOUT DE LA CATÉGORIE ET DE L'EMAIL ICI
            documentService.uploaderDocument(fauxFichier, "Titre", "Description", CategorieArchive.RH, "test@isis.fr");
        });

        assertEquals("Un fichier nommé test.pdf existe déjà !", exception.getMessage());

        verify(utilisateurRepository, never()).findByEmail(anyString());
        verify(documentRepository, never()).save(any(Document.class));
    }

    @Test
    void uploaderDocument_AuteurIntrouvable_DoitLeverException() {
        MockMultipartFile fauxFichier = new MockMultipartFile(
                "fichier", "nouveau.pdf", "application/pdf", "Contenu bidon".getBytes());

        when(documentRepository.existsByNomFichier(anyString())).thenReturn(false);

        // ON SIMULE LA RECHERCHE PAR EMAIL
        when(utilisateurRepository.findByEmail("test@isis.fr")).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            // AJOUT DE LA CATÉGORIE ET DE L'EMAIL ICI
            documentService.uploaderDocument(fauxFichier, "Titre", "Description", CategorieArchive.RH, "test@isis.fr");
        });

        assertEquals("Utilisateur introuvable", exception.getMessage());
    }
}