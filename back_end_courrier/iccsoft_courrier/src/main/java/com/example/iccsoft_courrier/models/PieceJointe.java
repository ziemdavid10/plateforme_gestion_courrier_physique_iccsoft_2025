package com.example.iccsoft_courrier.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "piece_jointes")
@Data
// La classe PieceJointe représente les pièces jointes associées à un courrier.
public class PieceJointe {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom", nullable = false)
    private String nom; // Nom de la pièce jointe

    @Column(name = "type", nullable = false)
    private String type; 

    @Column(name = "description", nullable = false)
    private String description;

    @ManyToOne(fetch = jakarta.persistence.FetchType.LAZY)
    @JoinColumn(name = "courrier_id", nullable = false)
    private Courrier courrier; // Référence au courrier auquel cette pièce jointe est associée
}
