package iccsoft.authentication_keycloak.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class homecontroller {

    @GetMapping("/home")
    public String home() {
        return "Welcome to the ICCSoft Authentication Keycloak Service!";
    }

    @GetMapping("/admin")
    public String admin() {
        return "Welcome to the ICCSoft Admin Keycloak Service!";
    }

    @GetMapping("/employee")
    public String user() {
        return "Welcome to the ICCSoft User Keycloak Service!";
    }

    @GetMapping("/secretaire")
    public String secretaire() {
        return "Welcome to the ICCSoft Secretaire Keycloak Service!";
    }

    @GetMapping("/public")
    public String publicEndpoint() {
        return "Welcome to the ICCSoft Public Keycloak Service!";
    }
}
