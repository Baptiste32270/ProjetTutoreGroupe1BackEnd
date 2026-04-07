package com.isis.archivage.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isis.archivage.entities.BoiteArchive;

@Repository
public interface BoiteArchiveRepository extends JpaRepository<BoiteArchive, Long> {
    Optional<BoiteArchive> findByNomBoite(String nomBoite);
}