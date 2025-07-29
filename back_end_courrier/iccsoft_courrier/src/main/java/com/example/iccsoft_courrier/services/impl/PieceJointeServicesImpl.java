package com.example.iccsoft_courrier.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.iccsoft_courrier.models.PieceJointe;
import com.example.iccsoft_courrier.repositories.PieceJointeRepository;
import com.example.iccsoft_courrier.services.PieceJointeServices;

@Service
public class PieceJointeServicesImpl implements PieceJointeServices {
    private final PieceJointeRepository pieceJointeRepository;

    public PieceJointeServicesImpl(PieceJointeRepository pieceJointeRepository) {
        this.pieceJointeRepository = pieceJointeRepository;
    }

    @Override
    public PieceJointe createPieceJointe(PieceJointe pieceJointe) {
        // Lorsqu'une pièce jointe est créée, elle est associée à un courrier existant.
        // Assurez-vous que la pièce jointe a un courrier associé avant de la
        // sauvegarder
        if (pieceJointe.getCourrier() == null) {
            throw new RuntimeException("PieceJointe must be associated with a Courrier");
        }
        return pieceJointeRepository.save(pieceJointe);
    }

    @Override
    public PieceJointe getPieceJointeById(Long id) {
        return pieceJointeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PieceJointe with id: " + id + " not found"));
    }

    @Override
    public List<PieceJointe> getAllPieceJointes() {
        return pieceJointeRepository.findAll();
    }

    @Override
    public String deletePieceJointe(Long id) {
        Optional<PieceJointe> pieceJointe = pieceJointeRepository.findById(id);
        if (pieceJointe.isPresent()) {
            pieceJointeRepository.delete(pieceJointe.get());
            return "PieceJointe with id: " + id + " deleted successfully";
        }
        throw new RuntimeException("PieceJointe with id: " + id + " not found");
    }

    @Override
    public PieceJointe updatePieceJointe(PieceJointe pieceJointe, Long id) {
        Optional<PieceJointe> existingPieceJointe = pieceJointeRepository.findById(id);
        if (existingPieceJointe.isPresent()) {
            PieceJointe pj = existingPieceJointe.get();
            pj.setNom(pieceJointe.getNom());
            pj.setType(pieceJointe.getType());
            pj.setDescription(pieceJointe.getDescription());

            return pieceJointeRepository.save(pj);
        }
        throw new RuntimeException("PieceJointe with id: " + id + " not found");
    }

    @Override
    public List<PieceJointe> getPieceJointesByCourrierId(Long courrierId) {
        return pieceJointeRepository.findByCourrierId(courrierId);
    }
}
