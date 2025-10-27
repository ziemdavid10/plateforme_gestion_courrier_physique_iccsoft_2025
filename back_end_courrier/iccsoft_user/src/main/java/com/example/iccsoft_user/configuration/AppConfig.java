package com.example.iccsoft_user.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {
    @Bean("appRestTemplate")
    public RestTemplate appRestTemplate(){
        return new RestTemplate();        
    }
       
}
