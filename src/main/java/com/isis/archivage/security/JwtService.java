package com.isis.archivage.security;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Service;

import com.isis.archivage.entities.Utilisateur;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    // Clé secrète générée aléatoirement pour signer les tokens (dans un vrai
    // projet, on la met dans application.properties)
    private final Key cleSecrete = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Le token sera valide pendant 24 heures (en millisecondes)
    private final long DUREE_VALIDITE = 1000 * 60 * 60 * 24;

    // Méthode pour générer un token JWT pour un utilisateur précis
    public String genererToken(Utilisateur utilisateur) {
        return Jwts.builder()
                .setSubject(utilisateur.getEmail()) // Le sujet principal du token est l'email
                .claim("id", utilisateur.getIdUtilisateur()) // On y cache aussi son ID
                .claim("nom", utilisateur.getNom()) // Et son nom pour l'afficher sur Vue.js
                .setIssuedAt(new Date(System.currentTimeMillis())) // Date de création
                .setExpiration(new Date(System.currentTimeMillis() + DUREE_VALIDITE)) // Date de fin
                .signWith(cleSecrete) // On signe avec notre clé secrète
                .compact();
    }

    // Lit le token et récupère l'email qu'il contient
    public String extraireEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(cleSecrete)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}