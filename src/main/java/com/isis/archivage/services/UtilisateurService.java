package com.isis.archivage.services;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.isis.archivage.dto.CreationUtilisateurRequest;
import com.isis.archivage.entities.DroitAcces;
import com.isis.archivage.entities.Utilisateur;
import com.isis.archivage.enums.CategorieArchive;
import com.isis.archivage.enums.NiveauAcces;
import com.isis.archivage.repositories.UtilisateurRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public Utilisateur creerUtilisateur(CreationUtilisateurRequest request) throws Exception {

        if (utilisateurRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new Exception("Un utilisateur avec cet email existe déjà !");
        }

        Utilisateur nouvelUtilisateur = new Utilisateur();
        nouvelUtilisateur.setNom(request.getNom());
        nouvelUtilisateur.setPrenom(request.getPrenom());
        nouvelUtilisateur.setEmail(request.getEmail());
        nouvelUtilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        nouvelUtilisateur.setPremiereConnexion(true);

        if (request.getDroits() != null) {
            for (Map.Entry<CategorieArchive, NiveauAcces> entry : request.getDroits().entrySet()) {
                DroitAcces droit = new DroitAcces();
                droit.setCategorie(entry.getKey());
                droit.setNiveauAcces(entry.getValue());
                droit.setUtilisateur(nouvelUtilisateur);
                nouvelUtilisateur.getDroitsAcces().add(droit);
            }
        }

        return utilisateurRepository.save(nouvelUtilisateur);
    }

    public Utilisateur trouverParEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'email : " + email));
    }
}