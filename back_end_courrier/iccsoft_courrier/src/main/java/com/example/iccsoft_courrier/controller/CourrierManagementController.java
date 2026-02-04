// Package contenant les contrôleurs REST de l'application
package com.example.iccsoft_courrier.controller;

// Importations des classes métier et DTO
import com.example.iccsoft_courrier.dto.CourrierDTO; // Objet de transfert de données pour les courriers
import com.example.iccsoft_courrier.models.Courrier; // Modèle de données Courrier
import com.example.iccsoft_courrier.services.CourrierServices; // Service métier pour les courriers
import com.example.iccsoft_courrier.services.CourrierStatusService; // Service de gestion des statuts
import com.example.iccsoft_courrier.services.EmailNotificationService; // Service de notification email

// Importations Spring Framework
import org.springframework.beans.factory.annotation.Autowired; // Injection de dépendances
import org.springframework.http.ResponseEntity; // Réponses HTTP
import org.springframework.messaging.simp.SimpMessagingTemplate; // Messagerie temps réel WebSocket
import org.springframework.web.bind.annotation.*; // Annotations REST
import org.springframework.jdbc.core.JdbcTemplate; // Accès direct à la base de données

// Importations pour la gestion des dates et du temps
import java.util.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;
import java.sql.Timestamp;

// Importations pour les collections et structures de données
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des courriers par les secrétaires
 * Fournit les endpoints CRUD pour les opérations sur les courriers
 */
@RestController // Indique que cette classe est un contrôleur REST
@RequestMapping("/v1/api/secretary/courriers") // Préfixe des URLs pour tous les endpoints
public class CourrierManagementController {

    // Injection du service métier pour les opérations sur les courriers
    @Autowired
    private CourrierServices courrierServices;

    // Injection du template JDBC pour les requêtes SQL directes
    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Injection optionnelle du template de messagerie pour les notifications temps réel
    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;

    // Injection du service de gestion des statuts de courrier
    @Autowired
    private CourrierStatusService statusService;

    // Injection du service de notification par email
    @Autowired
    private EmailNotificationService emailNotificationService;



    /**
     * Récupère la liste de tous les courriers avec leurs pièces jointes
     * @return Liste des courriers triés par date d'entrée décroissante
     */
    @GetMapping
    public ResponseEntity<Object> getAllCourriers() {
        try {
            // Requête SQL complexe joignant courriers et pièces jointes avec comptage
            String sql = "SELECT c.id, c.objet, c.destinataire, c.destinateur, c.description, c.etat, c.statut_courrier, c.numero_ordre, c.date_entree, c.date_courrier, c.date_reception, c.date_modification, c.destinataires_list, c.copies_list, c.file_path, c.file_name, COUNT(pj.id) as pieces_jointes_count FROM courriers c LEFT JOIN pieces_jointes pj ON c.id = pj.courrier_id GROUP BY c.id ORDER BY c.date_entree DESC";
            var courriers = jdbcTemplate.queryForList(sql); // Exécution de la requête
            System.out.println("Returning " + courriers.size() + " courriers with fields: " + (courriers.isEmpty() ? "none" : courriers.get(0).keySet()));
            return ResponseEntity.ok(courriers); // Retour de la liste des courriers
        } catch (Exception e) {
            System.err.println("Error fetching courriers: " + e.getMessage());
            return ResponseEntity.ok(java.util.List.of()); // Retour d'une liste vide en cas d'erreur
        }
    }

    /**
     * Récupère un courrier spécifique par son ID
     * Met automatiquement à jour le statut comme "VU" si un utilisateur est identifié
     * @param id Identifiant du courrier
     * @param userId Identifiant de l'utilisateur (optionnel, via header HTTP)
     * @return Le courrier demandé
     */
    @GetMapping("/{id}")
    public ResponseEntity<Courrier> getCourrier(@PathVariable Long id, @RequestHeader(value = "X-User-Id", required = false) String userId) {
        Courrier courrier = courrierServices.getCourrierById(id); // Récupération du courrier
        if (courrier != null && userId != null) {
            // Mise à jour automatique du statut comme "VU" si utilisateur identifié
            statusService.updateStatusBasedOnAction(id, CourrierStatusService.ActionType.VIEWED, userId);
        }
        return ResponseEntity.ok(courrier);
    }

