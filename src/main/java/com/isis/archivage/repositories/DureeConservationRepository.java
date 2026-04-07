package com.isis.archivage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isis.archivage.entities.DureeConservation;
import java.util.List;

@Repository
public interface DureeConservationRepository extends JpaRepository<DureeConservation, Long> {

    List<DureeConservation> findByDureeAnneesGreaterThanEqual(Integer annees);
}