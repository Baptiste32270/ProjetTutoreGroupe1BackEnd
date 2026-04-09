package com.isis.archivage.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isis.archivage.entities.Document;
import com.isis.archivage.enums.StatutDocument; // AJOUT DE L'IMPORT

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    boolean existsByNomFichier(String nomFichier);

    List<Document> findByTitreContainingIgnoreCase(String titre);

    List<Document> findByAuteur_NomAndAuteur_Prenom(String nom, String prenom);

    List<Document> findByStatut(StatutDocument statut);
}