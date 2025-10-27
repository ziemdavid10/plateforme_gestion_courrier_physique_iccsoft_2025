package com.example.iccsoft_courrier.validation;

import com.example.iccsoft_courrier.models.Courrier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CourrierValidator {
    
    public List<String> validate(Courrier courrier) {
        List<String> errors = new ArrayList<>();
        
        if (courrier.getObjet() == null || courrier.getObjet().trim().isEmpty()) {
            errors.add("L'objet du courrier est obligatoire");
        }
        
        if (courrier.getDestinateur() == null || courrier.getDestinateur().trim().isEmpty()) {
            errors.add("Le destinateur est obligatoire");
        }
        
        if (courrier.getDestinataire() == null || courrier.getDestinataire().trim().isEmpty()) {
            errors.add("Le destinataire est obligatoire");
        }
        
        if (courrier.getDateCourrier() == null) {
            errors.add("La date du courrier est obligatoire");
        }
        
        if (courrier.getDateEntree() == null) {
            errors.add("La date d'entrée est obligatoire");
        }
        
        if (courrier.getDescription() == null || courrier.getDescription().trim().isEmpty()) {
            errors.add("La description est obligatoire");
        }
        
        if (courrier.getEtat() == null || courrier.getEtat().trim().isEmpty()) {
            errors.add("L'état est obligatoire");
        }
        
        if (courrier.getStatutCourrier() == null || courrier.getStatutCourrier().trim().isEmpty()) {
            errors.add("Le statut du courrier est obligatoire");
        }
        
        return errors;
    }
}