package com.isis.archivage.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class BoiteArchive {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idBoite;

    private String nomBoite;

    @ManyToOne
    @JoinColumn(name = "id_emplacement")
    private Emplacement emplacement;
}