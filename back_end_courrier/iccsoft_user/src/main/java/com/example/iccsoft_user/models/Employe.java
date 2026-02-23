package com.example.iccsoft_user.models;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
@Table(name = "Employé")
public class Employe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 20)
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank
    @Size(max = 20)
    @Column(nullable = false)
    private String name;

    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    @Size(max = 120)
    private String password;

    private String adresse;

    private Date createdAt;
    private Date updatedAt;

    @Min(600000000L)
    @Max(699999999L)
    private Long telephone;

    private String entreprise;

    @Enumerated(EnumType.STRING)
    private ERole role;

    @Enumerated(EnumType.STRING)
    private EDepartment department;

    @Enumerated(EnumType.STRING)
    private EFonction fonction;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
        this.updatedAt = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }

    public Object getIsActive() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getIsActive'");
    }
}