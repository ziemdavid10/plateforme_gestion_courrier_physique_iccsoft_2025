// Service de notification par email pour les courriers
package com.example.iccsoft_courrier.services;

import com.example.iccsoft_courrier.models.Courrier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

/**
 * Service de notification par email
 * Envoie des notifications automatiques lors de la réception de nouveaux courriers
 */
@Service
public class EmailNotificationService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Envoie une notification email pour un nouveau courrier
     * @param courrier Le courrier créé
     */
    public void sendNewCourrierNotification(Courrier courrier) {
        try {
            // Récupérer l'email du destinataire principal
            String recipientEmail = getUserEmail(courrier.getDestinataire());
            if (recipientEmail != null) {
                sendEmailNotification(recipientEmail, courrier, "destinataire principal");
            }

            // Notifier les destinataires additionnels
            if (courrier.getDestinatairesList() != null && !courrier.getDestinatairesList().isEmpty()) {
                String[] additionalRecipients = courrier.getDestinatairesList().split(",");
                for (String recipient : additionalRecipients) {
                    String email = getUserEmail(recipient.trim());
                    if (email != null) {
                        sendEmailNotification(email, courrier, "destinataire");
                    }
                }
            }

            // Notifier les personnes en copie
            if (courrier.getCopiesList() != null && !courrier.getCopiesList().isEmpty()) {
                String[] copies = courrier.getCopiesList().split(",");
                for (String copy : copies) {
                    String email = getUserEmail(copy.trim());
                    if (email != null) {
                        sendEmailNotification(email, courrier, "copie");
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi de notification email: " + e.getMessage());
        }
    }

    /**
     * Récupère l'email d'un utilisateur par son nom d'utilisateur
     */
    private String getUserEmail(String username) {
        try {
            String sql = "SELECT email FROM employes WHERE username = ?";
            List<Map<String, Object>> results = jdbcTemplate.queryForList(sql, username);
            if (!results.isEmpty()) {
                return (String) results.get(0).get("email");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération de l'email pour " + username + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Envoie l'email de notification
     */
    private void sendEmailNotification(String recipientEmail, Courrier courrier, String role) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipientEmail);
            message.setSubject("Nouveau courrier reçu - " + courrier.getObjet());
            
            String emailBody = String.format(
                "Bonjour,\n\n" +
                "Vous avez reçu un nouveau courrier en tant que %s.\n\n" +
                "Détails du courrier :\n" +
                "- Objet : %s\n" +
                "- Expéditeur : %s\n" +
                "- Numéro d'ordre : %s\n" +
                "- Date de réception : %s\n" +
                "- Description : %s\n\n" +
                "Veuillez vous connecter à l'application pour consulter ce courrier.\n\n" +
                "Cordialement,\n" +
                "Système de gestion de courrier ICCSOFT",
                role,
                courrier.getObjet(),
                courrier.getDestinateur(),
                courrier.getNumeroOrdre(),
                courrier.getDateReception(),
                courrier.getDescription()
            );
            
            message.setText(emailBody);
            message.setFrom("noreply@iccsoft.com");
            
            mailSender.send(message);
            System.out.println("Email envoyé à " + recipientEmail + " pour le courrier " + courrier.getNumeroOrdre());
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi d'email à " + recipientEmail + ": " + e.getMessage());
        }
    }
}