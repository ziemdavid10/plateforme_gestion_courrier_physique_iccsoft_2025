package iccsoft.authentication_keycloak.security;



import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;


@Data
@Configuration
@ConfigurationProperties(prefix = "jwt.converter")
public class JwtConverterProperties {

    private String resourceId;
    private String principalAttribute;
    
}
