package com.example.iccsoft_courrier.services;

import java.util.List;

import com.example.iccsoft_courrier.models.Courrier;

public interface CourrierServices {

    Courrier createCourrier(Courrier courrier, Long employeId);

    Courrier getCourrierById(Long id);

    public List<Courrier> all();

    Courrier updateCourrier(Courrier courrier, Long courrierId);

    String deleteCourrier(Long id);
    
}
