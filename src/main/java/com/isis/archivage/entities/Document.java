package com.isis.archivage.entities;

import java.time.LocalDate;

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
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDocument;

    private String nomFichier;
    private LocalDate dateDepot;
    private String description;
    private Boolean estNumerise;
    private String titre;

    // Relation "déposé par"
    @ManyToOne
    @JoinColumn(name = "id_utilisateur_auteur", nullable = false)
    private Utilisateur auteur;

    // Relation "est de type"
    @ManyToOne
    @JoinColumn(name = "id_type_document")
    private TypeDocument type;

    // Relation "rangé dans"
    @ManyToOne
    @JoinColumn(name = "id_boite_archive")
    private BoiteArchive boite;

    // Relation "soumis a"
    @ManyToOne
    @JoinColumn(name = "id_duree")
    private DureeConservation dureeConservation;
}