package iccsoft_auth.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import iccsoft_auth.jwt.JwtResponse;
import iccsoft_auth.jwt.JwtUtils;
import iccsoft_auth.jwt.MessageResponse;
import iccsoft_auth.model.ERole;
import iccsoft_auth.model.Employe;
import iccsoft_auth.repo.EmployeRepository;
import iccsoft_auth.services.EmployeDetailsImpl;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/v1/api/auth")
// @CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final EmployeRepository employeRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager,
                          EmployeRepository employeRepository,
                          PasswordEncoder encoder,
                          JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.employeRepository = employeRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        EmployeDetailsImpl userDetails = (EmployeDetailsImpl) authentication.getPrincipal();
        // Récupérer le rôle de l'utilisateur
        String roles = userDetails.getAuthorities().stream()
                        .findFirst()
                        .map(item -> item.getAuthority())
                        .orElse("");

        return ResponseEntity
                .ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(),
                        roles));
        }
        
        
        
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (employeRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (employeRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        Employe employe = new Employe();
        employe.setUsername(signUpRequest.getUsername());
        employe.setEmail(signUpRequest.getEmail());
        employe.setName(signUpRequest.getName());
        employe.setPassword(encoder.encode(signUpRequest.getPassword()));

        String strRoles = signUpRequest.getRole();

        if (strRoles == null) {
            employe.setRole(ERole.EMPLOYE);
        } else {
            switch (strRoles.toLowerCase()) {
                case "administrateur":
                    employe.setRole(ERole.ADMINISTRATEUR);
                    break;
                case "secretaire":
                    employe.setRole(ERole.SECRETAIRE);
                    break;
                case "employe":
                default:
                    employe.setRole(ERole.EMPLOYE);
                    break;
            }
        }


        employeRepository.save(employe);
        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        // Invalidate the JWT token or perform any other logout logic
        // Note: JWT tokens are stateless, so you may not need to do anything here
        return ResponseEntity.ok(new MessageResponse("User logged out successfully!"));
    }
    
    
}

