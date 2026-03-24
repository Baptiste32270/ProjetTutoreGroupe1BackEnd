package com.isis.archivage.controllers;

import com.isis.archivage.dto.CreationUtilisateurRequest;
import com.isis.archivage.entities.Utilisateur;
import com.isis.archivage.services.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @PostMapping("/creer")
    public ResponseEntity<?> creerUtilisateur(@RequestBody CreationUtilisateurRequest request) {
        try {
            Utilisateur utilisateurCree = utilisateurService.creerUtilisateur(request);
            // On retourne un code 201 (Created)
            return ResponseEntity.status(HttpStatus.CREATED).body(utilisateurCree);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}