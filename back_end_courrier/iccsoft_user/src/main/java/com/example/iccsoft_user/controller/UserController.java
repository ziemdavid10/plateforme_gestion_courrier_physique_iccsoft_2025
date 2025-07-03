package com.example.iccsoft_user.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.iccsoft_user.models.Employe;
import com.example.iccsoft_user.services.UserServices;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/v2/user")
public class UserController {

    private final UserServices us;

    public UserController(UserServices us) {
        this.us = us;
    }

    @GetMapping
    public ResponseEntity<List<Employe>> getAll() {
        return new ResponseEntity<>(us.all(), HttpStatus.OK);
    }

    @PostMapping("/adduser")
    public ResponseEntity<Employe> createEmploye(@RequestBody Employe employe) {
        return new ResponseEntity<>(us.createEmploye(employe), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employe> updateEmploye(@RequestBody Employe employe, @PathVariable Long id) {
        Employe updatedEmploye = us.updateEmploye(employe, id);
        if (updatedEmploye != null){
            return new ResponseEntity<>(updatedEmploye, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employe> getEmployeById(@PathVariable Long id) {
        return new ResponseEntity<>(us.getEmployeById(id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public String deleteEmploye(@PathVariable Long id) {
        return us.deleteEmploye(id);
    }
}
