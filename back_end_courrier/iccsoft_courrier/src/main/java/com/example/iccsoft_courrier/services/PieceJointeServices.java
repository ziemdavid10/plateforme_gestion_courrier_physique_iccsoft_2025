package com.example.iccsoft_courrier.services;

import java.util.List;

import com.example.iccsoft_courrier.models.PieceJointe;

public interface PieceJointeServices {
    PieceJointe createPieceJointe(PieceJointe pieceJointe);

    PieceJointe getPieceJointeById(Long id);

    List<PieceJointe> getAllPieceJointes();

    String deletePieceJointe(Long id);

    PieceJointe updatePieceJointe(PieceJointe pieceJointe, Long id);

    List<PieceJointe> getPieceJointesByCourrierId(Long courrierId); 
}
