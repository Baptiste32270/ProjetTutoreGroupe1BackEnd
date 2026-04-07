package com.isis.archivage.controllers;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping
    public Map<String, Object> getApiEntryPoint() {
        Map<String, Object> api = new LinkedHashMap<>();

        api.put("nom", "API Système d'Archivage - Groupe 1");
        api.put("version", "v1.0.0");
        api.put("description", "Point d'entrée principal de l'API REST");

        Map<String, String> ressources = new LinkedHashMap<>();
        ressources.put("authentification", "/api/auth/login");
        ressources.put("documents", "/api/documents");
        ressources.put("utilisateurs", "/api/utilisateurs/creer");
        ressources.put("documentation_swagger", "/swagger-ui.html");

        api.put("endpoints", ressources);

        return api;
    }
}