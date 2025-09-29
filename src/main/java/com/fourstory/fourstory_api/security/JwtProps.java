package com.fourstory.fourstory_api.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.security.jwt")
public class JwtProps {

    private String privateKeyPath;

    private String publicKeyPath;

    private long accessTokenExpiration;
}
