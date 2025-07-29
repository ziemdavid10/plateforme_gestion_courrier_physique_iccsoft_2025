package com.example.iccsoft_courrier.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.iccsoft_courrier.models.Courrier;
import com.example.iccsoft_courrier.models.Courrier.EtatCourrier;
import com.example.iccsoft_courrier.models.Courrier.StatutRecu;

import java.util.Date;

@Repository
public interface CourrierRepository extends JpaRepository<Courrier, Long> {

    List<Courrier> findByDestinateur(String destinateur);
    List<Courrier> findByDateReception(Date dateReception);

    List<Courrier> findByObjet(String objet);
    List<Courrier> findByDescription(String description);
    List<Courrier> findByStatut(StatutRecu statut);
    List<Courrier> findByEtat(EtatCourrier etat);
    List<Courrier> findByNumeroOrdre(String numeroOrdre);
    List<Courrier> findByDateCourrier(String dateCourrier);
    List<Courrier> searchByDescription(@Param("description") String description);
}
