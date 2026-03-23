package com.isis.archivage.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isis.archivage.entities.Emplacement;

@Repository
public interface EmplacementRepository extends JpaRepository<Emplacement, Long> {
    Optional<Emplacement> findByNumeroArmoire(String numeroArmoire);
}