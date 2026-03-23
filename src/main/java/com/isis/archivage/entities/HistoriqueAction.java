package com.isis.archivage.entities;

import java.time.LocalDateTime;

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
public class HistoriqueAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAction;

    private String typeAction; // ex: "TELECHARGEMENT", "DEPOT"

    private LocalDateTime dateAction; // LocalDateTime est mieux que LocalDate pour avoir l'heure exacte

    private String adresseIp;

    // Relation "fait par" (Lien vers Utilisateur)
    @ManyToOne
    @JoinColumn(name = "id_utilisateur", nullable = false)
    private Utilisateur utilisateur;

    // Relation "concerne" (Lien vers Document)
    @ManyToOne
    @JoinColumn(name = "id_document", nullable = false)
    private Document document;
}