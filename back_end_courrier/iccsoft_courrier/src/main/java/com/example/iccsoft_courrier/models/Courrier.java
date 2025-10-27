package com.example.iccsoft_courrier.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "courriers")
@Data
public class Courrier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "objet")
    private String objet;

    @Column(name = "destinateur")
    private String destinateur;

    @Column(name = "destinataire")
    private String destinataire;

    @Column(name = "destinataires_list")
    private String destinatairesList; // JSON string of usernames

    @Column(name = "copies_list")
    private String copiesList; // JSON string of usernames for CC

    @Column(name = "date_courrier")
    private Date dateCourrier;

    @Column(name = "date_entree")
    private Date dateEntree;

    @Column(name = "date_reception")
    private Date dateReception;

    @Column(name = "date_modification")
    private Date dateModification;

    @PrePersist
    protected void onCreate() {
        this.dateReception = new Date();
        this.dateModification = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.dateModification = new Date();
    }

    @Column(name = "description")
    private String description;

    @Column(name = "statut_courrier")
    private String statutCourrier;

    @Column(name = "etat")
    private String etat;

    @Column(name = "numero_ordre")
    private String numeroOrdre;

    // Removed OneToMany relationships to avoid constraint violations
    // Use filePath and fileName for file attachments
    // Use destinatairesList and copiesList for recipients

    @Column(name = "file_path")
    private String filePath; // Path to uploaded file

    @Column(name = "file_name")
    private String fileName; // Original file name

    // Enums conservés pour compatibilité
    public enum StatutRecu {
        RECU, EN_ATTENTE, TRAITE, ARCHIVE, IMPORTANT
    }

    public enum EtatCourrier {
        PUBLIC, PRIVE, RECU, EN_ATTENTE, TRAITE, ARCHIVE, LU
    }


    public String genererNumeroOrdre(){
        LocalDate dateActuelle = LocalDate.now();
        String dataFormatee = dateActuelle.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long timestamp = System.currentTimeMillis() % 1000;
        numeroOrdre = String.format("CE-%s-%03d", dataFormatee, timestamp);
        return numeroOrdre;
    }

}
