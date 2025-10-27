package com.example.iccsoft_courrier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class IccsoftCourrierApplication {

	public static void main(String[] args) {
		SpringApplication.run(IccsoftCourrierApplication.class, args);
	}

}
