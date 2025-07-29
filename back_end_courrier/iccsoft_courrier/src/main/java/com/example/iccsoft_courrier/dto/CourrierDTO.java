package com.example.iccsoft_courrier.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.*;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.example.iccsoft_courrier.models.Courrier.EtatCourrier;
import com.example.iccsoft_courrier.models.Courrier.StatutRecu;
import com.example.iccsoft_courrier.models.Employe;
import com.example.iccsoft_courrier.models.MiseEnCopie;

import jakarta.validation.constraints.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourrierDTO {

    private Long id;

    @NotBlank(message = "L'objet doit être mentionné")
    @Size(max = 255, message = "L'objet ne peut pas dépasser 255 caractères")
    private String objet;

    @NotBlank(message = "Le destinataire doit être mentionné")
    @Size(max = 255, message = "Le destinataire ne peut pas dépasser 255 caractères")
    private String destinateur;

    @NotNull(message = "La date du courrier doit être spécifiée")
    private String dateCourrier;

    @Size(max = 2000, message = "la description ne peut pas dépasser 2000 caractères")
    @Column(nullable = true)
    private String description;

    @NotNull(message = "Le statut du courrier doit être spécifié")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutRecu statut = StatutRecu.RECU;

    @NotNull(message = "L'état du courrier doit être spécifié")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatCourrier etat;

    public String GenererNumeroOrdre(){
        
        ConcurrentHashMap<String, AtomicInteger> compteurs = null;

        LocalDate dateActuelle = LocalDate.now();
        String dataFormatee = dateActuelle.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        AtomicInteger compteur = compteurs.computeIfAbsent(dataFormatee, k -> new AtomicInteger(0));

        int numeroSequentiel = compteur.incrementAndGet();

        numeroOrdre = String.format("%s-%03d", dataFormatee, numeroSequentiel);

        return numeroOrdre;
    }

    @NotBlank(message = "Le numéro d'ordre doit être mentionné")
    @Size(max = 50, message = "Le numéro d'ordre ne peut pas dépasser 50 caractères")
    // Le numéro d'ordre est au format année-num avec année qui représente l'année en cours et num qui est auto-incrément
    @Column(nullable = false, unique = true)
    private String numeroOrdre = GenererNumeroOrdre() ;

    // Les pièces jointes sont des fichiers associés au courrier, tels que des
    // documents, des images, etc.
    // Elles peuvent être utilisées pour fournir des informations supplémentaires ou
    // des preuves liées au courrier.
    private List<PieceJointeDTO> pieceJointes;

    // Les mises en copie sont des copies du courrier envoyées à d'autres
    // destinataires pour information.
    private List<MiseEnCopie> miseEnCopies;

    // Les destinataires sont les personnes ou entités qui reçoivent le courrier.
    private List<Employe> destinataires;

    // Nous préciserons un attribut pour le stockage du fichier numérique associé au
    // courrier.
    // Pour la création d'un courrier, il faudra importer un fichier numérique. A ce
    // niveau, le backend devra gérer l'import de fichiers externes.

}
