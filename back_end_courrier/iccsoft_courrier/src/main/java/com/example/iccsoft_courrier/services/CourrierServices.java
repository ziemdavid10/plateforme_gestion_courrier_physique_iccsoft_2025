// Package contenant les interfaces de services métier
package com.example.iccsoft_courrier.services;

import java.util.List; // Interface pour les listes

import com.example.iccsoft_courrier.models.Courrier; // Modèle Courrier
import com.example.iccsoft_courrier.models.Courrier.EtatCourrier; // Énumération des états

/**
 * Interface définissant les services métier pour la gestion des courriers
 * Contient toutes les opérations CRUD et de recherche sur les courriers
 */
public interface CourrierServices {

    /**
     * Crée un nouveau courrier dans le système
     * @param courrier L'objet courrier à créer
     * @param employeId L'identifiant de l'employé créateur
     * @return Le courrier créé avec son ID généré
     */
    Courrier createCourrier(Courrier courrier, Long employeId);

    /**
     * Récupère un courrier par son identifiant unique
     * @param id L'identifiant du courrier recherché
     * @return Le courrier correspondant ou null si non trouvé
     */
    Courrier getCourrierById(Long id);

    /**
     * Récupère tous les courriers du système
     * @return Liste complète de tous les courriers
     */
    List<Courrier> all();

    /**
     * Met à jour un courrier existant
     * @param courrier L'objet courrier avec les nouvelles données
     * @param courrierId L'identifiant du courrier à modifier
     * @return Le courrier mis à jour
     */
    Courrier updateCourrier(Courrier courrier, Long courrierId);

    /**
     * Supprime un courrier du système
     * @param id L'identifiant du courrier à supprimer
     * @return Message de confirmation de suppression
     */
    String deleteCourrier(Long id);

    /**
     * Recherche les courriers par leur état de traitement
     * @param etat L'état recherché (RECU, EN_ATTENTE, TRAITE, etc.)
     * @return Liste des courriers ayant l'état spécifié
     */
    List<Courrier> getCourriersByEtat(EtatCourrier etat);

    /**
     * Recherche les courriers par leur numéro d'ordre
     * @param numeroOrdre Le numéro d'ordre recherché
     * @return Liste des courriers ayant ce numéro d'ordre
     */
    List<Courrier> getCourriersByNumeroOrdre(String numeroOrdre);

    /**
     * Recherche les courriers par leur date de création
     * @param dateCourrier La date de courrier recherchée (format string)
     * @return Liste des courriers créés à cette date
     */
    List<Courrier> getCourriersByDateCourrier(String dateCourrier);

}
