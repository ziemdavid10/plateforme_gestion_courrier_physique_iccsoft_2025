package com.example.iccsoft_user.services;

import java.util.List;

import com.example.iccsoft_user.models.Employe;

public interface UserServices {

    public Employe createEmploye(Employe employe);

    public List<Employe> all();

    public Employe updateEmploye(Employe employe, Long id);

    public String deleteEmploye(Long id);

    public Employe getEmployeById(Long id);
    
}
