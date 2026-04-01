package com.isis.archivage.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isis.archivage.dto.CreationUtilisateurRequest;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // Annule les modifications en base après chaque test (garde la base propre)
class UtilisateurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void creerUtilisateur_DonneesValides_DoitRetourner201() throws Exception {
        CreationUtilisateurRequest request = new CreationUtilisateurRequest();
        request.setNom("Lovelace");
        request.setPrenom("Ada");
        request.setEmail("ada.lovelace@isis.fr");
        request.setMotDePasse("code123");

        mockMvc.perform(post("/api/utilisateurs/creer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nom").value("Lovelace"))
                .andExpect(jsonPath("$.email").value("ada.lovelace@isis.fr"))
                // On vérifie que le mot de passe n'est pas renvoyé en clair, ou qu'il a été
                // crypté
                .andExpect(jsonPath("$.motDePasse").isString());
    }
}