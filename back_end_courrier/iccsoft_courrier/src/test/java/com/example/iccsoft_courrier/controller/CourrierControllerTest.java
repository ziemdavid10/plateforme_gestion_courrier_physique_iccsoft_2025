// Package contenant les tests pour les contrôleurs de l'application de gestion de courrier
package com.example.iccsoft_courrier.controller;

// Importation des classes nécessaires pour les tests
import com.example.iccsoft_courrier.models.Courrier; // Modèle de données pour les courriers
import com.example.iccsoft_courrier.services.CourrierServices; // Service métier pour la gestion des courriers
import com.fasterxml.jackson.databind.ObjectMapper; // Utilitaire pour la sérialisation/désérialisation JSON
import org.junit.jupiter.api.BeforeEach; // Annotation pour l'initialisation avant chaque test
import org.junit.jupiter.api.Test; // Annotation pour marquer les méthodes de test
import org.springframework.beans.factory.annotation.Autowired; // Injection de dépendances Spring
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest; // Test d'intégration pour les contrôleurs web
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc; // Configuration automatique de MockMvc
import org.springframework.boot.test.mock.mockito.MockBean; // Création de mocks pour les beans Spring
import org.springframework.http.MediaType; // Types de contenu HTTP
import org.springframework.test.web.servlet.MockMvc; // Framework de test pour les contrôleurs Spring MVC

import java.util.Arrays; // Utilitaire pour créer des listes
import java.util.Date; // Classe pour les dates

// Importations statiques pour les matchers et builders de test
import static org.mockito.ArgumentMatchers.*; // Matchers pour les arguments Mockito
import static org.mockito.Mockito.eq; // Matcher d'égalité Mockito
import static org.mockito.Mockito.when; // Configuration des comportements des mocks
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*; // Builders pour les requêtes HTTP de test
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*; // Matchers pour les résultats de test
// Annotation pour tester uniquement la couche web (contrôleurs) sans charger tout le contexte Spring
@WebMvcTest(controllers = CourrierManagementController.class)
// Configuration de MockMvc sans les filtres de sécurité pour simplifier les tests
@AutoConfigureMockMvc(addFilters = false)
class CourrierControllerTest {

    // Injection de MockMvc pour simuler les requêtes HTTP dans les tests
    @Autowired
    private MockMvc mockMvc;

    // Mock du service de gestion des courriers pour isoler les tests du contrôleur
    @MockBean
    private CourrierServices courrierServices;
    
    // Mock du template JDBC pour simuler les interactions avec la base de données
    @MockBean
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;
    
    // Mock du service de gestion des statuts de courrier
    @MockBean
    private com.example.iccsoft_courrier.services.CourrierStatusService statusService;
    
    // Mock du template de messagerie pour les notifications en temps réel
    @MockBean
    private org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    // Injection de l'ObjectMapper pour la conversion JSON
    @Autowired
    private ObjectMapper objectMapper;

    // Objet courrier de test utilisé dans plusieurs méthodes de test
    private Courrier testCourrier;

    // Méthode d'initialisation exécutée avant chaque test
    @BeforeEach
    void setUp() {
        // Création d'un courrier de test avec des données fictives
        testCourrier = new Courrier();
        testCourrier.setId(1L); // Identifiant unique du courrier
        testCourrier.setObjet("Test Courrier"); // Objet/sujet du courrier
        testCourrier.setDestinateur("sender"); // Expéditeur du courrier
        testCourrier.setDestinataire("recipient"); // Destinataire du courrier
        testCourrier.setDateCourrier(new Date()); // Date de création du courrier
        testCourrier.setDateEntree(new Date()); // Date d'entrée dans le système
        testCourrier.setDescription("Test description"); // Description détaillée
        testCourrier.setEtat("En attente"); // État de traitement du courrier
        testCourrier.setStatutCourrier("Public"); // Statut de confidentialité
    }

    // Test de récupération de tous les courriers
    @Test
    void getAllCourriers_ShouldReturnCourriersList() throws Exception {
        // Configuration du mock : simulation d'une requête SQL retournant une liste vide
        when(jdbcTemplate.queryForList(any(String.class))).thenReturn(Arrays.asList());

        // Exécution d'une requête GET sur l'endpoint de récupération des courriers
        mockMvc.perform(get("/v1/api/secretary/courriers"))
                .andExpect(status().isOk()); // Vérification que la réponse HTTP est 200 OK
    }

    // Test de création d'un courrier avec le rôle secrétaire
    @Test
    void createCourrier_WithSecretaireRole_ShouldCreateCourrier() throws Exception {
        // Configuration des mocks pour simuler une insertion réussie en base de données
        when(jdbcTemplate.update(any(String.class), (Object[]) any())).thenReturn(1); // Simulation d'une ligne affectée
        when(jdbcTemplate.queryForObject(any(String.class), eq(Long.class), any(String.class))).thenReturn(1L); // Simulation de récupération de l'ID généré

        // Exécution d'une requête POST avec des données JSON valides
        mockMvc.perform(post("/v1/api/secretary/courriers")
                .contentType(MediaType.APPLICATION_JSON) // Définition du type de contenu JSON
                .content("{\"objet\":\"Test\",\"destinataire\":\"test\",\"destinateur\":\"admin\",\"description\":\"test\"}")) // Corps de la requête JSON
                .andExpect(status().isOk()); // Vérification du succès de la création
    }

    // Test de création d'un courrier avec des données manquantes
    @Test
    void createCourrier_WithMissingData_ShouldReturnBadRequest() throws Exception {
        // Exécution d'une requête POST avec un objet JSON vide (données manquantes)
        mockMvc.perform(post("/v1/api/secretary/courriers")
                .contentType(MediaType.APPLICATION_JSON) // Type de contenu JSON
                .content("{}")) // Corps vide, données obligatoires manquantes
                .andExpect(status().isBadRequest()); // Vérification que la réponse est 400 Bad Request
    }

    // Test de mise à jour du statut d'un courrier
    @Test
    void updateCourrier_ShouldUpdateStatus() throws Exception {
        // Configuration des mocks pour simuler une mise à jour réussie
        when(jdbcTemplate.update(any(String.class), (Object[]) any())).thenReturn(1); // Simulation d'une ligne mise à jour
        when(courrierServices.getCourrierById(1L)).thenReturn(testCourrier); // Simulation de récupération du courrier existant

        // Exécution d'une requête PUT pour mettre à jour le statut du courrier avec l'ID 1
        mockMvc.perform(put("/v1/api/secretary/courriers/1/status")
                .contentType(MediaType.APPLICATION_JSON) // Type de contenu JSON
                .content("{\"etat\":\"TRAITE\"}")) // Nouveau statut à appliquer
                .andExpect(status().isOk()); // Vérification du succès de la mise à jour
    }

    // Test de suppression d'un courrier
    @Test
    void deleteCourrier_ShouldDeleteSuccessfully() throws Exception {
        // Configuration du mock pour simuler une suppression réussie
        when(courrierServices.deleteCourrier(1L)).thenReturn("Courrier supprimé"); // Message de confirmation de suppression

        // Exécution d'une requête DELETE pour supprimer le courrier avec l'ID 1
        mockMvc.perform(delete("/v1/api/secretary/courriers/1"))
                .andExpect(status().isOk()); // Vérification du succès de la suppression
    }
}