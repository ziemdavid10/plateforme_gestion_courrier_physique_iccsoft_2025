package com.example.iccsoft_user.controller;

import com.example.iccsoft_user.models.Employe;
import com.example.iccsoft_user.services.UserServices;
import com.example.iccsoft_user.services.AuthServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/v1/api/admin/users")
public class AdminUserController {

    @Autowired
    private UserServices userServices;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthServiceClient authServiceClient;

    @GetMapping
    public ResponseEntity<List<Employe>> getAllUsers() {
        return ResponseEntity.ok(userServices.all());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employe> getUserById(@PathVariable Long id) {
        Employe employe = userServices.getEmployeById(id);
        if (employe != null) {
            return ResponseEntity.ok(employe);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Employe> createUser(@RequestBody Employe employe) {
        try {
            // Store original password for auth service
            String originalPassword = employe.getPassword();
            
            // Create user in auth service first
            boolean authCreated = authServiceClient.createUserInAuthService(
                employe.getUsername(),
                employe.getEmail(),
                employe.getName(),
                employe.getRole().toString(),
                originalPassword
            );
            
            if (!authCreated) {
                return ResponseEntity.badRequest().build();
            }
            
            // Encode password before saving locally
            if (originalPassword != null && !originalPassword.isEmpty()) {
                employe.setPassword(passwordEncoder.encode(originalPassword));
            }
            
            Employe createdEmploye = userServices.createEmploye(employe);
            return ResponseEntity.ok(createdEmploye);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employe> updateUser(@PathVariable Long id, @RequestBody Employe employe) {
        employe.setId(id);
    if (employe.getPassword() != null && !employe.getPassword().isEmpty()) {
        employe.setPassword(passwordEncoder.encode(employe.getPassword()));
    }
    Employe updatedEmploye = userServices.updateEmploye(employe, id);
    if (updatedEmploye != null) {
        return ResponseEntity.ok(updatedEmploye);
    } else {
        return ResponseEntity.notFound().build();
    }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            // Get user info before deletion
            Employe employe = userServices.getEmployeById(id);
            if (employe != null) {
                // Delete from auth service
                authServiceClient.deleteUserInAuthService(employe.getUsername());
            }
            
            // Delete from local database
            userServices.deleteEmploye(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}