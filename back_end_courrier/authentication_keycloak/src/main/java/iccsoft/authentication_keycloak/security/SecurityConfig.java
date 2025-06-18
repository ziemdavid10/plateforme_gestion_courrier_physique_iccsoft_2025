package iccsoft.authentication_keycloak.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    public static final String  ADMIN = "iccsoft_admin";
    public static final String  EMPLOYEE = "iccsoft_employe";
    public static final String  SECRETAIRE = "iccsoft_secretaire";
    private final JwtConverter jwtConverter;

    @Bean
    public SecurityFilterChain configure (HttpSecurity http) throws Exception {
         // Désactive CSRF
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET, "/api/home").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/admin/**").hasRole(ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/employee/**").hasRole(EMPLOYEE)
                        .requestMatchers(HttpMethod.GET, "/api/secretaire/**").hasRole(SECRETAIRE)
                        .requestMatchers(HttpMethod.GET, "/api/public").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtConverter)))
                .build();
    }
}
