package com.example.iccsoft_courrier.security;

import org.springframework.stereotype.Component;

@Component
public class RoleBasedAccessControl {
    
    public boolean canCreateCourrier(String role) {
        return "SECRETAIRE".equals(role);
    }
    
    public boolean canUpdateCourrier(String role) {
        return "SECRETAIRE".equals(role);
    }
    
    public boolean canDeleteCourrier(String role) {
        return "SECRETAIRE".equals(role);
    }
    
    public boolean canViewAllCourriers(String role) {
        return "SECRETAIRE".equals(role) || "ADMINISTRATEUR".equals(role);
    }
    
    public boolean canViewOwnCourriers(String role) {
        return "EMPLOYE".equals(role) || "SECRETAIRE".equals(role) || "ADMINISTRATEUR".equals(role);
    }
}