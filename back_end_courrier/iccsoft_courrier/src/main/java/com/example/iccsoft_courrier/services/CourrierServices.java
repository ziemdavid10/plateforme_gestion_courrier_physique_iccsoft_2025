package com.example.iccsoft_courrier.services;

import java.util.List;

import com.example.iccsoft_courrier.models.Courrier;
import com.example.iccsoft_courrier.models.Courrier.EtatCourrier;

public interface CourrierServices {

    Courrier createCourrier(Courrier courrier, Long employeId);

    Courrier getCourrierById(Long id);

    List<Courrier> all();

    Courrier updateCourrier(Courrier courrier, Long courrierId);

    String deleteCourrier(Long id);

    List<Courrier> getCourriersByEtat(EtatCourrier etat);

    List<Courrier> getCourriersByNumeroOrdre(String numeroOrdre);

    List<Courrier> getCourriersByDateCourrier(String dateCourrier);

}
