package com.isis.archivage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isis.archivage.entities.DureeConservation;

@Repository
public interface DureeConservationRepository extends JpaRepository<DureeConservation, Long> {
    // Les méthodes CRUD de base (save, findAll, delete) suffisent ici en général
}