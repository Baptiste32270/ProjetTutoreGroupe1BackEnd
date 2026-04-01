package com.isis.archivage.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isis.archivage.dto.LoginRequest;
import com.isis.archivage.entities.Utilisateur;
import com.isis.archivage.repositories.UtilisateurRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc // Permet de simuler des requêtes HTTP
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        utilisateurRepository.deleteAll();

        Utilisateur admin = new Utilisateur();
        admin.setNom("Admin");
        admin.setEmail("admin@isis.fr");
        admin.setMotDePasse(passwordEncoder.encode("secret123"));
        admin.setPremiereConnexion(true);
        utilisateurRepository.save(admin);
    }

    @Test
    void connexion_AvecBonsIdentifiants_DoitRetournerToken() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@isis.fr");
        request.setMotDePasse("secret123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void connexion_AvecMauvaisMotDePasse_DoitEchouer() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("admin@isis.fr");
        request.setMotDePasse("mauvais_mot_de_passe");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}