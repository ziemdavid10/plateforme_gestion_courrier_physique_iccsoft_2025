// Package racine de l'application de gestion de courrier ICCSOFT
package com.example.iccsoft_courrier;

// Importations Spring Boot pour le démarrage de l'application
import org.springframework.boot.SpringApplication; // Classe principale pour démarrer une application Spring Boot
import org.springframework.boot.autoconfigure.SpringBootApplication; // Configuration automatique Spring Boot
import org.springframework.cloud.client.discovery.EnableDiscoveryClient; // Activation du client de découverte de services

/**
 * Classe principale de l'application ICCSOFT de gestion de courrier physique
 * Cette application fait partie d'une architecture microservices
 */
@SpringBootApplication // Active la configuration automatique, le scan des composants et la configuration des propriétés
@EnableDiscoveryClient // Active la découverte de services pour l'intégration avec un service registry (Eureka, Consul, etc.)
public class IccsoftCourrierApplication {

	/**
	 * Point d'entrée principal de l'application
	 * Démarre le contexte Spring Boot et initialise tous les composants
	 * @param args Arguments de ligne de commande passés à l'application
	 */
	public static void main(String[] args) {
		// Démarrage de l'application Spring Boot avec la classe de configuration principale
		SpringApplication.run(IccsoftCourrierApplication.class, args);
	}

}
