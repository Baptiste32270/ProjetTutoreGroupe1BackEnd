package com.isis.archivage.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.isis.archivage.dto.CreationUtilisateurRequest;
import com.isis.archivage.entities.DroitAcces;
import com.isis.archivage.entities.Utilisateur;
import com.isis.archivage.services.UtilisateurService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    @PostMapping("/creer")
    public ResponseEntity<Utilisateur> creerUtilisateur(@RequestBody CreationUtilisateurRequest request) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(utilisateurService.creerUtilisateur(request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Utilisateur> getMyProfile(Principal principal) {
        return ResponseEntity.ok(utilisateurService.trouverParEmail(principal.getName()));
    }

    @GetMapping
    public ResponseEntity<List<Utilisateur>> getAllUtilisateurs() {
        try {
            return ResponseEntity.ok(utilisateurService.obtenirTous());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/{id}/droits")
    public ResponseEntity<?> modifierDroits(
            @PathVariable("id") Long id,
            @RequestBody List<DroitAcces> nouveauxDroits) {

        try {
            utilisateurService.modifierDroits(id, nouveauxDroits);
            return ResponseEntity.ok().body("{\"message\": \"Droits mis à jour avec succès\"}");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("{\"erreur\": \"" + e.getMessage() + "\"}");
        }
    }
}