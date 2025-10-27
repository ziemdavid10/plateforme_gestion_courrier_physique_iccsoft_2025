package com.example.iccsoft_courrier.controller;

import com.example.iccsoft_courrier.dto.CourrierDTO;
import com.example.iccsoft_courrier.models.Courrier;
import com.example.iccsoft_courrier.services.CourrierServices;
import com.example.iccsoft_courrier.services.CourrierStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;
import java.sql.Timestamp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/secretary/courriers")
public class CourrierManagementController {

    @Autowired
    private CourrierServices courrierServices;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private CourrierStatusService statusService;



    @GetMapping
    public ResponseEntity<Object> getAllCourriers() {
        try {
            String sql = "SELECT c.id, c.objet, c.destinataire, c.destinateur, c.description, c.etat, c.statut_courrier, c.numero_ordre, c.date_entree, c.date_courrier, c.date_reception, c.date_modification, c.destinataires_list, c.copies_list, c.file_path, c.file_name, COUNT(pj.id) as pieces_jointes_count FROM courriers c LEFT JOIN pieces_jointes pj ON c.id = pj.courrier_id GROUP BY c.id ORDER BY c.date_entree DESC";
            var courriers = jdbcTemplate.queryForList(sql);
            System.out.println("Returning " + courriers.size() + " courriers with fields: " + (courriers.isEmpty() ? "none" : courriers.get(0).keySet()));
            return ResponseEntity.ok(courriers);
        } catch (Exception e) {
            System.err.println("Error fetching courriers: " + e.getMessage());
            return ResponseEntity.ok(java.util.List.of());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Courrier> getCourrier(@PathVariable Long id, @RequestHeader(value = "X-User-Id", required = false) String userId) {
        Courrier courrier = courrierServices.getCourrierById(id);
        if (courrier != null && userId != null) {
            statusService.updateStatusBasedOnAction(id, CourrierStatusService.ActionType.VIEWED, userId);
        }
        return ResponseEntity.ok(courrier);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createCourrier(@RequestBody Map<String, Object> courrierData, @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            // Validation des champs obligatoires
            String objet = (String) courrierData.get("objet");
            String destinataire = (String) courrierData.get("destinataire");
            String destinateur = (String) courrierData.get("destinateur");
            String description = (String) courrierData.get("description");
            
            // Vérifier que les champs obligatoires ne sont pas vides
            if (objet == null || objet.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "L'objet est obligatoire"));
            }
            if (destinataire == null || destinataire.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Le destinataire est obligatoire"));
            }
            if (destinateur == null || destinateur.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Le destinateur est obligatoire"));
            }
            
            // Generate numero ordre
            LocalDate dateActuelle = LocalDate.now();
            String dataFormatee = dateActuelle.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            long timestamp = System.currentTimeMillis() % 1000;
            String numeroOrdre = String.format("CE-%s-%03d", dataFormatee, timestamp);

            // Prepare SQL with all fields including new ones
            String sql = "INSERT INTO courriers (objet, destinataire, destinateur, description, etat, statut_courrier, numero_ordre, date_entree, date_courrier, date_reception, date_modification, destinataires_list, copies_list, file_path, file_name) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), NOW(), ?, ?, ?, ?)";
            
            String dateCourrier = (String) courrierData.get("dateCourrier");
            String destinatairesList = (String) courrierData.get("destinatairesList");
            String copiesList = (String) courrierData.get("copiesList");
            String filePath = (String) courrierData.get("filePath");
            String fileName = (String) courrierData.get("fileName");
            
            // Convert ISO 8601 date to MySQL compatible format
            Timestamp dateCourrierTimestamp = null;
            if (dateCourrier != null && !dateCourrier.trim().isEmpty()) {
                try {
                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateCourrier);
                    dateCourrierTimestamp = Timestamp.from(zonedDateTime.toInstant());
                } catch (Exception e) {
                    dateCourrierTimestamp = new Timestamp(System.currentTimeMillis());
                }
            } else {
                dateCourrierTimestamp = new Timestamp(System.currentTimeMillis());
            }
            
            // Execute insert avec des valeurs validées
            int result = jdbcTemplate.update(sql, 
                objet.trim(),
                destinataire.trim(),
                destinateur.trim(),
                description != null ? description.trim() : "Aucune description",
                "RECU",
                "PUBLIC",
                numeroOrdre,
                dateCourrierTimestamp,
                destinatairesList != null ? destinatairesList : "",
                copiesList != null ? copiesList : "",
                filePath != null ? filePath : "",
                fileName != null ? fileName : ""
            );

            if (result > 0) {
                // Get the created courrier ID
                String getIdSql = "SELECT id FROM courriers WHERE numero_ordre = ?";
                Long courrierId = jdbcTemplate.queryForObject(getIdSql, Long.class, numeroOrdre);
                
                // Trigger automatic status update
                if (userId != null) {
                    statusService.updateStatusBasedOnAction(courrierId, CourrierStatusService.ActionType.CREATED, userId);
                }
                
                Map<String, Object> response = new HashMap<>();
                Date now = new Date();
                response.put("id", courrierId);
                response.put("objet", objet);
                response.put("destinataire", destinataire);
                response.put("destinateur", destinateur);
                response.put("description", description);
                response.put("etat", "RECU");
                response.put("statutCourrier", "PUBLIC");
                response.put("numeroOrdre", numeroOrdre);
                response.put("dateEntree", now);
                response.put("dateCourrier", dateCourrier != null ? dateCourrier : now);
                response.put("dateReception", now);
                response.put("dateModification", now);
                response.put("destinatairesList", destinatairesList != null ? destinatairesList : "");
                response.put("copiesList", copiesList != null ? copiesList : "");
                response.put("filePath", filePath != null ? filePath : "");
                response.put("fileName", fileName != null ? fileName : "");
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(500).build();
            }
        } catch (Exception e) {
            System.err.println("Error creating courrier: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Courrier> updateCourrier(@PathVariable Long id, @RequestBody CourrierDTO courrierDTO, @RequestHeader("X-User-Id") String userId) {
        Courrier courrier = convertDTOToEntity(courrierDTO);
        Courrier updatedCourrier = courrierServices.updateCourrier(courrier, id);
        
        // Trigger automatic status update
        statusService.updateStatusBasedOnAction(id, CourrierStatusService.ActionType.PROCESSED, userId);
        
        // Notify real-time status change
        notifyStatusChange(updatedCourrier);
        
        return ResponseEntity.ok(updatedCourrier);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Courrier> updateCourrierStatus(@PathVariable Long id, @RequestBody Map<String, String> statusUpdate, @RequestHeader("X-User-Id") String userId) {
        Courrier courrier = courrierServices.getCourrierById(id);
        if (courrier != null) {
            String newStatus = statusUpdate.get("etat");
            courrier.setEtat(newStatus);
            Courrier updatedCourrier = courrierServices.updateCourrier(courrier, id);
            
            // Determine action type based on status
            CourrierStatusService.ActionType actionType = switch (newStatus) {
                case "TRAITE" -> CourrierStatusService.ActionType.PROCESSED;
                case "ARCHIVE" -> CourrierStatusService.ActionType.ARCHIVED;
                case "EN_ATTENTE" -> CourrierStatusService.ActionType.VIEWED;
                default -> CourrierStatusService.ActionType.VIEWED;
            };
            
            // Trigger automatic status update
            statusService.updateStatusBasedOnAction(id, actionType, userId);
            
            // Notify real-time status change
            notifyStatusChange(updatedCourrier);
            
            return ResponseEntity.ok(updatedCourrier);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/recipients")
    public ResponseEntity<Map<String, Object>> getCourrierRecipients(@PathVariable Long id) {
        Courrier courrier = courrierServices.getCourrierById(id);
        if (courrier != null) {
            Map<String, Object> recipients = new HashMap<>();
            
            // Parse destinataires list
            if (courrier.getDestinatairesList() != null) {
                String[] destinataires = courrier.getDestinatairesList().split(",");
                recipients.put("destinataires", Arrays.asList(destinataires));
            }
            
            // Parse copies list
            if (courrier.getCopiesList() != null) {
                String[] copies = courrier.getCopiesList().split(",");
                recipients.put("copies", Arrays.asList(copies));
            }
            
            return ResponseEntity.ok(recipients);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<Map<String, String>> assignCourrier(@PathVariable Long id, @RequestBody Map<String, String> assignData, @RequestHeader("X-User-Id") String userId) {
        String assignedTo = assignData.get("assignedTo");
        
        // Update assignee in database
        String sql = "UPDATE courriers SET destinataire = ?, date_modification = NOW() WHERE id = ?";
        jdbcTemplate.update(sql, assignedTo, id);
        
        // Trigger automatic status update to EN_ATTENTE
        statusService.updateStatusBasedOnAction(id, CourrierStatusService.ActionType.VIEWED, userId);
        
        return ResponseEntity.ok(Map.of("message", "Courrier assigné avec succès"));
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<Map<String, String>> archiveCourrier(@PathVariable Long id, @RequestHeader("X-User-Id") String userId) {
        // Trigger automatic status update
        statusService.updateStatusBasedOnAction(id, CourrierStatusService.ActionType.ARCHIVED, userId);
        
        return ResponseEntity.ok(Map.of("message", "Courrier archivé avec succès"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourrier(@PathVariable Long id) {
        courrierServices.deleteCourrier(id);
        return ResponseEntity.ok().build();
    }

    private Courrier convertDTOToEntity(CourrierDTO dto) {
        Courrier courrier = new Courrier();
        courrier.setObjet(dto.getObjet());
        courrier.setDestinateur(dto.getDestinateur());
        courrier.setNumeroOrdre(dto.getNumeroOrdre());
        courrier.setDescription(dto.getDescription());
        courrier.setStatutCourrier(dto.getStatut() != null ? dto.getStatut().name() : null);
        courrier.setEtat(dto.getEtat() != null ? dto.getEtat().name() : null);
        
        return courrier;
    }

    private void notifyStatusChange(Courrier courrier) {
        if (messagingTemplate != null) {
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "COURRIER_STATUS_CHANGE");
            notification.put("courrierId", courrier.getId());
            notification.put("etat", courrier.getEtat());
            notification.put("objet", courrier.getObjet());
            notification.put("numeroOrdre", courrier.getNumeroOrdre());
            
            // Notify all connected users
            messagingTemplate.convertAndSend("/topic/courrier-updates", notification);
            
            // Notify specific recipients if available
            if (courrier.getDestinatairesList() != null) {
                String[] recipients = courrier.getDestinatairesList().split(",");
                for (String recipient : recipients) {
                    messagingTemplate.convertAndSendToUser(recipient.trim(), "/queue/notifications", notification);
                }
            }
        }
    }
}