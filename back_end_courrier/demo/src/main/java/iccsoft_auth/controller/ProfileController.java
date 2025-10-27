package iccsoft_auth.controller;

import iccsoft_auth.model.Employe;
import iccsoft_auth.services.EmployeDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/profile")
public class ProfileController {

    @Autowired
    private EmployeDetailsServiceImpl employeService;

    @GetMapping
    public ResponseEntity<Employe> getProfile(Authentication authentication) {
        String username = authentication.getName();
        Employe employe = employeService.findByUsername(username);
        return ResponseEntity.ok(employe);
    }

    @PutMapping
    public ResponseEntity<Employe> updateProfile(@RequestBody Employe employe, Authentication authentication) {
        String username = authentication.getName();
        Employe existingEmploye = employeService.findByUsername(username);
        
        // Update only allowed fields
        existingEmploye.setName(employe.getName());
        existingEmploye.setEmail(employe.getEmail());
        existingEmploye.setDepartment(employe.getDepartment());
        existingEmploye.setFonction(employe.getFonction());
        existingEmploye.setAdresse(employe.getAdresse());
        existingEmploye.setTelephone(employe.getTelephone());
        existingEmploye.setEntreprise(employe.getEntreprise());
        
        Employe updatedEmploye = employeService.updateEmploye(existingEmploye);
        return ResponseEntity.ok(updatedEmploye);
    }
}