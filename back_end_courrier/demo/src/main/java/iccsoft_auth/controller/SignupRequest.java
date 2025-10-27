package iccsoft_auth.controller;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignupRequest {
    @NotBlank(message = "Username is required")
    @Size(max = 20, message = "Username must be less than 20 characters")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Name is required")
    @Size(max = 20, message = "Name must be less than 20 characters")
    private String name;
    
    private String role;
    
    @NotBlank(message = "Password is required")
    @Size(max = 120, message = "Password must be less than 120 characters")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
       return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
