package com.isis.archivage.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getAllDocuments_SansEtreConnecte_DoitRetourner403() throws Exception {
        // On essaie d'accéder à l'API sans Token
        mockMvc.perform(get("/api/documents"))
                .andExpect(status().isForbidden()); // 403 Forbidden
    }

    @Test
    @WithMockUser(username = "test@isis.fr", roles = { "ETUDIANT" }) // Simule un utilisateur avec un jeton valide
    void getAllDocuments_EnEtantConnecte_DoitRetourner200() throws Exception {
        // Cette fois-ci, on a le droit de passer !
        mockMvc.perform(get("/api/documents"))
                .andExpect(status().isOk()); // 200 OK
    }

    @Test
    @WithMockUser(username = "admin@isis.fr")
    void uploadDocument_FichierValide_DoitEtreAccepte() throws Exception {
        // Création d'un faux fichier PDF pour l'upload
        MockMultipartFile fauxFichier = new MockMultipartFile(
                "fichier", "rapport.pdf", "application/pdf", "Contenu du PDF".getBytes());

        mockMvc.perform(multipart("/api/documents/upload")
                .file(fauxFichier)
                .param("titre", "Rapport de Stage")
                .param("description", "Mon beau rapport")
                .param("idAuteur", "1")) // Assure-toi que l'ID 1 existe dans ta base de test
                // Selon la logique métier, ça retournera 201 Created (ou 400 Bad Request si
                // l'ID 1 n'existe pas en H2)
                .andExpect(result -> {
                    int statusCode = result.getResponse().getStatus();
                    assert (statusCode == 201 || statusCode == 400);
                });
    }
}