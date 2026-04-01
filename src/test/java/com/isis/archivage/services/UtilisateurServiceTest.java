package com.isis.archivage.services;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.isis.archivage.dto.CreationUtilisateurRequest;
import com.isis.archivage.entities.Utilisateur;
import com.isis.archivage.repositories.UtilisateurRepository;

@ExtendWith(MockitoExtension.class)
class UtilisateurServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UtilisateurService utilisateurService;

    @Test
    void creerUtilisateur_EmailExistant_DoitLeverException() {
        CreationUtilisateurRequest request = new CreationUtilisateurRequest();
        request.setEmail("test@isis.fr");

        when(utilisateurRepository.findByEmail("test@isis.fr"))
                .thenReturn(Optional.of(new Utilisateur()));

        Exception exception = assertThrows(Exception.class, () -> {
            utilisateurService.creerUtilisateur(request);
        });

        assertEquals("Un utilisateur avec cet email existe déjà !", exception.getMessage());

        verify(utilisateurRepository, never()).save(any(Utilisateur.class));
    }
}