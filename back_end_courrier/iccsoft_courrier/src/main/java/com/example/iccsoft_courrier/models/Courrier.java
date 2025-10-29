// Package contenant les modèles de données de l'application de gestion de courrier
package com.example.iccsoft_courrier.models;

// Importations pour la gestion des dates et du temps
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// Importations JPA pour la persistance des données
import jakarta.persistence.Column; // Annotation pour définir les colonnes de base de données
import jakarta.persistence.Entity; // Annotation pour marquer une classe comme entité JPA
import jakarta.persistence.GeneratedValue; // Génération automatique des valeurs
import jakarta.persistence.GenerationType; // Types de génération des clés primaires
import jakarta.persistence.Id; // Annotation pour la clé primaire
import jakarta.persistence.OneToMany; // Relation un-à-plusieurs
import jakarta.persistence.PrePersist; // Callback avant insertion
import jakarta.persistence.PreUpdate; // Callback avant mise à jour
import jakarta.persistence.Table; // Annotation pour définir le nom de la table
import lombok.Data; // Génération automatique des getters/setters

// Entité JPA représentant un courrier dans le système de gestion
@Entity
// Définition du nom de la table en base de données
@Table(name = "courriers")
// Génération automatique des getters, setters, toString, equals et hashCode
@Data
public class Courrier {
    // Identifiant unique du courrier, généré automatiquement par la base de données
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Objet ou sujet du courrier
    @Column(name = "objet")
    private String objet;

    // Nom de l'expéditeur du courrier
    @Column(name = "destinateur")
    private String destinateur;

    // Destinataire principal du courrier
    @Column(name = "destinataire")
    private String destinataire;

    // Liste des destinataires au format JSON (pour les courriers multiples)
    @Column(name = "destinataires_list")
    private String destinatairesList; // Chaîne JSON contenant les noms d'utilisateurs

    // Liste des personnes en copie au format JSON
    @Column(name = "copies_list")
    private String copiesList; // Chaîne JSON contenant les noms d'utilisateurs pour CC

    // Date de création/rédaction du courrier
    @Column(name = "date_courrier")
    private Date dateCourrier;

    // Date d'entrée du courrier dans le système
    @Column(name = "date_entree")
    private Date dateEntree;

    // Date de réception du courrier
    @Column(name = "date_reception")
    private Date dateReception;

    // Date de dernière modification du courrier
    @Column(name = "date_modification")
    private Date dateModification;

    // Méthode appelée automatiquement avant l'insertion en base de données
    @PrePersist
    protected void onCreate() {
        this.dateReception = new Date(); // Définit la date de réception à maintenant
        this.dateModification = new Date(); // Définit la date de modification à maintenant
    }

    // Méthode appelée automatiquement avant chaque mise à jour en base de données
    @PreUpdate
    protected void onUpdate() {
        this.dateModification = new Date(); // Met à jour la date de modification
    }

    // Description détaillée du contenu du courrier
    @Column(name = "description")
    private String description;

    // Statut de confidentialité du courrier (PUBLIC, PRIVÉ, etc.)
    @Column(name = "statut_courrier")
    private String statutCourrier;

    // État de traitement du courrier (EN_ATTENTE, TRAITÉ, ARCHIVÉ, etc.)
    @Column(name = "etat")
    private String etat;

    // Numéro d'ordre unique généré automatiquement
    @Column(name = "numero_ordre")
    private String numeroOrdre;

    // Relations OneToMany supprimées pour éviter les violations de contraintes
    // Utilisation de filePath et fileName pour les pièces jointes
    // Utilisation de destinatairesList et copiesList pour les destinataires

    // Chemin d'accès au fichier téléchargé sur le serveur
    @Column(name = "file_path")
    private String filePath; // Chemin vers le fichier téléchargé

    // Nom original du fichier téléchargé
    @Column(name = "file_name")
    private String fileName; // Nom original du fichier

    // Énumérations conservées pour la compatibilité avec les anciennes versions
    // Énumération des statuts de réception possibles
    public enum StatutRecu {
        RECU,       // Courrier reçu
        EN_ATTENTE, // En attente de traitement
        TRAITE,     // Courrier traité
        ARCHIVE,    // Courrier archivé
        IMPORTANT   // Courrier marqué comme important
    }

    // Énumération des états possibles d'un courrier
    public enum EtatCourrier {
        PUBLIC,     // Courrier public
        PRIVE,      // Courrier privé
        RECU,       // Courrier reçu
        EN_ATTENTE, // En attente de traitement
        TRAITE,     // Courrier traité
        ARCHIVE,    // Courrier archivé
        LU          // Courrier lu
    }


    /**
     * Génère un numéro d'ordre unique pour le courrier
     * Format: CE-AAAAMMJJ-XXX (CE = Courrier Entrant, AAAAMMJJ = date, XXX = timestamp)
     * @return Le numéro d'ordre généré
     */
    public String genererNumeroOrdre(){
        LocalDate dateActuelle = LocalDate.now(); // Récupère la date actuelle
        String dataFormatee = dateActuelle.format(DateTimeFormatter.ofPattern("yyyyMMdd")); // Formate la date
        long timestamp = System.currentTimeMillis() % 1000; // Récupère les 3 derniers chiffres du timestamp
        numeroOrdre = String.format("CE-%s-%03d", dataFormatee, timestamp); // Génère le numéro
        return numeroOrdre;
    }

}
