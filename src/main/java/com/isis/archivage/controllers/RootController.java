package com.isis.archivage.controllers;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping("/")
    public Map<String, String> index() {
        return Map.of(
                "status", "online",
                "message", "API Archivage Groupe 1 opérationnelle",
                "swagger", "/swagger-ui.html");
    }
}