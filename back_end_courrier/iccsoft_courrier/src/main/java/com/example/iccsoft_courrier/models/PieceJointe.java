package com.example.iccsoft_courrier.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "pieces_jointes")
@Data
public class PieceJointe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "courrier_id")
    private Long courrierId;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "content_type")
    private String contentType;
}