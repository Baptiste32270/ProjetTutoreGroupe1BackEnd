package com.isis.archivage.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isis.archivage.entities.TypeDocument;

@Repository
public interface TypeDocumentRepository extends JpaRepository<TypeDocument, Long> {
    Optional<TypeDocument> findByLibelle(String libelle);
}