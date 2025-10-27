package com.sji.microservice.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@EnableDiscoveryClient
// @EnableConfigurationProperties(UriConfiguration.class)
@RestController
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}
	// tag::route-locator[]
	@Bean
	public RouteLocator myRoutes(RouteLocatorBuilder builder) {
		// String httpUri = uriConfiguration.getHttpbin();
		return builder.routes()
				// .route(p -> p
				// 		.path("/calco/add/**")
				// 		.uri("lb://ADD"))
				.build();
	}
	// end::route-locator[]
}

// tag::uri-configuration[]
// @ConfigurationProperties
// class UriConfiguration {

// 	private String httpbin = "http://localhost:8079";

// 	public String getHttpbin() {
// 		return httpbin;
// 	}

// 	public void setHttpbin(String httpbin) {
// 		this.httpbin = httpbin;
// 	}
// }
// end::uri-configuration[]
// end::code[]
