package iccsoft_auth.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import iccsoft_auth.jwt.AuthEntryPointJwt;
import iccsoft_auth.jwt.AuthTokenFilter;
import iccsoft_auth.services.EmployeDetailsServiceImpl;


@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final EmployeDetailsServiceImpl employeDetailsServiceImpl;
    private final AuthEntryPointJwt unauthorizedHandler;

    public SecurityConfig(EmployeDetailsServiceImpl employeDetailsServiceImpl, AuthEntryPointJwt unauthorizedHandler) {
        this.employeDetailsServiceImpl = employeDetailsServiceImpl;
        this.unauthorizedHandler = unauthorizedHandler;
    }




    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Use BCrypt for password encoding
    }


    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(employeDetailsServiceImpl); // Set your user details service
        authProvider.setPasswordEncoder(passwordEncoder()); // Set the password encoder
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("*"); // Permettre toutes les origines
        configuration.addAllowedMethod("*"); // Toutes les méthodes HTTP
        configuration.addAllowedHeader("*"); // Tous les headers
        configuration.setAllowCredentials(true); // Permettre les cookies/credentials
        configuration.setMaxAge(3600L); // Cache preflight pendant 1 heure
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

     @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/v1/api/auth/signup", "/v1/api/auth/signin", "/v1/api/auth/admin/**", "/v1/api/auth/health", "/v1/api/auth/test", "/v1/api/auth/users", "/v1/api/auth/user/**").permitAll()
                                .requestMatchers("/v1/api/profile/**").authenticated()
                                .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());

        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    
}
