package com.isis.archivage.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isis.archivage.entities.Utilisateur;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    // Spring Boot comprend le nom de la méthode et génère le SQL automatiquement !
    // C'est l'équivalent de : SELECT * FROM utilisateur WHERE email = ?
    Optional<Utilisateur> findByEmail(String email);
}