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
import com.isis.archivage.enums.CategorieArchive;
import com.isis.archivage.repositories.DocumentRepository;
import com.isis.archivage.services.DocumentService;
import com.isis.archivage.services.PdfService;
import com.isis.archivage.services.QrCodeService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentRepository documentRepository;
    private final QrCodeService qrCodeService;
    private final PdfService pdfService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploaderDocument(
            @RequestParam("fichier") MultipartFile fichier,
            @RequestParam("titre") String titre,
            @RequestParam("description") String description,
            @RequestParam("categorie") CategorieArchive categorie,
            Principal principal) {

        try {
            Document documentSauvegarde = documentService.uploaderDocument(fichier, titre, description, categorie,
                    principal.getName());
            return ResponseEntity.status(HttpStatus.CREATED).body(documentSauvegarde);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllDocuments(Principal principal) {
        try {
            List<Document> documents = documentService.obtenirTousLesDocuments(principal.getName());
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/recherche/titre")
    public ResponseEntity<?> rechercherParTitre(@RequestParam("motCle") String motCle, Principal principal) {
        try {
            List<Document> resultats = documentService.rechercherParTitre(motCle, principal.getName());
            return ResponseEntity.ok(resultats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/recherche/auteur")
    public ResponseEntity<?> rechercherParAuteur(
            @RequestParam("nom") String nom,
            @RequestParam("prenom") String prenom,
            Principal principal) {
        try {
            List<Document> resultats = documentService.rechercherParAuteur(nom, prenom, principal.getName());
            return ResponseEntity.ok(resultats);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/{id}/telecharger")
    public ResponseEntity<?> telechargerDocument(
            @PathVariable("id") Long idDocument,
            Principal principal) {

        try {
            Resource resource = documentService.telechargerDocument(idDocument, principal.getName());
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping(value = "/{id}/qrcode", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> obtenirQrCodeDocument(@PathVariable("id") Long idDocument) {
        try {
            Document doc = documentRepository.findById(idDocument)
                    .orElseThrow(() -> new Exception("Document introuvable"));

            String urlFrontend = "http://localhost:5173/documents/details/" + doc.getIdDocument();
            byte[] image = qrCodeService.genererQrCodeImage(urlFrontend, 250, 250);

            return ResponseEntity.ok().body(image);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping(value = "/{id}/etiquette", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> telechargerEtiquettePdf(@PathVariable("id") Long idDocument) {
        try {
            Document doc = documentRepository.findById(idDocument)
                    .orElseThrow(() -> new Exception("Document introuvable"));

            byte[] pdfBytes = pdfService.genererEtiquettePdf(doc);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"etiquette_archive_" + idDocument + ".pdf\"")
                    .body(pdfBytes);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}