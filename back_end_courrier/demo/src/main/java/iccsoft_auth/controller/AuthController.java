package iccsoft_auth.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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


// @CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/v1/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
// @CrossOrigin(origins = "http://localhost:4200", maxAge = 3600)
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    EmployeRepository employeRepository;


    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                        loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        EmployeDetailsImpl userDetails = (EmployeDetailsImpl) authentication.getPrincipal();
        // String  roles =  userDetails.getAuthorities().stream().map(item -> item.getAuthority())
        //         .collect(Collectors.joining(","));
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


        // Set<String> roles = new HashSet<>();
        // if (strRoles == null) {
        //     String userRole = employeRepository.findByRole(ERole.EMPLOYE)
        //             .orElseThrow(new java.util.function.Supplier<RuntimeException>() {
        //                 @Override
        //                 public RuntimeException get() {
        //                     return new RuntimeException("Error: Role is not found.");
        //                 }
        //             });
        //     roles.add(userRole);
        // } else {
        //         String roleName = strRoles.toLowerCase();
        //         switch (roleName) {
        //             case "administrateur":
        //                 String adminRole = employeRepository.findByRole(ERole.ADMINISTRATEUR)
        //                         .orElseThrow(new java.util.function.Supplier<RuntimeException>() {
        //                 @Override
        //                 public RuntimeException get() {
        //                     return new RuntimeException("Error: Role is not found.");
        //                 }
        //             });
        //                 roles.add(adminRole);

        //                 break;
        //             case "secretaire":
        //                 String secretaireRole = employeRepository.findByRole(ERole.SECRETAIRE)
        //                         .orElseThrow(new java.util.function.Supplier<RuntimeException>() {
        //                 @Override
        //                 public RuntimeException get() {
        //                     return new RuntimeException("Error: Role is not found.");
        //                 }
        //             });
        //                 roles.add(secretaireRole);
        //                 break;

        //             case "employe":
        //                 String employeRole = employeRepository.findByRole(ERole.EMPLOYE)
        //                         .orElseThrow(new java.util.function.Supplier<RuntimeException>() {
        //                 @Override
        //                 public RuntimeException get() {
        //                     return new RuntimeException("Error: Role is not found.");
        //                 }
        //             });
        //                 roles.add(employeRole);
        //                 break;

        //             default:
        //                 String userRole = employeRepository.findByRole(ERole.EMPLOYE)
        //                         .orElseThrow(new java.util.function.Supplier<RuntimeException>() {
        //                 @Override
        //                 public RuntimeException get() {
        //                     return new RuntimeException("Error: Role is not found.");
        //                 }
        //             });
        //                 roles.add(userRole);
        //         }
        //     }
        // employe.setRoles(roles);
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

