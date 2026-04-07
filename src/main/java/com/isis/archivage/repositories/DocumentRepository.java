package com.isis.archivage.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isis.archivage.entities.Document;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    // EX-F-06 : Gérer les doublons au moment de l'upload
    boolean existsByNomFichier(String nomFichier);

    // EX-F-03 : Moteur de recherche (Exemple : Recherche par titre contenant un
    // mot)
    List<Document> findByTitreContainingIgnoreCase(String titre);

    List<Document> findByAuteur_NomAndAuteur_Prenom(String nom, String prenom);
}