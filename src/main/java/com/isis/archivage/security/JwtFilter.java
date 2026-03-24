package com.isis.archivage.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. On cherche l'en-tête "Authorization"
        final String authHeader = request.getHeader("Authorization");

        // 2. Si l'en-tête est absent ou mal formaté, on laisse Spring Security bloquer
        // la requête
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. On extrait le jeton (on enlève les 7 caractères de "Bearer ")
        final String token = authHeader.substring(7);

        try {
            // 4. On lit l'email dans le jeton
            String email = jwtService.extraireEmail(token);

            // 5. Si l'email existe et que l'utilisateur n'est pas encore identifié dans ce
            // contexte
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // On dit officiellement à Spring : "C'est bon, je reconnais cet utilisateur !"
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, null,
                        new ArrayList<>());

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            // Jeton expiré ou falsifié, on ne fait rien et Spring bloquera l'accès
        }

        // On passe à la suite (au contrôleur)
        filterChain.doFilter(request, response);
    }
}