package iccsoft_auth.model;


import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Entity
@Data
@Table(name = "employe")
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
    private Long telephone;
    private String entreprise;
    // Département de l'employé dans l'entreprise, du type énumération
    private Department department;

    // public String getUsername() {
    //     return username;
       
    // }

    public void setRoles(Set<String> roles) {
        this.role = role;
    }

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
