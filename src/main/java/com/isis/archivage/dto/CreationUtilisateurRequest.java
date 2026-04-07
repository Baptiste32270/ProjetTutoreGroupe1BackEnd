package com.isis.archivage.dto;

import java.util.Map;

import com.isis.archivage.enums.CategorieArchive;
import com.isis.archivage.enums.NiveauAcces;

import lombok.Data;

@Data
public class CreationUtilisateurRequest {
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;

    private Map<CategorieArchive, NiveauAcces> droits;
}