package com.example.iccsoft_user;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ApiError {
    private String message;
    private Long code;
    private LocalDateTime timestamp;
    
}
