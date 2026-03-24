package com.isis.archivage.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal; // Import à ajouter

import com.isis.archivage.entities.Document;
import com.isis.archivage.repositories.DocumentRepository;
import com.isis.archivage.services.DocumentService;
import com.isis.archivage.services.QrCodeService;

import lombok.RequiredArgsConstructor;

@RestController // Indique que cette classe répond aux requêtes web (API REST)
@RequestMapping("/api/documents") // L'URL de base pour ce contrôleur
@CrossOrigin(origins = "*") // Autorise ton futur front-end Vue.js à appeler cette API
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final QrCodeService qrCodeService;
    private final DocumentRepository documentRepository;

    /**
     * Point d'entrée pour l'upload d'un document.
     * Accessible via : POST http://localhost:8080/api/documents/upload
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("fichier") MultipartFile fichier,
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam("idAuteur") Long idAuteur) {

        try {
            // On délègue le travail compliqué au Service
            Document documentSauvegarde = documentService.uploaderDocument(fichier, titre, description, idAuteur);

            // Si tout va bien, on retourne le document créé avec un code 201 (CREATED)
            return ResponseEntity.status(HttpStatus.CREATED).body(documentSauvegarde);

        } catch (Exception e) {
            // Si le service lève une exception (ex: Doublon, Fichier trop gros), on renvoie
            // une erreur 400
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Point d'entrée pour récupérer tous les documents.
     * Accessible via : GET http://localhost:8080/api/documents
     */
    @GetMapping
    public ResponseEntity<?> getAllDocuments(Principal principal) {
        try {
            // principal.getName() contient l'email que notre JwtFilter a extrait du jeton !
            List<Document> documents = documentService.obtenirTousLesDocuments(principal.getName());
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    /**
     * Point d'entrée pour la recherche par titre.
     * Accessible via : GET
     * http://localhost:8080/api/documents/recherche/titre?motCle=java
     */
    @GetMapping("/recherche/titre")
    public ResponseEntity<List<Document>> rechercherParTitre(@RequestParam("motCle") String motCle) {
        List<Document> resultats = documentService.rechercherParTitre(motCle);
        return ResponseEntity.ok(resultats);
    }

    /**
     * Point d'entrée pour la recherche par auteur.
     * Accessible via : GET
     * http://localhost:8080/api/documents/recherche/auteur?nom=Dupont&prenom=Jean
     */
    @GetMapping("/recherche/auteur")
    public ResponseEntity<List<Document>> rechercherParAuteur(
            @RequestParam("nom") String nom,
            @RequestParam("prenom") String prenom) {

        List<Document> resultats = documentService.rechercherParAuteur(nom, prenom);
        return ResponseEntity.ok(resultats);
    }

    /**
     * Point d'entrée pour télécharger un document physique.
     * Accessible via : GET
     * http://localhost:8080/api/documents/1/telecharger?idUtilisateur=1
     */
    @GetMapping("/{id}/telecharger")
    public ResponseEntity<?> telechargerDocument(
            @PathVariable("id") Long idDocument,
            @RequestParam("idUtilisateur") Long idUtilisateur) {

        try {
            // On récupère le fichier via le service
            Resource resource = documentService.telechargerDocument(idDocument, idUtilisateur);

            // On construit la réponse HTTP spéciale pour un téléchargement
            return ResponseEntity.ok()
                    // On dit au navigateur que c'est un PDF
                    .contentType(MediaType.APPLICATION_PDF)
                    // "attachment" force le navigateur à télécharger le fichier plutôt que de
                    // l'ouvrir bêtement
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (Exception e) {
            // Si le fichier n'existe pas ou s'il y a une erreur
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Génère et affiche le QR Code d'un document spécifique.
     * Accessible via : GET http://localhost:8080/api/documents/1/qrcode
     */
    @GetMapping(value = "/{id}/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> obtenirQrCodeDocument(@PathVariable("id") Long idDocument) {
        try {
            // 1. On vérifie que le document existe (tu peux utiliser documentRepository
            // directement ici)
            Document doc = documentRepository.findById(idDocument)
                    .orElseThrow(() -> new Exception("Document introuvable"));

            // 2. On génère une vraie URL pointant vers le futur Front-End Vue.js
            // (En supposant que ton Vue.js tournera sur le port 5173 ou 8080)
            String urlFrontend = "http://localhost:5173/documents/details/" + doc.getIdDocument();

            // 3. On demande au service de générer l'image avec ce lien
            byte[] image = qrCodeService.genererQrCodeImage(urlFrontend, 250, 250);

            // 4. On renvoie l'image avec un code 200 OK
            return ResponseEntity.ok().body(image);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}