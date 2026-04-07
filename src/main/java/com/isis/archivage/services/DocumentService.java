package com.isis.archivage.services;

import com.isis.archivage.entities.Document;
import com.isis.archivage.entities.HistoriqueAction;
import com.isis.archivage.entities.Utilisateur;
import com.isis.archivage.enums.CategorieArchive;
import com.isis.archivage.enums.NiveauAcces;
import com.isis.archivage.repositories.DocumentRepository;
import com.isis.archivage.repositories.HistoriqueActionRepository;
import com.isis.archivage.repositories.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final HistoriqueActionRepository historiqueActionRepository;

    private final String UPLOAD_DIRECTORY = "uploads/documents/";

    private boolean possedeDroitSuffisant(Utilisateur utilisateur, CategorieArchive categorieDoc,
            NiveauAcces niveauRequis) {
        if (categorieDoc == null)
            return false;

        return utilisateur.getDroitsAcces().stream()
                .filter(droit -> droit.getCategorie() == categorieDoc)
                .findFirst()
                .map(droit -> {
                    NiveauAcces niveauPossede = droit.getNiveauAcces();
                    if (niveauPossede == NiveauAcces.ADMINISTRATEUR)
                        return true;
                    if (niveauRequis == NiveauAcces.MODIFICATION) {
                        return niveauPossede == NiveauAcces.MODIFICATION;
                    }
                    if (niveauRequis == NiveauAcces.VISUALISATION) {
                        return niveauPossede == NiveauAcces.VISUALISATION || niveauPossede == NiveauAcces.MODIFICATION;
                    }
                    return false;
                })
                .orElse(false);
    }

    @Transactional
    public Document uploaderDocument(MultipartFile fichier, String titre, String description,
            CategorieArchive categorie, String emailUtilisateur) throws Exception {

        Utilisateur auteur = utilisateurRepository.findByEmail(emailUtilisateur)
                .orElseThrow(() -> new Exception("Utilisateur introuvable"));

        if (!possedeDroitSuffisant(auteur, categorie, NiveauAcces.MODIFICATION)) {
            throw new Exception("Accès refusé : Droits de MODIFICATION requis pour la catégorie " + categorie);
        }

        if (documentRepository.existsByNomFichier(fichier.getOriginalFilename())) {
            throw new Exception("Un fichier nommé " + fichier.getOriginalFilename() + " existe déjà !");
        }

        Path cheminDossier = Paths.get(UPLOAD_DIRECTORY);
        if (!Files.exists(cheminDossier)) {
            Files.createDirectories(cheminDossier);
        }

        Path cheminFichier = cheminDossier.resolve(fichier.getOriginalFilename());
        fichier.transferTo(cheminFichier.toFile());

        Document nouveauDoc = new Document();
        nouveauDoc.setTitre(titre);
        nouveauDoc.setDescription(description);
        nouveauDoc.setNomFichier(fichier.getOriginalFilename());
        nouveauDoc.setDateDepot(LocalDateTime.now());
        nouveauDoc.setEstNumerise(true);
        nouveauDoc.setAuteur(auteur);
        nouveauDoc.setCategorie(categorie);

        return documentRepository.save(nouveauDoc);
    }

    public List<Document> obtenirTousLesDocuments(String emailUtilisateur) throws Exception {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(emailUtilisateur)
                .orElseThrow(() -> new Exception("Utilisateur introuvable"));

        return documentRepository.findAll().stream()
                .filter(doc -> possedeDroitSuffisant(utilisateur, doc.getCategorie(), NiveauAcces.VISUALISATION))
                .toList();
    }

    public List<Document> rechercherParTitre(String motCle, String emailUtilisateur) throws Exception {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(emailUtilisateur).orElseThrow();
        return documentRepository.findByTitreContainingIgnoreCase(motCle).stream()
                .filter(doc -> possedeDroitSuffisant(utilisateur, doc.getCategorie(), NiveauAcces.VISUALISATION))
                .toList();
    }

    public List<Document> rechercherParAuteur(String nom, String prenom, String emailUtilisateur) throws Exception {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(emailUtilisateur).orElseThrow();
        return documentRepository.findByAuteur_NomAndAuteur_Prenom(nom, prenom).stream()
                .filter(doc -> possedeDroitSuffisant(utilisateur, doc.getCategorie(), NiveauAcces.VISUALISATION))
                .toList();
    }

    @Transactional
    public Resource telechargerDocument(Long idDocument, String emailUtilisateur) throws Exception {

        Document doc = documentRepository.findById(idDocument)
                .orElseThrow(() -> new Exception("Document introuvable"));

        Utilisateur utilisateur = utilisateurRepository.findByEmail(emailUtilisateur)
                .orElseThrow(() -> new Exception("Utilisateur introuvable"));

        if (!possedeDroitSuffisant(utilisateur, doc.getCategorie(), NiveauAcces.VISUALISATION)) {
            throw new Exception(
                    "Accès refusé : Droits de VISUALISATION requis pour la catégorie " + doc.getCategorie());
        }

        Path cheminFichier = Paths.get(UPLOAD_DIRECTORY).resolve(doc.getNomFichier()).normalize();
        Resource resource = new UrlResource(cheminFichier.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new Exception("Le fichier physique est introuvable !");
        }

        HistoriqueAction log = new HistoriqueAction();
        log.setTypeAction("TELECHARGEMENT_DOCUMENT");
        log.setDateAction(LocalDateTime.now());
        log.setUtilisateur(utilisateur);
        log.setDocument(doc);
        historiqueActionRepository.save(log);

        return resource;
    }
}