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
            // pj.setChemin(pieceJointe.getChemin());
            pj.setDescription(pieceJointe.getDescription());
            
            return pieceJointeRepository.save(pj);
        }
        throw new RuntimeException("PieceJointe with id: " + id + " not found");
    }

    @Override
    public List<PieceJointe> getPieceJointesByCourrierId(Long courrierId) {
        return pieceJointeRepository.findByCourrierId(courrierId);
    }

    // Implementation of methods defined in PieceJointeServices interface
    // This class will handle the business logic related to PieceJointe entities


    // @Override
    // public List<PieceJointe> getPieceJointesByUrlStockage(String urlStockage) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'getPieceJointesByUrlStockage'");
    // }

    // @Override
    // public List<PieceJointe> getPieceJointesByDateCreation(String dateCreation) {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'getPieceJointesByDateCreation'");
    // }
    
}
