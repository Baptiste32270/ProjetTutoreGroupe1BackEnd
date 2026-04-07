package com.isis.archivage.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.isis.archivage.enums.CategorieArchive;
import com.isis.archivage.enums.NiveauAcces;

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
public class DroitAcces {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDroit;

    @Enumerated(EnumType.STRING)
    private CategorieArchive categorie;

    @Enumerated(EnumType.STRING)
    private NiveauAcces niveauAcces;

    @ManyToOne
    @JoinColumn(name = "id_utilisateur")
    @JsonIgnore
    private Utilisateur utilisateur;
}