    /**
     * Crée un nouveau courrier dans le système
     * Valide les données obligatoires et génère automatiquement un numéro d'ordre
     * @param courrierData Données du courrier à créer
     * @param userId Identifiant de l'utilisateur créateur (optionnel)
     * @return Le courrier créé avec toutes ses informations
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCourrier(@RequestBody Map<String, Object> courrierData, @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            // Extraction et validation des champs obligatoires depuis les données reçues
            String objet = (String) courrierData.get("objet");
            String destinataire = (String) courrierData.get("destinataire");
            String destinateur = (String) courrierData.get("destinateur");
            String description = (String) courrierData.get("description");
            
            // Validation : vérification que les champs obligatoires ne sont pas vides
            if (objet == null || objet.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "L'objet est obligatoire"));
            }
            if (destinataire == null || destinataire.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Le destinataire est obligatoire"));
            }
            if (destinateur == null || destinateur.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Le destinateur est obligatoire"));
            }
            
            // Génération automatique du numéro d'ordre unique
            LocalDate dateActuelle = LocalDate.now(); // Date actuelle
            String dataFormatee = dateActuelle.format(DateTimeFormatter.ofPattern("yyyyMMdd")); // Format AAAAMMJJ
            long timestamp = System.currentTimeMillis() % 1000; // 3 derniers chiffres du timestamp
            String numeroOrdre = String.format("CE-%s-%03d", dataFormatee, timestamp); // Format: CE-AAAAMMJJ-XXX

            // Préparation de la requête SQL d'insertion avec tous les champs
            String sql = "INSERT INTO courriers (objet, destinataire, destinateur, description, etat, statut_courrier, numero_ordre, date_entree, date_courrier, date_reception, date_modification, destinataires_list, copies_list, file_path, file_name) VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), ?, NOW(), NOW(), ?, ?, ?, ?)";
            
            // Extraction des champs optionnels
            String dateCourrier = (String) courrierData.get("dateCourrier"); // Date du courrier
            String destinatairesList = (String) courrierData.get("destinatairesList"); // Liste des destinataires
            String copiesList = (String) courrierData.get("copiesList"); // Liste des copies
            String filePath = (String) courrierData.get("filePath"); // Chemin du fichier joint
            String fileName = (String) courrierData.get("fileName"); // Nom du fichier joint
            
            // Conversion de la date ISO 8601 vers un format compatible MySQL
            Timestamp dateCourrierTimestamp = null;
            if (dateCourrier != null && !dateCourrier.trim().isEmpty()) {
                try {
                    // Tentative de parsing de la date au format ISO 8601
                    ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateCourrier);
                    dateCourrierTimestamp = Timestamp.from(zonedDateTime.toInstant());
                } catch (Exception e) {
                    // En cas d'erreur, utiliser la date actuelle
                    dateCourrierTimestamp = new Timestamp(System.currentTimeMillis());
                }
            } else {
                // Si pas de date fournie, utiliser la date actuelle
                dateCourrierTimestamp = new Timestamp(System.currentTimeMillis());
            }
            
            // Exécution de l'insertion avec des valeurs validées et nettoyées
            int result = jdbcTemplate.update(sql, 
                objet.trim(), // Objet nettoyé
                destinataire.trim(), // Destinataire nettoyé
                destinateur.trim(), // Destinateur nettoyé
                description != null ? description.trim() : "Aucune description", // Description ou valeur par défaut
                "RECU", // État initial : REÇU
                "PUBLIC", // Statut initial : PUBLIC
                numeroOrdre, // Numéro d'ordre généré
                dateCourrierTimestamp, // Date du courrier formatée
                destinatairesList != null ? destinatairesList : "", // Liste destinataires ou vide
                copiesList != null ? copiesList : "", // Liste copies ou vide
                filePath != null ? filePath : "", // Chemin fichier ou vide
                fileName != null ? fileName : "" // Nom fichier ou vide
            );

            if (result > 0) {
                // Récupération de l'ID du courrier créé via son numéro d'ordre unique
                String getIdSql = "SELECT id FROM courriers WHERE numero_ordre = ?";
                Long courrierId = jdbcTemplate.queryForObject(getIdSql, Long.class, numeroOrdre);
                
                // Ajouter à l'historique de statut
                String historySql = "INSERT INTO courrier_status_history (courrier_id, ancien_statut, nouveau_statut, utilisateur, date_changement, commentaire) VALUES (?, ?, ?, ?, NOW(), ?)";
                jdbcTemplate.update(historySql, courrierId, null, "RECU", userId != null ? userId : "system", "Courrier créé dans le système");
                
                // Créer l'objet Courrier pour la notification email
                Date now = new Date(); // Date actuelle pour les timestamps
                Courrier newCourrier = new Courrier();
                newCourrier.setId(courrierId);
                newCourrier.setObjet(objet);
                newCourrier.setDestinataire(destinataire);
                newCourrier.setDestinateur(destinateur);
                newCourrier.setDescription(description);
                newCourrier.setNumeroOrdre(numeroOrdre);
                newCourrier.setDateReception(now);
                newCourrier.setDestinatairesList(destinatairesList);
                newCourrier.setCopiesList(copiesList);
                
                // Envoyer les notifications email
                emailNotificationService.sendNewCourrierNotification(newCourrier);
                
                // Construction de la réponse avec toutes les informations du courrier créé
                Map<String, Object> response = new HashMap<>();
                response.put("id", courrierId); // ID généré
                response.put("objet", objet); // Objet du courrier
                response.put("destinataire", destinataire); // Destinataire principal
                response.put("destinateur", destinateur); // Expéditeur
                response.put("description", description); // Description
                response.put("etat", "RECU"); // État initial
                response.put("statutCourrier", "PUBLIC"); // Statut initial
                response.put("numeroOrdre", numeroOrdre); // Numéro d'ordre généré
                response.put("dateEntree", now); // Date d'entrée
                response.put("dateCourrier", dateCourrier != null ? dateCourrier : now); // Date du courrier
                response.put("dateReception", now); // Date de réception
                response.put("dateModification", now); // Date de modification
                response.put("destinatairesList", destinatairesList != null ? destinatairesList : ""); // Liste destinataires
                response.put("copiesList", copiesList != null ? copiesList : ""); // Liste copies
                response.put("filePath", filePath != null ? filePath : ""); // Chemin fichier
                response.put("fileName", fileName != null ? fileName : ""); // Nom fichier
                
                return ResponseEntity.ok(response); // Retour de la réponse de succès
            } else {
                return ResponseEntity.status(500).build(); // Erreur si insertion échouée
            }
        } catch (Exception e) {
            System.err.println("Error creating courrier: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build(); // Erreur serveur en cas d'exception
        }
    }

    /**
     * Met à jour un courrier existant
     * Déclenche automatiquement une mise à jour de statut et une notification temps réel
     * @param id Identifiant du courrier à modifier
     * @param courrierDTO Nouvelles données du courrier
     * @param userId Identifiant de l'utilisateur effectuant la modification
     * @return Le courrier mis à jour
     */
    @PutMapping("/{id}")
    public ResponseEntity<Courrier> updateCourrier(@PathVariable Long id, @RequestBody CourrierDTO courrierDTO, @RequestHeader("X-User-Id") String userId) {
        Courrier courrier = convertDTOToEntity(courrierDTO); // Conversion DTO vers entité
        Courrier updatedCourrier = courrierServices.updateCourrier(courrier, id); // Mise à jour via le service
        
        // Déclenchement de la mise à jour automatique du statut comme "TRAITÉ"
        statusService.updateStatusBasedOnAction(id, CourrierStatusService.ActionType.PROCESSED, userId);
        
        // Notification temps réel du changement de statut
        notifyStatusChange(updatedCourrier);
        
        return ResponseEntity.ok(updatedCourrier);
    }

