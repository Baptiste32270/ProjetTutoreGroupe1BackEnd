package com.isis.archivage.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // Permet à Spring d'injecter automatiquement le JwtFilter via le constructeur
public class SecurityConfig {

    private final JwtFilter jwtFilter; // Notre vigile personnalisé

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Outil pour crypter les mots de passe dans la base de données
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Désactiver la protection CSRF (inutile pour une API REST avec un Front
                // Vue.js séparé)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Configurer les règles d'accès aux URL
                .authorizeHttpRequests(auth -> auth
                        // Regroupement des routes publiques (accessibles sans Token)
                        .requestMatchers(
                                "/h2-console/**", // Console de base de données
                                "/api/documents/*/qrcode", // Consultation rapide via scan physique
                                "/api/auth/login", // Pour récupérer son Token
                                "/api/utilisateurs/creer" // Création (à restreindre aux ADMIN plus tard)
                        ).permitAll()

                        // TOUTES les autres requêtes nécessitent de présenter un Token valide !
                        .anyRequest().authenticated())

                // 3. Placer notre filtre JWT AVANT le filtre d'authentification standard de
                // Spring.
                // C'est lui qui va intercepter chaque requête pour extraire et vérifier le
                // Token.
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

                // 4. Autoriser l'affichage de la console H2 dans des iframes (sinon l'écran
                // reste blanc)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }
}