package com.isis.archivage.services;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.isis.archivage.entities.Document;
import com.isis.archivage.entities.HistoriqueAction;
import com.isis.archivage.entities.Utilisateur;
import com.isis.archivage.repositories.DocumentRepository;
import com.isis.archivage.repositories.HistoriqueActionRepository;
import com.isis.archivage.repositories.UtilisateurRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // Lombok génère le constructeur pour injecter les Repositories
public class DocumentService {

    // Injection des Repositories nécessaires
    private final DocumentRepository documentRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final HistoriqueActionRepository historiqueActionRepository;

    // Dossier où les vrais fichiers PDF seront sauvegardés sur ton PC
    private final String UPLOAD_DIRECTORY = "uploads/documents/";

    /**
     * Méthode principale pour gérer le dépôt d'un document
     */
    @Transactional // Si une erreur survient (ex: disque plein), annule aussi l'écriture en BDD
    public Document uploaderDocument(MultipartFile fichier, String titre, String description, Long idAuteur)
            throws Exception {

        String nomFichier = fichier.getOriginalFilename();

        // 1. VÉRIFICATION DES RÈGLES MÉTIER (EX-F-06 : Doublons)
        if (documentRepository.existsByNomFichier(nomFichier)) {
            throw new Exception("Un fichier nommé " + nomFichier + " existe déjà dans le système !");
        }

        // Vérification de la taille (EX-F-02 : max 20 Mo)
        if (fichier.getSize() > 20 * 1024 * 1024) {
            throw new Exception("Le fichier est trop volumineux (Maximum 20 Mo).");
        }

        // 2. SAUVEGARDE PHYSIQUE SUR LE DISQUE
        Path cheminDossier = Paths.get(UPLOAD_DIRECTORY);
        if (!Files.exists(cheminDossier)) {
            Files.createDirectories(cheminDossier); // Crée le dossier s'il n'existe pas
        }
        Path cheminFichier = cheminDossier.resolve(nomFichier);
        Files.copy(fichier.getInputStream(), cheminFichier); // Écrit le fichier

        // 3. RÉCUPÉRATION DE L'AUTEUR
        Utilisateur auteur = utilisateurRepository.findById(idAuteur)
                .orElseThrow(() -> new Exception("Utilisateur introuvable"));

        // 4. SAUVEGARDE EN BASE DE DONNÉES (Les métadonnées)
        Document doc = new Document();
        doc.setNomFichier(nomFichier);
        doc.setTitre(titre);
        doc.setDescription(description);
        doc.setDateDepot(LocalDate.now());
        doc.setAuteur(auteur);
        // (Ici tu pourrais aussi lier la BoiteArchive et le TypeDocument de la même
        // manière)

        Document documentSauvegarde = documentRepository.save(doc);

        // 5. TRAÇABILITÉ (EX-F-05)
        HistoriqueAction log = new HistoriqueAction();
        log.setTypeAction("DEPOT_DOCUMENT");
        log.setDateAction(LocalDateTime.now());
        log.setUtilisateur(auteur);
        log.setDocument(documentSauvegarde);
        // log.setAdresseIp("..."); (Ceci sera récupéré via le Controller web plus tard)

        historiqueActionRepository.save(log);

        return documentSauvegarde;
    }

    /**
     * Récupère absolument tous les documents de la base (pour l'affichage par
     * défaut)
     */
    public List<Document> obtenirTousLesDocuments() {
        return documentRepository.findAll();
    }

    /**
     * Recherche un document dont le titre contient un mot clé (insensible à la
     * casse)
     */
    public List<Document> rechercherParTitre(String motCle) {
        return documentRepository.findByTitreContainingIgnoreCase(motCle);
    }

    /**
     * Recherche les documents déposés par un auteur précis (via Nom et Prénom)
     */
    public List<Document> rechercherParAuteur(String nom, String prenom) {
        return documentRepository.findByAuteur_NomAndAuteur_Prenom(nom, prenom);
    }

    /**
     * Permet de télécharger un fichier physique et de tracer l'action
     */
    @Transactional
    public Resource telechargerDocument(Long idDocument, Long idUtilisateur) throws Exception {

        // 1. On cherche les métadonnées du document en Base de données
        Document doc = documentRepository.findById(idDocument)
                .orElseThrow(() -> new Exception("Document introuvable avec l'ID : " + idDocument));

        // 2. On cherche l'utilisateur qui fait l'action (pour l'audit)
        Utilisateur utilisateur = utilisateurRepository.findById(idUtilisateur)
                .orElseThrow(() -> new Exception("Utilisateur introuvable pour la traçabilité"));

        // 3. On va chercher le vrai fichier sur le disque dur
        Path cheminFichier = Paths.get(UPLOAD_DIRECTORY).resolve(doc.getNomFichier()).normalize();
        Resource resource = new UrlResource(cheminFichier.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new Exception("Le fichier physique est introuvable ou illisible sur le serveur !");
        }

        // 4. TRAÇABILITÉ (Exigence EX-F-05)
        HistoriqueAction log = new HistoriqueAction();
        log.setTypeAction("TELECHARGEMENT_DOCUMENT"); // On enregistre bien que c'est un téléchargement
        log.setDateAction(LocalDateTime.now());
        log.setUtilisateur(utilisateur);
        log.setDocument(doc);

        historiqueActionRepository.save(log); // Sauvegarde du log en base

        // 5. On retourne le fichier prêt à être envoyé par le réseau
        return resource;
    }
}