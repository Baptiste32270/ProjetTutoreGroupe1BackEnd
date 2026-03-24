package com.isis.archivage.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Désactiver la protection CSRF (inutile pour une API REST avec Vue.js)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Configurer les règles d'accès aux URL
                .authorizeHttpRequests(auth -> auth
                        // Autoriser l'accès à la console H2 pour nos tests
                        .requestMatchers("/h2-console/**").permitAll()
                        // Autoriser temporairement la consultation du QR Code sans être connecté
                        .requestMatchers("/api/documents/*/qrcode").permitAll()
                        // Toutes les autres requêtes nécessitent d'être connecté !
                        .anyRequest().authenticated())

                // 3. Autoriser l'affichage de H2 dans des iframes (sinon la console s'affiche
                // blanche)
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        return http.build();
    }
}