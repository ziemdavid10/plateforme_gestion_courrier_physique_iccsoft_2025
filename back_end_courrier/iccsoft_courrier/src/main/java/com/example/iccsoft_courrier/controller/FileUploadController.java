package com.example.iccsoft_courrier.controller;

import com.example.iccsoft_courrier.models.Courrier;
import com.example.iccsoft_courrier.services.CourrierServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/v1/api/secretary/files")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
public class FileUploadController {

    @Value("${file.upload.dir:uploads}")
    private String uploadDir;

    @Autowired
    private CourrierServices courrierServices;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestHeader("X-User-Role") String userRole) {
        
        // Only SECRETAIRE can upload files
        if (!"SECRETAIRE".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
            
            // Save file
            Path filePath = uploadPath.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), filePath);

            // Return file info
            Map<String, String> response = new HashMap<>();
            response.put("fileName", originalFilename);
            response.put("filePath", uniqueFilename);
            response.put("message", "File uploaded successfully");

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to upload file: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/view/{filename}")
    public ResponseEntity<byte[]> viewFile(
            @PathVariable String filename,
            @RequestParam String courrierId,
            @RequestHeader("X-User-Id") String userId) {
        
        try {
            Long courrierIdLong = Long.parseLong(courrierId);
            Courrier courrier = courrierServices.getCourrierById(courrierIdLong);
            
            if (courrier == null) {
                return ResponseEntity.notFound().build();
            }
            
            boolean isAuthorized = false;
            if (courrier.getDestinatairesList() != null) {
                String[] destinataires = courrier.getDestinatairesList().split(",");
                isAuthorized = Arrays.stream(destinataires)
                    .anyMatch(dest -> dest.trim().equals(userId));
            }
            
            if (!isAuthorized && courrier.getDestinataire() != null) {
                isAuthorized = courrier.getDestinataire().equals(userId);
            }
            
            if (!isAuthorized && courrier.getCopiesList() != null) {
                String[] copies = courrier.getCopiesList().split(",");
                isAuthorized = Arrays.stream(copies)
                    .anyMatch(copy -> copy.trim().equals(userId));
            }
            
            if (!isAuthorized) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            Path filePath = Paths.get(uploadDir).resolve(filename);
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileContent = Files.readAllBytes(filePath);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileContent);
                    
        } catch (IOException | NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<byte[]> downloadFile(
            @PathVariable String filename,
            @RequestParam String courrierId,
            @RequestHeader("X-User-Id") String userId) {
        
        try {
            // Check if user is authorized to download this file
            Long courrierIdLong = Long.parseLong(courrierId);
            Courrier courrier = courrierServices.getCourrierById(courrierIdLong);
            
            if (courrier == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Check if user is in destinataires list
            boolean isAuthorized = false;
            if (courrier.getDestinatairesList() != null) {
                String[] destinataires = courrier.getDestinatairesList().split(",");
                isAuthorized = Arrays.stream(destinataires)
                    .anyMatch(dest -> dest.trim().equals(userId));
            }
            
            // Also check if user is the destinataire (single recipient)
            if (!isAuthorized && courrier.getDestinataire() != null) {
                isAuthorized = courrier.getDestinataire().equals(userId);
            }
            
            // Check if user is in copies list
            if (!isAuthorized && courrier.getCopiesList() != null) {
                String[] copies = courrier.getCopiesList().split(",");
                isAuthorized = Arrays.stream(copies)
                    .anyMatch(copy -> copy.trim().equals(userId));
            }
            
            if (!isAuthorized) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            
            Path filePath = Paths.get(uploadDir).resolve(filename);
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] fileContent = Files.readAllBytes(filePath);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", courrier.getFileName() != null ? courrier.getFileName() : filename);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileContent);
                    
        } catch (IOException | NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}