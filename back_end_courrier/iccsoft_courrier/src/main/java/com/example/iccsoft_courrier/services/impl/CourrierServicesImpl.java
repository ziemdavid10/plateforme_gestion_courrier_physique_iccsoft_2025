package com.example.iccsoft_courrier.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.iccsoft_courrier.exception.CourrierExceptions;
import com.example.iccsoft_courrier.models.Courrier;
import com.example.iccsoft_courrier.models.Employe;
import com.example.iccsoft_courrier.repositories.CourrierRepository;
import com.example.iccsoft_courrier.services.CourrierServices;
import com.example.iccsoft_courrier.services.OrderServices;

@Service
public class CourrierServicesImpl implements CourrierServices {
    private final CourrierRepository cR;
    private final OrderServices oS;

    public CourrierServicesImpl(CourrierRepository cR, OrderServices oS) {
        this.cR = cR;
        this.oS = oS;
    }

    @Override
    public Courrier createCourrier(Courrier courrier, Long employeId) {
        Employe employe = oS.getEmployeFromServiceUser(employeId);
        courrier.setDestinataires(List.of(employe)); // Assuming you want to set the employee as a recipient
        // Assuming you want to set the employee's ID or some other attribute
        // courrier.setEmployeId(employe.getId()); // Uncomment if you have such a field in Courrier
        return cR.save(courrier);
    }

    @Override
    public List<Courrier> all(){
        return cR.findAll();
    }

    @Override
    public Courrier updateCourrier(Courrier courrierDetails, Long id) {
        Optional<Courrier> existingCourrier = cR.findById(id);
        

        if(existingCourrier.isPresent()){
                Courrier courrier = existingCourrier.get();
                
                courrier.setObjet(courrierDetails.getObjet());
                courrier.setDestinateur(courrierDetails.getDestinateur());
                courrier.setDescription(courrierDetails.getDescription());
                courrier.setEtat(courrierDetails.getEtat());
                courrier.setMiseEnCopies(courrierDetails.getMiseEnCopies());
                courrier.setDestinataires(courrierDetails.getDestinataires());
                courrier.setPieceJointes(courrierDetails.getPieceJointes()); 
                courrier.setDescription(courrierDetails.getDescription());
                courrier.setStatut(courrierDetails.getStatut());
        
            return cR.save(courrier);
        } else {
            throw new CourrierExceptions("Courrier with id: " + id + " not found");
        }
    }

    @Override
    public String deleteCourrier(Long id) {
        Optional<Courrier> courrier = cR.findById(id);
        if (courrier.isPresent()) {
            cR.delete(courrier.get());
            return "Courrier with id: " + id + " deleted Successfully";
        }
        throw new CourrierExceptions("Courrier with id: " + id + " not found");
    }

    @Override
    public Courrier getCourrierById(Long id) {
        return cR.findById(id).orElseThrow(() -> new RuntimeException("Courrier not found with id: " + id));
    }
    
    
}
