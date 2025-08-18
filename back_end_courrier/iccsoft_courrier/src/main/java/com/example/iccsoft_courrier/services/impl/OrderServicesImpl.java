package com.example.iccsoft_courrier.services.impl;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.iccsoft_courrier.exception.CourrierExceptions;
import com.example.iccsoft_courrier.models.Employe;
import com.example.iccsoft_courrier.services.OrderServices;
import com.example.iccsoft_courrier.services.impl.OrderServicesImpl;
import com.example.iccsoft_courrier.config.RestTemplateConfig;

@Service
public class OrderServicesImpl implements OrderServices {

    @Override
    public Employe getEmployeFromServiceUser(Long employeID){
        String url = "http://localhost:8081/v2/user/" + employeID;
        ResponseEntity<Employe> response = RestTemplateConfig.restTemplate().getForEntity(url, Employe.class);
        
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new CourrierExceptions("Failed to fetch employe from user service");
        }
    }

    @Override
    public boolean existsById(Long employeId) {
        String url = "http://localhost:8081/v2/user/" + employeId;
        ResponseEntity<Boolean> response = RestTemplateConfig.restTemplate().getForEntity(url, Boolean.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new CourrierExceptions("Failed to check if employe exists");  
        }
    }
}
