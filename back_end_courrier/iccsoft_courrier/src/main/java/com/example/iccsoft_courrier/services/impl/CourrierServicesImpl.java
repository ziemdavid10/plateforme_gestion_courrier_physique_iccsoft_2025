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
        if (courrier.getDestinataires() == null || courrier.getDestinataires().isEmpty()) {
            throw new CourrierExceptions("Au moins un destinataire doit être mentionné");
        }
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
        if (courrier.getDestinataires() == null || courrier.getDestinataires().isEmpty()) {
            courrier.setDestinataires(List.of());
        }
        // On ajoute l'employé à la liste des destinataires
        // On suppose que l'employé est déjà récupéré par son ID
        Employe employe = oS.getEmployeFromServiceUser(employeId);
        courrier.setDestinataires(List.of(employe));
        // Lorsque je crée un courrier, je peux directement créer autant de pièces
        // jointes que je veux
        // et les associer au courrier.
        if (courrier.getPieceJointes() != null && !courrier.getPieceJointes().isEmpty()) {
            for (var pieceJointe : courrier.getPieceJointes()) {
                pieceJointe.setCourrier(courrier); // Associate the piece jointe with the courrier
                pjR.save(pieceJointe); // Save the piece jointe
            }
        }
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
            courrier.setMiseEnCopies(courrierDetails.getMiseEnCopies());
            courrier.setDestinataires(courrierDetails.getDestinataires());
            courrier.setPieceJointes(courrierDetails.getPieceJointes());
            courrier.setDescription(courrierDetails.getDescription());
            courrier.setStatut(courrierDetails.getStatut());
            // Lorque je mets à jour un courrier, je peux également mettre à jour les pièces
            // jointes
            if (courrierDetails.getPieceJointes() != null && !courrierDetails.getPieceJointes().isEmpty()) {
                for (var pieceJointe : courrierDetails.getPieceJointes()) {
                    // Je peux mettre à jour les pièces jointes existantes ou en ajouter de
                    // nouvelles
                    // Si la pièce jointe a un ID, on la met à jour, sinon on la crée
                    if (pieceJointe.getId() != null) {
                        Optional<PieceJointe> existingPieceJointe = pjR.findById(pieceJointe.getId());
                        if (existingPieceJointe.isPresent()) {
                            PieceJointe pj = existingPieceJointe.get();
                            pj.setNom(pieceJointe.getNom());
                            pj.setType(pieceJointe.getType());
                            pj.setDescription(pieceJointe.getDescription());
                            pjR.save(pj); // Metre à jour la pièce jointe existante
                        }
                    } else {
                        // Si la pièce jointe n'existe pas, on la crée
                        pieceJointe.setId(null);
                        pieceJointe.setCourrier(courrier);
                        pjR.save(pieceJointe);
                    }
                }

                return cR.save(courrier);
            } else {
                throw new CourrierExceptions("Courrier with id: " + id + " not found");
            }
        }
        return courrierDetails;
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
        // On peut également charger les pièces jointes associées au courrier
        List<PieceJointe> pieceJointes = pjR.findByCourrierId(id);
        Courrier c = courrier.get();
        c.setPieceJointes(pieceJointes); // Associer les pièces jointes au courrier
        return c;
    }

    @Override
    public List<Courrier> getCourriersByEtat(EtatCourrier etat) {
        return cR.findByEtat(etat);
    }

    @Override
    public List<Courrier> getCourriersByNumeroOrdre(String numeroOrdre) {
        return cR.findByNumeroOrdre(numeroOrdre);
    }

    @Override
    public List<Courrier> getCourriersByDateCourrier(String dateCourrier) {
        return cR.findByDateCourrier(dateCourrier);
    }
}
