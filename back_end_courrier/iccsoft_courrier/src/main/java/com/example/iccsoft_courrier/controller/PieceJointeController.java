package com.example.iccsoft_courrier.controller;

import com.example.iccsoft_courrier.models.PieceJointe;
import com.example.iccsoft_courrier.repositories.PieceJointeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/api/pieces-jointes")
public class PieceJointeController {

    @Autowired
    private PieceJointeRepository pieceJointeRepository;

    private final String uploadDir = "uploads/pieces-jointes/";

    @PostMapping("/upload/{courrierId}")
    public ResponseEntity<PieceJointe> uploadPieceJointe(@PathVariable Long courrierId, @RequestParam("file") MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);
            
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);
            
            PieceJointe pieceJointe = new PieceJointe();
            pieceJointe.setCourrierId(courrierId);
            pieceJointe.setFileName(file.getOriginalFilename());
            pieceJointe.setFilePath(filePath.toString());
            pieceJointe.setFileSize(file.getSize());
            pieceJointe.setContentType(file.getContentType());
            
            PieceJointe saved = pieceJointeRepository.save(pieceJointe);
            return ResponseEntity.ok(saved);
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/courrier/{courrierId}")
    public ResponseEntity<List<PieceJointe>> getPiecesJointesByCourrier(@PathVariable Long courrierId) {
        List<PieceJointe> piecesJointes = pieceJointeRepository.findByCourrierId(courrierId);
        return ResponseEntity.ok(piecesJointes);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadPieceJointe(@PathVariable Long id) {
        try {
            PieceJointe pieceJointe = pieceJointeRepository.findById(id).orElse(null);
            if (pieceJointe == null) {
                return ResponseEntity.notFound().build();
            }

            Path filePath = Paths.get(pieceJointe.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(pieceJointe.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + pieceJointe.getFileName() + "\"")
                    .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePieceJointe(@PathVariable Long id) {
        try {
            PieceJointe pieceJointe = pieceJointeRepository.findById(id).orElse(null);
            if (pieceJointe != null) {
                Path filePath = Paths.get(pieceJointe.getFilePath());
                Files.deleteIfExists(filePath);
                pieceJointeRepository.deleteById(id);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}