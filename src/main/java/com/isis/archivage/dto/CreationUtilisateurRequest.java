package com.isis.archivage.dto;

import lombok.Data;

@Data
public class CreationUtilisateurRequest {
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
}