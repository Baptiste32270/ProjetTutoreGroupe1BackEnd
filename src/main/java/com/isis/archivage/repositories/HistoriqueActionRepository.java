package com.isis.archivage.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isis.archivage.entities.HistoriqueAction;

@Repository
public interface HistoriqueActionRepository extends JpaRepository<HistoriqueAction, Long> {

    List<HistoriqueAction> findByDocument_IdDocument(Long idDocument);

    List<HistoriqueAction> findByUtilisateur_IdUtilisateur(Long idUtilisateur);
}