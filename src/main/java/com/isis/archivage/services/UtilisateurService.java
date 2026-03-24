package com.isis.archivage.services;

import com.isis.archivage.dto.CreationUtilisateurRequest;
import com.isis.archivage.entities.Utilisateur;
import com.isis.archivage.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder; // Notre outil pour crypter !

    public Utilisateur creerUtilisateur(CreationUtilisateurRequest request) throws Exception {

        // 1. Vérifier si l'email est déjà pris
        if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new Exception("Un utilisateur avec cet email existe déjà !");
        }

        // 2. Créer l'entité
        Utilisateur nouvelUtilisateur = new Utilisateur();
        nouvelUtilisateur.setNom(request.getNom());
        nouvelUtilisateur.setPrenom(request.getPrenom());
        nouvelUtilisateur.setEmail(request.getEmail());

        // 3. CRYPTER LE MOT DE PASSE
        String motDePasseCrypte = passwordEncoder.encode(request.getMotDePasse());
        nouvelUtilisateur.setMotDePasse(motDePasseCrypte);

        // 4. Par défaut, c'est sa première connexion
        nouvelUtilisateur.setPremiereConnexion(true);

        // 5. Sauvegarder en base
        return utilisateurRepository.save(nouvelUtilisateur);
    }
}