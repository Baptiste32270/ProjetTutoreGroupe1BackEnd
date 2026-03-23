package com.isis.archivage.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isis.archivage.entities.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByNomRole(String nomRole);
}