package com.example.iccsoft_user.services;

import com.example.iccsoft_user.dto.SignupRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AuthServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    private final String AUTH_SERVICE_URL = "http://localhost:8081/v1/api/auth";

    public boolean createUserInAuthService(String username, String email, String name, String role, String password) {
        try {
            SignupRequest signupRequest = new SignupRequest(username, email, name, role, password);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<SignupRequest> request = new HttpEntity<>(signupRequest, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                AUTH_SERVICE_URL + "/signup", 
                request, 
                String.class
            );
            
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            System.err.println("Error creating user in auth service: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteUserInAuthService(String username) {
        try {
            // For now, we don't have a delete endpoint in auth service
            // This would need to be implemented in the auth service
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting user in auth service: " + e.getMessage());
            return false;
        }
    }
}