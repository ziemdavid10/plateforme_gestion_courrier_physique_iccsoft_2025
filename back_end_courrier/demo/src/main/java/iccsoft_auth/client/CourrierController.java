package iccsoft_auth.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.client.RestTemplate;

import iccsoft_auth.request.iccsoftcourrier.CMRequest;

@RestController
@RequestMapping("/v4/api/courrier")
public class CourrierController {

    private final RestTemplate restTemplate;

    public CourrierController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static final String COURIER_SERVICE_URL = "http://localhost:8082/v3/courrier";

    // Récupérer tous les courriers
    @GetMapping("/all")
    @PreAuthorize("hasRole('SECRETAIRE')")
    public ResponseEntity<String> getAllCourriers() {
        String endpoint = COURIER_SERVICE_URL;
        try {
            return restTemplate.getForEntity(endpoint, String.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la récupération des courriers.");
        }
    }

    // Récupérer un courrier par ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SECRETAIRE')")
    public ResponseEntity<String> getCourrierById(@PathVariable Long id) {
        String endpoint = COURIER_SERVICE_URL + "/" + id;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(endpoint, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response;
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("Courrier non trouvé.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la récupération du courrier.");
        }
    }

    // Créer un nouveau courrier
    @PostMapping("/create")
    @PreAuthorize("hasRole('SECRETAIRE')")
    public ResponseEntity<String> createCourrier(@RequestBody String courrier) {
        String endpoint = COURIER_SERVICE_URL + "?employeId=1"; // ID temporaire
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(endpoint, courrier, String.class);
            if (response.getStatusCode() == HttpStatus.CREATED) {
                return ResponseEntity.status(HttpStatus.CREATED).body("Courrier créé avec succès.");
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("Erreur lors de la création du courrier.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la création du courrier.");
        }
    }

    // Mettre à jour un courrier existant
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('SECRETAIRE')")
    public ResponseEntity<String> updateCourrier(@PathVariable Long id, @RequestBody CMRequest courrierRequest) {
        String endpoint = COURIER_SERVICE_URL + "/" + id;
        try {
            ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.PUT, new HttpEntity<>(courrierRequest), String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok("Courrier mis à jour avec succès.");
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("Erreur lors de la mise à jour du courrier.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la mise à jour du courrier.");
        }
    }

    // Supprimer un courrier
    
}
