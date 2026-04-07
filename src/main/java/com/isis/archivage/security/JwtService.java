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

    private final Key cleSecrete = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private final long DUREE_VALIDITE = 1000 * 60 * 60 * 24;

    public String genererToken(Utilisateur utilisateur) {
        return Jwts.builder()
                .setSubject(utilisateur.getEmail())
                .claim("id", utilisateur.getIdUtilisateur())
                .claim("nom", utilisateur.getNom())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + DUREE_VALIDITE))
                .signWith(cleSecrete)
                .compact();
    }

    public String extraireEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(cleSecrete)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}