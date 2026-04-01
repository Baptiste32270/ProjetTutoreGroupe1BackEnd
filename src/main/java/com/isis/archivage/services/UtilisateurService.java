package com.isis.archivage.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.isis.archivage.dto.CreationUtilisateurRequest;
import com.isis.archivage.entities.Utilisateur;
import com.isis.archivage.repositories.UtilisateurRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder; // Notre outil pour crypter !

    public Utilisateur creerUtilisateur(CreationUtilisateurRequest request) throws Exception {

        if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new Exception("Un utilisateur avec cet email existe déjà !");
        }

        Utilisateur nouvelUtilisateur = new Utilisateur();
        nouvelUtilisateur.setNom(request.getNom());
        nouvelUtilisateur.setPrenom(request.getPrenom());
        nouvelUtilisateur.setEmail(request.getEmail());

        String motDePasseCrypte = passwordEncoder.encode(request.getMotDePasse());
        nouvelUtilisateur.setMotDePasse(motDePasseCrypte);

        nouvelUtilisateur.setPremiereConnexion(true);

        return utilisateurRepository.save(nouvelUtilisateur);
    }
}