    /**
     * Met à jour uniquement le statut d'un courrier
     * Détermine automatiquement le type d'action basé sur le nouveau statut
     * @param id Identifiant du courrier
     * @param statusUpdate Map contenant le nouveau statut
     * @param userId Identifiant de l'utilisateur
     * @return Le courrier avec le statut mis à jour
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Courrier> updateCourrierStatus(@PathVariable Long id, @RequestBody Map<String, String> statusUpdate, @RequestHeader("X-User-Id") String userId) {
        Courrier courrier = courrierServices.getCourrierById(id); // Récupération du courrier
        if (courrier != null) {
            String oldStatus = courrier.getEtat(); // Ancien statut
            String newStatus = statusUpdate.get("etat"); // Extraction du nouveau statut
            courrier.setEtat(newStatus); // Application du nouveau statut
            Courrier updatedCourrier = courrierServices.updateCourrier(courrier, id); // Sauvegarde
            
            // Ajouter à l'historique de statut
            String historySql = "INSERT INTO courrier_status_history (courrier_id, ancien_statut, nouveau_statut, utilisateur, date_changement, commentaire) VALUES (?, ?, ?, ?, NOW(), ?)";
            String commentaire = "Changement de statut via interface";
            jdbcTemplate.update(historySql, id, oldStatus, newStatus, userId, commentaire);
            
            // Notification temps réel du changement de statut
            notifyStatusChange(updatedCourrier);
            
            return ResponseEntity.ok(updatedCourrier);
        }
        return ResponseEntity.notFound().build(); // Courrier non trouvé
    }

    /**
     * Récupère la liste des destinataires et copies d'un courrier
     * Parse les listes stockées sous forme de chaînes séparées par des virgules
     * @param id Identifiant du courrier
     * @return Map contenant les listes de destinataires et de copies
     */
    @GetMapping("/{id}/recipients")
    public ResponseEntity<Map<String, Object>> getCourrierRecipients(@PathVariable Long id) {
        Courrier courrier = courrierServices.getCourrierById(id); // Récupération du courrier
        if (courrier != null) {
            Map<String, Object> recipients = new HashMap<>();
            
            // Parsing de la liste des destinataires (format CSV)
            if (courrier.getDestinatairesList() != null) {
                String[] destinataires = courrier.getDestinatairesList().split(","); // Séparation par virgules
                recipients.put("destinataires", Arrays.asList(destinataires)); // Conversion en liste
            }
            
            // Parsing de la liste des copies (format CSV)
            if (courrier.getCopiesList() != null) {
                String[] copies = courrier.getCopiesList().split(","); // Séparation par virgules
                recipients.put("copies", Arrays.asList(copies)); // Conversion en liste
            }
            
            return ResponseEntity.ok(recipients);
        }
        return ResponseEntity.notFound().build(); // Courrier non trouvé
    }

