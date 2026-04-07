package com.isis.archivage.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isis.archivage.dto.LoginRequest;
import com.isis.archivage.entities.Utilisateur;
import com.isis.archivage.repositories.UtilisateurRepository;
import com.isis.archivage.security.JwtService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AuthController {

    private final UtilisateurRepository utilisateurRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> connexion(@RequestBody LoginRequest requete) {

        // On cherche l'utilisateur dans la base via son email
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(requete.getEmail());

        if (utilisateurOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email incorrect");
        }

        Utilisateur utilisateur = utilisateurOpt.get();

        // On vérifie le mot de passe
        if (!passwordEncoder.matches(requete.getMotDePasse(), utilisateur.getMotDePasse())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mot de passe incorrect");
        }

        String token = jwtService.genererToken(utilisateur);

        Map<String, String> reponse = new HashMap<>();
        reponse.put("token", token);

        return ResponseEntity.ok(reponse);
    }
}