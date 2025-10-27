package com.example.iccsoft_courrier.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.iccsoft_courrier.exception.CourrierExceptions;
import com.example.iccsoft_courrier.models.Courrier;
import com.example.iccsoft_courrier.models.Courrier.EtatCourrier;
import com.example.iccsoft_courrier.models.Employe;
import com.example.iccsoft_courrier.models.PieceJointe;
import com.example.iccsoft_courrier.repositories.CourrierRepository;
import com.example.iccsoft_courrier.repositories.PieceJointeRepository;
import com.example.iccsoft_courrier.services.CourrierServices;
import com.example.iccsoft_courrier.services.OrderServices;

@Service
public class CourrierServicesImpl implements CourrierServices {
    private final CourrierRepository cR;
    private final OrderServices oS;
    private final PieceJointeRepository pjR;

    public CourrierServicesImpl(CourrierRepository cR, OrderServices oS, PieceJointeRepository pjR) {
        this.cR = cR;
        this.oS = oS;
        this.pjR = pjR;
    }

    @Override
    public Courrier createCourrier(Courrier courrier, Long employeId) {
        // lorsque je crée un courrier, je peux lui associer autant de des destinataires
        // que je veux
        // et les destinataires sont des employés de l'entreprise.
        // Validation will be done using destinatairesList field
        // Vérifier si l'employé existe
        if (employeId == null) {
            throw new CourrierExceptions("Employee ID must be provided");
        }
        // Récupérer l'employé à partir de l'ID
        if (!oS.existsById(employeId)) {
            throw new CourrierExceptions("Employee with ID: " + employeId + " does not exist");
        }
        // Associer l'employé aux destinataires du courrier
        // Ici, on suppose que le courrier a un champ destinataires qui est une liste
        // d'Employe
        // On ajoute l'employé récupéré à la liste des destinataires
        // Si le courrier n'a pas de destinataires, on crée une nouvelle liste
        // Employee association will be handled via destinatairesList field
        Employe employe = oS.getEmployeFromServiceUser(employeId);
        // File attachments are now handled via filePath and fileName fields
        // No need for separate PieceJointe entities
        // Générer le numéro d'ordre pour le courrier
        String numeroOrdre = courrier.genererNumeroOrdre();
        courrier.setNumeroOrdre(numeroOrdre);
        return cR.save(courrier);
    }

    @Override
    public List<Courrier> all() {
        return cR.findAll();
    }

    @Override
    public Courrier updateCourrier(Courrier courrierDetails, Long id) {
        Optional<Courrier> existingCourrier = cR.findById(id);

        if (existingCourrier.isPresent()) {
            Courrier courrier = existingCourrier.get();

            courrier.setObjet(courrierDetails.getObjet());
            courrier.setDestinateur(courrierDetails.getDestinateur());
            courrier.setDescription(courrierDetails.getDescription());
            courrier.setEtat(courrierDetails.getEtat());
            courrier.setDestinatairesList(courrierDetails.getDestinatairesList());
            courrier.setCopiesList(courrierDetails.getCopiesList());
            courrier.setFilePath(courrierDetails.getFilePath());
            courrier.setFileName(courrierDetails.getFileName());
            courrier.setDescription(courrierDetails.getDescription());
            courrier.setStatutCourrier(courrierDetails.getStatutCourrier());
            
            return cR.save(courrier);
        } else {
            throw new CourrierExceptions("Courrier with id: " + id + " not found");
        }
    }

    @Override
    public String deleteCourrier(Long id) {
        Optional<Courrier> courrier = cR.findById(id);
        if (courrier.isPresent()) {
            cR.delete(courrier.get());
            // Supprimer les pièces jointes associées au courrier
            List<PieceJointe> pieceJointes = pjR.findByCourrierId(id);
            for (PieceJointe pieceJointe : pieceJointes) {
                pjR.delete(pieceJointe);
            }
            return "Courrier with id: " + id + " deleted Successfully";
        }
        throw new CourrierExceptions("Courrier with id: " + id + " not found");
    }

    @Override
    public Courrier getCourrierById(Long id) {
        // En récupérant un courrier par son ID, on peut également récupérer les pièces
        // jointes associées
        // en utilisant le repository des pièces jointes.
        // Les pièces jointes sont récupérées par le biais de la relation définie dans
        // la
        // classe Courrier.
        Optional<Courrier> courrier = cR.findById(id);
        if (courrier.isEmpty()) {
            throw new CourrierExceptions("Courrier with id: " + id + " not found");
        }
        // File attachments are handled via filePath and fileName fields
        return courrier.get();
    }

    @Override
    public List<Courrier> getCourriersByEtat(EtatCourrier etat) {
        return cR.findByEtat(etat.name());
    }

    @Override
    public List<Courrier> getCourriersByNumeroOrdre(String numeroOrdre) {
        return cR.findByNumeroOrdre(numeroOrdre);
    }

    @Override
    public List<Courrier> getCourriersByDateCourrier(String dateCourrier) {
        // Pour l'instant, retourner une liste vide car la conversion String->Date nécessite plus de logique
        return List.of();
    }
}
