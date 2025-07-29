package com.example.iccsoft_courrier.models;


import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Entity
@Data
public class Employe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    private String username;

    @NotBlank
    @Size(max = 20)
    private String name;

    @Email
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;
    // Fonction dans l'entreprise, du type enumération
    private Fonction fonction;

    @Enumerated(EnumType.STRING)
    private ERole role;

    private Date createdAt;
    private Date updatedAt;

    private String adresse;

    @Min(value = 600000000, message = "Le numéro de téléphone doit comporter 9 chiffres")
    @Max(value = 699999999, message = "Le numéro de téléphone doit comporter 9 chiffres")
    @Pattern(regexp = "^\\d*$", message = "Le numéro de téléphone ne doit comporter que des chiffres")
    private Long telephone;
    private String entreprise;

    private Department department;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }
    
}

// Enum for fonction
enum Fonction {
    DEVELOPPEMENT_LOGICIEL,
    RESEAUX,
    CYBERSECURITE,
    DATA_IA,
    OTHER
}

// Enum for department
enum Department {
    DG,
    DSI,
    RH,
    ASSISTANT_DSI,
    SUPPORT,
    DEVELOPPEUR,
    INGENIEUR_LOGICIEL
}
