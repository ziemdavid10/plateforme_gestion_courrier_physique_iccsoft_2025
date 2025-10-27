package iccsoft_auth.request.iccsoftcourrier;

import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.Getter;

@NoArgsConstructor
@Getter
@Setter
public class CMRequest {
    private Long id;
    private String objet;
    private String destinateur;
    private String dateCourrier;
    private String dateReception;
    private String dateModification;
    private String description;
    private String statut; // Assuming StatutRecu is a string representation
    
}
