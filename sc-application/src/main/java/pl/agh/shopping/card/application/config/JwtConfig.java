package pl.agh.shopping.card.application.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class JwtConfig {
    @Value("${security.jwt.header:Authorization}")
    private String header;

    @Value("${security.jwt.prefix:Bearer }")
    private String prefix;

    @Value("${security.jwt.secret:hbGciOiJIUzUxMiJ9}")
    private String secret;
}
