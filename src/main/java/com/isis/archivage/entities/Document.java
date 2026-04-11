package com.isis.archivage.entities;

import java.time.LocalDateTime;

import com.isis.archivage.enums.CategorieArchive;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDocument;

    private String nomFichier;
    private String titre;
    private String description;
    private LocalDateTime dateDepot;
    private boolean estNumerise;

    @Enumerated(EnumType.STRING)
    private CategorieArchive categorie;

    @ManyToOne
    @JoinColumn(name = "id_utilisateur_auteur")
    private Utilisateur auteur;
}