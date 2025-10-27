package com.example.iccsoft_user.dto;

public class SignupRequest {
    private String username;
    private String email;
    private String name;
    private String role;
    private String password;

    public SignupRequest() {}

    public SignupRequest(String username, String email, String name, String role, String password) {
        this.username = username;
        this.email = email;
        this.name = name;
        this.role = role;
        this.password = password;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}