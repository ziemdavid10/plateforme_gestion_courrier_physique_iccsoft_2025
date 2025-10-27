package com.example.iccsoft_courrier.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CourrierStatusService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public enum ActionType {
        CREATED, VIEWED, PROCESSED, ARCHIVED
    }

    public void updateStatusBasedOnAction(Long courrierId, ActionType action, String userId) {
        String currentStatus = getCurrentStatus(courrierId);
        String newStatus = determineNewStatus(currentStatus, action);
        
        if (!currentStatus.equals(newStatus)) {
            updateCourrierStatus(courrierId, newStatus, userId);
        }
    }

    private String getCurrentStatus(Long courrierId) {
        String sql = "SELECT etat FROM courriers WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, String.class, courrierId);
    }

    private String determineNewStatus(String currentStatus, ActionType action) {
        switch (action) {
            case CREATED:
                return "RECU";
            case VIEWED:
                return currentStatus.equals("RECU") ? "EN_ATTENTE" : currentStatus;
            case PROCESSED:
                return "TRAITE";
            case ARCHIVED:
                return "ARCHIVE";
            default:
                return currentStatus;
        }
    }

    private void updateCourrierStatus(Long courrierId, String newStatus, String userId) {
        String sql = "UPDATE courriers SET etat = ?, date_modification = NOW() WHERE id = ?";
        jdbcTemplate.update(sql, newStatus, courrierId);
        
        // Log status change
        logStatusChange(courrierId, newStatus, userId);
    }

    private void logStatusChange(Long courrierId, String newStatus, String userId) {
        String sql = "INSERT INTO courrier_status_log (courrier_id, status, changed_by, change_date) VALUES (?, ?, ?, NOW())";
        try {
            jdbcTemplate.update(sql, courrierId, newStatus, userId);
        } catch (Exception e) {
            // Table might not exist, ignore for now
        }
    }
}