    /**
     * Assigne un courrier à un utilisateur spécifique
     * Met à jour le destinataire et déclenche un changement de statut
     * @param id Identifiant du courrier
     * @param assignData Map contenant l'utilisateur assigné
     * @param userId Identifiant de l'utilisateur effectuant l'assignation
     * @return Message de confirmation
     */
    @PostMapping("/{id}/assign")
    public ResponseEntity<Map<String, String>> assignCourrier(@PathVariable Long id, @RequestBody Map<String, String> assignData, @RequestHeader("X-User-Id") String userId) {
        String assignedTo = assignData.get("assignedTo"); // Récupération de l'utilisateur assigné
        
        // Mise à jour du destinataire en base de données
        String sql = "UPDATE courriers SET destinataire = ?, date_modification = NOW() WHERE id = ?";
        jdbcTemplate.update(sql, assignedTo, id);
        
        // Déclenchement de la mise à jour automatique du statut vers EN_ATTENTE
        statusService.updateStatusBasedOnAction(id, CourrierStatusService.ActionType.VIEWED, userId);
        
        return ResponseEntity.ok(Map.of("message", "Courrier assigné avec succès"));
    }

    /**
     * Archive un courrier en changeant son statut
     * @param id Identifiant du courrier à archiver
     * @param userId Identifiant de l'utilisateur effectuant l'archivage
     * @return Message de confirmation
     */
    @PostMapping("/{id}/archive")
    public ResponseEntity<Map<String, String>> archiveCourrier(@PathVariable Long id, @RequestHeader("X-User-Id") String userId) {
        // Déclenchement de la mise à jour automatique du statut vers ARCHIVÉ
        statusService.updateStatusBasedOnAction(id, CourrierStatusService.ActionType.ARCHIVED, userId);
        
        return ResponseEntity.ok(Map.of("message", "Courrier archivé avec succès"));
    }

