package com.example.iccsoft_courrier.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/secretary/users")
@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
public class UserListController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getUsers() {
        try {
            // Call user service to get list of users
            String userServiceUrl = "http://localhost:8083/v1/api/admin/users";
            ResponseEntity<List> response = restTemplate.getForEntity(userServiceUrl, List.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                return ResponseEntity.ok(response.getBody());
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            // Return empty list if service is not available
            return ResponseEntity.ok(List.of());
        }
    }
}