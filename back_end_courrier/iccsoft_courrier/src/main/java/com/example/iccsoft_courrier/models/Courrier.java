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

    @Column(name = "date_courrier")
    private String dateCourrier;

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

    @Column(name = "statut")
    private StatutRecu statut;

    @Column(name = "etat")
    private EtatCourrier etat;

    @Column(name = "numero_ordre")
    private String numeroOrdre;

    @OneToMany(mappedBy = "courrier", cascade = jakarta.persistence.CascadeType.ALL, fetch = jakarta.persistence.FetchType.LAZY)
    private List<PieceJointe> pieceJointes;

    @OneToMany(mappedBy = "courrier", cascade = jakarta.persistence.CascadeType.ALL, fetch = jakarta.persistence.FetchType.LAZY)
    private List<MiseEnCopie> miseEnCopies;

    @OneToMany
    private List<Employe> destinataires;

    public enum StatutRecu {
        RECU, EN_ATTENTE, TRAITE, ARCHIVE
    }

    public enum EtatCourrier {
        PUBLIC, PRIVE
    }


    public String genererNumeroOrdre(){
        
        ConcurrentHashMap<String, AtomicInteger> compteurs = null;

        LocalDate dateActuelle = LocalDate.now();
        String dataFormatee = dateActuelle.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        AtomicInteger compteur = compteurs.computeIfAbsent(dataFormatee, k -> new AtomicInteger(0));

        int numeroSequentiel = compteur.incrementAndGet();

        numeroOrdre = String.format("%s-%03d", dataFormatee, numeroSequentiel);

        return numeroOrdre;
    }

}
