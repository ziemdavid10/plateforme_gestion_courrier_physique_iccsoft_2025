package com.example.iccsoft_courrier.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.iccsoft_courrier.models.PieceJointe;
import com.example.iccsoft_courrier.services.PieceJointeServices;

@RestController
@RequestMapping("/v4/piece-jointe")
public class PieceJointeController {

    private final PieceJointeServices pieceJointeServices;

    public PieceJointeController(PieceJointeServices pieceJointeServices) {
        this.pieceJointeServices = pieceJointeServices;
    }

    @GetMapping("/{Courrier_id}")
    public ResponseEntity<List<PieceJointe>> getPieceJointesByCourrierId(@PathVariable Long courrierId) {
        List<PieceJointe> pieceJointes = pieceJointeServices.getPieceJointesByCourrierId(courrierId);
        return new ResponseEntity<>(pieceJointes, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PieceJointe> createPieceJointe(@RequestBody PieceJointe pieceJointe) {
        PieceJointe createdPieceJointe = pieceJointeServices.createPieceJointe(pieceJointe);
        return new ResponseEntity<>(createdPieceJointe, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PieceJointe>> getAllPieceJointes() {
        List<PieceJointe> pieceJointes = pieceJointeServices.getAllPieceJointes();
        return new ResponseEntity<>(pieceJointes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PieceJointe> getPieceJointeById(@PathVariable Long id) {
        PieceJointe pieceJointe = pieceJointeServices.getPieceJointeById(id);
        if (pieceJointe != null) {
            return new ResponseEntity<>(pieceJointe, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PieceJointe> updatePieceJointe(@RequestBody PieceJointe pieceJointe, @PathVariable Long id) {
        PieceJointe updatedPieceJointe = pieceJointeServices.updatePieceJointe(pieceJointe, id);
        if (updatedPieceJointe != null) {
            return new ResponseEntity<>(updatedPieceJointe, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePieceJointe(@PathVariable Long id) {
        String response = pieceJointeServices.deletePieceJointe(id);
        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
