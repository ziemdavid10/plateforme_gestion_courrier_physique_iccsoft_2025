package iccsoft_auth.client;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import iccsoft_auth.request.iccsoftuser.UMRequest;


@RestController
@RequestMapping("/v1/api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {
     
    private final RestTemplate restTemplate;

    public UserController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private static final String USER_SERVICE_URL = "http://localhost:8083/v2/user";

    // Récupérer tous les utilisateurs
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMINISTRATEUR') or hasRole('EMPLOYE') or hasRole('SECRETAIRE')")
    public ResponseEntity<String> getAllUsers() {
        String endpoint = USER_SERVICE_URL + "/all";
        try {
            return restTemplate.getForEntity(endpoint, String.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la récupération des utilisateurs.");
        }
    }

    // Récupérer un utilisateur par ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR') or hasRole('EMPLOYE') or hasRole('SECRETAIRE')")
    public ResponseEntity<String> getUserById(@PathVariable Long id) {
        String endpoint = USER_SERVICE_URL + "/" + id;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(endpoint, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response;
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("Utilisateur non trouvé."); 
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la récupération de l'utilisateur.");
        }
    }

    // Créer un nouvel utilisateur
    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<String> createUser(@RequestBody UMRequest userRequest) {
        String endpoint = USER_SERVICE_URL;
        try {
            ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.POST, new HttpEntity<>(userRequest), String.class);
            if (response.getStatusCode() == HttpStatus.CREATED) {
                return response;
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("Erreur lors de la création de l'utilisateur.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la création de l'utilisateur.");
        }
    }

    // Mettre à jour un utilisateur
    @PutMapping("/update/{id}")
    // Même l'employé peut mettre à jour son profil, mais l'administrateur peut mettre à jour n'importe quel utilisateur
    @PreAuthorize("hasRole('EMPLOYE') or hasRole('ADMINISTRATEUR')")
    public ResponseEntity<String> updateUser(@PathVariable Long id, @RequestBody UMRequest userRequest) {
        String endpoint = USER_SERVICE_URL + "/" + id;
        try {
            ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.PUT, new HttpEntity<>(userRequest), String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response;
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("Erreur lors de la mise à jour de l'utilisateur.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la mise à jour de l'utilisateur.");
        }
    }

    // Supprimer un utilisateur par ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        String endpoint = USER_SERVICE_URL + "/" + id;
        try {
            ResponseEntity<String> response = restTemplate.exchange(endpoint, HttpMethod.DELETE, null, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok("Utilisateur supprimé avec succès.");
            } else {
                return ResponseEntity.status(response.getStatusCode()).body("Erreur lors de la suppression de l'utilisateur.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erreur lors de la suppression de l'utilisateur.");
        }
    }
}
