package com.example.iccsoft_courrier.exception;

public class CourrierExceptions extends RuntimeException {
    public CourrierExceptions(String message) {
        super(message);
    }
    
    public CourrierExceptions(String message, Throwable cause) {
        super(message, cause);
    }
    
    public CourrierExceptions(Throwable cause) {
        super(cause);
    }
    
}
