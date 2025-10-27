package com.example.iccsoft_courrier.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.example.iccsoft_courrier.models.Courrier;
import com.example.iccsoft_courrier.models.PieceJointe;
import com.example.iccsoft_courrier.services.CourrierServices;
import com.example.iccsoft_courrier.services.PieceJointeServices;
import com.example.iccsoft_courrier.services.CourrierStatusService;

import java.util.HashMap;
import java.util.Map;

import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/v1/api/employee")
@CrossOrigin(origins = "*")
public class EmployeeCourrierController {

    private final CourrierServices courrierServices;
    private final PieceJointeServices pieceJointeServices;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;
    
    @Autowired
    private CourrierStatusService statusService;

    public EmployeeCourrierController(CourrierServices courrierServices, PieceJointeServices pieceJointeServices) {
        this.courrierServices = courrierServices;
        this.pieceJointeServices = pieceJointeServices;
    }

    @GetMapping("/courriers/{id}/attachments")
    public ResponseEntity<List<PieceJointe>> getAttachments(@PathVariable Long id) {
        try {
            List<PieceJointe> attachments = pieceJointeServices.getPieceJointesByCourrierId(id);
            System.out.println("Found " + attachments.size() + " attachments for courrier " + id);
            return ResponseEntity.ok(attachments);
        } catch (Exception e) {
            System.err.println("Error fetching attachments for courrier " + id + ": " + e.getMessage());
            return ResponseEntity.ok(java.util.List.of());
        }
    }

    @GetMapping("/courriers/{id}/attachments/{filename}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable Long id, @PathVariable String filename) {
        try {
            // Simuler le téléchargement d'une pièce jointe
            byte[] content = ("Contenu de la pièce jointe: " + filename).getBytes();
            ByteArrayResource resource = new ByteArrayResource(content);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/courriers/{id}/download")
    public ResponseEntity<Resource> downloadCourrier(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean includeAttachments) {
        try {
            Courrier courrier = courrierServices.getCourrierById(id);
            if (courrier == null) {
                return ResponseEntity.notFound().build();
            }

            if (includeAttachments) {
                // Créer un ZIP avec le PDF et les pièces jointes
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ZipOutputStream zos = new ZipOutputStream(baos);

                // Ajouter le PDF principal
                ZipEntry pdfEntry = new ZipEntry(courrier.getObjet() + ".pdf");
                zos.putNextEntry(pdfEntry);
                zos.write(("Contenu PDF du courrier: " + courrier.getObjet()).getBytes());
                zos.closeEntry();

                // Ajouter les pièces jointes
                List<PieceJointe> attachments = pieceJointeServices.getPieceJointesByCourrierId(id);
                for (PieceJointe attachment : attachments) {
                    ZipEntry attachmentEntry = new ZipEntry("attachments/" + attachment.getFileName());
                    zos.putNextEntry(attachmentEntry);
                    zos.write(("Contenu de: " + attachment.getFileName()).getBytes());
                    zos.closeEntry();
                }

                zos.close();
                ByteArrayResource resource = new ByteArrayResource(baos.toByteArray());

                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                                "attachment; filename=\"" + courrier.getObjet() + "_avec_pieces_jointes.zip\"")
                        .body(resource);
            } else {
                // Télécharger seulement le PDF
                byte[] pdfContent = ("Contenu PDF du courrier: " + courrier.getObjet()).getBytes();
                ByteArrayResource resource = new ByteArrayResource(pdfContent);

                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_PDF)
                        .header(HttpHeaders.CONTENT_DISPOSITION, 
                                "attachment; filename=\"" + courrier.getObjet() + ".pdf\"")
                        .body(resource);
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/courriers/{id}/archive")
    public ResponseEntity<Courrier> archiveCourrier(@PathVariable Long id, @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            String sql = "UPDATE courriers SET etat = 'ARCHIVE', date_modification = NOW() WHERE id = ?";
            int result = jdbcTemplate.update(sql, id);
            
            if (result > 0) {
                statusService.updateStatusBasedOnAction(id, CourrierStatusService.ActionType.ARCHIVED, userId);
                Courrier courrier = courrierServices.getCourrierById(id);
                notifyStatusChange(courrier);
                return ResponseEntity.ok(courrier);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    private void notifyStatusChange(Courrier courrier) {
        if (messagingTemplate != null) {
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "COURRIER_STATUS_CHANGE");
            notification.put("courrierId", courrier.getId());
            notification.put("etat", courrier.getEtat());
            notification.put("objet", courrier.getObjet());
            notification.put("numeroOrdre", courrier.getNumeroOrdre());
            
            messagingTemplate.convertAndSend("/topic/courrier-updates", notification);
            
            if (courrier.getDestinatairesList() != null) {
                String[] recipients = courrier.getDestinatairesList().split(",");
                for (String recipient : recipients) {
                    messagingTemplate.convertAndSendToUser(recipient.trim(), "/queue/notifications", notification);
                }
            }
        }
    }

    @PutMapping("/courriers/{id}/mark-read")
    public ResponseEntity<Courrier> markAsRead(@PathVariable Long id, @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            String sql = "UPDATE courriers SET etat = 'LU', date_modification = NOW() WHERE id = ?";
            int result = jdbcTemplate.update(sql, id);
            
            if (result > 0) {
                statusService.updateStatusBasedOnAction(id, CourrierStatusService.ActionType.VIEWED, userId);
                Courrier courrier = courrierServices.getCourrierById(id);
                notifyStatusChange(courrier);
                return ResponseEntity.ok(courrier);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/courriers/{id}/mark-important")
    public ResponseEntity<Courrier> markAsImportant(@PathVariable Long id, @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            String sql = "UPDATE courriers SET statut_courrier = 'IMPORTANT', date_modification = NOW() WHERE id = ?";
            int result = jdbcTemplate.update(sql, id);
            
            if (result > 0) {
                statusService.updateStatusBasedOnAction(id, CourrierStatusService.ActionType.PROCESSED, userId);
                Courrier courrier = courrierServices.getCourrierById(id);
                notifyStatusChange(courrier);
                return ResponseEntity.ok(courrier);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}