package com.isis.archivage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isis.archivage.entities.DureeConservation;
import com.isis.archivage.enums.CategorieArchive; // À importer si tu lies la durée à tes catégories

import java.util.Optional;
import java.util.List;

@Repository
public interface DureeConservationRepository extends JpaRepository<DureeConservation, Long> {

    Optional<DureeConservation> findByCategorie(CategorieArchive categorie);

    List<DureeConservation> findByDureeAnneesGreaterThanEqual(Integer annees);
}