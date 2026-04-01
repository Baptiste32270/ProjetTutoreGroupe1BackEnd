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

import com.isis.archivage.entities.Document;
import com.isis.archivage.repositories.DocumentRepository;
import com.isis.archivage.services.DocumentService;
import com.isis.archivage.services.QrCodeService;

import lombok.RequiredArgsConstructor;

@RestController // cette classe répond aux requêtes web (API REST)
@RequestMapping("/api/documents") // L'URL de base pour ce contrôleur
@CrossOrigin(origins = "*") // Autorise le front-end à appeler cette API
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final QrCodeService qrCodeService;
    private final DocumentRepository documentRepository;

    // Point d'entrée pour l'upload d'un document.
    @PostMapping("/upload")
    public ResponseEntity<?> uploadDocument(
            @RequestParam("fichier") MultipartFile fichier,
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam("idAuteur") Long idAuteur) {

        try {
            Document documentSauvegarde = documentService.uploaderDocument(fichier, titre, description, idAuteur);

            return ResponseEntity.status(HttpStatus.CREATED).body(documentSauvegarde);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // Point d'entrée pour récupérer tous les documents.
    @GetMapping
    public ResponseEntity<?> getAllDocuments(Principal principal) {
        try {
            List<Document> documents = documentService.obtenirTousLesDocuments(principal.getName());
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    // Point d'entrée pour la recherche par titre.
    @GetMapping("/recherche/titre")
    public ResponseEntity<List<Document>> rechercherParTitre(@RequestParam("motCle") String motCle) {
        List<Document> resultats = documentService.rechercherParTitre(motCle);
        return ResponseEntity.ok(resultats);
    }

    // Point d'entrée pour la recherche par auteur.
    @GetMapping("/recherche/auteur")
    public ResponseEntity<List<Document>> rechercherParAuteur(
            @RequestParam("nom") String nom,
            @RequestParam("prenom") String prenom) {

        List<Document> resultats = documentService.rechercherParAuteur(nom, prenom);
        return ResponseEntity.ok(resultats);
    }

    // Point d'entrée pour télécharger un document physique.
    @GetMapping("/{id}/telecharger")
    public ResponseEntity<?> telechargerDocument(
            @PathVariable("id") Long idDocument,
            @RequestParam("idUtilisateur") Long idUtilisateur) {

        try {
            // On récupère le fichier
            Resource resource = documentService.telechargerDocument(idDocument, idUtilisateur);

            return ResponseEntity.ok()
                    // Dit au navigateur que c'est un PDF
                    .contentType(MediaType.APPLICATION_PDF)
                    // Propose de télécharger le fichier avec son nom d'origine
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // Génère et affiche le QR Code d'un document spécifique.
    @GetMapping(value = "/{id}/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> obtenirQrCodeDocument(@PathVariable("id") Long idDocument) {
        try {
            Document doc = documentRepository.findById(idDocument)
                    .orElseThrow(() -> new Exception("Document introuvable"));

            // Demander à gemini
            String urlFrontend = "http://localhost:5173/documents/details/" + doc.getIdDocument();

            byte[] image = qrCodeService.genererQrCodeImage(urlFrontend, 250, 250);

            return ResponseEntity.ok().body(image);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}