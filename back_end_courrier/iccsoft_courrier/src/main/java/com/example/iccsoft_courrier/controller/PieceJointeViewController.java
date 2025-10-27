package com.example.iccsoft_courrier.controller;

import com.example.iccsoft_courrier.models.PieceJointe;
import com.example.iccsoft_courrier.services.PieceJointeServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/employee/courriers/{courrierId}/pieces-jointes")
public class PieceJointeViewController {

    @Autowired
    private PieceJointeServices pieceJointeServices;

    @GetMapping
    public ResponseEntity<List<PieceJointe>> getPiecesJointes(@PathVariable Long courrierId, @RequestHeader("X-User-Id") String userId) {
        List<PieceJointe> piecesJointes = pieceJointeServices.getPieceJointesByCourrierId(courrierId);
        return ResponseEntity.ok(piecesJointes);
    }

    @GetMapping("/{pieceId}/preview")
    public ResponseEntity<String> previewPieceJointe(@PathVariable Long courrierId, @PathVariable Long pieceId, @RequestHeader("X-User-Id") String userId) {
        PieceJointe pieceJointe = pieceJointeServices.getPieceJointeById(pieceId);
        
        if (pieceJointe == null) {
            return ResponseEntity.notFound().build();
        }
        
        String previewContent = "Preview of file: " + pieceJointe.getFileName() + "\nType: " + pieceJointe.getContentType() + "\nSize: " + pieceJointe.getFileSize();
        return ResponseEntity.ok(previewContent);
    }

    @GetMapping("/{pieceId}/download")
    public ResponseEntity<byte[]> downloadPieceJointe(@PathVariable Long courrierId, @PathVariable Long pieceId, @RequestHeader("X-User-Id") String userId) {
        PieceJointe pieceJointe = pieceJointeServices.getPieceJointeById(pieceId);
        
        if (pieceJointe == null) {
            return ResponseEntity.notFound().build();
        }
        
        // For now, return empty byte array as file content is not stored in this model
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + pieceJointe.getFileName())
                .header("Content-Type", pieceJointe.getContentType())
                .body(new byte[0]);
    }
}