package com.example.iccsoft_user.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.iccsoft_user.exception.EmployeExceptions;
import com.example.iccsoft_user.models.Employe;
import com.example.iccsoft_user.repositories.UserRepository;
import com.example.iccsoft_user.services.UserServices;

@Service
public class UserServicesImpl implements UserServices {

    private final UserRepository userRepository;

    public UserServicesImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Employe createEmploye(Employe user) {
        return userRepository.save(user);
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
            employe.setPassword(employeDetails.getPassword());
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

}
