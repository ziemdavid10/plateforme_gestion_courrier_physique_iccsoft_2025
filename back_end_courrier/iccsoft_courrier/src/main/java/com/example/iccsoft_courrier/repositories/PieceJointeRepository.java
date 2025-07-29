package com.example.iccsoft_courrier.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.iccsoft_courrier.models.PieceJointe;

@Repository
public interface PieceJointeRepository extends JpaRepository<PieceJointe, Long> {

    List<PieceJointe> findByCourrierId(Long courrierId);
    
}
