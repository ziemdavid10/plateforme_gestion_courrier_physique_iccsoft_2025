package com.example.iccsoft_courrier.controller;

import com.example.iccsoft_courrier.models.CourrierStatusHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api")
@CrossOrigin(origins = "*")
public class CourrierStatusController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/courriers/{id}/status-history")
    public ResponseEntity<List<Map<String, Object>>> getStatusHistory(@PathVariable Long id) {
        String sql = """
            SELECT ancien_statut, nouveau_statut, utilisateur, date_changement, commentaire
            FROM courrier_status_history 
            WHERE courrier_id = ? 
            ORDER BY date_changement ASC
        """;
        
        List<Map<String, Object>> history = jdbcTemplate.queryForList(sql, id);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/courriers/{id}/add-status")
    public ResponseEntity<String> addStatusChange(
            @PathVariable Long id,
            @RequestBody Map<String, String> request,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        
        String ancienStatut = request.get("ancienStatut");
        String nouveauStatut = request.get("nouveauStatut");
        String commentaire = request.get("commentaire");
        String utilisateur = userId != null ? userId : "system";
        
        String sql = """
            INSERT INTO courrier_status_history (courrier_id, ancien_statut, nouveau_statut, utilisateur, date_changement, commentaire)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        
        jdbcTemplate.update(sql, id, ancienStatut, nouveauStatut, utilisateur, LocalDateTime.now(), commentaire);
        
        return ResponseEntity.ok("Historique de statut ajouté");
    }
}