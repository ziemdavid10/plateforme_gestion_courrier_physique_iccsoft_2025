package com.example.iccsoft_user.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.iccsoft_user.exception.EmployeExceptions;
import com.example.iccsoft_user.models.Employe;
import com.example.iccsoft_user.repositories.UserRepository;
import com.example.iccsoft_user.services.UserServices;

import jakarta.transaction.Transactional;

import com.example.iccsoft_user.dto.LoginRequest;
@Service
@Transactional
public class UserServicesImpl implements UserServices {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RestTemplate appRestTemplate;

    public UserServicesImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RestTemplate appRestTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.appRestTemplate = appRestTemplate;
    }

    @Override
    public Employe createEmploye(Employe user) {
        // Chiffrer le mot de passe avant sauvegarde
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Employe savedUser = userRepository.save(user);
        // Appel du micro-service d'authentification pour la création des credentials
        LoginRequest loginRequest = new LoginRequest(
            savedUser.getUsername(),
            user.getPassword()
        );

       try{
        ResponseEntity<String> response = appRestTemplate.postForEntity("http://localhost:8081/signin", loginRequest, String.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return savedUser;
        } else {
            // En cas d'erreur, supprimer l'utilisateur créé pour maintenir la cohérence
            userRepository.delete(savedUser);
            throw new EmployeExceptions("Failed to create credentials in auth service");
        }
       } catch (Exception e) {
            // En cas d'exception, supprimer l'utilisateur créé pour maintenir la cohérence
            userRepository.delete(savedUser);
            throw new EmployeExceptions("Error communicating with auth service: " + e.getMessage());
        
       }
        
    }

    @Override
    public List<Employe> all() {
        return userRepository.findAll();
    }

    @Override
    public Employe updateEmploye(Employe employeDetails, Long id) {
        Optional<Employe> existingEmploye = userRepository.findById(id);

        if (existingEmploye.isPresent()) {
            Employe employe = existingEmploye.get();
            employe.setAdresse(employeDetails.getAdresse());
            employe.setDepartment(employeDetails.getDepartment());
            employe.setEmail(employeDetails.getEmail());
            employe.setEntreprise(employeDetails.getEntreprise());
            employe.setFonction(employeDetails.getFonction());
            employe.setName(employeDetails.getName());
            // Chiffrer le nouveau mot de passe si fourni
            if (employeDetails.getPassword() != null && !employeDetails.getPassword().isEmpty()) {
                employe.setPassword(passwordEncoder.encode(employeDetails.getPassword()));
            }
            employe.setRole(employeDetails.getRole());
            employe.setUsername(employeDetails.getUsername());
            employe.setUpdatedAt(employeDetails.getCreatedAt());

            return userRepository.save(employe);
        }
        throw new EmployeExceptions("Employe with id:\\\" + id + \\\" not found");
    }

    @Override
    public String deleteEmploye(Long id) {
        Optional<Employe> employe = userRepository.findById(id);

        if (employe.isPresent()) {
            userRepository.delete(employe.get());
            return "Employe with id: " + id + " deleted successfully";
        }
        throw new EmployeExceptions("Employe with id:\\\" + id + \\\" not found");
    }

    @Override
    public Employe getEmployeById(Long id) {
        return userRepository.findById(id).orElseThrow(()-> new EmployeExceptions("Employe with id:\\\" + id + \\\" not found"));
    }
    
    @Override
    public Employe findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(()-> new EmployeExceptions("Employe with username: " + username + " not found"));
    }

}
