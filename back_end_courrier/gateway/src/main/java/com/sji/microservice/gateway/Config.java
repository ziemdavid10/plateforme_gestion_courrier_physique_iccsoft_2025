package com.sji.microservice.gateway;

import com.sji.microservice.gateway.security.AuthenticationFilter;
import com.sji.microservice.gateway.security.RoleBasedAuthorizationFilter;
import com.sji.microservice.gateway.security.EmployeeCourrierFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class Config {

	@Autowired
	private AuthenticationFilter authenticationFilter;

	@Autowired
	private RoleBasedAuthorizationFilter roleBasedAuthorizationFilter;

	@Autowired
	private EmployeeCourrierFilter employeeCourrierFilter;

	@Bean
	public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
				// Auth routes (no authentication required)
				.route("auth-service", r -> r.path("/v1/api/auth/**")
						.uri("lb://demo"))
				
				// Admin-only routes (User CRUD) - Rediriger vers le service d'authentification
				.route("admin-users", r -> r.path("/v1/api/admin/users/**")
						.filters(f -> {
							RoleBasedAuthorizationFilter.Config config = new RoleBasedAuthorizationFilter.Config();
							config.setRequiredRole("ADMINISTRATEUR");
							return f.filter(roleBasedAuthorizationFilter.apply(config));
						})
						.uri("lb://demo"))
				
				// Secretary-only routes (Courrier CRUD)
				.route("secretary-courriers", r -> r.path("/v1/api/secretary/courriers/**")
						.filters(f -> {
							RoleBasedAuthorizationFilter.Config config = new RoleBasedAuthorizationFilter.Config();
							config.setRequiredRole("SECRETAIRE");
							return f.filter(roleBasedAuthorizationFilter.apply(config));
						})
						.uri("lb://iccsoft-courrier"))
				
				// Employee-only routes (Own courriers read/delete)
				.route("employee-courriers", r -> r.path("/v1/api/employee/courriers/**")
						.filters(f -> f.filter(employeeCourrierFilter.apply(new EmployeeCourrierFilter.Config())))
						.uri("lb://iccsoft-courrier"))
				
				// Profile routes (authenticated users)
				.route("profile-service", r -> r.path("/v1/api/profile/**")
						.filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
						.uri("lb://demo"))
				
				// Admin user management routes
				.route("admin-users", r -> r.path("/v1/api/admin/users/**")
						.filters(f -> f.filter(roleBasedAuthorizationFilter.apply(new RoleBasedAuthorizationFilter.Config())))
						.uri("lb://iccsoft-user"))
				.build();
	}




}
