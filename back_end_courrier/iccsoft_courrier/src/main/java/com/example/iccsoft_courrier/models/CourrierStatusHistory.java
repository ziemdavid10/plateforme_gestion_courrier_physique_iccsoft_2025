package com.example.iccsoft_courrier.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "courrier_status_history")
public class CourrierStatusHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "courrier_id", nullable = false)
    private Long courrierId;
    
    @Column(name = "ancien_statut")
    private String ancienStatut;
    
    @Column(name = "nouveau_statut", nullable = false)
    private String nouveauStatut;
    
    @Column(name = "utilisateur", nullable = false)
    private String utilisateur;
    
    @Column(name = "date_changement", nullable = false)
    private LocalDateTime dateChangement;
    
    @Column(name = "commentaire")
    private String commentaire;
    
    // Constructeurs
    public CourrierStatusHistory() {
        this.dateChangement = LocalDateTime.now();
    }
    
    public CourrierStatusHistory(Long courrierId, String ancienStatut, String nouveauStatut, String utilisateur, String commentaire) {
        this();
        this.courrierId = courrierId;
        this.ancienStatut = ancienStatut;
        this.nouveauStatut = nouveauStatut;
        this.utilisateur = utilisateur;
        this.commentaire = commentaire;
    }
    
    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getCourrierId() { return courrierId; }
    public void setCourrierId(Long courrierId) { this.courrierId = courrierId; }
    
    public String getAncienStatut() { return ancienStatut; }
    public void setAncienStatut(String ancienStatut) { this.ancienStatut = ancienStatut; }
    
    public String getNouveauStatut() { return nouveauStatut; }
    public void setNouveauStatut(String nouveauStatut) { this.nouveauStatut = nouveauStatut; }
    
    public String getUtilisateur() { return utilisateur; }
    public void setUtilisateur(String utilisateur) { this.utilisateur = utilisateur; }
    
    public LocalDateTime getDateChangement() { return dateChangement; }
    public void setDateChangement(LocalDateTime dateChangement) { this.dateChangement = dateChangement; }
    
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
}