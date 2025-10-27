package iccsoft_auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
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

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/v1/api/auth")
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
        System.out.println("Login attempt for username: " + loginRequest.getUsername());
        
        try {
            if (!employeRepository.existsByUsername(loginRequest.getUsername())) {
                System.out.println("User not found in database: " + loginRequest.getUsername());
                return ResponseEntity.status(401).body(new MessageResponse("Invalid username or password"));
            }
            
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            EmployeDetailsImpl userDetails = (EmployeDetailsImpl) authentication.getPrincipal();
            String roles = userDetails.getAuthorities().stream()
                            .findFirst()
                            .map(item -> item.getAuthority())
                            .orElse("");
            
            System.out.println("Login successful for user: " + loginRequest.getUsername() + " with role: " + roles);

            return ResponseEntity
                    .ok(new JwtResponse(jwt, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(),
                            roles));
        } catch (Exception e) {
            System.out.println("Authentication failed for user: " + loginRequest.getUsername() + ". Error: " + e.getMessage());
            return ResponseEntity.status(401).body(new MessageResponse("Invalid username or password"));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        try {
            System.out.println("Signup request: " + signUpRequest.getUsername());
            
            if (signUpRequest.getUsername() == null || signUpRequest.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Username is required"));
            }
            if (signUpRequest.getEmail() == null || signUpRequest.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Email is required"));
            }
            if (signUpRequest.getPassword() == null || signUpRequest.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Password is required"));
            }
            if (signUpRequest.getName() == null || signUpRequest.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Name is required"));
            }

            if (employeRepository.existsByUsername(signUpRequest.getUsername().trim())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Username is already taken"));
            }
            if (employeRepository.existsByEmail(signUpRequest.getEmail().trim())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Email is already in use"));
            }

            Employe employe = new Employe();
            employe.setUsername(signUpRequest.getUsername().trim());
            employe.setEmail(signUpRequest.getEmail().trim());
            employe.setName(signUpRequest.getName().trim());
            employe.setPassword(encoder.encode(signUpRequest.getPassword()));
            
            String role = signUpRequest.getRole();
            if (role != null && !role.trim().isEmpty()) {
                switch (role.toLowerCase().trim()) {
                    case "administrateur":
                        employe.setRole(ERole.ADMINISTRATEUR);
                        break;
                    case "secretaire":
                        employe.setRole(ERole.SECRETAIRE);
                        break;
                    default:
                        employe.setRole(ERole.EMPLOYE);
                        break;
                }
            } else {
                employe.setRole(ERole.EMPLOYE);
            }

            employeRepository.save(employe);
            System.out.println("User registered: " + employe.getUsername());
            
            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
            
        } catch (Exception e) {
            System.out.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new MessageResponse("Registration failed: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(new MessageResponse("Auth service is running on port 8081"));
    }
    
    @GetMapping("/admin/test")
    public ResponseEntity<?> testAdmin() {
        return ResponseEntity.ok(new MessageResponse("Admin endpoint working!"));
    }
    
    @GetMapping("/admin/users")
    public ResponseEntity<?> getAdminUsers() {
        try {
            System.out.println("Loading all users from database");
            var users = employeRepository.findAll();
            System.out.println("Found " + users.size() + " users");
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            System.out.println("Error loading users: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("Error loading users: " + e.getMessage()));
        }
    }
    
    @PostMapping("/admin/users")
    public ResponseEntity<?> createAdminUser(@RequestBody SignupRequest signUpRequest) {
        try {
            System.out.println("Creating user via admin: " + signUpRequest.getUsername());
            
            if (signUpRequest.getUsername() == null || signUpRequest.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Username is required"));
            }
            if (signUpRequest.getEmail() == null || signUpRequest.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Email is required"));
            }
            if (signUpRequest.getPassword() == null || signUpRequest.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Password is required"));
            }
            if (signUpRequest.getName() == null || signUpRequest.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(new MessageResponse("Name is required"));
            }

            if (employeRepository.existsByUsername(signUpRequest.getUsername().trim())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Username is already taken"));
            }
            if (employeRepository.existsByEmail(signUpRequest.getEmail().trim())) {
                return ResponseEntity.badRequest().body(new MessageResponse("Email is already in use"));
            }

            Employe employe = new Employe();
            employe.setUsername(signUpRequest.getUsername().trim());
            employe.setEmail(signUpRequest.getEmail().trim());
            employe.setName(signUpRequest.getName().trim());
            employe.setPassword(encoder.encode(signUpRequest.getPassword()));
            
            String role = signUpRequest.getRole();
            if (role != null && !role.trim().isEmpty()) {
                switch (role.toLowerCase().trim()) {
                    case "administrateur":
                        employe.setRole(ERole.ADMINISTRATEUR);
                        break;
                    case "secretaire":
                        employe.setRole(ERole.SECRETAIRE);
                        break;
                    default:
                        employe.setRole(ERole.EMPLOYE);
                        break;
                }
            } else {
                employe.setRole(ERole.EMPLOYE);
            }

            Employe savedUser = employeRepository.save(employe);
            System.out.println("User created successfully: " + savedUser.getUsername());
            
            return ResponseEntity.ok(savedUser);
            
        } catch (Exception e) {
            System.out.println("Error creating user: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new MessageResponse("Error creating user: " + e.getMessage()));
        }
    }
    
    @PutMapping("/admin/users/{id}")
    public ResponseEntity<?> updateAdminUser(@PathVariable Long id, @RequestBody SignupRequest updateRequest) {
        try {
            System.out.println("Updating user with ID: " + id);
            
            var userOpt = employeRepository.findById(id);
            if (!userOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Employe user = userOpt.get();
            
            if (updateRequest.getUsername() != null && !updateRequest.getUsername().trim().isEmpty()) {
                user.setUsername(updateRequest.getUsername().trim());
            }
            if (updateRequest.getEmail() != null && !updateRequest.getEmail().trim().isEmpty()) {
                user.setEmail(updateRequest.getEmail().trim());
            }
            if (updateRequest.getName() != null && !updateRequest.getName().trim().isEmpty()) {
                user.setName(updateRequest.getName().trim());
            }
            if (updateRequest.getPassword() != null && !updateRequest.getPassword().trim().isEmpty()) {
                user.setPassword(encoder.encode(updateRequest.getPassword()));
            }
            if (updateRequest.getRole() != null && !updateRequest.getRole().trim().isEmpty()) {
                switch (updateRequest.getRole().toLowerCase().trim()) {
                    case "administrateur":
                        user.setRole(ERole.ADMINISTRATEUR);
                        break;
                    case "secretaire":
                        user.setRole(ERole.SECRETAIRE);
                        break;
                    default:
                        user.setRole(ERole.EMPLOYE);
                        break;
                }
            }
            
            employeRepository.save(user);
            System.out.println("User updated successfully: " + user.getUsername());
            
            return ResponseEntity.ok(user);
            
        } catch (Exception e) {
            System.out.println("Error updating user: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("Error updating user: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<?> deleteAdminUser(@PathVariable Long id) {
        try {
            System.out.println("Deleting user with ID: " + id);
            if (employeRepository.existsById(id)) {
                employeRepository.deleteById(id);
                return ResponseEntity.ok(new MessageResponse("User deleted successfully!"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.out.println("Error deleting user: " + e.getMessage());
            return ResponseEntity.badRequest().body(new MessageResponse("Error deleting user: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal EmployeDetailsImpl userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body(new MessageResponse("User not authenticated"));
        }
        
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(item -> item.getAuthority())
                .orElse("");
        
        return ResponseEntity.ok(new JwtResponse("", userDetails.getId(), userDetails.getUsername(), 
                userDetails.getEmail(), role));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        return ResponseEntity.ok(new MessageResponse("User logged out successfully!"));
    }
}