    /**
     * Récupère l'historique des changements de statut d'un courrier
     * @param id Identifiant du courrier
     * @return Liste des changements de statut
     */
    @GetMapping("/{id}/status-history")
    public ResponseEntity<List<Map<String, Object>>> getStatusHistory(@PathVariable Long id) {
        String sql = "SELECT ancien_statut, nouveau_statut, utilisateur, date_changement, commentaire FROM courrier_status_history WHERE courrier_id = ? ORDER BY date_changement ASC";
        List<Map<String, Object>> history = jdbcTemplate.queryForList(sql, id);
        return ResponseEntity.ok(history);
    }

    /**
     * Supprime définitivement un courrier du système
     * @param id Identifiant du courrier à supprimer
     * @return Réponse vide avec statut de succès
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourrier(@PathVariable Long id) {
        courrierServices.deleteCourrier(id); // Suppression via le service métier
        return ResponseEntity.ok().build(); // Confirmation de suppression
    }

    /**
     * Convertit un DTO (Data Transfer Object) en entité Courrier
     * Méthode utilitaire pour la transformation des données
     * @param dto Objet de transfert de données
     * @return Entité Courrier correspondante
     */
    private Courrier convertDTOToEntity(CourrierDTO dto) {
        Courrier courrier = new Courrier(); // Création d'une nouvelle entité
        courrier.setObjet(dto.getObjet()); // Copie de l'objet
        courrier.setDestinateur(dto.getDestinateur()); // Copie du destinateur
        courrier.setNumeroOrdre(dto.getNumeroOrdre()); // Copie du numéro d'ordre
        courrier.setDescription(dto.getDescription()); // Copie de la description
        // Conversion des énumérations en chaînes de caractères
        courrier.setStatutCourrier(dto.getStatut() != null ? dto.getStatut().name() : null);
        courrier.setEtat(dto.getEtat() != null ? dto.getEtat().name() : null);
        
        return courrier;
    }

    /**
     * Envoie des notifications temps réel lors des changements de statut
     * Utilise WebSocket pour notifier tous les utilisateurs connectés et les destinataires spécifiques
     * @param courrier Le courrier dont le statut a changé
     */
    private void notifyStatusChange(Courrier courrier) {
        if (messagingTemplate != null) { // Vérification que le template de messagerie est disponible
            // Construction de la notification
            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "COURRIER_STATUS_CHANGE"); // Type de notification
            notification.put("courrierId", courrier.getId()); // ID du courrier
            notification.put("etat", courrier.getEtat()); // Nouvel état
            notification.put("objet", courrier.getObjet()); // Objet du courrier
            notification.put("numeroOrdre", courrier.getNumeroOrdre()); // Numéro d'ordre
            
            // Notification broadcast à tous les utilisateurs connectés
            messagingTemplate.convertAndSend("/topic/courrier-updates", notification);
            
            // Notifications ciblées aux destinataires spécifiques
            if (courrier.getDestinatairesList() != null) {
                String[] recipients = courrier.getDestinatairesList().split(","); // Séparation des destinataires
                for (String recipient : recipients) {
                    // Envoi de notification personnalisée à chaque destinataire
                    messagingTemplate.convertAndSendToUser(recipient.trim(), "/queue/notifications", notification);
                }
            }
        }
    }
}