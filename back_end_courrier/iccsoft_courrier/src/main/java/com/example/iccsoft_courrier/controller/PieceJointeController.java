package com.example.iccsoft_courrier.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/pieces-jointes")
@CrossOrigin(origins = "*")
public class PieceJointeController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/courrier/{courrierId}")
    public ResponseEntity<List<Map<String, Object>>> getPiecesJointesByCourrier(@PathVariable Long courrierId) {
        try {
            String sql = "SELECT id, courrier_id, file_name, file_path, file_size, content_type, upload_date FROM pieces_jointes WHERE courrier_id = ?";
            List<Map<String, Object>> piecesJointes = jdbcTemplate.queryForList(sql, courrierId);
            return ResponseEntity.ok(piecesJointes);
        } catch (Exception e) {
            System.err.println("Error fetching pieces jointes: " + e.getMessage());
            return ResponseEntity.ok(List.of());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePieceJointe(@PathVariable Long id) {
        try {
            String sql = "DELETE FROM pieces_jointes WHERE id = ?";
            int deleted = jdbcTemplate.update(sql, id);
            if (deleted > 0) {
                return ResponseEntity.ok("Pièce jointe supprimée");
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur: " + e.getMessage());
        }
    }
}